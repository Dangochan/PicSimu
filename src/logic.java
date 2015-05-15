//TODO Programm2 zeile 002B Zero-Flag müsste gesetzt sein (erst kopieren dann rücksetzen)
public class logic
{
	private static logic instance;

	private StepStack stepStack;
	private storage sto;
	private GUI gui;
	
	public void setGUI(GUI aGUI) {
		gui = aGUI;
	}
	
	private logic()
	{
		
	}
	public static synchronized logic getInstance () {
		if (logic.instance == null) {
			logic.instance = new logic ();
			instance.sto = storage.getInstance();
			instance.gui = GUI.getInstance();
			instance.stepStack = StepStack.getInstance();
	    }
	    return logic.instance;
	}
	public void step()
	{
		executeCommand();
		
		gui.updateProgress();
		System.out.println("PC " + sto.getPc());
	}
	
	public void executeCommand()
	{
		stepStack.pushStack(new Step());
		//Integer, für Programmcode
		//0000 0000  0000 0000  0000 0000  0000 0000
		
		//ohne Maske
		switch (sto.getProgStorage(sto.getPC()))
		{
			case 0B00000001100100://Clear Watchdog Timer
				System.out.println("Clear Watchdog Timer");
				break;
			case 0B00000000001001://Return from interrupt
				commandRETFIE();
				System.out.println("Return from interrupt");
				break;	
			case 0B00000000001000://Return from Subroutine	
				commandRETURN();
				System.out.println("Return from Subroutine");
				break;
			case 0B00000001100011://Go into standby mode	
				System.out.println("Go into standby mode");
				break;
			default:
				//Maske
				//        --0--         0000 0000  0xx0 0000
				switch (sto.getProgStorage(sto.getPC()) & 0B11111110011111)
				{
					case 0B0://No Operation
						commandNOP();
						System.out.println("No Operation");
						break;	
					default:
						//Maske
						//        --0--         0011 1111  1fff ffff
						//        --0--         0011 1111  1xxx xxxx
						switch ((sto.getProgStorage(sto.getPC()) & 0B11111110000000))
						{
							case 0B110000000://Clear f
								commandCLRF();
								System.out.println("Clear f");
								break;	
							case 0B100000000://Clear W
								commandCLRW();
								System.out.println("Clear W");
								break;
							case 0B10000000://Move W to f	
								commandMOVWF();
								System.out.println("Move W to f");
								break;	

								
							default:
								//Maske
								//        --0--         0011 1111  dfff ffff
								switch ((sto.getProgStorage(sto.getPC()) & 0B11111100000000))
								{
									case 0B11100000000://Add W and f
										commandADDWF();
										System.out.println("add w and f");
										break;
									case 0B10100000000://AND W with f
										commandANDWF();
										System.out.println("AND W with f");
										break;
									case 0B100100000000://Complement f
										commandCOMF();
										System.out.println("Complement f");
										break;
									case 0B1100000000://Decrement f
										commandDECF();
										System.out.println("Decrement f");
										break;	
									case 0B101100000000://Decrement f, Skip if 0
										commandDECFSZ();
										System.out.println("Decrement f, Skip if 0");
										break;	
									case 0B101000000000://Increment f
										commandINCF();
										System.out.println("Increment f");
										break;
									case 0B111100000000://Increment f, Skip if 0
										commandINCFSZ();
										System.out.println("Increment f, Skip if 0");
										break;	
									case 0B10000000000://Inclusive OR W with f
										commandIORWF();
										System.out.println("Inclusive OR W with f");
										break;		
									case 0B100000000000://Move f
										commandMOVF();
										System.out.println("Move f");
										break;					
									case 0B110100000000://Rotate Left f through Carry
										commandRLF();
										System.out.println("Rotate Left f through Carry");
										break;	
									case 0B110000000000://Rotate Right f through Carry
										commandRRF();
										System.out.println("Rotate Right f through Carry");
										break;
									case 0B1000000000://Subtract W from f
										commandSUBWF();
										System.out.println("Subtract W from f");
										break;	
									case 0B111000000000://Swap nibbles in f
										commandSWAPF();
										System.out.println("Swap nibbles in f");
										break;	
									case 0B11000000000://Exclusive OR W with f
										commandXORWF();
										System.out.println("Exclusive OR W with f");
										break;	
									case 0B11100100000000://AND literal with W
										commandANDLW();
										System.out.println("AND literal with W");
										break;
									case 0B11100000000000://Inclusive OR literal with W
										commandIORLW();
										System.out.println("Inclusive OR literal with W");
										break;
									case 0B11101000000000://Exclusive OR literal with W
										commandXORLW();
										System.out.println("Exclusive OR literal with W");
										break;
									default:
										//Maske
										//      --0--         0011 111x  kkkk kkkk
										switch ((sto.getProgStorage(sto.getPC()) & 0B11111000000000))
										{
											case 0B11111000000000://Add literal and W
												commandADDLW();
												System.out.println("Add literal and W");
												break;
											case 0B11110000000000://Subtract W from literal
												commandSUBLW();
												System.out.println("Subtract W from literal");
												break;
												
												
											default:
												//Maske
												//      --0--         0011 11bb  bfff ffff
												//      --0--         0011 11xx  kkkk kkkk
												switch ((sto.getProgStorage(sto.getPC()) & 0B11110000000000))
												{
													case 0B1000000000000://Bit Clear f
														commandBCF();
														System.out.println("Bit Clear f");
														break;
													case 0B1010000000000://Bit Set f
														commandBSF();
														System.out.println("Bit Set f");
														break;
													case 0B1100000000000://Bit Test f, Skip if Clear
														commandBTFSC();
														System.out.println("Bit Test f, Skip if Clear");
														break;
													case 0B1110000000000://Bit Test f, Skip if Set
														commandBTFSS();
														System.out.println("Bit Test f, Skip if Set");
														break;
													case 0B11000000000000://Move literal to W
														commandMOVLW();
														System.out.println("Move literal to W");
														break;	
													case 0B11010000000000://Return with literal in W
														commandRETLW();
														System.out.println("Return with literal in W");
														break;	
													default:
														//Maske
														//      --0--         0011 1kkk  kkkk kkkk
														switch ((sto.getProgStorage(sto.getPC()) & 0B11100000000000))
														{
															case 0B10000000000000://Call subroutine
																commandCALL();
																System.out.println("Call subroutine");
																break;
															case 0B10100000000000://Go to address
																commandGOTO();
																System.out.println("Go to address");
																break;

															default:
																break;
														}
														break;
												}
												break;
										} 
										break;
								}
								break;
						} 
						break;
				} 
				break;
		}
	}
	
	
	/**
	 * Befehlsfunktionen
	 */
	void commandADDLW() {
		sto.setZ(false);
		sto.testAndSetC(sto.getW(), extractShortK());
		sto.testAndSetDC(sto.getW(), extractShortK());
		sto.setW(sto.getW()+extractShortK());
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandADDWF() {
		sto.setZ(false);
		sto.testAndSetDC(sto.getW(), sto.getDataStorage(checkZeroF(extractF())));
		sto.testAndSetC(sto.getW(), sto.getDataStorage(checkZeroF(extractF())));
		int ergebnis = sto.getW() + sto.getDataStorage(checkZeroF(extractF()));
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandANDLW() {
		sto.setZ(false);
		sto.setW(sto.getW() & extractShortK());
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandANDWF() {
		sto.setZ(false);
		int ergebnis = sto.getDataStorage(checkZeroF(extractF())) & sto.getW();
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandBCF() {
		boolean tempZ = sto.getZ();
		int mask = (1 << extractB());
		mask = mask ^ 0B11111111;
		sto.writeStorage(checkZeroF(extractF()), (sto.getDataStorage(checkZeroF(extractF())) & mask));
		sto.setZ(tempZ);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandBSF() {
		boolean tempZ = sto.getZ();
		int mask = (1 << extractB()); 
		sto.writeStorage(checkZeroF(extractF()), (sto.getDataStorage(checkZeroF(extractF())) | mask));
		sto.setZ(tempZ);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandBTFSS() {
		int mask = (1 << extractB());
		if((sto.getDataStorage(checkZeroF(extractF())) & mask) != 0) {
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandBTFSC() {
		int mask = (1 << extractB());
		if((sto.getDataStorage(checkZeroF(extractF())) & mask) == 0) {
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandCALL() {
		sto.pushStack(sto.getPC() + 1);
		sto.setPC(((sto.getPCLath() & 0x18) << 8) | extractLongK());
		//Standardanweisungen
		sto.incTime();
		sto.incTime();
	}
	
	void commandCLRF() {
		sto.setZ(false);
		sto.writeStorage(checkZeroF(extractF()), 0);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandCLRW() {
		sto.setZ(false);
		sto.setW(0);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	/*
	 * CLRWDT
	 */
	
	void commandCOMF() { 
		sto.setZ(false);
		int ergebnis = (sto.getDataStorage(extractF())^0xFF);
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
			sto.writeStorage(extractF(), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandDECF() {
		sto.setZ(false);
		if(extractD() == 0) {
			sto.setW(checkZeroF(extractF())-1);
		} else {
			sto.writeStorage(checkZeroF(extractF()), sto.getDataStorage(checkZeroF(extractF()))-1);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandDECFSZ() {
		boolean tempZ = sto.getZ();
		int ergebnis = sto.getDataStorage(checkZeroF(extractF()))-1;
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), sto.getDataStorage(checkZeroF(extractF()))-1);
		}
		sto.setZ(tempZ);
		if (ergebnis == 0) {
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandGOTO() {	
		boolean tempZ = sto.getZ();
		sto.setPC(((sto.getPCLath() & 0x18) << 8) | extractLongK());
		sto.setZ(tempZ);
		//Standardanweisungen
		sto.incTime();
		sto.incTime();
	}
	
	void commandINCF() {
		sto.setZ(false);
		if(extractD() == 0) {
			sto.setW(sto.getDataStorage(checkZeroF(extractF()))+1);
		} else {
			sto.writeStorage(checkZeroF(extractF()), sto.getDataStorage(checkZeroF(extractF()))+1);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandINCFSZ() {
		boolean zTemp = sto.getZ();
		int ergebnis = sto.getDataStorage(checkZeroF(extractF()))+1;
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), sto.getDataStorage(checkZeroF(extractF()))+1);
		}
		if ((ergebnis & 0xFF) == 0) {
			commandNOP();
		}
		sto.setZ(zTemp);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandIORLW() {
		sto.setZ(false);
		sto.setW( extractShortK() | sto.getW() );
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandIORWF() {
		sto.setZ(false);
		int ergebnis = ( sto.getDataStorage(checkZeroF(extractF())) | sto.getW() );
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandMOVF() {
		int ergebnis = sto.getDataStorage(checkZeroF(extractF()));
		//sto.setZ(false);
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		if(ergebnis != 0) {
			sto.setZ(false);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandMOVLW() {
		boolean tempZ = sto.getZ();
		sto.setW(extractShortK());
		sto.setZ(tempZ);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	
	void commandMOVWF() {
		boolean tempZ = sto.getZ();
		sto.writeStorage(extractF(), sto.getW());
		sto.setZ(tempZ);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandNOP() {
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	/*
	 * RETFIE
	 */
	
	void commandRETFIE() {
		sto.setPc(sto.popStack());
		sto.setGIE(true);
		//Standardanweisungen
		sto.incTime();
		sto.incTime();
	}	
	
	void commandRETLW() {
		boolean tempZ = sto.getZ();
		sto.setW(extractF());
		sto.setPc(sto.popStack());
		sto.setZ(tempZ);
		//Standardanweisungen
		sto.incTime();
		sto.incTime();
	}	
	
	void commandRETURN() {
		sto.setPc(sto.popStack());
		//Standardanweisungen
		sto.incTime();
		sto.incTime();		
	}	
	
	void commandRLF() {
		boolean zTemp = sto.getZ();
		int ergebnis= (sto.getDataStorage(checkZeroF(extractF())) << 1);
		if (sto.getC()) {
			ergebnis++;
		}
		if((ergebnis & 0B100000000) != 0 ) { // ist das rausgeschobene bit = 1?
			sto.setC(true);
		} else {
			sto.setC(false);
		}
		sto.writeStorage(checkZeroF(extractF()), (ergebnis & 0B11111111));
		sto.setZ(zTemp);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandRRF() {
		boolean zTemp = sto.getZ();
		boolean cMerker = sto.getC();
		System.out.println("der Wert an der Speicherstelle ist " + sto.getDataStorage(checkZeroF(extractF())) );
		System.out.println("Das 0bit ist " + (sto.getDataStorage(checkZeroF(extractF()))& 0B1));
		if((sto.getDataStorage(checkZeroF(extractF()))& 0B1) != 0 ) {
			sto.setC(true);
		} else {
			sto.setC(false);
		}
		int ergebnis= (sto.getDataStorage(checkZeroF(extractF())) >> 1);
		if (cMerker) {
			ergebnis = ergebnis | 0B10000000;
		}
		sto.writeStorage(checkZeroF(extractF()), (ergebnis & 0B11111111));
		sto.setZ(zTemp);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	/*
	 * SLEEP
	 */
	
	void commandSUBLW() {
		sto.setZ(false);
		sto.setW(extractShortK() + subtract(sto.getW()));
		sto.testAndSetDC(extractShortK(), subtract(sto.getW()));
		sto.testAndSetC(extractShortK(), subtract(sto.getW()));
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandSUBWF() {
		sto.setZ(false);
		int ergebnis = sto.getDataStorage(checkZeroF(extractF())) + subtract(sto.getW());
		sto.testAndSetDC(sto.getDataStorage(checkZeroF(extractF())), subtract(sto.getW()));
		sto.testAndSetC(sto.getDataStorage(checkZeroF(extractF())), subtract(sto.getW()));
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
			sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		if(sto.parseToByte(ergebnis) != 0){
			sto.setZ(false);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandSWAPF() {
		boolean tempZ = sto.getZ();
		int ergebnis = (((sto.getDataStorage(checkZeroF(extractF())) & 0B1111) << 4) | ((sto.getDataStorage(checkZeroF(extractF())) & 0B11110000) >> 4)  );
		if(extractD() == 0) {
			sto.setW(ergebnis);
		} else {
		sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		sto.setZ(tempZ);
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandXORLW() {
		sto.setZ(false);
		sto.setW(extractShortK() ^ sto.getW());
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}	
	
	void commandXORWF() {
		sto.setZ(false);
		int ergebnis = sto.getDataStorage(checkZeroF(extractF())) ^ sto.getW();
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
		sto.writeStorage(checkZeroF(extractF()), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.incTime();
	}
	

	
	/**
	 * Funktionen zum Extrahieren und Shiften der relevanten Befehlsteile.
	 */
	int extractF()
	{
		return (sto.getProgStorage(sto.getPC()) & 0x7F);
	}
	int extractD()
	{
		return ((sto.getProgStorage(sto.getPC()) & 0x80) >> 7);
	}
	int extractB()
	{
		return ((sto.getProgStorage(sto.getPC()) & 0x380) >> 7);
	}
	int extractShortK()
	{
		
		return ((sto.getProgStorage(sto.getPC()) & 0xFF));
	}
	int extractLongK()
	{
		return ((sto.getProgStorage(sto.getPC()) & 0x7FF));
	}

	int subtract(int value) {
		value = (value ^ 0B11111111);
		value++;
		return value;
	}
	
	void increasePC() {
		//PC ändern
		sto.setPC(sto.getPC()+1);
	}

	int checkZeroF(int value) {
		if(value != 0){
			return value;
		}
		else{
			value = sto.getDataStorage(0x04);
		return value;
		}
	}
}
