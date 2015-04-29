public class storage {
	private static storage instance;
	private logic log;

	private GUI gui;
	private control ctrl;

	private int[] progStorage = new int[1024]; // Array für 14 bit
												// Programmspeicher
	private int[] dataStorage = new int[256]; // Array für 8 bit Datenspeicher
	private int pc = 0;
	private int[] stack = new int[8];
	private int stackptr = 0;
	private int w = 0;
	private boolean z = false;
	
	private storage() {
	}

	{
		// testline
		// dataStorage[10] = 255;
	}

	public static synchronized storage getInstance() {
		if (storage.instance == null) {
			storage.instance = new storage();
			instance.log = logic.getInstance();
		}
		return storage.instance;
	}

	public void setGUI(GUI aGUI) {
		gui = aGUI;
	}

	public void setCTRL(control aCTRL) {
		ctrl = aCTRL;
	}

	/**
	 * Writermethode
	 */
	void writeStorage(int destination, int value) {
		parseToByte(value);
		if ((destination & 0x7F) <= 0x0B) {
			switch (destination & 0x7F) {
			case 0x00:// INDF
				dataStorage[getDataStorage(0x04)] = value;
				break;
			case 0x01:// TMR0

				break;
			case 0x02:// PCL - Programmzähler
				setPC(value);
				break;
			case 0x03:// STATUS
				dataStorage[destination] = value;
				dataStorage[destination | (1 << 7)] = value;
				break;
			case 0x04:// FSR
				dataStorage[destination] = value;
				dataStorage[destination | (1 << 7)] = value;
				break;
			case 0x05:// PORTA
				writePort(destination, value);
				break;
			case 0x06:// PORTB
				writePort(destination, value);
				break;
			case 0x07:

				break;
			case 0x08:// EEDATA

				break;
			case 0x09:// EEADR

				break;
			case 0x0A:// PCLATH
				setPCLath(value);
				break;
			case 0x0B:// INTCON

				break;
			default:
				break;
			}
		} else {

			dataStorage[destination | (getRP0() << 7)] = value;
			
		}
		
		
		if(value == 0) {
			z = true;
		}
	}

	/**
	 * Getters Sonstige
	 */
	int getPC() {
		return pc;
	}

	int getW() {
		return w;
	}

	boolean getZ() {
		return z;
	}
	
	/**
	 * Getters Byte
	 */
	int getPCL() {
		return dataStorage[0x02];
	}

	int getStatus() {
		return dataStorage[0x03];
	}

	/**
	 * Getters Bit
	 */
	int getRP0() {
		return ((getStatus() & 0x20) >> 5);
	}

	public int getProgStorage(int i) {
		return progStorage[i];
	}

	public int getDataStorage(int i) {
		return dataStorage[i];
	}

	public int getPc() {
		return pc;
	}

	public int getStack(int i) {
		return stack[i];
	}

	public int getStackptr() {
		return stackptr;
	}

	public void setProgStorage(int destination, int value) {
		this.progStorage[destination] = value;
	}

	public void setDataStorage(int[] dataStorage) {
		this.dataStorage = dataStorage;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	public void setStack(int[] stack) {
		this.stack = stack;
	}

	public void setStackptr(int stackptr) {
		this.stackptr = stackptr;
	}

	public void setW(int w) {
		this.w = w;
		w = parseToByte(w);
		if (w == 0) {
			z = true;
		}
	}

	public void setZ(boolean z) {
		this.z = z;
	}
	
	/**
	 * Setters
	 */

	void setPC(int value) {
		pc = (value & 0x7FF) | pc & 0xC00;
		dataStorage[0x02] = value & 0xFF;
		dataStorage[0x82] = value & 0xFF;
	}

	void setPCLath(int value) {
		dataStorage[0x0A] = value;
		dataStorage[0x8A] = value;
	}

	/**
	 * Other methods
	 */
	void pushStack(int newAdress) {
		stack[stackptr & 7] = newAdress;
		stackptr++;
	}

	int popStack() {
		stackptr--;
		return stack[stackptr & 7];
		
	}

	int parseToByte(int value) {
		while(value < 0) {
			value = value + 256;	
		}
		while(value >= 256) {
			value = value -256;
		}
		return value;
	}
	
	void writePort(int destination, int value) {
		if(getRP0() == 1) {
			dataStorage[destination | (1 << 7)] = value;
		}
		else {
			dataStorage[destination] = (value ^ dataStorage[destination | (1 << 7)]); //TODO Fehler -> macht complement, soll aber eingaben nur bei 0 zulassen
		}
	}	
}
