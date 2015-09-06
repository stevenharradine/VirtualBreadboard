/*
 *      author:  Steven J. Harradine
 *
 *    filename:  Conversion.java
 *
 *     started:  20050315
 *    finished:  2005xxxx
 *
 * description:  This object will allow the programer to convert value to and
 *               from binary, decmial and hex.
 */
 
 import java.util.*;
 
 class Conversion {
 	static public String dec2Bin(int decmialNumber) {
		String binaryNumber = "";
		for (int i = 7; i >= 0; i--) {
			if (decmialNumber / 2.0 != (int)Math.floor(decmialNumber / 2)) {
				binaryNumber = "1" + binaryNumber;
			} else {
				binaryNumber = "0" + binaryNumber;
			}
			
			decmialNumber = (int)Math.floor(decmialNumber / 2);
		}
		
 		return binaryNumber;
 	}
 	static public int bin2Dec (String binaryNumber) {
 		final int numberLength = binaryNumber.length();
		int decmialNumber = 0;
		
		for (int i = 0; i < numberLength; i++) {
			if (binaryNumber.charAt(i) == '1') {
				decmialNumber += (int)Math.pow(2,(numberLength - i));
			}
		}

 		return decmialNumber;
 	}
 }