
public class storage
{
	public int[] progStorage = new int[1024];	//Array für 14 bit Programmspeicher
	public byte[] dataStorage = new  byte[256];		//Array für 8 bit Datenspeicher
	public int pc = 0; 								//Programmzählert
	
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
