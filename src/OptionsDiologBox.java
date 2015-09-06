/*
 *      author:  Steven J. Harradine
 *
 *    filename:  OptionsDiologBox.java
 *
 *     started:  20050312
 *    finished:  2005xxxx
 *
 * description:  This object will allow the user to modify the options of the
 *               virtual breadboard (ie. reading and writing the options.dat
 *               file)
 *
 */
 
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
 class OptionsDiologBox extends JFrame implements ActionListener {
 	QuickieForms formBuilder = new QuickieForms();
 	optionsController optionsCtrl = new optionsController();
 	
	JTextField pinsColumnsTexField;
	JTextField pinsRowsTexField;
	JTextField pinSizeTexField;
	JTextField pinSpacingTexField;
	JTextField ioColumnsTexField;
	JTextField ioRowsTexField;
	JTextField ioInputTexField;
	JTextField ioOutputTexField;
	JTextField webBrowserLocation;
	JTextField helpLocation;

	JFrame mainFrame = new JFrame("Virtual Breadboard Options");
	
	JTabbedPane tabPane = new JTabbedPane();
	JPanel generalPanel = new JPanel(null);
	
	public OptionsDiologBox () {
		QuickieForms formBuilder = new QuickieForms();
		Dimension screenSize = VirtualBreadboard.screenSize;
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setSize(484, 338);

		// centers it on the screen
		mainFrame.setLocation ((int)Math.round(screenSize.getWidth() / 2) - (int)Math.round (mainFrame.getSize().getWidth() / 2),
							   (int)Math.round(screenSize.getHeight() / 2) - (int)Math.round (mainFrame.getSize().getHeight() / 2));
		
		JPanel buttonsPanel = new JPanel(null);
		JButton applyButton = formBuilder.makeButtonWithAL ("Apply", this);
		JButton defaultButton = formBuilder.makeButtonWithAL ("Load Default Settings", this);
		JButton cancelButton = formBuilder.makeButtonWithAL ("Cancel", this);
		
		tabPane.addTab ("General", generalPanel);
		
		buttonsPanel.add (applyButton);
		buttonsPanel.add (defaultButton);
		buttonsPanel.add (cancelButton);
		
		mainFrame.getContentPane().add(buttonsPanel);
		
		buttonsPanel.setBounds (1, (int)mainFrame.getSize().getHeight() - 75,
		                        (int)mainFrame.getSize().getWidth() - 11, 40);
		
		cancelButton.setBounds (10, 8, 90, 25);
		defaultButton.setBounds ((int)(mainFrame.getSize().getWidth() / 2) - 80, 8, 160, 25);
		applyButton.setBounds ((int)mainFrame.getSize().getWidth() - 110, 8, 90, 25);
		
		mainFrame.getContentPane().add(tabPane);
		tabPane.setBounds (10, 10, (int)mainFrame.getSize().getWidth() - 29,
		                   (int)(mainFrame.getSize().getHeight() - buttonsPanel.getSize().getHeight()) - 50);
		
		loadGeneralPanel();
		
		mainFrame.getContentPane().add(new JLabel());
		mainFrame.setVisible(true);
	}
	
	private void loadGeneralPanel() {
		JPanel pinsPanel = new JPanel(new GridLayout (0, 6, 10, 5));
		JPanel ioPanel = new JPanel(new GridLayout (0, 6, 10, 5));
		JPanel helpPanel = new JPanel(new GridLayout (0, 2, 10, 5));
		
		formBuilder.createJLabel ("Pins:", generalPanel, 5, 5, 30, 15);
		
		formBuilder.createJLabel ("Columns:", pinsPanel);
		pinsColumnsTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.pinsColumns), pinsPanel);
			
		formBuilder.createJLabel ("Rows:", pinsPanel);
		pinsRowsTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.pinsRows), pinsPanel);
		
		formBuilder.createJLabel ("Width:", pinsPanel);
		pinSizeTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.pinSize), pinsPanel);
		
		formBuilder.createJLabel ("Spacing:", pinsPanel);
		pinSpacingTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.pinSpacing), pinsPanel);
		
		generalPanel.add (pinsPanel);
		pinsPanel.setBounds (10, 20, 430, 40);
		
		formBuilder.createJLabel ("Input/Output:", generalPanel, 5, 30 + pinsPanel.getSize().height, 75, 15);
		
		formBuilder.createJLabel ("Columns:", ioPanel);
		ioColumnsTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.ioColumns), ioPanel);
		
		formBuilder.createJLabel ("Rows:", ioPanel);
		ioRowsTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.ioRows), ioPanel);
		
		formBuilder.createJLabel ("# of Input:", ioPanel);
		ioInputTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.ioInput), ioPanel);
		
		formBuilder.createJLabel ("# of Output:", ioPanel);
		ioOutputTexField = formBuilder.createJTextField (Integer.toString(optionsCtrl.ioOutput), ioPanel);
		
		generalPanel.add (ioPanel);
		ioPanel.setBounds (10, 45 + pinsPanel.getSize().height, 430, 40);
		
		formBuilder.createJLabel ("Help:", generalPanel, 5, 55 + pinsPanel.getSize().height + ioPanel.getSize().height, 30, 15);
		
		formBuilder.createJLabel ("Web Browser:", helpPanel);
		webBrowserLocation = formBuilder.createJTextField (optionsCtrl.webBrowserLocation, helpPanel);
		
		formBuilder.createJLabel ("Help Page:", helpPanel);
		helpLocation = formBuilder.createJTextField (optionsCtrl.helpLocation, helpPanel);
		
		generalPanel.add (helpPanel);
		helpPanel.setBounds (10, 70 + pinsPanel.getSize().height + ioPanel.getSize().height, 430, 40);
	}
	
	public void actionPerformed (ActionEvent e) {
		if (e.getActionCommand().equals("Cancel"))
			mainFrame.dispose();
		else if (e.getActionCommand().equals("Apply")) {
			// get new data from the form
			optionsCtrl.pinsColumns = Integer.parseInt(pinsColumnsTexField.getText());
			optionsCtrl.pinsRows = Integer.parseInt(pinsRowsTexField.getText());
			optionsCtrl.pinSize = Integer.parseInt(pinSizeTexField.getText());
			optionsCtrl.pinSpacing = Integer.parseInt(pinSpacingTexField.getText());
			optionsCtrl.ioColumns = Integer.parseInt(ioColumnsTexField.getText());
			optionsCtrl.ioRows = Integer.parseInt(ioRowsTexField.getText());
			optionsCtrl.ioInput = Integer.parseInt(ioInputTexField.getText());
			optionsCtrl.ioOutput = Integer.parseInt(ioOutputTexField.getText());
			optionsCtrl.webBrowserLocation = webBrowserLocation.getText();
			optionsCtrl.helpLocation = helpLocation.getText();
			
			optionsCtrl.saveOptions();
			
			mainFrame.dispose();
		} else if (e.getActionCommand().equals("Load Default Settings")) {
			optionsCtrl.loadDefaultSettings();
			
			// put new data back into the form
			pinsColumnsTexField.setText(Integer.toString(optionsCtrl.pinsColumns));
			pinsRowsTexField.setText(Integer.toString(optionsCtrl.pinsRows));
			pinSizeTexField.setText(Integer.toString(optionsCtrl.pinSize));
			pinSpacingTexField.setText(Integer.toString(optionsCtrl.pinSpacing));
			ioColumnsTexField.setText(Integer.toString(optionsCtrl.ioColumns));
			ioRowsTexField.setText(Integer.toString(optionsCtrl.ioRows));
			ioInputTexField.setText(Integer.toString(optionsCtrl.ioInput));
			ioOutputTexField.setText(Integer.toString(optionsCtrl.ioOutput));
		}
	}
}