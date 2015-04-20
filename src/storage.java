
public class storage
{
	public logic log;
	public GUI gui;
	public control ctrl;
	
	public int[] progStorage = new int[1024];		//Array für 14 bit Programmspeicher
	public int[] dataStorage = new  int[256];		//Array für 8 bit Datenspeicher
	
	storage()
	{
		//testline
		//dataStorage[10] = 255;
	}
	
	/**
	 * Getters
	 */
	int getPC() {
		return dataStorage[0x82];
	}
	
	/**
	 * Setters
	 */
	
	void setPC(int value) {
		dataStorage[0x82] = value;
	}
	
	void setPCLath(int value) {
		System.out.println(dataStorage[0x0A]);
		System.out.println(dataStorage[0x0A] & 00011111);
		System.out.println((value << 5));
		
		dataStorage[0x0A] = (dataStorage[0x0A] & 00011111) | (value << 5);
		
	}
	
	/**
	 * Other methods
	 */
	
	void increasePC() {
		dataStorage[0x82]++;
	}
}
