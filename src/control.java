import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.JFileChooser;

public class control 
{
	/**
	 * Variables
	 */
	//Prüfvar ob ein Programm geladen ist
	boolean isLoad = false;
	//Liste um Befehle einzulesen
	public ArrayList<String> arrayL = new ArrayList<String>();
	//Dieses Array bildet den Programmspeicher des Pic ab.
	
	public GUI gui;
	public static storage sto;
	public static logic log;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		/**
		 * Erzeugen des controllers
		 */
		 final control ctrl = new control();
		/**
		 * Erzeugen des storage
		 */
		 storage createsto = new storage();
		 ctrl.sto = createsto;
		/**
		* Erzeugen der Pic Logik
		*/
		logic createlog = new logic();
		ctrl.log = createlog;
		ctrl.log.setStorage(ctrl.sto);
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
					ctrl.gui = frame;
					frame.ctrl = ctrl; //GUI->controller Verbindung
					ctrl.log.gui = frame;
					frame.sto = sto;
					frame.log = log;
					frame.initializeStorage();
					frame.initializeSpecialRegister();
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
		int linecounter = 0; //Zählt Zeilen mit Programmcode
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
				arrayL.add(zeile); 
				if(zeile.charAt(0) != ' ')//linecounter wird nur erhöht, wenn die Zeile Code enthält.
					linecounter++;
			}
			in.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		/**
		 * Programmtext in Tabelle schreiben
		 */
		Object[][] data = new Object[arrayL.size()][2];
		
		for (int i=0; i < arrayL.size(); i++)
		{
			data[i][1]= arrayL.get(i);
		}
		//neue Tabelle erstellen und damit die alte ersetzen
		JTable table_2 = new JTable(data, gui.columnNames);
		gui.scrollPane_source_code.setViewportView(table_2);
		table_2.setBounds(0, 0, 100, 100);
	
		
		if(linecounter <= (1024))
		{
			/**
			 * Programmspeicher löschen
			 */
			for(int i = 0; i < (1024); i++)
				sto.progStorage[i]=0;
			/**
			 * Einlesen des Programms
			 */
			int j = 0; //Zähler nur für Codezeilen
			for(int i=0; i < arrayL.size(); i++)//Zähler für alle Zeilen
			{
				
				String zeile = arrayL.get(i);
				if(zeile.charAt(0) != ' ') //Codezeile?
				{
					String comand = zeile.substring(5, 9); //Programmcode extrahieren
					sto.progStorage[j] = Integer.parseInt(comand, 16); //Hexzahl in Int parsen & Programmspeicher füllen
					//System.out.println(sto.progStorage[j]); //Test
					j++;//Codezähler erhöhen
				}
				
			}
			isLoad = true; //Angeben, dass ein Programm geladen wurde.
			
			log.run();
		}
		else
		{
			gui.showError(1); //
		}
		
	}

	


}
