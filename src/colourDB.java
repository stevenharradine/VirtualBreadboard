import java.awt.Color;

class colourDB {
	/*
	 * This varible type will create a database for colours and what index the
	 * corrisponding item is.
	 *
	 * For use with wires, leds, etc.
	 */
	 int compNumber = -1;	// -1 states that this index in the array is not in use
	 Color compColour;
	 
	 public colourDB (int newComponentNumber, Color newComponentColour) {
	 	this.compNumber = newComponentNumber;
	 	this.compColour = newComponentColour;
	 }
	 
	 public void setComponentNumber (int newComponentNumber) {
	 	compNumber = newComponentNumber;
	 }
	 public void setComponentColour (Color newComponentColour) {
	 	compColour = newComponentColour;
	 }
	 
	 public int getComponentNumber() {
	 	return compNumber;
	 }
	 public Color getComponentColour() {
	 	return compColour;
	 }
}