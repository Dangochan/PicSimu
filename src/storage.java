
public class storage
{
	public int[] progStorage = new int[1024];	//Array f�r 14 bit Programmspeicher
	public byte[] dataStorage = new  byte[256];		//Array f�r 8 bit Datenspeicher
	public int pc = 0; 								//Programmz�hlert
	
	storage()
	{
		
	}
	
	public void executeCommand()
	{
		//ADDWF 00 0111 dfff ffff
		if(progStorage[pc] >= 1792 && progStorage[pc] <= 2047)	
		{
			
		}
		
				
	}
	
	
}
