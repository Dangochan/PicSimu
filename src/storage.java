
public class storage
{
	public logic log;
	public GUI gui;
	public control ctrl;
	
	public int[] progStorage = new int[1024];		//Array für 14 bit Programmspeicher
	public int[] dataStorage = new  int[256];		//Array für 8 bit Datenspeicher
	public int pc = 0; 
	public int[] stack = new int[8];
	public int stackptr = 0;
	
	storage()
	{
		//testline
		//dataStorage[10] = 255;
	}
	
	/**
	 * Writermethode
	 */
	void writeStorage(int destination, int value) {
		if((destination & 0x7F) <= 0x0B) {
			switch (destination & 0x7F) {
			case 0x00://INDF
				
				break;
			case 0x01://TMR0
				
				break;
			case 0x02://PCL - Programmzähler
					setPC(value);
				break;
			case 0x03://STATUS
	
				break;
			case 0x04://FSR
				
				break;
			case 0x05://PORTA
				
				break;
			case 0x06://PORTB
				
				break;
			case 0x07:
				
				break;
			case 0x08://EEDATA
				
				break;
			case 0x09://EEADR
				
				break;
			case 0x0A://PCLATH
					setPCLath(value);
				break;
			case 0x0B://INTCON
				
				break;
			default:
				break;
			}
		}
		else
		{
			dataStorage[destination & (getRP0() << 7)] = value;
		}
	}
	
	/**
	 * Getters
	 * Sonstige
	 */
	int getPC() {
		return pc;
	}
	
	/**
	 * Getters
	 * Byte
	 */
	int getPCL() {
		return dataStorage[0x02];
	}
	
	int getStatus() {
		return dataStorage[0x03];
	}
	
	/**
	 * Getters
	 * Bit
	 */
	int getRP0() {
		return ((getStatus() & 0x20) >> 5);
	}
	
	
	/**
	 * Setters
	 */
	
	void setPC(int value) {
		pc= (value & 0x7FF) | pc & 0xC00;
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
	
}
