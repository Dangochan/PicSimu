import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;


public class control 
{
	/**
	 * Variables
	 */
	ArrayList<String> array = new ArrayList<String>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		/**
		 * Erzeugen des controllers
		 */
		control ctrl = new control();
		
		/**
		 * Erstellen der GUI 
		 */
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					GUI frame = new GUI();
					frame.setVisible(true);

					frame.ctrl = ctrl;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Control Constructor
	 */
	public control ()
	{
		
	}
	
	public void readFile()
	{
		int linecounter = 0;
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		File file = fc.getSelectedFile();
		//System.out.println(file.getPath()); testweise pfad ausgeben
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String zeile = null;
			while ((zeile = in.readLine()) != null) 
			{
				//System.out.println("Gelesene Zeile: " + zeile); zeile ausgeben
				array.add(zeile); 
				linecounter++;
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		for(int i=0; i < array.size(); i++)
			System.out.println( array.get( i ) ); 
		
	}


}
