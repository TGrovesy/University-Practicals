package com.grovesy.onlinedrawingapp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.prism.Graphics;

public class Window extends JFrame {

	private Canvas drawingPanel;

	private boolean isServer = false;

	public Window(boolean server) {
		this.isServer = server;
		if (!server) {
			setTitle("Drawing Client");
			addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent event) {

	                drawingPanel.CloseConnection();
	                
	            }
	        });        
		} else {
			setTitle("Drawing Server");
		}
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		SetupWindow();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	public JPanel GetPanel() {
		return drawingPanel;
	}

	private void SetupWindow() {
		if (!this.isServer) {

			drawingPanel = new Canvas(false);
		} else {

			drawingPanel = new Canvas(true);
		}

		this.add(drawingPanel);
	}

	
	public Canvas getCanvas() {
		return this.drawingPanel;
	}
}
