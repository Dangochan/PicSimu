public class storage {
	
	private static storage instance;
	private logic log;

	private GUI gui;
	private control ctrl;

	private int[] progStorage = new int[1024]; // Array für 14 bit Programmspeicher
	private int[] dataStorage = new int[256]; // Array für 8 bit Datenspeicher
	private int pc = 0;
	private int[] stack = new int[8];
	private int stackptr = 0;
	private int w = 0;
	private boolean c = false;
	private boolean dc = false;
	private boolean z = false;
	private double time = 0;
	private double deltatime = 1;
	//zeit = 4/quarzfreq
	
	private storage() {
		initializeStorage();
	}

	public void initializeStorage() {
		/*
		 * initalizing storage
		 */
		//TODO initialize storage prüfen. muss noch was gemacht werden??
		dataStorage[0x3] = 0x18;
		dataStorage[0x85] = 0xFF;
		dataStorage[0x86] = 0xFF;
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
		value = parseToByte(value);
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
				//TODO bits setzen
				if((value&0B1) != 0) {
					c = true;
				}
				else {
					c = false;
				}
				if((value&0B10) != 0) {
					dc = true;
				}
				else {
					dc = false;
				}
				if((value&0B100) != 0) {
					z = true;
				}
				else {
					z = false;
				}
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
	
	boolean getC() {
		return c;
	}
	
	boolean getDC() {
		return c;
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
	
	public int getWait() {
		return (int)(100 * deltatime);
	}
	
	public double getTime() {
		return time;
	}
	
	/**
	 * Setters
	 */

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
		w = parseToByte(w);
		this.w = w;
		if (w == 0) {
			setZ(true);
		}
	}

	public void setZ(boolean z) {
		this.z = z;
		if(z==true) {
			writeStorage(0X03, (getDataStorage(0X03)|0B100));
		}
		else {
			writeStorage(0X03, (getDataStorage(0X03) & 0B11111011));
		}
	}
	
	public void setC(boolean c) {
		this.c = c;
		if(c==true) {
			writeStorage(0X03, (getDataStorage(0X03)|0B1));
		}
		else {
			writeStorage(0X03, (getDataStorage(0X03) & 0B11111110));
		}
	}
	
	public void setDC(boolean dc) {
		this.dc = dc;
		if(dc==true) {
			writeStorage(0X03, (getDataStorage(0X03)|0B10));
		}
		else {
			writeStorage(0X03, (getDataStorage(0X03) & 0B11111101));
		}
	}
	
	public void testAndSetDC(int zahl1, int zahl2) {
		if(((zahl1 & 0xF) + (zahl2 & 0xF)) < 16) {
			setDC(false);
		}
		else {
			setDC(true);
		}
	}
	
	public void testAndSetC(int zahl1, int zahl2) {
		if((zahl1 + zahl2 >= 256)) {
			setC(true);
		}
		else {
			setC(false);
		}
	}

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
	
	public void changePortBit(int port, int bit){
		int mask = 0b1 << bit;
		if ((mask & dataStorage[port+5]) != 0){
			dataStorage[port+5]= dataStorage[port+5]&(mask^0xff);
		}
		else{
			dataStorage[port+5] = dataStorage[port+5]|(mask);
		}
	}
	
	public int readPortBit(int port, int bit){
		int mask = 0b1 << bit;
		if((mask & dataStorage[port+5]) != 0){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	void incTime() {
		time += deltatime;
	}
}
