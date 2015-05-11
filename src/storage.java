public class storage {
	
	private static storage instance;

	private int[] progStorage = new int[8192]; // Array für 14 bit Programmspeicher
	private int[] dataStorage = new int[256]; // Array für 8 bit Datenspeicher
	private int pc = 0;
	private int[] stack = new int[8];
	private int stackptr = 0;
	private int w = 0;
	private double time = 0;
	private double deltatime = 1;
	/*
	 * Interrupt
	 */
	boolean interruptOccured = false;
	int prescaleCount = 1;
	
	private storage() {
		for(int i = 0; i < progStorage.length; i++) {
			progStorage[i] = 0;
		}
		initializeStorage();
	}

	public void initializeStorage() {
		/*
		 * Programmcounter und Stackpointer zurücksetzen
		 */
		setPC(0);
		stackptr = 0;
		/*
		 * Special Function Register initialisieren
		 */
		dataStorage[0x2] = 0x00;
		dataStorage[0x3] = 0x18 | (dataStorage[0x3]&0B111);
		dataStorage[0xA] = 0x00;
		dataStorage[0xB] = 0x00 | (dataStorage[0xB]&0B1);
		
		dataStorage[0x81] = 0xFF;
		dataStorage[0x82] = 0x00;
		dataStorage[0x83] = 0x18 | (dataStorage[0x83]&0B111);
		dataStorage[0x85] = 0xFF;
		dataStorage[0x86] = 0xFF;
		dataStorage[0x88] = 0x00 | (dataStorage[0x88]&0B1000);
		dataStorage[0x8A] = 0x00;
		dataStorage[0x8B] = 0x00 | (dataStorage[0x8B]&0B1);;
		
	}

	

	public static synchronized storage getInstance() {
		if (storage.instance == null) {
			storage.instance = new storage();
		}
		return storage.instance;
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
				if(getRP0() == 0) {
					dataStorage[destination] = value;
				} else {
					dataStorage[destination | (1 << 7)] = value;
				}
				if(getPSA() == 0) { //wenn Prescaler für Timer aktiv und TMR0 beschrieben wir
					prescaleCount = getPrescale();//muss Prescale zurückgesetzt werden.
				}
				break;
			case 0x02:// PCL - Programmzähler
				dataStorage[destination] = value;
				dataStorage[destination | (1 << 7)] = value;
				pc = (pc&0xFF00) | value;
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
				checkExtTimer((value&0x10)>> 4);//noch steht der alte Wert an PortA, dadurch vergleich möglich
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
				dataStorage[destination] = value;
				dataStorage[destination | (1 << 7)] = value;
				break;
			case 0x0B:// INTCON
				dataStorage[destination] = value;
				dataStorage[destination | (1 << 7)] = value;
				break;
			default:
				break;
			}
		} else {

			dataStorage[destination | (getRP0() << 7)] = value;
			
		}
		
		
		if(value == 0) {
			setZ(true);
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
		if ((dataStorage[0x03] & 0B100) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	boolean getC() {
		if ((dataStorage[0x03] & 0B1) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	boolean getDC() {
		if ((dataStorage[0x03] & 0B10) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Getters Byte
	 */
	int getPCL() {
		return dataStorage[0x02];
	}
	
	int getPCLath() {
		return dataStorage[0x0A];
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
	public int[] getDataStorage() {
		return dataStorage;
	}

	public int getPc() {
		return pc;
	}

	public int getStack(int i) {
		return stack[i];
	}

	public int getStackPtr() {
		return stackptr;
	}
	
	public int getWait() {
		return (int)(25 * deltatime);
	}
	
	public double getTime() {
		return time;
	}
	
	public double getDeltatime() {
		return deltatime;
	}
	
	public double getFreq() {
		return 4/deltatime;
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
		writeStorage(0x02, pc & 0xFF);//PCL
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
		//this.z = z;
		if(z==true) {
			writeStorage(0X03, (getDataStorage(0X03)|0B100));
		}
		else {
			writeStorage(0X03, (getDataStorage(0X03) & 0B11111011));
		}
	}
	
	public void setC(boolean c) {
		if(c==true) {
			writeStorage(0X03, (getDataStorage(0X03)|0B1));
		}
		else {
			writeStorage(0X03, (getDataStorage(0X03) & 0B11111110));
		}
	}
	
	public void setDC(boolean dc) {
		//this.dc = dc;
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
		writeStorage(0x0A, value);
	}

	void setDeltatime(double value) {
		deltatime = value;
	}
	
	void setFreq(double value) {
		deltatime = 4/value;
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
			int mask = (dataStorage[destination | (1 << 7)] ^ 0xFF);
			dataStorage[destination] = value & (mask);
		}
	}	
	
	public void changePortBit(int port, int bit){
		int mask = 0b1 << bit;
		if((dataStorage[(port+5)|0x80] & mask) != 0) {
			boolean tempZ = getZ();
			
			if ((mask & dataStorage[port+5]) != 0){
				System.out.println("change port bit: bit löschen");
				//writeStorage(port+5, getDataStorage(port+5)&(mask^0xff));
				checkExtTimer(        ((dataStorage[0x05]&0x10)>> 4)^0x1           );//invertierte Wert übergeben, da bit gerade invertiert wurde
				checkExtInterrupt(((dataStorage[0x06]&0x1))^0x1);
				checkRBInterrupt(port, bit);
				dataStorage[port+5]= dataStorage[port+5]&(mask^0xff);
			}
			else{
				//writeStorage(port+5, getDataStorage(port+5)|(mask));
				System.out.println("change port bit: bit Setzen");
				checkExtTimer(((dataStorage[0x05]&0x10)>> 4)^0x1);//invertierte Wert übergeben, da bit gerade invertiert wurde
				checkExtInterrupt(((dataStorage[0x06]&0x1))^0x1);
				checkRBInterrupt(port, bit);
				dataStorage[port+5] = dataStorage[port+5]|(mask);
			}
			setZ(tempZ);
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
		checkIntTimer();
		checkInterrupt();
	}
	
	/*
	 * Interrupts
	 */
	void checkInterrupt() {
		//testen ob interrupts an sind
		//testen ob timer erhoht werden muss
		//testen ob timer overflow vorhanden				->interrupt boolean setzen
		//testen ob externes interrupt registriert wurde	->interrupt boolean setzen	->muss in logic.step geprüft und behandelt werden
		interruptOccured = false;
		if(getGIE()!= 0) {
			System.out.println("T0IE: " + getT0IE() + ". T0IF: " + getT0IF());
			if(getT0IE() != 0 && getT0IF() != 0) {
				interruptOccured = true;
			}
			if(getINTF() != 0) {
				interruptOccured = true;
			}
			if(getRBIF() != 0) {
				interruptOccured = true;
			}
		}
		if(interruptOccured) {
			pushStack(getPc() + 1);
			setPc(0x4);
			setGIE(false);
		}
	}
	
	void checkIntTimer() {
		if(getT0CS() == 0) {
			incPrescalerAndTimerInterrupt();
		}
	}
	
	void checkExtTimer(int newRA4) {
		if((getT0CS() == 1) && (newRA4 != ((getDataStorage(5)&0x10)>>4) )) {//externer Timer gewählt und RA4 hat sich geändert?
			System.out.println("T0SE: " + getT0SE());
			if((newRA4 == 0 && getT0SE() == 1) || ((newRA4 == 1 && getT0SE() == 0))) {//fallende flanke und RA4 jetzt0 oder steigende flanke und RA4 jetzt 1?
				incPrescalerAndTimerInterrupt();// wenn ja wird TMR0 Interrupt routine ausgelöst
			}
		}
	}
	
	private void incPrescalerAndTimerInterrupt() {
		
			if(getPSA() == 0) {
				if(prescaleCount != 0) {
					prescaleCount--;
				} else {
					prescaleCount = getPrescale()-1;
					incTimerInterrupt();
				}	
			} else {
				incTimerInterrupt();
			}
		
	}

	public void incTimerInterrupt() {
		dataStorage[0x01]++;
		if (dataStorage[0x01] > 255) {
			dataStorage[0x01] = dataStorage[0x01] &0xFF;
			setT0IF(true);
		}
	}
	
	void checkExtInterrupt(int newRB0) {
		if((getINTE() != 0) && (newRB0 != (getDataStorage(6)&0x01)) ) {//externes Interrupt aktiviert und RB0 hat sich geändert?
			if((newRB0 == 0 && getINTEDG() == 0) || ((newRB0 == 1 && getINTEDG() == 1))) {//fallende flanke und RB0 jetzt 0 oder steigende flanke und RB0 jetzt 1?
				setINTF(true);// wenn ja wird RBIE Interrupt routine ausgelöst
			}
		}
	}
	void checkRBInterrupt(int port, int bit) {
		if((getRBIE() != 0) && (port == 1) && (bit >= 5)) {
			setRBIF(true);
		}
	}
	
    /*
     * Interrupt Getter und Setter
     */
	public int getGIE() {
		return ((dataStorage[0x0B] & 0x80) >> 7);
	}
	
	public int getINTF() {
		return ((dataStorage[0x0B] & 0x2) >> 1);
	}
	
	public int getRBIF() {
		return (dataStorage[0x0B] & 0x01);
	}
    
	public int getT0IE() {
		return ((dataStorage[0x0B] & 0x20) >> 5);
	}   
	
	public int getT0IF() {
		return ((dataStorage[0x0B] & 0x04) >> 2);
	} 
	
	public int getINTE() {
		return ((dataStorage[0x0B] & 0x10) >> 4);
	}
	
	
	public int getRBIE() {
		return ((dataStorage[0x0B] & 0x08) >> 3);
	}
	
	public int getPSA() {
		return ((dataStorage[0x81] & 0x08) >> 3);
	}
	
	public int getT0CS() {
		return ((dataStorage[0x81] & 0x20) >> 5);
	}
	
	public int getT0SE() {
		return ((dataStorage[0x81] & 0x10) >> 4);
	}
	
	public int getINTEDG() {
		return ((dataStorage[0x81] & 0x40) >> 6);
	}
	
	public boolean getInterruptOccured(){
		return interruptOccured;
	}
	
	private int getPrescale() {
		return (int)Math.pow(2,(dataStorage[0x81] & 0B111)+1);//Prescaler aus drei bit errechnen
	}
	
	public void setT0IF(boolean T0IF) {
		if(T0IF==true) {
			writeStorage(0X0B, (getDataStorage(0X0B)|0B100));
		} else {
			writeStorage(0X0B, (getDataStorage(0X0B) & 0B11111011));
		}
	}
	
	public void setGIE(boolean GIE) {
		if(GIE==true) {
			writeStorage(0X0B, (getDataStorage(0X0B)|0x80));
		} else {
			writeStorage(0X0B, (getDataStorage(0X0B) & 0x7F));
		}
	}
	
	
	
	
	public void setRBIF(boolean RBIF) {
		if(RBIF==true) {
			writeStorage(0X0B, (getDataStorage(0X0B)|0x01));
		} else {
			writeStorage(0X0B, (getDataStorage(0X0B) & 0B11111110));
		}
	}
	
	public void setINTF(boolean INTF) {
		if(INTF==true) {
			writeStorage(0X0B, (getDataStorage(0X0B)|0x02));
		} else {
			writeStorage(0X0B, (getDataStorage(0X0B) & 0B11111101));
		}
	}
}