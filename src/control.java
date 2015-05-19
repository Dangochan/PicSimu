import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumn;

public class control {
	private static control instance;
	private storage sto = storage.getInstance();
	private StepStack stepStack = StepStack.getInstance();
	private GUI gui;
	/**
	 * Variables
	 */
	// Prüfvar ob ein Programm geladen ist
	public boolean isLoad = false;
	// Liste um Befehle einzulesen
	public ArrayList<String> arrayL = new ArrayList<String>(); // Dieses Array
																// bildet den
																// Programmspeicher
																// des Pic ab.
	public MyThread myThread;
	public static MyThread startThread;

	public Object[][] data;
	public JTable table_source_code;
	public boolean[] isSourcecode;
	public int aktuelleZeile = 0;

	private int linecounter;

	public int getLinecounter() {
		return linecounter;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/**
		 * Erstellen der GUI
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					/**
					 * state den Thread
					 */
					startThread = MyThread.getInstance();
					startThread.start();
					GUI gui = GUI.getInstance();
					gui.setVisible(true);
					gui.initializeStorage();
					gui.initializeSpecialRegister();
					gui.initializePinsARegister();
					gui.initializePinsBRegister();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Control Constructor
	 */
	private control() {
	}

	public static synchronized control getInstance() {
		if (control.instance == null) {
			control.instance = new control();
			instance.sto = storage.getInstance();
			instance.gui = GUI.getInstance();
		}
		return control.instance;
	}

	public void initializeNewFile() {
		/*
		 * Hardwareconnection
		 */
		boolean success = Hardwareconnection.initializeConnection("COM2");
		Thread hardware = new Thread(new HardwareThread());
		hardware.start();
		if (success == true) { // Feedback, ob Verbindungsaufbau erfolgreich war
			System.out.println("Erfolg!!!");
		} else {
			System.out.println("Kein Erfolg");
		}
		/*
		 * File laden
		 */
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		File file = fc.getSelectedFile();
		sto.initializeStorage();
		sto.deleteProgramStorage();
		arrayL.clear();
		linecounter = 0; // Zählt Zeilen mit Programmcode
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			readFileInArray(in);
			countProgramLines(in);
			in.close();
			writeSourceCodeArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createSourceCodeTable();
		/*
		 * Prüfen, ob Assemblercode zu lange für den Programmspeicher des
		 * u-Controllers
		 */
		checkAssemblyLenght();
		stepStack.resetStepStack();
	}

	private void createSourceCodeTable() {
		/**
		 * Programmtext in Tabelle schreiben
		 */
		data = new Object[arrayL.size()][2];

		for (int i = 0; i < arrayL.size(); i++) {
			data[i][1] = arrayL.get(i);
		}

		// neue Tabelle erstellen und damit die alte ersetzen
		table_source_code = new JTable(data, gui.columnNames) {
			// Editierbarkeit für zweite Spalte ausschalten
			@Override
			public boolean isCellEditable(int row, int column) {
				switch (column) {
				case 0:
					return true;
				default:
					return false;
				}
			}

			// Checkbox in erste Spalte
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return Boolean.class;
				default:
					return String.class;
				}
			}
		};
		gui.scrollPane_source_code.setViewportView(table_source_code);
		// Spaltenbreite von table_sourcecode setzen
		table_source_code.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn col_bp = table_source_code.getColumnModel().getColumn(0);
		col_bp.setPreferredWidth(20);
		TableColumn col_prog = table_source_code.getColumnModel().getColumn(1);
		col_prog.setMinWidth(500);
		table_source_code.setBounds(0, 0, 100, 100);
	}

	private void readFileInArray(BufferedReader in) throws IOException {
		String zeile = null;
		while ((zeile = in.readLine()) != null) {
			arrayL.add(zeile);
		}
	}

	private void countProgramLines(BufferedReader in) throws IOException {
		String zeile = null;
		while ((zeile = in.readLine()) != null) {
			if (zeile.charAt(0) != ' ') {// linecounter wird nur erhöht, wenn
											// die Zeile Code enthält.
				linecounter++;
			}
		}
	}

	private void writeSourceCodeArray() {
		isSourcecode = new boolean[arrayL.size()];
		for (int i = 0; i < arrayL.size(); i++) {
			if (arrayL.get(i).charAt(0) != ' ') {
				isSourcecode[i] = true;
			} else {
				isSourcecode[i] = false;
			}
		}
	}

	private void checkAssemblyLenght() {
		if (linecounter <= (sto.getDataStorage().length)) {
			/**
			 * Programmspeicher löschen
			 */
			for (int i = 0; i < (1024); i++) {
				sto.setProgStorage(i, 0);
			}
			/**
			 * Einlesen des Programms
			 */
			int j = 0; // Zähler nur für Codezeilen
			for (int i = 0; i < arrayL.size(); i++) {// Zähler für alle Zeilen
				String zeile = arrayL.get(i);
				if (zeile.charAt(0) != ' ') {// Codezeile?
					String comand = zeile.substring(5, 9); // Programmcode
															// extrahieren
					sto.setProgStorage(j, Integer.parseInt(comand, 16));// Hexzahl
																		// in
																		// Int
																		// parsen
																		// &
																		// Programmspeicher
																		// füllen
					j++;// Codezähler erhöhen
				}
			}
			isLoad = true; // Angeben, dass ein Programm geladen wurde.
		} else {
			gui.showError(1); //
		}
	}

	/**
	 * Zeile zum Markieren auswählen
	 */
	public void selectRow() {
		int sc_pc = 0;
		aktuelleZeile = 0;
		// durchläuft isSourcecode
		for (int i = 0; i < arrayL.size(); i++) {
			if (isSourcecode[i] == true) {
				sc_pc++;
			}
			if (sc_pc == sto.getPC() + 1) {
				aktuelleZeile = i;
				break;
			}
		}
		table_source_code.changeSelection(aktuelleZeile, 1, false, false);
	}
}

class HardwareThread implements Runnable { // Thread für Hardwareansteuerung
	storage sto = storage.getInstance();
	GUI gui = GUI.getInstance();

	@Override
	public void run() {
		while (true) {
			try {
				Hardwareconnection.sendData();
				ArrayList<Integer> answer = Hardwareconnection.readData();
				sto.dataStorage[5] = answer.get(0);
				sto.dataStorage[6] = answer.get(1);
				gui.updatePinsA();
				gui.updatePinsB();
				gui.lblComConnection.setText("true");
			} catch (Exception e1) {
				gui.lblComConnection.setText("false");
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// Do nothing
			}
		}
	}
}