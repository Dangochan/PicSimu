import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Event;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.DefaultComboBoxModel;

//TODO tris register i/o anzeigen  (pins durch klick nur bei i invertierbar????)


public class GUI extends JFrame {
	//TODO gui idiotensicher machen
	//TODO Zeile umfärben statt nur markieren
	//TODO zahlenausgaben auf zwei nachkommastellen runden
	private static GUI instance;
	private JPanel contentPane;

	public control ctrl;
	private storage sto;
	private logic log;
	private MyThread startThread;
	private StepStack stepStack;
	
	public JTable table_source_code_temp;
	public JScrollPane scrollPane_source_code;
	public JScrollPane scrollPane_storage;
	
	public JLabel lblW;
	public JLabel lblPC;
	public JLabel lblZ;
	public JLabel lblC;
	public JLabel lblDC;
	public JLabel lblStackPtr;
	public JLabel lblTime;
	public JLabel lblDeltaTime;
	public JLabel lblexternStautus;
	
	DefaultTableModel model_storage = new DefaultTableModel(); 
	DefaultTableModel model_special_register = new DefaultTableModel(); 
	DefaultTableModel model_source_code = new DefaultTableModel();
	DefaultTableModel model_pinsA = new DefaultTableModel();
	DefaultTableModel model_pinsB = new DefaultTableModel();
	
	public String[] columnNames = {"BP","Program"};	
	public Object[][] tempData = new Object[1][2];
	
	private JTable table_storage;
	private JTable table_special_register;
	private JButton btn_startstop;
	private JTable table_pinsA;
	private JScrollPane scrollPane_pinsA;
	private JTable table_pinsB;
	private JScrollPane scrollPane_pinsB;
	private JButton btnReset;
	private JTextField textFieldFrequenz;
	private JButton btnExternalClock;
	private JTextField textField;
	 JButton btnUndo;

	/**
	 * Create the frame.
	 */
	private GUI() {
		
		
		/**
		 * Spawn Layout
		 */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 927, 563);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/**
		 * Spawn Load File Button
 		 */
		JButton btn_open_file = new JButton("Open File");
		btn_open_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ctrl.initializeNewFile();		// Datei einlesen
				updateProgress();
			}
		});
		btn_open_file.setBounds(10, 11, 89, 23);
		contentPane.add(btn_open_file);
		
		/**
		 * Spawn Step Button
		 */
		JButton btn_step = new JButton("Step");
		btn_step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				log.step();
			}
		});
		btn_step.setBounds(109, 11, 89, 23);
		contentPane.add(btn_step);
		
		/**
		 * Spawn Start Button
		 */
		btn_startstop = new JButton("Start / Stop");
		btn_startstop.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if(!startThread.isRunning) {
					log.step(); //damit nach BP die Zeile "übersprungen" wird
				}
				startThread.isRunning = !startThread.isRunning;
			}
		});
		btn_startstop.setBounds(208, 11, 120, 23);
		contentPane.add(btn_startstop);
		
		/**
		 * Spawn Help Button
		 */
		
		JButton btn_help = new JButton("Help");
		btn_help.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String current = System.getProperty("user.dir");
				try{
					Desktop.getDesktop().open(new File (current + "/help.pdf"));
				}
				catch(Exception ex){
					
				}
			}
		});
		btn_help.setBounds(659, 11, 89, 23);
		contentPane.add(btn_help);
		
		/**
		 *  Spawn Source Code Table
		 */
		scrollPane_source_code = new JScrollPane();
		scrollPane_source_code.setBounds(220, 177, 540, 342);
		contentPane.add(scrollPane_source_code);
		
		table_source_code_temp = new JTable(tempData, columnNames);
		scrollPane_source_code.setViewportView(table_source_code_temp);
		//Spaltenbreite von table_sourcecode_temp setzen
		table_source_code_temp.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn col_bp = table_source_code_temp.getColumnModel().getColumn(0);
		col_bp.setPreferredWidth(20);
		TableColumn col_prog = table_source_code_temp.getColumnModel().getColumn(1);
		col_prog.setMinWidth(515);
		table_source_code_temp.setBounds(0, 0, 100, 100);
		
		/**
		 * Spawn Storage Table
		 */
		scrollPane_storage = new JScrollPane();
		scrollPane_storage.setBounds(10, 45, 200, 250);
		contentPane.add(scrollPane_storage);
		
		table_storage = new JTable(model_storage);
		scrollPane_storage.setViewportView(table_storage);
		
		/**
		 * Spawn Special Register Table
		 */
		JScrollPane scrollPane_special_register = new JScrollPane();
		scrollPane_special_register.setBounds(773, 45, 128, 250);
		contentPane.add(scrollPane_special_register);
		
		table_special_register = new JTable(model_special_register);
		scrollPane_special_register.setViewportView(table_special_register);
		
		/**
		 * Spawn PinA Table
		 */
		scrollPane_pinsA = new JScrollPane();
		scrollPane_pinsA.setBounds(220, 45, 228, 55);
		contentPane.add(scrollPane_pinsA);
		
		table_pinsA = new JTable(model_pinsA){
			@Override	
			public boolean isCellEditable(int row, int column){
				if (row ==0){
					return false;
				}
				else {
					switch (column){
					case 1: case 2: case 3: case 4: case 5: case 6: case 7: return true;
					default: return false;
					}
				}
			}
		};
		table_pinsA.addMouseListener(new MouseAdapter() {
			//@Override
			public void mouseClicked(MouseEvent e){
				int col = 8-table_pinsA.columnAtPoint(e.getPoint());
				sto.changePortBit(0, col);
				updatePinsA();
				updateStorage();
			}
		});
		scrollPane_pinsA.setViewportView(table_pinsA);
		
		/**
		 * Spawn PinB Table
		 */
		
		scrollPane_pinsB = new JScrollPane();
		scrollPane_pinsB.setBounds(220, 111, 228, 55);
		contentPane.add(scrollPane_pinsB);
		
		table_pinsB = new JTable(model_pinsB){
			@Override	
			public boolean isCellEditable(int row, int column){
				if (row ==0){
					return false;
				}
				else {
					switch (column){
					case 1: case 2: case 3: case 4: case 5: case 6: case 7: return true;
					default: return false;
					}
				}
			}
		};
		table_pinsB.addMouseListener(new MouseAdapter() {
			//@Override
			public void mouseClicked(MouseEvent e){
				int col = 8-table_pinsB.columnAtPoint(e.getPoint());
				sto.changePortBit(1, col);
				updatePinsB();
				updateStorage();
			}
		});
		scrollPane_pinsB.setViewportView(table_pinsB);
		
		/**
		 * Spawn Status Label
		 */
		
		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelStatus.setBounds(10, 306, 200, 168);
		contentPane.add(panelStatus);
		panelStatus.setLayout(null);
		
		JLabel lblTxtW = new JLabel("W");
		lblTxtW.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtW.setBounds(10, 10, 50, 15);
		panelStatus.add(lblTxtW);
		
		JLabel lblTxtPC = new JLabel("PC");
		lblTxtPC.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtPC.setBounds(10, 25, 50, 15);
		panelStatus.add(lblTxtPC);
		
		JLabel lblTxtZ = new JLabel("Z");
		lblTxtZ.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtZ.setBounds(10, 40, 50, 15);
		panelStatus.add(lblTxtZ);
		
		JLabel lblTxtC = new JLabel("C");
		lblTxtC.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtC.setBounds(10, 55, 50, 15);
		panelStatus.add(lblTxtC);
		
		JLabel lblTxtDC = new JLabel("DC");
		lblTxtDC.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtDC.setBounds(10, 70, 50, 15);
		panelStatus.add(lblTxtDC);
		
		JLabel lblTxtStackptr = new JLabel("StackPtr");
		lblTxtStackptr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtStackptr.setBounds(10, 85, 50, 15);
		panelStatus.add(lblTxtStackptr);
		
		JLabel lblTxtTime = new JLabel("Time");
		lblTxtTime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTxtTime.setBounds(10, 100, 50, 15);
		panelStatus.add(lblTxtTime);
		
		lblW = new JLabel("0");
		lblW.setBounds(80, 10, 100, 15);
		panelStatus.add(lblW);
		
		lblPC = new JLabel("0");
		lblPC.setBounds(80, 25, 100, 15);
		panelStatus.add(lblPC);
		
		lblZ = new JLabel("false");
		lblZ.setBounds(80, 40, 100, 15);
		panelStatus.add(lblZ);
		
		lblC = new JLabel("false");
		lblC.setBounds(80, 55, 100, 15);
		panelStatus.add(lblC);
		
		lblDC = new JLabel("false");
		lblDC.setBounds(80, 70, 100, 15);
		panelStatus.add(lblDC);
		
		lblStackPtr = new JLabel("0");
		lblStackPtr.setBounds(80, 85, 100, 15);
		panelStatus.add(lblStackPtr);
		
		lblTime = new JLabel("0 us");
		lblTime.setBounds(80, 100, 100, 15);
		panelStatus.add(lblTime);
		
		JButton btnSetFreq = new JButton("Set Freq.");
		btnSetFreq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sto.setFreq(Double.parseDouble(textFieldFrequenz.getText()));
				updateFrequenz();
			}
		});
		btnSetFreq.setBounds(121, 476, 89, 23);
		contentPane.add(btnSetFreq);
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sto.initializeStorage();
				updateProgress();
			}
		});
		btnReset.setBounds(338, 11, 89, 23);
		contentPane.add(btnReset);
		
		textFieldFrequenz = new JTextField();
		textFieldFrequenz.setHorizontalAlignment(SwingConstants.RIGHT);
		textFieldFrequenz.setText("4.0");
		textFieldFrequenz.setBounds(10, 477, 101, 22);
		contentPane.add(textFieldFrequenz);
		textFieldFrequenz.setColumns(10);
		
		lblDeltaTime = new JLabel("1 us");
		lblDeltaTime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDeltaTime.setBounds(65, 505, 46, 14);
		contentPane.add(lblDeltaTime);
		
		JPanel panelExternClock = new JPanel();
		panelExternClock.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelExternClock.setBounds(458, 45, 142, 121);
		contentPane.add(panelExternClock);
		panelExternClock.setLayout(null);
		
		btnExternalClock = new JButton("Externer Takt");
		btnExternalClock.setBounds(10, 11, 118, 23);
		panelExternClock.add(btnExternalClock);
		btnExternalClock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sto.setExternalFreq(Double.parseDouble(textField.getText()));
				sto.setClockIsRunning(!sto.getClockIsRunning());
				if(sto.getClockIsRunning()){
					lblexternStautus.setText("Running");
				} else {
					lblexternStautus.setText("Stopped");
				}
				
			}
		});
		
		JLabel lblFreqkhz = new JLabel("Freq. [Khz]");
		lblFreqkhz.setBounds(10, 45, 69, 14);
		panelExternClock.add(lblFreqkhz);
		
		textField = new JTextField();
		textField.setText("1.000");
		textField.setBounds(72, 42, 56, 20);
		panelExternClock.add(textField);
		textField.setColumns(10);
		
		JComboBox comBoxExtClkSrc = new JComboBox();
		comBoxExtClkSrc.setModel(new DefaultComboBoxModel(new String[] {"RA0", "RA1", "RA2", "RA3", "RA4", "RA5", "RA6", "RA7", "RB0", "RB1", "RB2", "RB3", "RB4", "RB5", "RB6", "RB7"}));
		comBoxExtClkSrc.setBounds(10, 70, 118, 20);
		panelExternClock.add(comBoxExtClkSrc);
		comBoxExtClkSrc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        String pin = (String)cb.getSelectedItem();
		        switch(pin) {
		        case "RA0":sto.setExternPin(0);
		        			sto.setExternPort(0);
		        	break;
		        case "RA1":sto.setExternPin(1);
							sto.setExternPort(0);
	        	break;
		        case "RA2":sto.setExternPin(2);
							sto.setExternPort(0);
	        	break;
		        case "RA3":sto.setExternPin(3);
							sto.setExternPort(0);
	        	break;
		        case "RA4":sto.setExternPin(4);
							sto.setExternPort(0);
	        	break;
		        case "RA5":sto.setExternPin(5);
							sto.setExternPort(0);
	        	break;
		        case "RA6":sto.setExternPin(6);
							sto.setExternPort(0);
	        	break;
		        case "RA7":sto.setExternPin(7);
							sto.setExternPort(0);
	        	break;
		        case "RB0":	sto.setExternPin(0);
		        			sto.setExternPort(1);
	        	break;
		        case "RB1":sto.setExternPin(1);
		        			sto.setExternPort(1);
	        	break;
		        case "RB2":sto.setExternPin(2);
		        			sto.setExternPort(1);
	        	break;
		        case "RB3":sto.setExternPin(3);
		        			sto.setExternPort(1);
	        	break;
		        case "RB4":sto.setExternPin(4);
		        			sto.setExternPort(1);
	        	break;
		        case "RB5":sto.setExternPin(5);
		        			sto.setExternPort(1);
	        	break;
		        case "RB6":sto.setExternPin(6);
		        			sto.setExternPort(1);
	        	break;
		        case "RB7":sto.setExternPin(7);
		        			sto.setExternPort(1);
	        	break;
	        	default:sto.setExternPin(0);
		        }
			}
		});
		
		
		lblexternStautus = new JLabel("Stopped");
		lblexternStautus.setBounds(10, 96, 118, 14);
		panelExternClock.add(lblexternStautus);
		
		btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Step step = stepStack.popStack();
				if(step!=null) {
				for(int i=0;i<256;i++){
					sto.dataStorage[i] = step.dataStorage[i];
					}
					sto.pc = step.pc;
					for(int i=0;i<8;i++){
						sto.stack[i] = step.stack[i];
					}
					sto.stackptr = step.stackptr;
					sto.w = step.w;
					sto.time = step.time;
					sto.deltatime = step.deltatime;
					sto.konstDelta = step.konstDelta;
					sto.externalClock = step.externalClock;
					sto.externalClockCount = step.externalClockCount;
					sto.clockIsRunning = step.clockIsRunning;
					updateProgress();
				} else {
					btnUndo.setEnabled(false);
				}
			}
		});
		btnUndo.setBounds(437, 11, 89, 23);
		btnUndo.setEnabled(false);
		contentPane.add(btnUndo);
		

		
	}
	
	public static synchronized GUI getInstance () {
		if (GUI.instance == null) {
			GUI.instance = new GUI ();
			instance.sto = storage.getInstance();
			instance.log = logic.getInstance();
			instance.ctrl = control.getInstance();
			instance.startThread = MyThread.getInstance();
			instance.stepStack = StepStack.getInstance();
	    }
	    return GUI.instance;
	}

	public void updateProgress(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				updateStorage();
				updateSpecialRegister();
				updatePinsA();
				updatePinsB();
				updatePanelStatus();
				updateFrequenz();
				ctrl.selectRow();
			}
		});
	}

	void updateFrequenz() {
		textFieldFrequenz.setText(Double.toString(sto.getFreq()));
		lblDeltaTime.setText(Double.toString(sto.getDeltatime()) +" us");
	}
	
	void initializeStorage() {
		 model_storage.addColumn(""); 
		 model_storage.addColumn("00"); 
		 model_storage.addColumn("01"); 
		 model_storage.addColumn("02"); 
		 model_storage.addColumn("03"); 
		 model_storage.addColumn("04"); 
		 model_storage.addColumn("05"); 
		 model_storage.addColumn("06"); 
		 model_storage.addColumn("07"); 
		 
		 updateStorage();
	}
	
	void updateStorage() {
		model_storage.getDataVector().removeAllElements();
		model_storage.fireTableDataChanged(); // notifies the JTable that the model has changed

		String[] a = new String[9];
		 
		for(int j = 0; j < 32; j++) {
			a[0]=Integer.toHexString(j * 8);
			for(int k = 0; k < 8; k++){
				a[k+1] = Integer.toHexString(sto.getDataStorage((j * 8) + k));
			}
			model_storage.addRow(new Object[]{ a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]});
		}
		
	}
	
	void updatePanelStatus() {
		lblW.setText(Integer.toHexString(sto.getW()));
		lblPC.setText(Integer.toHexString(sto.getPC()));
		lblZ.setText(Boolean.toString(sto.getZ()));
		lblC.setText(Boolean.toString(sto.getC()));
		lblDC.setText(Boolean.toString(sto.getDC()));
		lblStackPtr.setText(Integer.toHexString(sto.getStackPtr()));
		lblTime.setText(sto.getTime() + " us");
	}
	
	void initializeSpecialRegister() { 
		 
		 model_special_register.addColumn("Stack");
		 model_special_register.addColumn("Value");
		 
		 updateSpecialRegister();
	}
	
	void updateSpecialRegister() {
		model_special_register.getDataVector().removeAllElements();
		model_special_register.fireTableDataChanged(); // notifies the JTable that the model has changed

		model_special_register.addRow(new Object[]{"Tmr0", Integer.toHexString(sto.getDataStorage(0x1))});
		model_special_register.addRow(new Object[]{"PreSCount", Integer.toHexString(sto.prescaleCount)});
		for(int i = 0; i < 8; i++) {
			model_special_register.addRow(new Object[]{"Stack" + i, Integer.toHexString(sto.getStack(i))});
		}
	}	
	
	void initializePinsARegister(){
		model_pinsA.addColumn("RA");
		model_pinsA.addColumn("7");
		model_pinsA.addColumn("6");
		model_pinsA.addColumn("5");
		model_pinsA.addColumn("4");
		model_pinsA.addColumn("3");
		model_pinsA.addColumn("2");
		model_pinsA.addColumn("1");
		model_pinsA.addColumn("0");
		
		updatePinsA();
	}
	
	void updatePinsA(){
		model_pinsA.getDataVector().removeAllElements();
		model_pinsA.fireTableDataChanged();
		
		model_pinsA.addRow(new Object[]{"Tris","i","i","i","i","i","i","i","i"});
		model_pinsA.addRow(new Object[]{"Pin",
				sto.readPortBit(0, 7), sto.readPortBit(0, 6),
				sto.readPortBit(0, 5), sto.readPortBit(0, 4),
				sto.readPortBit(0, 3), sto.readPortBit(0, 2),
				sto.readPortBit(0, 1), sto.readPortBit(0, 0)});
	}
	
	void initializePinsBRegister(){
		model_pinsB.addColumn("RB");
		model_pinsB.addColumn("7");
		model_pinsB.addColumn("6");
		model_pinsB.addColumn("5");
		model_pinsB.addColumn("4");
		model_pinsB.addColumn("3");
		model_pinsB.addColumn("2");
		model_pinsB.addColumn("1");
		model_pinsB.addColumn("0");
		
		updatePinsB();
	}
	
	void updatePinsB(){
		model_pinsB.getDataVector().removeAllElements();
		model_pinsB.fireTableDataChanged();
		
		model_pinsB.addRow(new Object[]{"Tris","i","i","i","i","i","i","i","i"});
		model_pinsB.addRow(new Object[]{"Pin",
				sto.readPortBit(1, 7), sto.readPortBit(1, 6),
				sto.readPortBit(1, 5), sto.readPortBit(1, 4),
				sto.readPortBit(1, 3), sto.readPortBit(1, 2),
				sto.readPortBit(1, 1), sto.readPortBit(1, 0)});
	}
	
	void showError(int fehler)
	{
		/**
		 * Fehlermeldungen:
		 * 1: Eingelesener Programmcode ist gößer als Programmspeicher
		 * 
		 */
		switch (fehler)
		{
			case 1: 
				JOptionPane.showMessageDialog(null, "Programmcode zu lang", "Fehler", JOptionPane.OK_CANCEL_OPTION);
				break;

			default:
				break;
		}
	}
}

class MyThread extends Thread {
	private static MyThread instance;

	public boolean isRunning = false;
	
	public GUI gui;
	private logic log;
	private control ctrl;
	private storage sto;
	Boolean _check;
	MyThread() {
	}
	public static synchronized MyThread getInstance () {
		if (MyThread.instance == null) {
			MyThread.instance = new MyThread();
			instance.sto = storage.getInstance();
			instance.log = logic.getInstance();
			instance.ctrl = control.getInstance();
			instance.gui = GUI.getInstance();
		}
	    return MyThread.instance;
	}
	
	@Override
	public void run(){
		while(true) {
			if(isRunning){
				ctrl.selectRow();
				_check = (Boolean)ctrl.table_source_code.getModel().getValueAt(ctrl.aktuelleZeile,0); //Breakpoint gesetzt?
				if (!Boolean.TRUE.equals(_check)){ 
					log.executeCommand();
					gui.updateProgress();
					System.out.println(_check);
					System.out.println("PC " + sto.getPc());
				} else {
					isRunning = false;
				}
			}
			try {
				Thread.sleep(sto.getWait());
			} catch(InterruptedException e){
				//do nothing
			}		
		}
	}
}
