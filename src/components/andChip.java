/*
 *      author:  Steven J. Harradine
 *
 *    filename:  andChip.java
 *
 *     started:  20050608
 *    finished:  2005xxxx
 *
 * description:  This object will act as an AND Chip
 *
 */
 
import java.awt.*;

class andChipComponent {
	static float[] breadboardPowerUsage = drawBreadboard.breadboardPowerUsage;
	static String[] breadboardUsage = VirtualBreadboard.breadboardUsage;
	
	private void andChipCircut (int powerPinLocation) {
						// find input pins (a and b), power, and ground pins and
						// pass them to the AND chip processor then send the
						// values to the appropriate rails
						float[] a = new float[4];
						float[] b = new float[4];
						float[] y;
						float powerPin = breadboardPowerUsage[powerPinLocation];
						float groundPin = breadboardPowerUsage[powerPinLocation + 65];
				
				
						a[0] = breadboardPowerUsage[powerPinLocation + 2];
						a[1] = breadboardPowerUsage[powerPinLocation + 5];
						a[2] = breadboardPowerUsage[powerPinLocation + 59];
						a[3] = breadboardPowerUsage[powerPinLocation + 62];
				
						b[0] = breadboardPowerUsage[powerPinLocation + 1];
						b[1] = breadboardPowerUsage[powerPinLocation + 4];
						b[2] = breadboardPowerUsage[powerPinLocation + 60];
						b[3] = breadboardPowerUsage[powerPinLocation + 63];
				
						y = andChip (powerPin, a, b, groundPin);
						
						if (y[0] > 0f)
							breadboardPowerUsage[powerPinLocation + 3] = y[0];
						if (y[1] > 0f)
							breadboardPowerUsage[powerPinLocation + 6] = y[1];
						if (y[2] > 0f)
							breadboardPowerUsage[powerPinLocation + 61] = y[2];
						if (y[3] > 0f)
							breadboardPowerUsage[powerPinLocation + 64] = y[3];
	}
	
	private float[] andChip (float power, float[] a, float[] b, float gnd) {
		/*
		 * This method calculates AND for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    4
		 *    1			3
		 *	  2			1
		 *	  3			2
		 *
		 */
		float[] and = new float[4];
		if (power > 0f && gnd < 0f) {
			int[] input1 = new int [4];
			int[] input2 = new int [4];

			for (int i = 0; i < 4; i++) {
				if (a[i] > 0f)
					input1[i] = 1;
				else
					input1[i] = 0;

				if (b[i] > 0f)
					input2[i] = 1;
				else
					input2[i] = 0;

				if ((input1[i] & input2[i]) == 1)
					and[i] = 5f;
				else
					and[i] = 0f;
			}
		}
		return and;
	}
	
	static public void draw (int index, Graphics g) {
		Components.drawChip("74LS08", index, 7, g);	// draw And chip
	}
	
	static public int isPower (int pin) {
		return 1;
	}
	
	/*
	 * What pin is the out pin based on the input pin
	 */
	static public int getOutPin (int inPin) {
		return inPin + 65;
	}
}