import javax.swing.*;
import java.awt.*;
import java.io.*;

class drawBreadboard extends JPanel {
	/*
	 * draws the breadboard and detects and draws its components
	 */
	int[][] breadboardPinArray = VirtualBreadboard.breadboardPinArray;
	String[] breadboardUsage = VirtualBreadboard.breadboardUsage;
	static float[] breadboardPowerUsage = new float[VirtualBreadboard.totalPins];		// what pins have power
	static componentInventory numberOfComponents = VirtualBreadboard.numberOfComponents;
	static int[][] dipswitchPinArray = VirtualBreadboard.dipswitchPinArray;
	static boolean[] breadboardFlags = new boolean[VirtualBreadboard.totalPins];
	
	// the components can be added to the breadboard
	String[] scanFor;
	
	/*
	 * this method detects what components are where then allows power to
	 * flow through them as the components dictates
	 */
	private void detectionSystem () {
		 int indexScannedTo = -1;		// the index of the last scanned components location
		 String compontedLastScanned;	// the componed that corrisponds with indexScannedTo

		init();
		
		// distribute power from +5
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				int pinLocation = (i + 710) + (90 * j);	// the power pin being tested
				if (breadboardUsage[pinLocation] != "") {
					//System.out.println (i + " " + isPower (pinLocation);
					isPower (pinLocation);
				}
			}
		}
		
		// distribute power from input pins
		//isPower (718);
	}
	
	/*
	 * Initilizes the breadboard by: setting the whole board to 0V
	 *                               setting +5V to power and inputs
	 *                               setting -5V to outputs
	 *                               populates scanFor
	 */
	private int findComponentOut (int componentInIndex) {
		String component = breadboardUsage[componentInIndex];
		int returnValue = -1;
		
		if (breadboardUsage[componentInIndex].length() >= 7) {
			// if and chip
			if (breadboardUsage[componentInIndex].substring(0, 7).equals("andChip")) {
				returnValue = andChipComponent.isPower(andChipComponent.getOutPin (componentInIndex));
			}
		}
		if (breadboardUsage[componentInIndex].length() >= 4) {
			if (breadboardUsage[componentInIndex].substring(0, 4).equals("wire")) {
				returnValue = wireComponent.isPower(wireComponent.getOutPin (componentInIndex));
				System.out.println ("-SHIT" + component);
			}
		} else {
			System.out.println ("-FUCK-" + breadboardUsage[componentInIndex] + "|");
		}

		
		return returnValue;
	}
	public void expandPin (int pin) {
		float highestValuePin = 0f;
		
		// if the pin is on the top rail
		if (pin <= 58) {
			
			// find the highest power pin
			for (int i = 0; i <= 58; i++) {
				if (breadboardPowerUsage[i] > highestValuePin)
					highestValuePin = breadboardPowerUsage[i];
			}
			
			// set the pins to the highest pin value
			for (int i = 0; i <= 58; i++)
				breadboardPowerUsage[i] = highestValuePin;
		}
	}
	
	/*
	 * This method will trace each route that the power can traval and decide if
	 * it ever reaches ground (-5).
	 */
	private boolean isPower (int pin) {
		boolean rootPath = false;	// is the previous path valid based on ever reaching
									// ground
									
		// where the components negitive component is locatated on the vbb
		int componentOut;
		
		componentOut = findComponentOut (pin);
		
		System.out.println (componentOut + " " + pin);
		
		// stops wires and other components from being caught in a recursive loop
		if (breadboardFlags [pin] == false && breadboardFlags [componentOut] == false) {
			// TODO: make function to set all pins of used component to false
			breadboardFlags [pin] = true;
			breadboardFlags [componentOut] = true;
	
			// TODO: just stops GUI from Crashing (it may stop the function from working)
			if (componentOut < 0)
				return false;	
			// if the component made it to ground then return true
			if (breadboardPowerUsage[componentOut] < 0) {
				return true;
			}
	
			
			// if the component is outputted to the top rail
			if (componentOut <= 58) {
				for (int i = 0; i <= 58; i++) {
					if (breadboardUsage[i] != "" && i != componentOut) {
						if (isPower(i)) {
							breadboardPowerUsage[i] = 5f;
							expandPin (i);
							return true;
						}
					}
				}
				
			// if the component is outputted to the top half
			} else if (componentOut <= 353) {
				for (int i = 59 + (componentOut % 59); i <= 353; i += 59) {
					if (breadboardUsage[i] != "" && i != componentOut) {
						if (isPower(i)) {
							return true;
						}
					}
				}
				
			// if the component is outputted to the bottom half
			} else if (componentOut <= 648) {
				for (int i = 354 + (componentOut % 59); i <= 648; i += 59) {
					if (breadboardUsage[i] != "" && i != componentOut) {
						if (isPower(i)) {
							return true;
						}
					}
				}
				
			// if the component is outputted to the bottom rail
			} else if (componentOut <= 707) {
				for (int i = 649; i <= 707; i++) {
					if (breadboardUsage[i] != "" && i != componentOut) {
						if (isPower(i)) {
							return true;
						}
					}
				}
			}
		}
		
		return rootPath;
	}

	private void init() {
		// reset breadboard power values to 0, flags to false
		for (int i = 0; i < VirtualBreadboard.totalPins; i++) {
			breadboardPowerUsage[i] = 0f;
			breadboardFlags[i] = false;
		}

		// set the +5V to +5V
		for (int i = 710; i <= 983; i += 90)
			for (int j = 0; j < 4; j++)
				breadboardPowerUsage[i + j] = 5f;

		// set the gnd to -1
		for (int i = 795; i < VirtualBreadboard.totalPins; i += 90)
			for (int j = 0; j < 4; j++)
				breadboardPowerUsage[i + j] = -5f;

		// set the inputs to +5 if needed
		for (int k = 0; k < 8; k++)
			if (dipswitchPinArray[1][k] == 4)
				for (int i = 715 + (k * 5); i <= 988 + (k * 5); i += 90)
					for (int j = 0; j < 4; j++)
						breadboardPowerUsage[i + j] = 5f;
		
		// load components to scanFor
		try {
			BufferedReader in = new BufferedReader (new FileReader ("components.txt"));
			int numOfComponents = Integer.parseInt(in.readLine());
			scanFor = new String [numOfComponents];
			
			for (int i = 0; i <= numOfComponents - 1; i++) {
				scanFor[i] = in.readLine();
			}
			
			in.close();
		} catch (Exception e) {
			DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered during opening of components.txt" + e.getMessage());
			errorBox.show();
		}
	}
	
	public void paintComponent (Graphics g) {
		// NEEDED because paintComponent is the first method run
		detectionSystem();
		
		// links ioLEDs from VirtualBreadboard
		drawLED[] ioLEDs = VirtualBreadboard.ioLEDs;
				
		// if the power is on detection where the electricity should go
		if (VirtualBreadboard.powerSwitchPosition == 4)
			detectionSystem();

		// Create box
		g.drawRect(breadboardPinArray[0][0] - 5, 0, 363, 95);
		// Top Rail
		for (int i = 1; i < 60; i++) {
			if (i % 6 != 0)
				g.drawRect(breadboardPinArray[0][i-1], breadboardPinArray[1][i-1], 4, 4);
		}

		// Top and bottom half
		for (int i = 59; i < 649; i++) {
			g.drawRect (breadboardPinArray[0][i], breadboardPinArray[1][i], 4, 4);
		}

		// Bottom Rail
		for (int i = 649; i < 709; i++) {
			if (breadboardPinArray[0][i] != 0)
				g.drawRect(breadboardPinArray[0][i], breadboardPinArray[1][i], 4, 4);
		}

		//
		// +5, inputs, outputs, GND
		for (int i = 709; i < VirtualBreadboard.totalPins; i++) {
			if (breadboardPinArray[0][i] != 0)
				g.drawRect(breadboardPinArray[0][i], breadboardPinArray[1][i], 4, 4);
		}

		// detect if an output is high
		for (int i = 0; i < 8; i++) {
			if (breadboardPowerUsage[(i + 755) + (i * 4)] > 0f && VirtualBreadboard.powerIndicator.getPower() == true)
				ioLEDs[i + 8].setPowerHigh();
			else
				ioLEDs[i + 8].setPowerLow();
		}
		
		drawComponents (g);
	}
	
	private void drawComponents (Graphics g) {
		for (int i = 0; i < VirtualBreadboard.totalPins; i++) {
			if (componentReduce(breadboardUsage[i]).equals("wire")) {
				wireComponent.draw (i, g);
			} else if (componentReduce(breadboardUsage[i]).equals("andChipPower")) {
				andChipComponent.draw (i, g);
			}
		}
	}
	
	private String componentReduce (String component) {
		for (int i = 0; i < scanFor.length; i++) {
			if (component.length() >= scanFor[i].length()) {
				if (component.substring(0, scanFor[i].length()).equals(scanFor[i])) {
					return scanFor[i];
				}
			}
		}
		
		return "";
	}
}