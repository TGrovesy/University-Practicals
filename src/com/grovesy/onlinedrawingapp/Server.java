package com.grovesy.onlinedrawingapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Window canvas = new Window(true);

		ServerSocket serverS = new ServerSocket(61209);

		while (true) {
			System.out.println("Waiting for clients to connect on port " + serverS.getLocalPort() + " ...");
			Socket s = serverS.accept();
			CanvasService service = new CanvasService(s, canvas.getCanvas());
			Thread t = new Thread(service);
			t.start();
		}

	}

}
