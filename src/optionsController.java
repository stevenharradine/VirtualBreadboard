/*
 *      author:  Steven J. Harradine
 *
 *    filename:  optionsController.java
 *
 *     started:  20050519
 *    finished:  200505xx
 *
 * description:  This object will control the flow of data to and from the
 *               options file (options.txt).
 *
 */
 
 import java.io.*;
 
 class optionsController {
 	final static String OPTIONS_FILE_NAME = "options.txt";
 	
	static int pinsColumns;
	static int pinsRows;
	static int pinSize;
	static int pinSpacing;
	static int ioColumns;
	static int ioRows;
	static int ioInput;
	static int ioOutput;
	static String webBrowserLocation;
	static String helpLocation;
	static String installLocation;
	
	public optionsController() {
		BufferedReader in;
		
		try {
			in = new BufferedReader (new FileReader (OPTIONS_FILE_NAME));
			
			// read the options				
			pinsColumns = Integer.parseInt(in.readLine());
			pinsRows = Integer.parseInt(in.readLine());
			pinSize = Integer.parseInt(in.readLine());
			pinSpacing = Integer.parseInt(in.readLine());
			ioColumns = Integer.parseInt(in.readLine());
			ioRows = Integer.parseInt(in.readLine());
			ioInput = Integer.parseInt(in.readLine());
			ioOutput = Integer.parseInt(in.readLine());
			webBrowserLocation = in.readLine();
			helpLocation = in.readLine();
			installLocation = in.readLine();
			
			in.close();
		} catch (Exception e) {
			DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered opening the options file now reverting to default values." + e.getMessage());
			errorBox.show();
			
			// load default options and save
			loadDefaultSettings();
			saveOptions();
		} finally {
		}
	}
	
	public void saveOptions() {
		PrintWriter out;
		
		try {
			out = new PrintWriter (new FileWriter (OPTIONS_FILE_NAME));
			
			out.println (pinsColumns);
			out.println (pinsRows);
			out.println (pinSize);
			out.println (pinSpacing);
			out.println (ioColumns);
			out.println (ioRows);
			out.println (ioInput);
			out.println (ioOutput);
			out.println (webBrowserLocation);
			out.println (helpLocation);
			out.print (installLocation);
			
			out.close();
		} catch (Exception e) {
			DialogBox errorBox = new DialogBox("Error: Unknown", "This is an unknown error that was encountered saving the options file." + e.getMessage());
			errorBox.show();
		} finally {
		}
	}
		
	public void loadDefaultSettings() {
			pinsColumns = 59;
			pinsRows = 5;
			pinSize = 4;
			pinSpacing = 1;
			ioColumns = 4;
			ioRows = 4;
			ioInput = 8;
			ioOutput = 8;
	}
 }