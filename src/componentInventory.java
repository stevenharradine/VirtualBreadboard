class componentInventory {
	/*
	 * This object will retain all the information of the number of components.
	 * The number of actual components will be the correct values however the
	 * total number of components will just be a running total of how many
	 * components there have been.  For example when two wires are on the
	 * breadboard and you remove the first wire then the number of wires will
	 * decrease by one, however the number of components will stay the same.
	 * This will prevent conflict when the second wire is added again.
	 *
	 * Old error
	 * Wire 1 index = 0
	 * Wire 2 index = 1
	 * Wire 1 removed
	 * Wire 2 becomes 1
	 * New wire 2 index = 1
	 *
	 * Fixed
	 * Wire 1 index = 0
	 * Wire 2 index = 1
	 * Wire 1 removed
	 * Wire 2 becomes 1
	 * New wire 2 index = 2
	 *
	 */
	int numberOfWires = 0;
	int numberOfChips = 0;
	int numberOfResistors = 0;
	int numberOfLeds = 0;
	int numberOfComponents = 1;
	
	// add a component
	public void addWire() {
		numberOfWires++;
		numberOfComponents++;
	}
	public void addChip() {
		numberOfChips++;
		numberOfComponents++;
	}
	public void addResistor() {
		numberOfResistors++;
		numberOfComponents++;
	}
	public void addLed() {
		numberOfLeds++;
		numberOfComponents++;
	}
	
	// remove a component
	public void subWire() {
		numberOfWires--;
	}
	public void subChip() {
		numberOfChips--;
	}
	public void subResistor() {
		numberOfResistors--;
	}
	public void subLed() {
		numberOfLeds--;
	}
	
	// set the value of a component
	public void setWire(int newNumberOfWires) {
		numberOfWires = newNumberOfWires;
		numberOfComponents += newNumberOfWires;
	}
	public void setChips(int newNumberOfChips) {
		numberOfChips = newNumberOfChips;
		numberOfComponents += newNumberOfChips;
	}
	public void setResistor(int newNumberOfResistors) {
		numberOfResistors = newNumberOfResistors;
		numberOfComponents += newNumberOfResistors;
	}
	public void setLed(int newNumberOfLeds) {
		numberOfLeds = newNumberOfLeds;
		numberOfComponents += newNumberOfLeds;
	}
	public void setComponent(int newNumberOfComponents) {
		numberOfComponents += newNumberOfComponents + 1;
	}
	
	// return value of a component
	public int getWire() {
		return numberOfWires;
	}
	public int getChip() {
		return numberOfChips;
	}
	public int getResistor() {
		return numberOfResistors;
	}
	public int getLed() {
		return numberOfLeds;
	}
	public int getComponents() {
		return numberOfComponents;
	}
	public int getActualComponents() {
		return numberOfWires + numberOfResistors + numberOfLeds + numberOfChips;
	}
}