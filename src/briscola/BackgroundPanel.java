package briscola;


import utils.ImageLoader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel class for displaying a background image with additional components.
 */

public class BackgroundPanel extends JPanel {

	private final ImageLoader imgLoad = new ImageLoader();
	private static final long serialVersionUID = 1L;
	private final String path;


	/**
	 * Constructs a BackgroundPanel with the specified background image path.
	 *
	 * @param path The path to the background image
	 */

	public BackgroundPanel(String path ) {

		// Set the background image path
		this.path = path;

		// Set panel size and layout
		setSize(1178, 861);
		setLayout(null);

		// Add copyright label
		JLabel copyright = new JLabel("Copyright (c) 2025 Leonardo Belli");
		copyright.setFont(new Font("Serif", Font.PLAIN, 14));
		copyright.setBounds(960, 793, 428, 30);
		copyright.setForeground(new Color(51,51,51));
		add(copyright);
		
	}

	/**
	 * Overrides the paintComponent method to draw the background image.
	 *
	 * @param g The Graphics context to draw on
	 */

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw the background image

		g.drawImage(imgLoad.loadImage(path).getImage(), 0, 0, null);


	}
	
}
