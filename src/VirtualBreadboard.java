/*
 *      author:  Steven J. Harradine
 *
 *    filename:  VirtualBreadboard.java
 *
 *     started:  20040517
 *    finished:  2005xxxx
 *
 * description:  This program will simulate the workings of a breadboard.
 *               Features will include the ability to use wires, chips, and many
 *               other components.
 *
 *  Wish list
 * ***********
 * 1) capasiter
 * 2) buzzer
 * 3) diode (led without light) black box with line for direction
 * 4) transister (pn2222a) (2n2907)
 * 5) moter w/ and w/o stepper
 * 6) IR
 * 7) parrellel
 * 8) demultiplexer (74ls154)
 * 9) addressable latch (74ls259)
 *
 */
// TODO: Convert Wire colours and led colours to linked lists.
// TODO: Arc wires, leds, resistors.
// TODO: Parallel port abilities form turing to VBB (C || ASM Interface)

import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

public class VirtualBreadboard extends JFrame implements MouseListener,
														 ActionListener,
														 WindowListener,
														 ComponentListener {
	public static void main (String[] args) throws Exception {
		// Load the options from options.txt
		loadOptions();
		loadWindows();

		// create the virtual breadboard		
		System.out.println ("Loading Graphical Interface . . .");
		try{
			new VirtualBreadboard();
			
			System.out.println ("Graphical Interface Loaded");
			System.out.println ("\n\n\n\t\t\t\t** DO NOT CLOSE **");
		} catch (Exception e) {
			System.out.println ("Error an unknown exception occurred: " + e.getMessage());
			
			System.out.print ("\nLoading default settings . . . ");
			optionsCtrl.loadDefaultSettings();
			optionsCtrl.saveOptions();
			System.out.println ("Done");
			System.out.print ("\nPlease restart the program");
		}
	}
	
	QuickieForms formBuilder = new QuickieForms();	// Build forms with short cuts
	static optionsController optionsCtrl = new optionsController(); // allows access to the options
	
	static int totalPins;
	
	static final String version = "2005";				// version number
	final String breadboardTitle = "Untitled.vbb";		// default title on the breadboard

	static int[][] dipswitchPinArray = new int[2][8];	// [0 = x, 1 = y][dipswitch number value (ie A=1)]
	static int[][] breadboardPinArray;
	static boolean[] breadboardLinkage;			// what pins are in use
	static String[] breadboardUsage;			// what pins are connected to what and where
	static int[] breadboardComponentNumbers;	// Contains the unique component numbers of each component.  This is used for removal of the component because all indexes of the same number are the same component.
	static drawDipswitches dipswitches = new drawDipswitches();
	static drawPower powerIndicator = new drawPower();
	static String currentHover;					// the current item the user is over
	static int powerSwitchPosition = 21;		// position of the power toggle switch (21 off, 4 on)

	JFrame mainFrame;							// main window

	// the options panels
	static JPanel toolOptionPanel = new JPanel();	// the general option panel that the specific ones are loaded to
	static JPanel resistorOptionsPanel = new JPanel();
	static JPanel colourOptionsPanel = new JPanel();
	static JPanel testerOptionsPanel = new JPanel();
	
	static int testerIndex = -1;
	static JLabel testerIndexLabel;
	static JLabel testerUseageLabel;
	static JLabel testerPowerLabel;
	static JLabel testerIdLabel;

	static JLabel toolOptionTitle = new JLabel("Options:");	// options title
	static drawLED[] ioLEDs =  new drawLED[16];				// LEDs for the i/o, +5 and gnd
	static Color currentColour = Color.BLACK;				// the current colour the user has selected
	static JInternalFrame toolbox;			// the tool box internal frame
	static JInternalFrame breadBoardFrame;	// the bread boards internal frame
	static JMenuBar menuBar;				// the main menu
	static JDesktopPane desktop;			// the intenal frames reside in this
	static final int numOfToolboxButtons = 12;	// how many buttons there are
	static JButton[] buttons = new JButton[numOfToolboxButtons];
	static int[][] toolBoxButtonLocations = new int[2][numOfToolboxButtons];
	static showSelectedTool currentSelectedToolShow = new showSelectedTool();
	static int numberOfToolsPerRows = 150 / 73;		// calculates the number of chips per row (150 default width, 73 button width)
	static String currentTool = "And Chip";			// the currently selected tool
	static drawBreadboard breadBoard;
	static ImageIcon[] buttonImages = new ImageIcon[numOfToolboxButtons];

	// tests to see if one end is pluged in and that the next click will be the
	// second end
	static boolean newWire = true;
	static boolean newLed = true;
	static boolean newResistor = true;

	// stores the point of the first click
	static int firstWirePoint = 0;
	static int firstLedPoint = 0;
	static int firstResistorPoint = 0;

	// counts the number of wire, LEDs and resistors, etc...
	static componentInventory numberOfComponents = new componentInventory();

	// gets the screens size
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	// the starting location on the breadboard (offset b/c of the inputs on the right side)
	static final int startingX = 186;
	static JComboBox[] resistanceComboBox = new JComboBox[4];
	static JLabel totalResistanceLable = new JLabel ();

	// colour options of resistors
	static String[] resistanceOptions = {"Black", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Violet"};
	
	// the colours of the wires/leds used
	static colourDB[] wireColours;
	static colourDB[] ledColours;
	
	// the buttons and fields dealing with the colour options
	static JRadioButton[] colourOptions;
	static JTextField colourCustom = new JTextField();
	
	// create JPopupMenu
	static JPopupMenu popupMenu = new JPopupMenu();
	
	boolean isSavedEver = false;	// has the file been saved
	boolean isSavedCurrent = false;	// has current work been save (since last save)
	String saveLocation = "";		// where the file is saved
	
	static private void loadOptions() {
		// calculate the number of pins on the board
		totalPins = ((optionsCtrl.pinsColumns * ((optionsCtrl.pinsRows * 2) + 2)) +
							(((optionsCtrl.ioColumns + 1) * optionsCtrl.ioRows)) *
							(optionsCtrl.ioInput + optionsCtrl.ioInput + 2)) + 1;
		
		breadboardPinArray = new int[2][totalPins];
		breadboardLinkage = new boolean[totalPins];
		breadboardUsage = new String[totalPins];
		breadboardComponentNumbers = new int[totalPins];
		
		wireColours = new colourDB[(int)Math.ceil (totalPins / 2)];
		ledColours = new colourDB[(int)Math.ceil (totalPins / 2)];
	}
	static private void loadWindows() {
		// preload the about window
		About loadAbout = new About ();
		loadAbout.mainFrame.dispose();
		
		// preload the options window
		OptionsDiologBox loadOptionsDiolog = new OptionsDiologBox();
		loadOptionsDiolog.mainFrame.dispose();
	}

	private VirtualBreadboard() throws Exception {
		mainFrame.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame("Virtual Breadboard");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize (740, 480);
		mainFrame.addWindowListener(this);

		// Center main frame
		mainFrame.setLocation ((int)Math.round(screenSize.getWidth() / 2) - (int)Math.round (mainFrame.getSize().getWidth() / 2),
							   (int)Math.round(screenSize.getHeight() / 2) - (int)Math.round (mainFrame.getSize().getHeight() / 2));
		mainFrame.addComponentListener(this);
		
		// create combo boxes for the options
		for (int i = 0; i < 3; i++)
			resistanceComboBox[i] = new JComboBox(resistanceOptions);

		// set breadboard's inputs to false
		for (int i = 0; i < totalPins; i++) {
			breadboardLinkage[i] = false;
			breadboardUsage[i] = "";
		}

		// +5, gnd, inputs and outputs
		for (int i = 709; i < totalPins; i++) {
			if ((i - 709) % 5 != 0) {
				breadboardPinArray[0][i] = ((i - 709) % 90) * 6;
				breadboardPinArray[1][i] = 125 + (((i - 709) / 90) * 6);
			}
		}

		breadBoard = new drawBreadboard();

		// add menu
		addMenu();

		// set coordinates for dipswiches
		for (int i = 0; i < 8; i++) {
			int xLoc = 6 + (i * 10);
			int yLoc = 15;

			dipswitchPinArray[0][i] = xLoc;
			dipswitchPinArray[1][i] = yLoc;
		}

		// set coordinates for breadboard
		// top rail
		for (int i = 1; i < 60; i++) {
			if (i % 6 != 0) {
				breadboardPinArray[0][i - 1] = startingX + ((i - 1) * 6);
				breadboardPinArray[1][i - 1] = 5;
			}
		}

		// top half
		for (int i = 59; i < 354; i++) {
			breadboardPinArray[0][i] = startingX + (((i - 59) % 59) * 6);
			breadboardPinArray[1][i] = 15 + (((i - 59) / 59) * 6);
		}

		// bottom half
		for (int i = 354; i < 649; i++) {
			breadboardPinArray[0][i] = startingX + (((i - 354) % 59) * 6);
			breadboardPinArray[1][i] = 50 + (((i - 354) / 59) * 6);
		}

		// bottom rail
		for (int i = 649; i < 709; i++) {
			if ((i - 648) % 6 != 0) {
				breadboardPinArray[0][i] = (startingX - 6) + ((i - 648) * 6);
				breadboardPinArray[1][i] = 85;
			}
		}

		desktop = new JDesktopPane();
		desktop.setBackground(Color.GRAY);
		mainFrame.getContentPane().add(desktop);
		desktop.setBounds(0, 20, mainFrame.getSize().width, 426);

		// load images for buttons
		buttonImages[0] = new ImageIcon("images/and.png");
		buttonImages[1] = new ImageIcon("images/or.png");
		buttonImages[2] = new ImageIcon("images/xor.png");
		buttonImages[3] = new ImageIcon("images/not.png");
		buttonImages[4] = new ImageIcon("images/nor.png");
		buttonImages[5] = new ImageIcon("images/nand.png");
		buttonImages[6] = new ImageIcon("images/7decoder.png");
		buttonImages[7] = new ImageIcon("images/led.png");
		buttonImages[8] = new ImageIcon("images/button.png");
		buttonImages[9] = new ImageIcon("images/resistor.png");
		buttonImages[10] = new ImageIcon("images/wire.png");
		buttonImages[11] = new ImageIcon("images/tester.png");

		// link images to button and create button text incase image is not found
		buttons[0] = new JButton("And", buttonImages[0]);
		buttons[1] = new JButton("Or", buttonImages[1]);
		buttons[2] = new JButton("Xor", buttonImages[2]);
		buttons[3] = new JButton("Not", buttonImages[3]);
		buttons[4] = new JButton("Nor", buttonImages[4]);
		buttons[5] = new JButton("Nand", buttonImages[5]);
		buttons[6] = new JButton("7-Seg Decoder", buttonImages[6]);
		buttons[7] = new JButton("LED", buttonImages[7]);
		buttons[8] = new JButton("Button", buttonImages[8]);
		buttons[9] = new JButton("Resistor", buttonImages[9]);
		buttons[10] = new JButton("Wire", buttonImages[10]);
		buttons[11] = new JButton("Tester", buttonImages[11]);

		// create and show tool box
		toolbox = new JInternalFrame("Tool Box", true, true, false, false);
		toolbox.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		toolbox.addComponentListener(this);	// detect resize to replace buttons
		toolbox.addMouseListener(this);		// detect if an action happend here
		toolbox.setLocation(0, 0);
		toolbox.setSize(150, desktop.getSize().height);
		toolbox.setBackground(Color.WHITE);
		desktop.add(toolbox);
		toolbox.setVisible(true);

		// create tool tip texts (ie on hover)
		String[] toolTips = new String[numOfToolboxButtons];
		toolTips[0] = "AND Chip";
		toolTips[1] = "OR Chip";
		toolTips[2] = "XOR Chip";
		toolTips[3] = "NOT Chip";
		toolTips[4] = "NOR Chip";
		toolTips[5] = "NAND Chip";
		toolTips[6] = "7-Segment Decoder Chip";
		toolTips[7] = "LEDs";
		toolTips[8] = "Buttons";
		toolTips[9] = "Resistors";
		toolTips[10] = "Wire";
		toolTips[11] = "Tester";

		// position the buttons so that only a certain number will fit on a row
		// then move onto the next one
		// set bounds is executed through the redrawToolbox method when
		// the toolbox window is resized there for is not found below
		for (int i = 0; i < numOfToolboxButtons; i += numberOfToolsPerRows) {
			for (int j = 0; j < numberOfToolsPerRows; j++) {
				if ((i + j) <= buttons.length - 1) {
					// add buttons to toolbox
					toolbox.getContentPane().add(buttons[i + j]);
					buttons[i + j].addActionListener(this);
					buttons[i + j].setToolTipText(toolTips[i + j]);
				}
			}
		}

		// create the options boxes then hide them. the will be shown when that
		// given tool is selected
		toolOptionPanel.add(toolOptionTitle);
		createColourOptions();
		createResistorOptions();
		createTesterOptions();
		hideOptions();

		// add the toolbox title that is custimizeable by the options
		toolOptionTitle.setBounds (5, 5, 120, 10);

		// set bounds done through redrawToolbox methed when window is drawn
		toolbox.getContentPane().add(toolOptionPanel);

		toolbox.getContentPane().add(currentSelectedToolShow);

		// for some reason the last item added is the whole size of the toolbox
		// so this blank label is added is the whole button is not resize but
		// rather the lable is
		toolbox.getContentPane().add(new JLabel());

		// create and show breadboard
		breadBoardFrame = new JInternalFrame("Bread Board - " + breadboardTitle, true, true, false, false);
		breadBoardFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		breadBoardFrame.setLocation(toolbox.getSize().width, 0);
		breadBoardFrame.setSize(730 - toolbox.getSize().width, 300);
		breadBoardFrame.setBackground(Color.white);
		desktop.add(breadBoardFrame);
		breadBoardFrame.setVisible(true);

		// create the power switch
		powerSwitch powerIOSwitch = new powerSwitch();
		breadBoardFrame.getContentPane().add(powerIOSwitch);
		powerIOSwitch.setBounds(7, 40, 15, 30);
		powerIOSwitch.addMouseListener(this);

		// create labels and LEDs for the inputs and outputs +5v and GND
		JLabel[] ioLabels = new JLabel[18];
		ioLabels[0] = new JLabel("+5V");
		ioLabels[1] = new JLabel("A");
		ioLabels[2] = new JLabel("B");
		ioLabels[3] = new JLabel("C");
		ioLabels[4] = new JLabel("D");
		ioLabels[5] = new JLabel("E");
		ioLabels[6] = new JLabel("F");
		ioLabels[7] = new JLabel("G");
		ioLabels[8] = new JLabel("H");
		ioLabels[9] = new JLabel("S");
		ioLabels[10] = new JLabel("T");
		ioLabels[11] = new JLabel("U");
		ioLabels[12] = new JLabel("V");
		ioLabels[13] = new JLabel("W");
		ioLabels[14] = new JLabel("X");
		ioLabels[15] = new JLabel("Y");
		ioLabels[16] = new JLabel("Z");
		ioLabels[17] = new JLabel("GND");

		// and the labels
		breadBoardFrame.getContentPane().add(ioLabels[0]);
		ioLabels[0].setBounds (20, 175, 25, 20);
		for (int i = 1; i < 17; i++) {
			ioLEDs[i - 1] = new drawLED();

			// set LED colours
			if (i <= 8) {
				ioLEDs[i - 1].setColour(Color.RED);
				ioLEDs[i - 1].setShow(false);
			} else
				ioLEDs[i - 1].setColour(Color.GREEN);

			breadBoardFrame.getContentPane().add(ioLabels[i]);
			breadBoardFrame.getContentPane().add(ioLEDs[i - 1]);

			ioLabels[i].setBounds (30 + (i * 30), 175, 25, 20);
			ioLEDs[i - 1].setBounds(17 + (i * 30), 125, 21, 21);
		}
		breadBoardFrame.getContentPane().add(ioLabels[17]);
		ioLabels[17].setBounds (530, 175, 25, 20);

		// add bread board picture to breadboard internal frame
		breadBoardFrame.getContentPane().add (dipswitches);
		dipswitches.setBounds (7, 80, 85, 40);
		dipswitches.addMouseListener(this);

		breadBoard.addMouseListener(this);
		breadBoardFrame.getContentPane().add (breadBoard);
		breadBoard.setBounds (15, 25, 545, 148);

		// add power indicator and locate to the breadboard intenal frame
		breadBoardFrame.getContentPane().add (powerIndicator);
		powerIndicator.setBounds(30, 30, 45,30);

		// for some reason the last item added is the whole size of the toolbox
		// so this blank label is added is the whole button is not resize but
		// rather the lable is
		breadBoardFrame.getContentPane().add(new JLabel());
		mainFrame.getContentPane().add(new JLabel());
		
		// add popupMenu option to remove the current component
		popupMenu.add (formBuilder.createMenuItem("Remove Current Object", 'R', this));
		popupMenu.setInvoker (this);
		
		mainFrame.setVisible(true);
	}
	private void repositionToolbox() {
		/*
		 * This method recalculates and positions the tools when the toolbox is
		 * resized
		 */
		int yLevel = 5;
		for (int i = 0; i < numOfToolboxButtons; i += numberOfToolsPerRows) {
			for (int j = 0; j < numberOfToolsPerRows; j++) {
				if ((i + j) <= buttons.length - 1) {
					buttons[i + j].setBounds(5 + (j * 70), yLevel, 60, 25);
					toolBoxButtonLocations[0][i + j] = 5 + (j * 70);
					toolBoxButtonLocations[1][i + j] = yLevel;
				}
			}
			yLevel += 30;
		}
		
		currentSelectedToolShow.setBounds (0, 0, toolbox.getSize().width, toolbox.getSize().height);
		
		toolOptionPanel.setBounds(5, yLevel, 130, 150);
	}
	private void addMenu() {
		// Create main menu
		menuBar = new JMenuBar();
		JMenu menu;

		menu = formBuilder.createMenu("File", 'F');

		menu.add(formBuilder.createMenuItem("New", 'N', this));
		menu.add(formBuilder.createMenuItem("Open", 'O', this));
		menu.add(formBuilder.createMenuItem("Save", 'S', this));
		menu.add(formBuilder.createMenuItem("Save As", this));
		
		{			
			JMenuItem menuTemp = formBuilder.createMenuItem("Save As Chip", this);
			menuTemp.setEnabled (false);
			menu.add(menuTemp);
			menuBar.add(menu);
		}
		menu.add(new JSeparator());
		menu.add(formBuilder.createMenuItem("Exit", 'E', this));
		menuBar.add(menu);

		menu = formBuilder.createMenu("View", 'V');
		menu.add(formBuilder.createMenuItem("Toolbox", 'T', this));
		menu.add(formBuilder.createMenuItem("Breadboard", 'B', this));
		menu.add(formBuilder.createMenuItem("Wires", 'W', this));
		{			
			JMenuItem menuTemp = formBuilder.createMenuItem("Parallel Port", 'P', this);
			menuTemp.setEnabled (false);
			menu.add(menuTemp);
			menuBar.add(menu);
		}
		
		menu = formBuilder.createMenu("Tools", 'T');
		menu.add(formBuilder.createMenuItem("Options", 'O', this));
		menuBar.add(menu);

		menu = formBuilder.createMenu("Help", 'H');
		{			
			JMenuItem menuTemp = formBuilder.createMenuItem("Help", 'e', this);
			menuTemp.setEnabled (false);
			menu.add(menuTemp);
			menuBar.add(menu);
		}
		menu.add(new JSeparator());
		menu.add(formBuilder.createMenuItem("About", 'A', this));
		menuBar.add(menu);

		mainFrame.getContentPane().add(menuBar);
		menuBar.setBounds(0, 0, 815, 20);
	}
	private void createColourOptions () {
		/*
		 * create the colour options panel for use with objects thats options
		 * allow a colour change
		 */
		colourOptions = new JRadioButton[4];
		colourOptionsPanel.setLayout(new GridLayout(0,1));
		ButtonGroup optionGroup = new ButtonGroup();

		colourOptions[0] = new JRadioButton ("Black");
		colourOptions[1] = new JRadioButton ("Red");
		colourOptions[2] = new JRadioButton ("Green");
		colourOptions[3] = new JRadioButton ("Custom");

		for (int i = 0; i < colourOptions.length; i++) {
			colourOptionsPanel.add (colourOptions[i]);
			colourOptions[i].addActionListener(this);
			optionGroup.add(colourOptions[i]);
		}
		
		colourOptionsPanel.add(colourCustom);
		
		toolOptionPanel.add(colourOptionsPanel);
		colourOptions[0].setSelected(true);
	}
	private void createResistorOptions () {
		/*
		 * create the Resistor options panel
		 */
		resistorOptionsPanel.setLayout(new GridLayout(0, 1));

		for (int i = 0; i < 3; i++) {
			resistorOptionsPanel.add(resistanceComboBox[i]);
			resistanceComboBox[i].addActionListener(this);
			resistanceComboBox[i].setSelectedIndex(0);
		}

		resistorOptionsPanel.add(totalResistanceLable);

		toolOptionPanel.add(resistorOptionsPanel);
	}
	private static String resistanceCalc() {
		/*
		 * this method calculates the resistance that a given resistor had baced
		 * on its bands. This is done intirely through string manipulation then
		 * converted to and integer.
		 */
		int band1 = 0;
		int band2 = 0;
		int band3 = 0;
		String tempZeros = "";
		String totalResistance;
		String howManyCondenced = "";	// used to condence the resistance number (ie k or m)

		for (int i = 0; i < resistanceOptions.length; i++) {
			if (((String)resistanceComboBox[0].getSelectedItem()).equals(resistanceOptions[i]))
				band1 = i;
			if (((String)resistanceComboBox[1].getSelectedItem()).equals(resistanceOptions[i]))
				band2 = i;
			if (((String)resistanceComboBox[2].getSelectedItem()).equals(resistanceOptions[i]))
				band3 = i;
		}

		// find the approprate number of zeros to the resistance value
		for (int i = 0; i < band3; i++)
			tempZeros += "0";
		
		tempZeros = tempZeros.substring (0, tempZeros.length() % 3);
					
		// convert then whole thing to an int to have it automaticly simplify
		// 00 to 0 then convert it back to String
		totalResistance = " " + Integer.toString(Integer.parseInt(Integer.toString(band1) + Integer.toString(band2) + tempZeros)) + " " + howManyCondenced;

		// reduce length by simplifing
		if (band3 / 3 == 0)
			totalResistance += (char)(234);
		else if (band3 / 3 == 1)
			totalResistance += "K" + (char)(234);
		else if (band3 / 3 == 2)
			totalResistance += "M" + (char)(234);

		return totalResistance;
	}
	private static void createTesterOptions() {
		testerIndexLabel = new JLabel ("Pin: " + testerIndex);
		testerUseageLabel = new JLabel ("Useage:");
		testerPowerLabel = new JLabel ("Power:");
		testerIdLabel = new JLabel ("ID Number:");
		
		testerOptionsPanel.setLayout(new GridLayout(0, 1));
		
		testerOptionsPanel.add(testerIndexLabel);
		testerOptionsPanel.add(testerUseageLabel);
		testerOptionsPanel.add(testerPowerLabel);
		testerOptionsPanel.add(testerIdLabel);
		
		toolOptionPanel.add (testerOptionsPanel);
	}
	private static void hideOptions () {
		/*
		 * hides the all the options
		 */
		colourOptionsPanel.setVisible(false);
		resistorOptionsPanel.setVisible(false);
		testerOptionsPanel.setVisible(false);
	}
	private void newVBB() {
		/*
		 * deletes the current breadboard so the user can start over again
		 */
		breadBoardFrame.setTitle("Breadboard - Untitled.vbb");
		isSavedEver = false;
		isSavedCurrent = false;
		saveLocation = "";

		for (int i = 0; i < breadboardUsage.length; i++) {
			breadboardUsage[i] = "";
			breadboardLinkage[i] = false;
			breadboardComponentNumbers[i] = 0;
		}

		numberOfComponents.setLed(0);
		numberOfComponents.setResistor(0);
		numberOfComponents.setWire(0);
	}
	private void save() {
		String tempSaveLocation = "";	// The save file name with no directories
		try {
			for (int i = saveLocation.length() - 1; i > 0; i--)
				if (saveLocation.charAt(i) == '\\' || saveLocation.charAt(i) == '/') {
					tempSaveLocation = saveLocation.substring(i + 1, saveLocation.length());
					break;
				}

			breadBoardFrame.setTitle("Breadboard - " + tempSaveLocation);
			
			PrintWriter out = new PrintWriter(new FileWriter (saveLocation));
			out.println (numberOfComponents.getWire());

			// write the array of where wires connect to
			for (int i = 0; i < breadboardUsage.length; i++)
				if (breadboardUsage[i] != "")
					out.println (i + " " + breadboardUsage[i] + " " + breadboardComponentNumbers[i]);
					
			out.println ();
			
			// write the array of what the wire colours are
			for (int i = 0; i < wireColours.length; i++)
				if (wireColours[i] != null)
					out.println (wireColours[i].getComponentNumber() + " " + wireColours[i].getComponentColour());
				else
					break;
					
			out.println (numberOfComponents.getLed());
			for (int i = 0; i < ledColours.length; i++)
				if (ledColours[i] != null)
					out.println (ledColours[i].getComponentNumber() + " " + ledColours[i].getComponentColour());
				else
					break;
			
			out.println (numberOfComponents.getResistor());

			out.close();
			
			isSavedEver = true;
			isSavedCurrent = true;			
		} catch (IOException e) {
			DialogBox errorBox = new DialogBox("Error: Writing To File", "Writing to the file" + saveLocation + " incountered problems, it may be corrupt");
			errorBox.show();
		} catch (Exception e) {
			DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered during the saving process." + e.getMessage());
			errorBox.show();
		} finally {
		}
	}
	private void saveAs() {
		/*
		 * creates a dialog box for the user to save their file then it saves it
		 * for them
		 */
		JFileChooser saveDialog = new JFileChooser("Save File");
		saveDialog.setCurrentDirectory (new File("*.*"));
		int result = saveDialog.showSaveDialog(this);
		String tempSaveLocation = "";	// file name with no directorys

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = saveDialog.getSelectedFile();
			
			saveLocation = file.toString();

			for (int i = saveLocation.length() - 1; i > 0; i--)
				if (saveLocation.charAt(i) == '\\' || saveLocation.charAt(i) == '/') {
					tempSaveLocation = saveLocation.substring(i + 1, saveLocation.length());
					break;
				}

			breadBoardFrame.setTitle("Breadboard - " + tempSaveLocation);
			try {
				PrintWriter out = new PrintWriter(new FileWriter (file));
				out.println (numberOfComponents.getWire());

				// write the array of where wires connect to
				for (int i = 0; i < breadboardUsage.length; i++)
					if (breadboardUsage[i] != "")
						out.println (i + " " + breadboardUsage[i] + " " + breadboardComponentNumbers[i]);
						
				out.println ();
				
				// write the array of what the wire colours are
				for (int i = 0; i < wireColours.length; i++)
					if (wireColours[i] != null)
						out.println (wireColours[i].getComponentNumber() + " " + wireColours[i].getComponentColour());
					else
						break;
						
				out.println (numberOfComponents.getLed());
				for (int i = 0; i < ledColours.length; i++)
					if (ledColours[i] != null)
						out.println (ledColours[i].getComponentNumber() + " " + ledColours[i].getComponentColour());
					else
						break;
				
				out.println (numberOfComponents.getResistor());

				out.close();
			
				isSavedEver = true;
				isSavedCurrent = true;
			} catch (IOException e) {
				DialogBox errorBox = new DialogBox("Error: Writing To File", "Writing to the file" + file + " incountered problems, it may be corrupt");
				errorBox.show();
				newVBB();
			} catch (Exception e) {
				DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered during the saving process." + e.getMessage());
				errorBox.show();
				newVBB();
			} finally {
			}
		}
	}
	private void open() {
		/*
		 * creates a dialog box for the user to save their file then it saves it
		 * for them
		 */
		newVBB();	// clear out old values

		JFileChooser openDialog = new JFileChooser("Open File");
		openDialog.setCurrentDirectory (new File(optionsCtrl.installLocation + "\\Saves"));
		int result = openDialog.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = openDialog.getSelectedFile();
			int maxComponents = 0;		// the component with the highest component number
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				numberOfComponents.setWire(Integer.parseInt (in.readLine()));
				
				String temp;
				while ((temp = in.readLine()) != null) {
					if (temp.equals("")) {
						break;
					} else {
						StringTokenizer line = new StringTokenizer (temp);
						int tempIndex = Integer.parseInt (line.nextToken());
						breadboardUsage[tempIndex] = line.nextToken();
						breadboardComponentNumbers[tempIndex] = Integer.parseInt (line.nextToken());
					}
				}

				for (int i = 0; i < numberOfComponents.getWire(); i++) {
					StringTokenizer line = new StringTokenizer(in.readLine());
					wireColours[i] = new colourDB (Integer.parseInt (line.nextToken()), decodeJavaColor (line.nextToken()));
				}
				
				numberOfComponents.setLed(Integer.parseInt (in.readLine()));
				for (int i = 0; i < numberOfComponents.getLed(); i++) {
					StringTokenizer line = new StringTokenizer(in.readLine());
					ledColours[i] = new colourDB (Integer.parseInt (line.nextToken()), decodeJavaColor (line.nextToken()));
				}

				
				numberOfComponents.setResistor(Integer.parseInt (in.readLine()));	
				in.close();
				
				// Scan the breadboardUsage array for all non-null strings then
				// set the corrasponding points in the breadboardLinkage array
				// to true accordingly
				for (int i = 0; i < totalPins; i++)
					if (breadboardUsage[i] != "")
						breadboardLinkage[i] = true;
				
				// TODO: scan through all components and find the highest value to set to maxComponents
				// Scan components number (ie wire3 the number we are looking
				// for is the 3) find the highest number of all the componets,
				// wire, leds, resistors ...
				for (int i = 0; i < totalPins; i++)
					if (breadboardUsage[i].length() >= 4)
						if (breadboardUsage[i].substring(0, 4).equals("wire") || breadboardUsage[i].substring(0, 4).equals("led+"))
							if (maxComponents < Integer.parseInt(breadboardUsage[i].substring(4, breadboardUsage[i].length()))) {
								maxComponents = Integer.parseInt(breadboardUsage[i].substring(4, breadboardUsage[i].length()));
							}
					else if (breadboardUsage[i].length() >= 8)
						if (breadboardUsage[i].substring(0, 8).equals("resistor"))
							if (maxComponents < Integer.parseInt(breadboardUsage[i].substring(8, breadboardUsage[i].length()))) {
								maxComponents = Integer.parseInt(breadboardUsage[i].substring(8, breadboardUsage[i].length()));
							}

				
				breadBoardFrame.setTitle("Breadboard - " + file.toString());
			} catch (FileNotFoundException e) {
				DialogBox errorBox = new DialogBox("Error: File Not Found", "The file " + file + " was not found");
				errorBox.show();
				newVBB();
			} catch (IOException e) {
				DialogBox errorBox = new DialogBox("Error: Reading From File", "Reading the file" + file + " encountered problems, it may be corrupt");
				errorBox.show();
				newVBB();
			} catch (Exception e) {
				DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered during the opening process." + e.getMessage());
				errorBox.show();
				newVBB();
			} finally {
			}
		}
	}
	public static Color decodeJavaColor (String javaColor) {
		/*
		 * This method will convert the java colour discription
		 * "java.awt.Color[r=0,g=0,b=0]" into its color components then return
		 * the approprate colour.
		 */
		String red = "";
		String green = "";
		String blue = "";
		for (int i = 0; i < javaColor.length(); i++) {
			if (javaColor.charAt(i) == 'r') {
				int tempI = 0;
				for (int j = 1;; j++) {	// j = 1 because array starts at 0
					char currChar = javaColor.charAt(i + j + 3);	// the 3 is to offset the = and the current letter ie. r, g, b
					if (currChar == ',')
						break;
					red += currChar;
					
					tempI++;
				}
				i += tempI + 1;
			} else if (javaColor.charAt(i) == 'g') {
				int tempI = 0;
				for (int j = 1;; j++) {	// j = 1 because array starts at 0
					char currChar = javaColor.charAt(i + j + 1);	// the 3 is to offset the = and the current letter ie. r, g, b
					if (currChar == ',')
						break;
					green += currChar;
					
					tempI++;
				}
				i += tempI + 1;
			} else if (javaColor.charAt(i) == 'b') {
				int tempI = 0;
				for (int j = 1;; j++) {	// j = 1 because array starts at 0
					char currChar = javaColor.charAt(i + j + 1);	// the 3 is to offset the = and the current letter ie. r, g, b
					if (currChar == ']')
						break;
					blue += currChar;
					
					tempI++;
				}
				i += tempI + 1;
			}
		}
		
		return new Color(Integer.parseInt (red), Integer.parseInt (green), Integer.parseInt (blue));
	}
	private static String decodeMouseEvent (MouseEvent e) {
		/*
		 * This method gets the event of a mouseEntered and mouseExited
		 * then seaches through to find on what object the event occered
		 * on.  Then it returns it as a string.
		 */
		String stringEvent = String.valueOf(e);
		int startPoint;
		int endPoint;
		boolean foundCommand = false;

		for (startPoint = 0; startPoint < stringEvent.length() - 6; startPoint++) {
			if (stringEvent.substring(startPoint, startPoint + 6).equals("title=")){
				foundCommand = true;
				break;
			}
		}

		if (foundCommand == false) {
			for (startPoint = 0; startPoint < stringEvent.length() - 4; startPoint++) {
				if (stringEvent.substring(startPoint, startPoint + 4).equals(" on ")){
					break;
				}
			}
			startPoint += 4;
			endPoint = startPoint;
			while (true) {
				if (stringEvent.charAt(endPoint) == '[')
					break;
				else
					endPoint++;
			}
		} else {
			startPoint += 6;
			endPoint = startPoint;
			while (true) {
				if (stringEvent.charAt(endPoint) == ']')
					break;
				else
					endPoint++;
			}
		}

		return stringEvent.substring(startPoint, endPoint);
	}
	private int findPin (int x, int y) {
		/*
		 * This will find the pin clicked based on an x and y coordinate
		 */
		 int pinNumber = -1;
		 
		 for (int i = 0; i < totalPins; i++)
		 	if (breadboardPinArray[0][i] <= x && breadboardPinArray[0][i] + 4 >= x && breadboardPinArray[1][i] <= y && breadboardPinArray[1][i] + 4 >= y) {
		 		pinNumber = i;
		 		break;
		 	}
		 
		 return pinNumber;
	}
	public void mouseClicked (MouseEvent e) {
		int x = e.getPoint().x;
		int y = e.getPoint().y;
		boolean pinsFree = true;
		
		// Detect dipswitches
		if (currentHover.equals("drawDipswitches")) {
			for (int i = 0; i < 8; i++) {
				if ((dipswitchPinArray[0][i] <= x && dipswitchPinArray[0][i] + 4>= x)
				&& (4 <= y && 19 >= y)) {
					// If a dipswich is hit its number is stored in i
					if (dipswitchPinArray[1][i] == 4)
						dipswitchPinArray[1][i] = 15;
					else
						dipswitchPinArray[1][i] = 4;

					ioLEDs[i].togglePower();
					breadBoardFrame.repaint();
					break;
				}
			}
		} else if (currentHover.equals("powerSwitch")) {
			if (powerSwitchPosition == 21) {	// power on
				powerSwitchPosition = 4;
				for (int i = 0; i < 8; i++)
					ioLEDs[i].setShow(true);
			} else {							// power off
				powerSwitchPosition = 21;

				// reset breadboards power useage to 0f for off
				for (int i = 0; i < totalPins; i++)
					drawBreadboard.breadboardPowerUsage[i] = 0f;

				for (int i = 0; i < 8; i++)
					ioLEDs[i].setShow(false);
			}

			powerIndicator.togglePower();

			breadBoardFrame.repaint();
		} else if (currentHover.equals("drawBreadboard")) {
			// detect clicks on the breadboard (so it can break out of
			// everything)
			detect:
			for (int i = 0; i < totalPins; i++) {
				if (breadboardPinArray[0][i] <= x && breadboardPinArray[0][i] + 4 >= x && breadboardPinArray[1][i] <= y && breadboardPinArray[1][i] + 4 >= y){
					
					// set isSavedCurrent to false when somthing is added to the breadboard
					if (currentTool != "Tester" && breadBoardFrame.getTitle().charAt(breadBoardFrame.getTitle().length() - 1) != '*') {
						isSavedCurrent = false;
						breadBoardFrame.setTitle (breadBoardFrame.getTitle() + "*");
					}
					
					if (currentTool == "And Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									if ((i + j) / 59 == 5) {
										pinsFree = false;
									}
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "andChipPower";
								breadboardUsage[i + 1] = "andChipB4";
								breadboardUsage[i + 2] = "andChipA4";
								breadboardUsage[i + 3] = "andChipY4";
								breadboardUsage[i + 4] = "andChipB3";
								breadboardUsage[i + 5] = "andChipA3";
								breadboardUsage[i + 6] = "andChipY3";
								breadboardUsage[i + 59] = "andChipA1";
								breadboardUsage[i + 60] = "andChipB1";
								breadboardUsage[i + 61] = "andChipY1";
								breadboardUsage[i + 62] = "andChipA2";
								breadboardUsage[i + 63] = "andChipB2";
								breadboardUsage[i + 64] = "andChipY2";
								breadboardUsage[i + 65] = "andChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Or Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									if ((i + j) / 59 == 5) {
										pinsFree = false;
									}
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "orChipPower";
								breadboardUsage[i + 1] = "orChipB4";
								breadboardUsage[i + 2] = "orChipA4";
								breadboardUsage[i + 3] = "orChipY4";
								breadboardUsage[i + 4] = "orChipB3";
								breadboardUsage[i + 5] = "orChipA3";
								breadboardUsage[i + 6] = "orChipY3";
								breadboardUsage[i + 59] = "orChipA1";
								breadboardUsage[i + 60] = "orChipB1";
								breadboardUsage[i + 61] = "orChipY1";
								breadboardUsage[i + 62] = "orChipA2";
								breadboardUsage[i + 63] = "orChipB2";
								breadboardUsage[i + 64] = "orChipY2";
								breadboardUsage[i + 65] = "orChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Xor Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									pinsFree = false;
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "xorChipPower";
								breadboardUsage[i + 1] = "xorChipB4";
								breadboardUsage[i + 2] = "xorChipA4";
								breadboardUsage[i + 3] = "xorChipY4";
								breadboardUsage[i + 4] = "xorChipB3";
								breadboardUsage[i + 5] = "xorChipA3";
								breadboardUsage[i + 6] = "xorChipY3";
								breadboardUsage[i + 59] = "xorChipA1";
								breadboardUsage[i + 60] = "xorChipB1";
								breadboardUsage[i + 61] = "xorChipY1";
								breadboardUsage[i + 62] = "xorChipA2";
								breadboardUsage[i + 63] = "xorChipB2";
								breadboardUsage[i + 64] = "xorChipY2";
								breadboardUsage[i + 65] = "xorChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Not Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									pinsFree = false;
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "notChipPower";
								breadboardUsage[i + 1] = "notChipB4";
								breadboardUsage[i + 2] = "notChipA4";
								breadboardUsage[i + 3] = "notChipY4";
								breadboardUsage[i + 4] = "notChipB3";
								breadboardUsage[i + 5] = "notChipA3";
								breadboardUsage[i + 6] = "notChipY3";
								breadboardUsage[i + 59] = "notChipA1";
								breadboardUsage[i + 60] = "notChipB1";
								breadboardUsage[i + 61] = "notChipY1";
								breadboardUsage[i + 62] = "notChipA2";
								breadboardUsage[i + 63] = "notChipB2";
								breadboardUsage[i + 64] = "notChipY2";
								breadboardUsage[i + 65] = "notChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Nor Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									pinsFree = false;
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "norChipPower";
								breadboardUsage[i + 1] = "norChipB4";
								breadboardUsage[i + 2] = "norChipA4";
								breadboardUsage[i + 3] = "norChipY4";
								breadboardUsage[i + 4] = "norChipB3";
								breadboardUsage[i + 5] = "norChipA3";
								breadboardUsage[i + 6] = "norChipY3";
								breadboardUsage[i + 59] = "norChipA1";
								breadboardUsage[i + 60] = "norChipB1";
								breadboardUsage[i + 61] = "norChipY1";
								breadboardUsage[i + 62] = "norChipA2";
								breadboardUsage[i + 63] = "norChipB2";
								breadboardUsage[i + 64] = "norChipY2";
								breadboardUsage[i + 65] = "norChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Nand Chip") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 53)) {
							// checks to see if the pins are free
							for (int j = 0; j < 7; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									pinsFree = false;
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 7; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "nandChipPower";
								breadboardUsage[i + 1] = "nandChipB4";
								breadboardUsage[i + 2] = "nandChipA4";
								breadboardUsage[i + 3] = "nandChipY4";
								breadboardUsage[i + 4] = "nandChipB3";
								breadboardUsage[i + 5] = "nandChipA3";
								breadboardUsage[i + 6] = "nandChipY3";
								breadboardUsage[i + 59] = "nandChipA1";
								breadboardUsage[i + 60] = "nandChipB1";
								breadboardUsage[i + 61] = "nandChipY1";
								breadboardUsage[i + 62] = "nandChipA2";
								breadboardUsage[i + 63] = "nandChipB2";
								breadboardUsage[i + 64] = "nandChipY2";
								breadboardUsage[i + 65] = "nandChipGND";
								
								for (int j = 0; j < 7; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "7-Seg Decoder") {
						// makes sure the click will span the rail and that
						// it will not go off the right side of the
						// breadboard
						if ((i / 59 == 5) && (i % 59 < 52)) {
							// checks to see if the pins are free
							for (int j = 0; j < 8; j++){
								if ((breadboardLinkage[i + j] == true) && (breadboardLinkage[i + j + 59] == true) && ((i + j) / 59 == 5)) {
									pinsFree = false;
								}
							}

							if (pinsFree == true) {
								//
								// set pins to in-use
								for (int j = 0; j < 8; j++){
									breadboardLinkage[i + j] = true;
									breadboardLinkage[i + j + 59] = true;
								}

								breadboardUsage[i] = "decoderChipPower";
								breadboardUsage[i + 1] = "decoderChipF";
								breadboardUsage[i + 2] = "decoderChipG";
								breadboardUsage[i + 3] = "decoderChipOutA";
								breadboardUsage[i + 4] = "decoderChipOutB";
								breadboardUsage[i + 5] = "decoderChipOutC";
								breadboardUsage[i + 6] = "decoderChipOutD";
								breadboardUsage[i + 7] = "decoderChipOutE";
								breadboardUsage[i + 59] = "decoderChipInB";
								breadboardUsage[i + 60] = "decoderChipInC";
								breadboardUsage[i + 61] = "decoderChip";
								breadboardUsage[i + 62] = "decoderChip";
								breadboardUsage[i + 63] = "decoderChip";
								breadboardUsage[i + 64] = "decoderChipInD";
								breadboardUsage[i + 65] = "decoderChipInA";
								breadboardUsage[i + 66] = "decoderChipGND";
								
								for (int j = 0; j < 8; j++) {
									breadboardComponentNumbers[i + j] = numberOfComponents.getComponents();
									breadboardComponentNumbers[i + j + 59] = numberOfComponents.getComponents();
								}
								
								numberOfComponents.addChip();
							}
						}
					} else if (currentTool == "Wire") {
						if ((newWire == true) && (breadboardLinkage[i] == false)) {
							newWire = false;
							firstWirePoint = i;
						} else {
							newWire = true;
							// make sure the pins are not in use
							if ((breadboardLinkage[firstWirePoint] == false) && (breadboardLinkage[i] == false) && (firstWirePoint != i)) {
								String wireNumber = String.valueOf(numberOfComponents.getComponents());
								// set what the pins are used for
								breadboardUsage[firstWirePoint] = "wire" + wireNumber;
								breadboardUsage[i] = "wire" + wireNumber;
								breadboardComponentNumbers[firstWirePoint] = numberOfComponents.getComponents();
								breadboardComponentNumbers[i] = numberOfComponents.getComponents();
								
								// set the colour of the wire
								if (colourOptions[0].isSelected() == true)
									wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.BLACK);
								else if (colourOptions[1].isSelected() == true)
									wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.RED);
								else if (colourOptions[2].isSelected() == true)
									wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.GREEN);
								else if (colourOptions[3].isSelected() == true)
									wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), hex2Colour(colourCustom.getText()));

								// set the pins to in use
								breadboardLinkage[firstWirePoint] = true;
								breadboardLinkage[i] = true;

								numberOfComponents.addWire();
							}
						}
					} else if (currentTool == "Resistor") {
						if (newResistor == true) {
							newResistor = false;
							firstResistorPoint = i;
						} else {
							newResistor = true;
							// make sure the pins are not in use
							if ((breadboardLinkage[firstResistorPoint] == false) && (breadboardLinkage[i] == false) && (firstResistorPoint != i)) {
								String resistorNumber = String.valueOf(numberOfComponents.getComponents());
								// set what the pins are used for
								breadboardUsage[firstResistorPoint] = "resistor" + resistorNumber;
								breadboardUsage[i] = "resistor" + resistorNumber;
								breadboardComponentNumbers[firstResistorPoint] = numberOfComponents.getComponents();
								breadboardComponentNumbers[i] = numberOfComponents.getComponents();
								

								// set the pins to in use
								breadboardLinkage[firstResistorPoint] = true;
								breadboardLinkage[i] = true;

								numberOfComponents.addResistor();
							}
						}
					} else if (currentTool == "LED") {
						if (newLed == true) {
							newLed = false;
							firstLedPoint = i;
						} else {
							newLed = true;
							// make sure the pins are not in use
							if ((breadboardLinkage[firstLedPoint] == false) && (breadboardLinkage[i] == false) && (firstLedPoint != i)) {
								String ledNumber = String.valueOf(numberOfComponents.getComponents());
								// set what the pins are used for
								breadboardUsage[firstLedPoint] = "led+" + ledNumber;
								breadboardUsage[i] = "led-" + ledNumber;
								breadboardComponentNumbers[firstLedPoint] = numberOfComponents.getComponents();
								breadboardComponentNumbers[i] = numberOfComponents.getComponents();
								
								// set the colour of the led
								if (colourOptions[0].isSelected() == true)
									ledColours[Integer.parseInt(ledNumber)] = new colourDB (numberOfComponents.getComponents(), Color.BLACK);
								else if (colourOptions[1].isSelected() == true)
									ledColours[Integer.parseInt(ledNumber)] = new colourDB (numberOfComponents.getComponents(), Color.RED);
								else if (colourOptions[2].isSelected() == true)
									ledColours[Integer.parseInt(ledNumber)] = new colourDB (numberOfComponents.getComponents(), Color.GREEN);
								else if (colourOptions[3].isSelected() == true)
									ledColours[Integer.parseInt(ledNumber)] = new colourDB (numberOfComponents.getComponents(), Components.hex2Colour(colourCustom.getText()));

								// set the pins to in use
								breadboardLinkage[firstLedPoint] = true;
								breadboardLinkage[i] = true;

								numberOfComponents.addLed();
							}
						}
					} else if (currentTool == "Tester") {
						testerIndex = findPin (e.getPoint().x, e.getPoint().y);
						
						testerIndexLabel.setText ("Pin: " + testerIndex);
						testerUseageLabel.setText ("Useage: " + breadboardUsage[testerIndex]);
						testerPowerLabel.setText ("Power: " + drawBreadboard.breadboardPowerUsage[testerIndex] +"V");
						testerIdLabel.setText ("ID Number: " + breadboardComponentNumbers[testerIndex]);
					}
					// skip the detection on the inputoutputs +5 and gnd
					break detect;
				}
			}

			// update the changes to the frame
			breadBoardFrame.repaint();
		}
	}
	public static int hex2dec (String hexCode) {
		/*
		 * This method will convert a hex number to decmial.  it only works if
		 * 1 byte is sent (ie FF will work but FFF will not)
		 */
		int convertedNum = 0;
		
		for (int i = 0; i < hexCode.length(); i++) {
			char currChar = hexCode.charAt(i);
			int tempNum = 0;
			
			if (currChar == 'F' || currChar == 'f')
				tempNum = 15;
			else if (currChar == 'E' || currChar == 'e')
				tempNum = 14;
			else if (currChar == 'D' || currChar == 'd')
				tempNum = 13;
			else if (currChar == 'C' || currChar == 'c')
				tempNum = 12;
			else if (currChar == 'B' || currChar == 'b')
				tempNum = 11;
			else if (currChar == 'A' || currChar == 'a')
				tempNum = 10;
			else
				tempNum = Integer.parseInt(String.valueOf(currChar));
			
			if (i > 0) {
				tempNum *= 16;
			}
			convertedNum += tempNum;
		}
		return convertedNum;
	}
	public Color hex2Colour (String hexCode) {
		int red = 0;
		int green = 0;
		int blue = 0;
		if (hexCode.charAt(0) == '#')
			hexCode = hexCode.substring(1, hexCode.length());
		
		if (hexCode.length() == 6) {
			red = hex2dec (hexCode.substring(0, 2));
			green = hex2dec (hexCode.substring(2, 4));
			blue = hex2dec (hexCode.substring(4, 6));
		}
		return new Color(red, green, blue);
	}
	private void resetClicks () {
		newWire = true;
		newLed = true;
		newResistor = true;
		
		toolOptionTitle.setText ("Options:");
		
		//
		// Removes any flags a component may have on the VBB
		breadBoardFrame.repaint();
	}
	
	//
	// required for mouse event handling
	public void mouseEntered (MouseEvent e) {
		currentHover = decodeMouseEvent(e);
		try {
			if (currentHover.equals("drawBreadboard"))
				breadBoardFrame.setSelected (true);
		} catch (Exception error) {
		} finally {
		}
	}
	public void mouseExited (MouseEvent e) {
		currentHover = null;
	}
	static int componentToRemoveIndex = -1;
	public void mousePressed (MouseEvent e) {
		final int X = (int)e.getPoint().getX();
		final int Y = (int)e.getPoint().getY();
		// handle popup menu
		// loop through all the pins to see if the click was in one of them
		for (int i = 0; i < totalPins; i++)
			// is the click in a box?
			if ((breadboardPinArray[0][i] <= X && breadboardPinArray[0][i] + 4 >= X) && (breadboardPinArray[1][i] <= Y && breadboardPinArray[1][i] + 4 >= Y))
				// is the click on the breadboard and is the pin clicked on in use?
				if (currentHover.equals("drawBreadboard") && breadboardLinkage[i] == true && currentTool != "Tester") {
					componentToRemoveIndex = i;
					popupMenu.show (e.getComponent(), X, Y);
					break;
				}
	}
	public void mouseReleased (MouseEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {
		// recalculates the toolbox buttons and repositions them when the
		// toolbox frame is resized
		numberOfToolsPerRows = toolbox.getSize().width / 73;
		repositionToolbox();
		
		// locate what tool is selected then draw a black box around it to
		// indicate that it is selected
		repositionOutlineOfButtons();

		// desktop frame and menu resize with the main window
		desktop.setSize(mainFrame.getSize().width, mainFrame.getSize().height - menuBar.getSize().height);
		menuBar.setSize(mainFrame.getSize().width, menuBar.getSize().height);
	}
	public void componentShown(ComponentEvent e) {}
	public void actionPerformed (ActionEvent e) {
		// reset toolbox option text
		// Handle menus
		if (e.getActionCommand().equals("Exit"))
			System.exit(0);
		else if (e.getActionCommand().equals("Toolbox")) {
			if (toolbox.isVisible() == false)
				toolbox.setVisible(true);
			else
				toolbox.setVisible(false);
		} else if (e.getActionCommand().equals("Breadboard")) {
			if (breadBoardFrame.isVisible() == false)
				breadBoardFrame.setVisible(true);
			else
				breadBoardFrame.setVisible(false);
		} else if (e.getActionCommand().equals("About"))
			new About();
		 else if (e.getActionCommand().equals("Save")) {
		 	if (isSavedEver == false)
				saveAs();
			else
				save();
		} else if (e.getActionCommand().equals("Save As")) {
			saveAs();
			isSavedEver = true;
		} else if (e.getActionCommand().equals("Open")) {
			open();
			breadBoard.repaint();
		} else if (e.getActionCommand().equals("New")) {
			newVBB();
			breadBoard.repaint();
		} else if (e.getActionCommand().equals("Help")) {
			try {
				Process p = Runtime.getRuntime().exec(optionsCtrl.helpLocation);
			} catch (Exception error) {
				DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered. The help may not open correctly or at all." + error.getMessage());
				errorBox.show();
			} finally {
			}
		} else if (e.getActionCommand().equals("Options")) {
			new OptionsDiologBox();
		}

		// Handle buttons
		else if (e.getActionCommand().equals("And")) {
			currentTool = "And Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Or")) {
			currentTool = "Or Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Not")) {
			currentTool = "Not Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Nor")) {
			currentTool = "Nor Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Nand")) {
			currentTool = "Nand Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Xor")) {
			currentTool = "Xor Chip";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("7-Seg Decoder")) {
			currentTool = "7-Seg Decoder";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("LED")) {
			currentTool = "LED";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();

			toolOptionTitle.setText ("Options: LEDs");
			colourOptionsPanel.setVisible(true);
		} else if (e.getActionCommand().equals("Button")) {
			currentTool = "Button";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();
		} else if (e.getActionCommand().equals("Resistor")) {
			currentTool = "Resistor";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();

			toolOptionTitle.setText ("Options: Resistor");
			resistorOptionsPanel.setVisible(true);
		} else if (e.getActionCommand().equals("Wire")) {
			currentTool = "Wire";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();

			toolOptionTitle.setText ("Options: Wires");
			colourOptionsPanel.setVisible(true);
		} else if (e.getActionCommand().equals("Tester")) {
			currentTool = "Tester";
			hideOptions();
			resetClicks();
			repositionOutlineOfButtons();

			toolOptionTitle.setText ("Options: Tester");
			
			testerOptionsPanel.setVisible(true);
		} else if (e.getActionCommand().equals("comboBoxChanged")) {
			totalResistanceLable.setText ("Resistance:" + resistanceCalc());
			toolbox.repaint();
		}

		// handle colour options
		else if (e.getActionCommand().equals("Black"))
			currentColour = Color.BLACK;
		else if (e.getActionCommand().equals("Red"))
			currentColour = Color.RED;
		else if (e.getActionCommand().equals("Yellow"))
			currentColour = Color.YELLOW;
		else if (e.getActionCommand().equals("Green"))
			currentColour = Color.GREEN;
		else if (e.getActionCommand().equals("Blue"))
			currentColour = Color.BLUE;
		
		// handle popupMenu
		else if (e.getActionCommand().equals("Remove Current Object")) {
			// Problem null int equals 0 therfor all non used indexes are 0, so inventory starts at 1
			for (int i = 0; i < totalPins; i++) {
				if (breadboardComponentNumbers[i] == breadboardComponentNumbers[componentToRemoveIndex] && i != componentToRemoveIndex) {
					
					// reset for the index given
					breadboardComponentNumbers[i] = 0;
					breadboardLinkage[i] = false;
					breadboardUsage[i] = "";
				}
			}
			
			// detects what was removed and removes it from the inventory
			for (int j = 0; j < breadboardUsage[componentToRemoveIndex].length(); j++) {
				// contains the charators to stop searching at (ie enough to get
				// a unique match to the component being removed
				char[] stopChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', 'C'};
				for (int k = 0; k < stopChars.length; k++) {
					if (breadboardUsage[componentToRemoveIndex].charAt(j) == stopChars[k])
						if (breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("wire")) {
							numberOfComponents.subWire();
							/* moves the data from the last index used index to
							 * the newly vacant index
							 */
							 int wireNumberBeingRemoved = Integer.parseInt (breadboardUsage[componentToRemoveIndex].substring (j, breadboardUsage[componentToRemoveIndex].length()));

							 for (int l = 0; l < wireColours.length; l++) {
							 	if ((wireColours[l] != null) && (wireNumberBeingRemoved == wireColours[l].getComponentNumber())) {
							 		wireColours[l] = null;	// make unavailable again
							 	}
							 }

						} else if (breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("led"))
							numberOfComponents.subLed();
						else if (breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("resistor"))
							numberOfComponents.subResistor();
						else if (breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("and") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("or") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("xor") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("not") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("nand") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("nor") || breadboardUsage[componentToRemoveIndex].substring (0, j).equals ("decoder"))
							numberOfComponents.subChip();
				}
			}
			
			// reset for the index clicked on
			breadboardComponentNumbers[componentToRemoveIndex] = 0;
			breadboardLinkage[componentToRemoveIndex] = false;
			breadboardUsage[componentToRemoveIndex] = "";
			
			breadBoardFrame.repaint();
		}
	}
	public void windowOpened (WindowEvent e) {
	}
	public void windowIconified (WindowEvent e) {
	}
	public void windowDeiconified (WindowEvent e) {
	}
	public void windowDeactivated (WindowEvent e) {
	}
	public void windowClosing (WindowEvent e) {
		if (isSavedCurrent == false) {
			int answer = JOptionPane.showConfirmDialog (new JOptionPane(), "Would you like to save your breadboard?", "Save", JOptionPane.YES_NO_OPTION);
			
			if (answer == 0)
				if (isSavedEver == true)
					save();
				else
					saveAs();
		}
	}
	public void windowClosed (WindowEvent e) {
	}
	public void windowActivated (WindowEvent e) {
	}
	public void repositionOutlineOfButtons () {
		int x = 0;
		int y = 0;
		if (currentTool.equalsIgnoreCase("And Chip")) {
			x = toolBoxButtonLocations[0][0];
			y = toolBoxButtonLocations[1][0];
		} else if (currentTool.equalsIgnoreCase("Or Chip")) {
			x = toolBoxButtonLocations[0][1];
			y = toolBoxButtonLocations[1][1];
		} else if (currentTool.equalsIgnoreCase("Xor Chip")) {
			x = toolBoxButtonLocations[0][2];
			y = toolBoxButtonLocations[1][2];
		} else if (currentTool.equalsIgnoreCase("Not Chip")) {
			x = toolBoxButtonLocations[0][3];
			y = toolBoxButtonLocations[1][3];
		} else if (currentTool.equalsIgnoreCase("Nor Chip")) {
			x = toolBoxButtonLocations[0][4];
			y = toolBoxButtonLocations[1][4];
		} else if (currentTool.equalsIgnoreCase("Nand Chip")) {
			x = toolBoxButtonLocations[0][5];
			y = toolBoxButtonLocations[1][5];
		} else if (currentTool.equalsIgnoreCase("7-Seg Decoder")) {
			x = toolBoxButtonLocations[0][6];
			y = toolBoxButtonLocations[1][6];
		} else if (currentTool.equalsIgnoreCase("LED")) {
			x = toolBoxButtonLocations[0][7];
			y = toolBoxButtonLocations[1][7];
		} else if (currentTool.equalsIgnoreCase("Button")) {
			x = toolBoxButtonLocations[0][8];
			y = toolBoxButtonLocations[1][8];	
		} else if (currentTool.equalsIgnoreCase("Resistor")) {
			x = toolBoxButtonLocations[0][9];
			y = toolBoxButtonLocations[1][9];
		} else if (currentTool.equalsIgnoreCase("Wire")) {
			x = toolBoxButtonLocations[0][10];
			y = toolBoxButtonLocations[1][10];
		} else if (currentTool.equalsIgnoreCase("Tester")) {
			x = toolBoxButtonLocations[0][11];
			y = toolBoxButtonLocations[1][11];
		}
		
		// Move the outline for currently selected tool to the correct one
		currentSelectedToolShow.update (x, y);
		
		// Make the change visable
		toolbox.repaint();
	}
}
class showSelectedTool extends JPanel {
	int x = 0;
	int y = 0;
	public void update (int newX, int newY) {
		this.x = newX;
		this.y = newY;
	}
	public void paintComponent (Graphics g) {
		g.drawRect (x - 3, y - 3, 64, 29);
	}
}