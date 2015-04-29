
public class logic
{
	private static logic instance;
	
	private storage sto;
	private GUI gui;
	private control ctrl;
	
	public void setGUI(GUI aGUI) {
		gui = aGUI;
	}
	
	public void setCTRL(control aCTRL) {
		ctrl = aCTRL;
	}
	
	private logic()
	{
		
	}
	public static synchronized logic getInstance () {
		if (logic.instance == null) {
			logic.instance = new logic ();
			instance.sto = storage.getInstance();
	    }
	    return logic.instance;
	}
	public void run()
	{
		/*
		System.out.println("Start Program");
		while(sto.progStorage[sto.pc]!=0)
		{
			executeCommand();
			sto.pc++;
		}
		*/
		executeCommand();
		
		gui.updateStorage();
		gui.updateSpecialRegister();
		ctrl.selectRow();
		System.out.println("PC " + sto.getPc());
	}
	
	public void executeCommand()
	{
		
		//Integer, für Programmcode
		//0000 0000  0000 0000  0000 0000  0000 0000
		
		//ohne Maske
		switch (sto.getProgStorage(sto.getPC()))
		{
			case 0B00000001100100://Clear Watchdog Timer
				System.out.println("Clear Watchdog Timer");
				break;
			case 0B00000000001001://Return from interrupt
				System.out.println("Return from interrupt");
				break;	
			case 0B00000000001000://Return from Subroutine	
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
										System.out.println("AND W with f");
										break;
									case 0B100100000000://Complement f
										commandCOMF();
										System.out.println("Complement f");
										break;
									case 0B1100000000://Decrement f
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
										System.out.println("Increment f, Skip if 0");
										break;	
									case 0B10000000000://Inclusive OR W with f
										System.out.println("Inclusive OR W with f");
										break;		
									case 0B100000000000://Move f
										System.out.println("Move f");
										break;					
									case 0B110100000000://Rotate Left f through Carry
										System.out.println("Rotate Left f through Carry");
										break;	
									case 0B110000000000://Rotate Right f through Carry
										System.out.println("Rotate Right f through Carry");
										break;
									case 0B1000000000://Subtract W from f
										commandSUBWF();
										System.out.println("Subtract W from f");
										break;	
									case 0B111000000000://Swap nibbles in f
										System.out.println("Swap nibbles in f");
										break;	
									case 0B11000000000://Exclusive OR W with f
										System.out.println("Exclusive OR W with f");
										break;	
									case 0B11100100000000://AND literal with W
										System.out.println("AND literal with W");
										break;
									case 0B11100000000000://Inclusive OR literal with W
										System.out.println("Inclusive OR literal with W");
										break;
									case 0B11101000000000://Exclusive OR literal with W
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
	
	/* executeCommand alt
	 public void executeCommand()
	{
		
		//Integer, für Programmcode
		//0000 0000  0000 0000  0000 0000  0000 0000
		
		//ohne Maske
		switch (sto.getProgStorage(sto.getPC()))
		{
			case 0B00000001100100://Clear Watchdog Timer
				System.out.println("Clear Watchdog Timer");
				break;
			case 0B00000000001001://Return from interrupt
				System.out.println("Return from interrupt");
				break;	
			case 0B00000000001000://Return from Subroutine	
				System.out.println("Return from Subroutine");
				break;
			case 0B00000001100011://Go into standby mode	
				System.out.println("Go into standby mode");
				break;
			default:
				break;
		}
		//Maske
		//        --0--         0000 0000  0xx0 0000
		switch (sto.getProgStorage(sto.getPC()) & 0B11111110011111)
		{
			case 0B0://No Operation
				commandNOP();
				System.out.println("No Operation");
				break;	
			default:
				
				break;
		} 
		//Maske
		//        --0--         0011 1111  1fff ffff
		//        --0--         0011 1111  1xxx xxxx
		switch ((sto.getProgStorage(sto.getPC()) & 0B11111110000000))
		{
			case 0B110000000://Clear f
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
				
				break;
		} 
		//Maske
		//        --0--         0011 1111  dfff ffff
		switch ((sto.getProgStorage(sto.getPC()) & 0B11111100000000))
		{
			case 0B11100000000://Add W and f
				System.out.println("add w and f");
				break;
			case 0B10100000000://AND W with f
				System.out.println("AND W with f");
				break;
			case 0B100100000000://Complement f
				System.out.println("Complement f");
				break;
			case 0B1100000000://Decrement f
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
				System.out.println("Increment f, Skip if 0");
				break;	
			case 0B10000000000://Inclusive OR W with f
				System.out.println("Inclusive OR W with f");
				break;		
			case 0B100000000000://Move f
				System.out.println("Move f");
				break;					
			case 0B110100000000://Rotate Left f through Carry
				System.out.println("Rotate Left f through Carry");
				break;	
			case 0B110000000000://Rotate Right f through Carry
				System.out.println("Rotate Right f through Carry");
				break;
			case 0B1000000000://Subtract W from f
				System.out.println("Subtract W from f");
				break;	
			case 0B111000000000://Swap nibbles in f
				System.out.println("Swap nibbles in f");
				break;	
			case 0B11000000000://Exclusive OR W with f
				System.out.println("Exclusive OR W with f");
				break;	
			case 0B11100100000000://AND literal with W
				System.out.println("AND literal with W");
				break;
			case 0B11100000000000://Inclusive OR literal with W
				System.out.println("Inclusive OR literal with W");
				break;
			case 0B11101000000000://Exclusive OR literal with W
				System.out.println("Exclusive OR literal with W");
				break;
			default:
				break;
		} 
		//Maske
		//      --0--         0011 111x  kkkk kkkk
		switch ((sto.getProgStorage(sto.getPC()) & 0B11111000000000))
		{
			case 0B11111000000000://Add literal and W
				commandADDLW();
				System.out.println("Add literal and W");
				break;
			case 0B11110000000000://Subtract W from literal
				System.out.println("Subtract W from literal");
				break;
				
				
			default:
				break;
		} 
		//Maske
		//      --0--         0011 11bb  bfff ffff
		//      --0--         0011 11xx  kkkk kkkk
		switch ((sto.getProgStorage(sto.getPC()) & 0B11110000000000))
		{
			case 0B1000000000000://Bit Clear f
				System.out.println("Bit Clear f");
				break;
			case 0B1010000000000://Bit Set f
				System.out.println("Bit Set f");
				break;
			case 0B1100000000000://Bit Test f, Skip if Clear
				System.out.println("Bit Test f, Skip if Clear");
				break;
			case 0B1110000000000://Bit Test f, Skip if Set
				System.out.println("Bit Test f, Skip if Set");
				break;
			case 0B11000000000000://Move literal to W
				commandMOVLW();
				System.out.println("Move literal to W");
				break;	
			case 0B11010000000000://Return with literal in W
				System.out.println("Return with literal in W");
				break;	
			default:
				break;
		} 
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
	}
	 */
	
	/**
	 * Befehlsfunktionen
	 */
	void commandGOTO() {	
		sto.setPC(((sto.getPCL() & 0x18) << 8) | extractLongK());
		//Standardanweisungen
		sto.setZ(false);
	}
	
	void commandCLRW() {
		sto.setW(0);
		sto.setZ(true);
		//Standardanweisungen
		increasePC();
	}
	
	void commandCALL() {
		sto.pushStack(sto.getPC() + 1);
		sto.setPC(((sto.getPCL() & 0x18) << 8) | extractLongK());
		//Standardanweisungen
		sto.setZ(false);
	}
	
	void commandMOVLW() {
		sto.setW(extractShortK());
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandMOVWF() {
		sto.writeStorage(extractF(), sto.getW());
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandINCF() {
		sto.setZ(false);
		if(extractD() == 0) {
			sto.setW(extractF()+1);
		}
		else {
			sto.writeStorage(extractF(), sto.getDataStorage(extractF())+1);
		}
		//Standardanweisungen
		increasePC();
	}
	
	void commandADDLW() {
		sto.setZ(false);
		sto.setW(sto.getW()+extractShortK());
		//Standardanweisungen
		increasePC();
	}
	

	void commandDECFSZ() {
		sto.setZ(false);
		int ergebnis = sto.getDataStorage(extractF())-1;
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
			sto.writeStorage(extractF(), sto.getDataStorage(extractF())-1);
			System.out.println(ergebnis);
		}
		if (ergebnis == 0) {
			System.out.println("execute nop");
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandNOP() {
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandRETLW() {
		sto.setW(extractF());
		sto.setPc(sto.popStack());
		//Standardanweisungen
		sto.setZ(false);
	}
	
	void commandADDWF() { //TODO: C, DC Flags
		int ergebnis;
		if(extractF() != 0)
			ergebnis = sto.getW() + extractF();
		else
			ergebnis = sto.getW() + sto.getDataStorage(sto.getDataStorage(0x04));
		
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
			sto.writeStorage(extractF(), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandCOMF() { 
		int ergebnis = (sto.getDataStorage(extractF())^0B11111111);
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
			sto.writeStorage(extractF(), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandSUBWF() { //TODO: C, DC Flags
		int ergebnis;
		if(extractF() != 0)
			ergebnis = extractF() + subtract(sto.getW());
		else
			ergebnis = sto.getDataStorage(sto.getDataStorage(0x04)) + subtract(sto.getW());
		
		if(extractD() == 0) {
			sto.setW(ergebnis);
		}
		else {
			sto.writeStorage(extractF(), ergebnis);
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandBSF() {
		int mask = (1 << extractB()); 
		sto.writeStorage(extractF(), (sto.getDataStorage(extractF()) | mask));
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandBCF() {
		int mask = (1 << extractB());
		mask = mask ^ 0B11111111;
		sto.writeStorage(extractF(), (sto.getDataStorage(extractF()) & mask));
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandBTFSC() {
		int mask = (1 << extractB());
		if(sto.getDataStorage(extractF() & mask) == 0) {
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
	}
	
	void commandBTFSS() {
		int mask = (1 << extractB());
		if(sto.getDataStorage(extractF() & mask) != 0) {
			commandNOP();
		}
		//Standardanweisungen
		increasePC();
		sto.setZ(false);
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
		sto.writeStorage(0x02, sto.getPC()+1);
	}

	
}
