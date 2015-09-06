import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class About extends JFrame implements ActionListener {
	/*
	 * creates a new windows and displays infomation about this program
	 */
	JFrame mainFrame = new JFrame("About: Virtual Breadboard");

	public About() {
		Dimension screenSize = VirtualBreadboard.screenSize;
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setSize(484, 338);

		// centers it on the screen
		mainFrame.setLocation ((int)Math.round(screenSize.getWidth() / 2) - (int)Math.round (mainFrame.getSize().getWidth() / 2),
							   (int)Math.round(screenSize.getHeight() / 2) - (int)Math.round (mainFrame.getSize().getHeight() / 2));
		String version = VirtualBreadboard.version;

		ImageIcon logo = new ImageIcon ("images\\logo.jpg");
		JLabel logoLabel = new JLabel ();
		JLabel versionLabel = new JLabel ("Version: " + version);
		JPanel info = new JPanel();
		JLabel uniqueID = new JLabel("S/N: " + "xxxx-xxx-xxxx");	// This is where the ID number will show to uniquely ID the product
		info.setLayout(null);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);

		logoLabel.setIcon (logo);
		mainFrame.getContentPane().add(logoLabel);
		logoLabel.setBounds (2, 2, 470, 141);

		mainFrame.getContentPane().add(info);
		info.setBounds (2, 145, 470, 130);
		info.setBorder (BorderFactory.createEtchedBorder());

		JLabel creators = new JLabel("Created by: Steven Harradine");
		info.add(creators);
		creators.setBounds (175, 10, 200, 30);

		JLabel description = new JLabel ("<html>Description: This program simulates the workings of a breadboard.  This program is protected under Canadian Law and may not be distributed.  Reproduction of this program in part or in whole is strictly prohibited.<center>&#169&#32 2005</center></html>");
		info.add(description);
		description.setBounds (7, 60, 470, 65);

		mainFrame.getContentPane().add (okButton);
		okButton.setBounds ((mainFrame.getSize().width / 2) - 36, 277, 75, 25);

		mainFrame.getContentPane().add (versionLabel);
		versionLabel.setBounds(mainFrame.getSize().width - (mainFrame.getSize().width / 3), 283, 150, 15);
		
		mainFrame.getContentPane().add (uniqueID);
		uniqueID.setBounds (15, 215, 150, 150);

		mainFrame.getContentPane().add(new JLabel());
		mainFrame.setVisible(true);
	}
	public void actionPerformed (ActionEvent e) {
		if (e.getActionCommand().equals("OK"))
			mainFrame.dispose();
	}
}