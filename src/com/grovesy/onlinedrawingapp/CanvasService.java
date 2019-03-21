package com.grovesy.onlinedrawingapp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CanvasService implements Runnable {

	private Socket socket;

	private Scanner in;
	private PrintWriter out;

	private ObjectOutputStream outBigData;

	private Canvas canvas;

	public CanvasService(Socket socket, Canvas canvas) {
		this.socket = socket;
		this.canvas = canvas;
	}

	private boolean running;

	@Override
	public void run() {
		try {
			try {
				System.out.println("Client " + socket.getInetAddress() + " connected.");

				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream());
				outBigData = new ObjectOutputStream(socket.getOutputStream());
				running = true;
				ServeClient();

			} finally {
				socket.close();
				running = false;
				System.out.println("Client " + socket.getInetAddress() + " disconnected.");
			}
		} catch (IOException e) {
			// Add warning
			e.printStackTrace();
		}
	}

	private void ServeClient() throws IOException {
		while (running) {
			System.out.println("Execute");
			if (!in.hasNext()) {
				// UpdateClientCanvas();
				continue;
			}
			String command = in.next();
			if (command.equals("Quit")) {
				running = false;
				break;
			} else if (command.equals("fetch")) {
				UpdateClientCanvas();
			} else {
				System.out.println("Executeing...");
				ExecuteCommand(command);
			}
		}
	}

	private void UpdateClientCanvas() {
		try {
			outBigData.writeObject(canvas.getFreehandXY());
			outBigData.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ExecuteCommand(String command) {
		String[] commandSplit = command.split(",");
		int x = Integer.parseInt(commandSplit[0]), y = Integer.parseInt(commandSplit[1]);
		canvas.AddPixel(x, y);
		out.println("Executed");
		out.flush();
	}

}
