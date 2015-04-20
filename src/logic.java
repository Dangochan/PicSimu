
public class logic
{
	public storage sto;
	public GUI gui;
	public control ctrl;
	
	
	logic()
	{
		
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
	}
	
	public void executeCommand()
	{
		//Integer, für Programmcode
		//0000 0000  0000 0000  0000 0000  0000 0000
		
		//ohne Maske
		switch (sto.progStorage[sto.getPC()])
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
		switch (sto.progStorage[sto.getPC()] & 0B11111110011111)
		{
			case 0B0://No Operation
				System.out.println("No Operation");
				break;	
			default:
				
				break;
		} 
		//Maske
		//        --0--         0011 1111  1fff ffff
		//        --0--         0011 1111  1xxx xxxx
		switch ((sto.progStorage[sto.getPC()] & 0B11111110000000) >> 7)
		{
			case 0B11://Clear f
				System.out.println("Clear f");
				break;	
			case 0B10://Clear W
				System.out.println("Clear W");
				break;
			case 0B1://Move W to f	
				System.out.println("Move W to f	");
				break;	

				
			default:
				
				break;
		} 
		//Maske
		//        --0--         0011 1111  dfff ffff
		switch ((sto.progStorage[sto.getPC()] & 0B11111100000000) >> 8)
		{
			case 0B111://Add W and f
				System.out.println("add w and f");
				break;
			case 0B101://AND W with f
				System.out.println("AND W with f");
				break;
			case 0B1001://Complement f
				System.out.println("Complement f");
				break;
			case 0B11://Decrement f
				System.out.println("Decrement f");
				break;	
			case 0B1011://Decrement f, Skip if 0
				System.out.println("Decrement f, Skip if 0");
				break;	
			case 0B1010://Increment f
				System.out.println("Increment f");
				break;
			case 0B1111://Increment f, Skip if 0
				System.out.println("Increment f, Skip if 0");
				break;	
			case 0B100://Inclusive OR W with f
				System.out.println("Inclusive OR W with f");
				break;		
			case 0B1000://Move f
				System.out.println("Move f");
				break;					
			case 0B1101://Rotate Left f through Carry
				System.out.println("Rotate Left f through Carry");
				break;	
			case 0B1100://Rotate Right f through Carry
				System.out.println("Rotate Right f through Carry");
				break;
			case 0B10://Subtract W from f
				System.out.println("Subtract W from f");
				break;	
			case 0B1110://Swap nibbles in f
				System.out.println("Swap nibbles in f");
				break;	
			case 0B110://Exclusive OR W with f
				System.out.println("Exclusive OR W with f");
				break;	
			case 0B111001://AND literal with W
				System.out.println("AND literal with W");
				break;
			case 0B111000://Inclusive OR literal with W
				System.out.println("Inclusive OR literal with W");
				break;
			case 0B111010://Exclusive OR literal with W
				System.out.println("Exclusive OR literal with W");
				break;
			default:
				break;
		} 
		//Maske
		//      --0--         0011 111x  kkkk kkkk
		switch ((sto.progStorage[sto.getPC()] & 0B11110000000000) >> 9)
		{
			case 0B11111://Add literal and W
				System.out.println("Add literal and W");
				break;
			case 0B11110://Subtract W from literal
				System.out.println("Subtract W from literal");
				break;
				
				
			default:
				break;
		} 
		//Maske
		//      --0--         0011 11bb  bfff ffff
		//      --0--         0011 11xx  kkkk kkkk
		switch ((sto.progStorage[sto.getPC()] & 0B11110000000000) >> 10)
		{
			case 0B100://Bit Clear f
				System.out.println("Bit Clear f");
				break;
			case 0B101://Bit Set f
				System.out.println("Bit Set f");
				break;
			case 0B110://Bit Test f, Skip if Clear
				System.out.println("Bit Test f, Skip if Clear");
				break;
			case 0B111://Bit Test f, Skip if Set
				System.out.println("Bit Test f, Skip if Set");
				break;
			case 0B1100://Move literal to W
				System.out.println("Move literal to W");
				break;	
			case 0B1101://Return with literal in W
				System.out.println("Return with literal in W");
				break;	
			default:
				break;
		} 
		//Maske
		//      --0--         0011 1kkk  kkkk kkkk
		switch ((sto.progStorage[sto.getPC()] & 0B11100000000000) >> 11)
		{
			case 0B100://Call subroutine
				commandCall();
				System.out.println("Call subroutine");
				break;
			case 0B101://Go to address
				commandGoTo();
				System.out.println("Go to address");
				break;

			default:
				break;
		}
	}
	
	/**
	 * Befehlsfunktionen
	 */
	void commandGoTo() {	
		sto.setPC(((sto.getPCL() & 0x18) << 8) | extractLongK());
		
	}
	
	void commandCall() {
		sto.pushStack(sto.getPC() + 1);
		sto.setPC(((sto.getPCL() & 0x18) << 8) | extractLongK());
	}
	
	
	/**
	 * Funktionen zum Extrahieren und Shiften der relevanten Befehlsteile.
	 */
	int extractF()
	{
		return (sto.progStorage[sto.getPC()] & 0x7F);
	}
	int extractD()
	{
		return ((sto.progStorage[sto.getPC()] & 0x80) >> 7);
	}
	int extractB()
	{
		return ((sto.progStorage[sto.getPC()] & 0x380) >> 7);
	}
	int extractShortK()
	{
		
		return ((sto.progStorage[sto.getPC()] & 0xFF));
	}
	int extractLongK()
	{
		return ((sto.progStorage[sto.getPC()] & 0x7FF));
	}

	void increasePC() {
		sto.writeStorage(0x02, sto.getPC()+1);
	}
<<<<<<< HEAD

=======
	
	
>>>>>>> 49b009d4858d986ff6e130f8abec3d1cd4032257
	
}
