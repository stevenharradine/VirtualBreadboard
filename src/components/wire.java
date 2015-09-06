/*
 *      author:  Steven J. Harradine
 *
 *    filename:  wire.java
 *
 *     started:  20050608
 *    finished:  2005xxxx
 *
 * description:  This object will act as a wire
 *
 */
 
import java.awt.*;
import javax.swing.*;

class wireComponent {
	static float[] breadboardPowerUsage = drawBreadboard.breadboardPowerUsage;
	static boolean[] breadboardLinkage = VirtualBreadboard.breadboardLinkage;
	static String[] breadboardUsage = VirtualBreadboard.breadboardUsage;
	static componentInventory numberOfComponents = VirtualBreadboard.numberOfComponents;
	static int[] breadboardComponentNumbers = VirtualBreadboard.breadboardComponentNumbers;
	static JRadioButton[] colourOptions = VirtualBreadboard.colourOptions;
	static colourDB[] wireColours = VirtualBreadboard.wireColours;
	static JTextField colourCustom = VirtualBreadboard.colourCustom;
	
	static int firstWirePoint;
	static boolean newWire = true;
	
	public void wireCircut (int point) {
		int secondPoint = findComponentOut (point);
				
		if (secondPoint >= 0) {
			if (breadboardPowerUsage[point] > breadboardPowerUsage[secondPoint]) {
				breadboardPowerUsage[secondPoint] = breadboardPowerUsage[point];
				expandPin (secondPoint);
			} else {
				breadboardPowerUsage[point] = breadboardPowerUsage[secondPoint];
				expandPin (point);
			}
		}
	}
	
	static private int findComponentOut (int componentInIndex) {
		String component = breadboardUsage[componentInIndex];
		int returnValue = -1;
		
		for (int i = 0; i < VirtualBreadboard.totalPins; i++) {
			if (breadboardUsage[i].equals(component) && componentInIndex != i) {
				returnValue = i;
				break;  
			}
		}
		
		return returnValue;
	}
	private void expandPin (int pin) {
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
	
	static public void draw (int index, Graphics g) {
		// x and y points on either end of the wire and init to 3 to center the
		// wire when breadboardPinArray is added to it
		int x1 = 2;
		int y1 = 2;
		int x2 = 2;
		int y2 = 2;
		
		// the index of the second point
		int index2;
		
		// location of pins
		int[][] breadboardPinArray = VirtualBreadboard.breadboardPinArray;
		
		// find second point
		index2 = findComponentOut (index);
		
		x1 += breadboardPinArray[0][index];
		y1 += breadboardPinArray[1][index];
		x2 += breadboardPinArray[0][index2];
		y2 += breadboardPinArray[1][index2];
		
		g.setColor (Color.BLACK);
		g.drawLine (x1, y1, x2, y2);
	}
	
	static public void click (int index) {
		if ((newWire == true) && (breadboardLinkage[index] == false)) {
			newWire = false;
			firstWirePoint = index;
		} else {
			newWire = true;
			// make sure the pins are not in use
			if ((breadboardLinkage[firstWirePoint] == false) && (breadboardLinkage[index] == false) && (firstWirePoint != index)) {
				String wireNumber = String.valueOf(numberOfComponents.getComponents());
				// set what the pins are used for
				breadboardUsage[firstWirePoint] = "wire" + wireNumber;
				breadboardUsage[index] = "wire" + wireNumber;
				breadboardComponentNumbers[firstWirePoint] = numberOfComponents.getComponents();
				breadboardComponentNumbers[index] = numberOfComponents.getComponents();
				
				// set the colour of the wire
				if (colourOptions[0].isSelected() == true)
					wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.BLACK);
				else if (colourOptions[1].isSelected() == true)
					wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.RED);
				else if (colourOptions[2].isSelected() == true)
					wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Color.GREEN);
				else if (colourOptions[3].isSelected() == true)
					wireColours[Integer.parseInt(wireNumber)] = new colourDB (numberOfComponents.getComponents(), Components.hex2Colour(colourCustom.getText()));

				// set the pins to in use
				breadboardLinkage[firstWirePoint] = true;
				breadboardLinkage[index] = true;

				numberOfComponents.addWire();
			}
		}
	}
	
	static public int isPower (int pin) {
		return 1;
	}
	
	public static int getOutPin (int inPin) {
		int returnValue = 0;
	/*	for (int i = 0; i < VirtualBreadboard.totalPins; i++) {
			if (breadboardUsage[i].equals(component) && inPin != i) {
				returnValue = i;
				break;
			}
		}*/
	
		return returnValue;	
	}
}

/*	private void wireCircut (int point) {
		int secondPoint = findComponentOut (breadboardUsage[point], point);
				
		if (secondPoint >= 0)
			if (breadboardPowerUsage[point] > breadboardPowerUsage[secondPoint]) {
				breadboardPowerUsage[secondPoint] = breadboardPowerUsage[point];
				expandPin (secondPoint);
			} else {
				breadboardPowerUsage[point] = breadboardPowerUsage[secondPoint];
				expandPin (point);
			}
	}
		
		// if the pin is on the top half
		else if (pin <= 353) {
			int currentMultiplier = (pin / 59) - 1;
			int currentPin;
			
			// find the highest power pin
			for (int i = 0; i < 5; i++) {
				currentPin = pin - ((currentMultiplier - i) * 59);
				
				if (breadboardPowerUsage[currentPin] > highestValuePin)
					highestValuePin = breadboardPowerUsage[currentPin];
			}
			
			// set the pins to the highest pin value
			for (int i = 0; i < 5; i++) {
				currentPin = pin - ((currentMultiplier - i) * 59);

				breadboardPowerUsage[currentPin] = highestValuePin;
			}
		}
		
		// if the pin is on the bottom half
		else if (pin <= 648) {
			int currentMultiplier = (pin / 59) - 6;
			int currentPin;
			
			// find the highest power pin
			for (int i = 0; i < 5; i++) {
				currentPin = pin - ((currentMultiplier - i) * 59);
				
				if (breadboardPowerUsage[currentPin] > highestValuePin)
					highestValuePin = breadboardPowerUsage[currentPin];
			}
			
			// set the pins to the highest pin value
			for (int i = 0; i < 5; i++) {
				currentPin = pin - ((currentMultiplier - i) * 59);

				breadboardPowerUsage[currentPin] = highestValuePin;
			}
		}
		
		// if the pin is on the bottom rail
		if (pin <= 707) {
			
			// find the highest power pin
			for (int i = 649; i <= 707; i++) {
				if (breadboardPowerUsage[i] > highestValuePin)
					highestValuePin = breadboardPowerUsage[i];
			}
			
			// set the pins to the highest pin value
			for (int i = 649; i <= 707; i++)
				breadboardPowerUsage[i] = highestValuePin;
		}
	}
	
	private StrInt scanFrom (int startPin) {		
		for (int i = startPin; i < VirtualBreadboard.totalPins; i++) {
			for (int j = 0; j < scanFor.length; j++) {
				if (breadboardUsage[i].length() >= scanFor[j].length()) {
					if (breadboardUsage[i].substring (0, scanFor[j].length()).equals(scanFor[j])) {
						
						// retun the component and index it was found at
						return new StrInt(breadboardUsage[i].substring (0, scanFor[j].length()), i);
					}
				}
			}
		}
		
		// return this if no new components are found
		return new StrInt("", -1);
	}
}

class StrInt {
	private String str = "";
	private int integer = 0;
	
	public StrInt () {
	}
	public StrInt (String newStr, int newInt) {
		this.str = newStr;
		this.integer = newInt;
	}
	
	public String getStr() {
		return str;
	}
	
	public int getInt() {
		return integer;
	}
}*/