package com.grovesy.onlinedrawingapp;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JPanel;

public class Canvas extends JPanel {

	private int[] mousePos = new int[2];
	DrawingMouseMotionListener mouseListener;
	DrawingMouseListener mouseListen;

	private final int MAX_PIXEL_COUNT = 100000;
	private int[][] freehandXY = new int[MAX_PIXEL_COUNT][3];
	private int currentFreehandPixelCount = 0;

	public Canvas(boolean isServer) {
		mouseListener = new DrawingMouseMotionListener();
		mouseListen = new DrawingMouseListener();
		if (!isServer) {
			this.addMouseMotionListener(mouseListener);
			this.addMouseListener(mouseListen);
			ConnectServer();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		for (int i = 0; i < currentFreehandPixelCount; i++) {
			g.drawRect(freehandXY[i][0], freehandXY[i][1], 10, 10);
			g.fillRect(freehandXY[i][0], freehandXY[i][1], 10, 10);
		}
	}

	private Socket socket;
	private InputStream instream;
	private OutputStream outstream;
	private Scanner in;
	private PrintWriter out;

	private void ConnectServer() {
		try {
			socket = new Socket("localhost", 61209);
			instream = socket.getInputStream();
			outstream = socket.getOutputStream();
			in = new Scanner(instream);
			out = new PrintWriter(outstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection Failed!");
		}
	}

	public void CloseConnection() {
		send("Quit\n");
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}

	}

	public String send(String command) {
		System.out.println("Sending: " + command);
		out.print(command);
		System.out.println("Flushing: " + command);
		out.flush();
		System.out.println("Flushed: " + command);
		if (in.hasNextLine()) {
			String response = in.nextLine();
			System.out.println("Response: " + response);
			return response;
		}

		return "";
	}

	private void FetchCanvasFromServer() {
		try {
			send("fetch");
			ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
			freehandXY = (int[][]) inFromServer.readObject();

			repaint();
		} catch (IOException | ClassNotFoundException e) {

		}
	}

	public int[][] getFreehandXY() {
		return freehandXY;
	}

	public void AddPixel(int x, int y) {
		freehandXY[currentFreehandPixelCount][0] = x;
		freehandXY[currentFreehandPixelCount][1] = y;
		currentFreehandPixelCount++;
		repaint();
	}

	class DrawingMouseMotionListener implements MouseMotionListener {

		private String mousePosString;

		@Override
		public void mouseDragged(MouseEvent event) {
			mouseMoved(event);
			int x = mousePos[0];
			int y = mousePos[1];
			freehandXY[currentFreehandPixelCount][0] = x;
			freehandXY[currentFreehandPixelCount][1] = y;
			currentFreehandPixelCount++;
			String command = String.valueOf(x) + "," + String.valueOf(y) + "\n";
			repaint();
			send(command);
		}

		@Override
		public void mouseMoved(MouseEvent event) {
			// Update the coordinates label with the new mouse pos.
			mousePosString = String.format("%04dpix, %04dpix", event.getX(), event.getY());
			mousePos[0] = event.getX();
			mousePos[1] = event.getY();

		}

		public int[] GetMousePos() {
			return mousePos;
		}
	}

	class DrawingMouseListener implements MouseListener {

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			FetchCanvasFromServer();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
			
		}

		public void mouseClicked(MouseEvent e) {
		}

	}

}
