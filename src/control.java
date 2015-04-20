import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumn;

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
	public Object[][] data;
	public JTable table_source_code;
	public boolean[] isSourcecode;
	
	private int markierung;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		/**
		 * Erzeugen des controllers
		 */
//		 final control ctrl = new control();
		/**
		 * Erzeugen des storage
		 */
//		 storage createsto = new storage();
//		 ctrl.sto = createsto;
		/**
		* Erzeugen der Pic Logik
		*/
//		logic createlog = new logic();
//		ctrl.log = createlog;
//		ctrl.log.setStorage(ctrl.sto);
		/**
		 * Erstellen der GUI 
		 */
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					/*
					GUI frame = new GUI();
					frame.setVisible(true);
					ctrl.gui = frame;
					frame.ctrl = ctrl; //GUI->controller Verbindung
					ctrl.log.gui = frame;
					frame.sto = sto;
					frame.log = log;
					frame.initializeStorage();
					frame.initializeSpecialRegister();
					*/
					
					final control ctrl = new control();
					storage newsto = new storage(); 
					logic newlog = new logic();
					GUI newgui = new GUI();
	
					newgui.setVisible(true);
					/**
					 * Spawn Connections between Objects
					 */
					ctrl.sto = newsto;
					ctrl.log = newlog;
					ctrl.gui = newgui;
					
					ctrl.log.sto = newsto;
					ctrl.log.gui = newgui;
					ctrl.log.ctrl = ctrl;
					
					ctrl.sto.log = newlog;
					ctrl.sto.gui = newgui;
					ctrl.sto.ctrl = ctrl;
					
					ctrl.gui.sto = newsto;
					ctrl.gui.log = newlog;
					ctrl.gui.ctrl = ctrl;
				
					newgui.initializeStorage();
					newgui.initializeSpecialRegister();
					
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
			isSourcecode = new boolean[arrayL.size()];
			for (int i = 0; i < arrayL.size();i++)
			{
				if (arrayL.get(i).charAt(0) != ' '){
					isSourcecode[i] = true;
				}
				else{
					isSourcecode[i] = false;
				}
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		/**
		 * Programmtext in Tabelle schreiben
		 */
		data = new Object[arrayL.size()][2];
		
		for (int i=0; i < arrayL.size(); i++)
		{
			data[i][1]= arrayL.get(i);
		}
		
		
		//neue Tabelle erstellen und damit die alte ersetzen
		table_source_code = new JTable(data, gui.columnNames);
		gui.scrollPane_source_code.setViewportView(table_source_code);
		//Spaltenbreite von table_sourcecode setzen
		table_source_code.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn col_bp = table_source_code.getColumnModel().getColumn(0);
		col_bp.setPreferredWidth(20);
		TableColumn col_prog = table_source_code.getColumnModel().getColumn(1);
		col_prog.setMinWidth(500);
		table_source_code.setBounds(0, 0, 100, 100);
		
		
		
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
			
			
		}
		else
		{
			gui.showError(1); //
		}
		
	}
	

	/**
	 * Zeile zum Markieren auswählen
	 */
	public void selectRow(){
		int sc_pc = 0;
		markierung = 0;
		//durchläuft isSourcecode
		for (int i = 0; i<arrayL.size();i++)
		{
			if(isSourcecode[i]==true)
				sc_pc++;
			if (sc_pc == sto.getPC())
			{
				markierung = i;
				break;
			}
		}
		table_source_code.changeSelection(markierung, 1, false, false);
	}
	

}
