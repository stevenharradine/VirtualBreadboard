import java.awt.*;
import java.io.*;

class Components {
	static int[][] breadboardPinArray = VirtualBreadboard.breadboardPinArray;
	
	static public void drawChip (String chipCode, int i, int numberOfPinsPerSide, Graphics g) {
		/*
		 * draws chips of differents sizes and writes its code on the back
		 */
		g.setColor(Color.BLACK);
		g.fillRect(0 + breadboardPinArray[0][i], 3 + breadboardPinArray[1][i], (6 * numberOfPinsPerSide) - 1, 10);

		g.setColor(Color.GRAY);
		for (int j = 0; j < numberOfPinsPerSide; j++) {
			g.drawLine(1 + (j * 6) + breadboardPinArray[0][i], 2 + breadboardPinArray[1][i], 3 + (j * 6) + breadboardPinArray[0][i], 2 + breadboardPinArray[1][i]);
			g.drawLine(2 + (j * 6) + breadboardPinArray[0][i], 1 + breadboardPinArray[1][i], 2 + (j * 6) + breadboardPinArray[0][i], 1 + breadboardPinArray[1][i]);

			g.drawLine(1 + (j * 6) + breadboardPinArray[0][i], 13 + breadboardPinArray[1][i], 3 + (j * 6) + breadboardPinArray[0][i], 13 + breadboardPinArray[1][i]);
			g.drawLine(2 + (j * 6) + breadboardPinArray[0][i], 14 + breadboardPinArray[1][i], 2 + (j * 6) + breadboardPinArray[0][i], 14 + breadboardPinArray[1][i]);
		}

		g.setColor(Color.WHITE);
		g.setFont (new Font("Arial", Font.PLAIN, 9));
		g.drawString(chipCode,breadboardPinArray[0][i + numberOfPinsPerSide - 1] - (chipCode.length() * 6) + 10, 11 + breadboardPinArray[1][i]);
	}
	static public Color hex2Colour (String hexCode) {
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
	static public int hex2dec (String hexCode) {
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
/*	static public float[] andChip (float power, float[] a, float[] b, float gnd) {
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
		 *//*
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
	}*/
	static public float[] orChip (float power, float[] a, float[] b, float gnd) {
		/*
		 * This method calculates OR for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    4
		 *    1			3
		 *	  2			1
		 *	  3			2
		 *
		 */
		float[] or = new float[4];
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

				if ((input1[i] | input2[i]) == 1)
					or[i] = 5f;
				else
					or[i] = 0f;
			}
		}
		return or;
	}
	static public float[] xorChip (float power, float[] a, float[] b, float gnd) {
		/*
		 * This method calculates OR for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    4
		 *    1			3
		 *	  2			1
		 *	  3			2
		 *
		 */
		float[] xor = new float[4];
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

				if (((input1[i] | input2[i]) == 1) && (input1[i] != input2[i]))
					xor[i] = 5f;
				else
					xor[i] = 0f;
			}
		}
		return xor;
	}
	static public float[] notChip (float power, float[] a, float gnd) {
		/*
		 * This method calculates NOT for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    6
		 *    1			5
		 *	  2			4
		 *	  3			1
		 *	  4			2
		 *	  5			3
		 *
		 */
		float[] not = new float[6];
		if (power > 0f && gnd < 0f) {
			int[] input1 = new int [6];

			for (int i = 0; i < 6; i++) {
				if (a[i] > 0f)
					input1[i] = 1;
				else
					input1[i] = 0;

				if (input1[i] == 0)
					not[i] = 5f;
				else
					not[i] = 0f;
			}
		}
		return not;
	}
	static public float[] norChip (float power, float[] a, float[] b, float gnd) {
		/*
		 * This method calculates NOR for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    4
		 *    1			3
		 *	  2			1
		 *	  3			2
		 *
		 */
		float[] nor = new float[4];
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

				if ((input1[i] | input2[i]) == 0)
					nor[i] = 5f;
				else
					nor[i] = 0f;
			}
		}
		return nor;
	}
	static public float[] nandChip (float power, float[] a, float[] b, float gnd) {
		/*
		 * This method calculates NAND for the and chip. It out puts the results
		 * in an array.
		 *
		 *	Index	Gate number
		 *    0		    4
		 *    1			3
		 *	  2			1
		 *	  3			2
		 *
		 */
		float[] nand = new float[4];
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

				if ((input1[i] & input2[i]) == 0)
					nand[i] = 5f;
				else
					nand[i] = 0f;
			}
		}
		return nand;
	}
	static public float[] decoderChip (float power, float[] in, float gnd) {
		/*
		 * This method returns the output needed for the 7-segment display. It
		 * out puts the results in an array.
		 *
		 */
		float[] decoder = new float[7];
		if (power > 0f && gnd < 0f) {
			if (in[0] > 0f && in[1] > 0f && in[2] > 0f && in[3] > 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 5f;
				decoder[6] = 0f;
			} else if (in[0] > 0f && in[1] > 0f && in[2] > 0f && in[3] == 0f) {
				decoder[0] = 0f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 0f;
				decoder[6] = 0f;
			} else if (in[0] > 0f && in[1] > 0f && in[2] == 0f && in[3] > 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 0f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 0f;
				decoder[6] = 5f;
			} else if (in[0] > 0f && in[1] > 0f && in[2] == 0f && in[3] == 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 0f;
				decoder[5] = 0f;
				decoder[6] = 5f;
			} else if (in[0] > 0f && in[1] == 0f && in[2] > 0f && in[3] > 0f) {
				decoder[0] = 0f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] > 0f && in[1] == 0f && in[2] > 0f && in[3] == 0f) {
				decoder[0] = 5f;
				decoder[1] = 0f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 0f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] > 0f && in[1] == 0f && in[2] == 0f && in[3] > 0f) {
				decoder[0] = 0f;
				decoder[1] = 0f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] > 0f && in[1] == 0f && in[2] == 0f && in[3] == 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 0f;
				decoder[6] = 0f;
			} else if (in[0] == 0f && in[1] > 0f && in[2] > 0f && in[3] > 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] > 0f && in[2] > 0f && in[3] == 0f) {
				decoder[0] = 5f;
				decoder[1] = 5f;
				decoder[2] = 5f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] > 0f && in[2] == 0f && in[3] > 0f) {
				decoder[0] = 0f;
				decoder[1] = 0f;
				decoder[2] = 0f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 0f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] > 0f && in[2] == 0f && in[3] == 0f) {
				decoder[0] = 0f;
				decoder[1] = 0f;
				decoder[2] = 5f;
				decoder[3] = 5f;
				decoder[4] = 0f;
				decoder[5] = 0f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] == 0f && in[2] > 0f && in[3] > 0f) {
				decoder[0] = 0f;
				decoder[1] = 5f;
				decoder[2] = 0f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] == 0f && in[2] > 0f && in[3] == 0f) {
				decoder[0] = 5f;
				decoder[1] = 0f;
				decoder[2] = 0f;
				decoder[3] = 5f;
				decoder[4] = 0f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else if (in[0] == 0f && in[1] == 0f && in[2] == 0f && in[3] > 0f) {
				decoder[0] = 0f;
				decoder[1] = 0f;
				decoder[2] = 0f;
				decoder[3] = 5f;
				decoder[4] = 5f;
				decoder[5] = 5f;
				decoder[6] = 5f;
			} else {
				decoder[0] = 0f;
				decoder[1] = 0f;
				decoder[2] = 0f;
				decoder[3] = 0f;
				decoder[4] = 0f;
				decoder[5] = 0f;
				decoder[6] = 0f;
			}
		}
		return decoder;
	}
}