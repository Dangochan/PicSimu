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

import javax.swing.JLabel;


public class GUI extends JFrame {

	private JPanel contentPane;

	public control ctrl;
	private storage sto = storage.getInstance();
	private logic log = logic.getInstance();
	
	
	public JTable table_source_code_temp;
	public JScrollPane scrollPane_source_code;
	public JScrollPane scrollPane_storage;
	public JLabel lbl_ProgramTime;
	
	DefaultTableModel model_storage = new DefaultTableModel(); 
	DefaultTableModel model_special_register = new DefaultTableModel(); 
	DefaultTableModel model_source_code = new DefaultTableModel();
	DefaultTableModel model_pinsA = new DefaultTableModel();
	DefaultTableModel model_pinsB = new DefaultTableModel();
	
	public String[] columnNames = {"BP","Program"};	
	public Object[][] tempData = new Object[1][2];
	private JTable table_storage;
	private JTable table_special_register;
	private JButton btn_start;
	public boolean isRunning = false;
	private JTable table_pinsA;
	private JScrollPane scrollPane_pinsA;
	private JTable table_pinsB;
	private JScrollPane scrollPane_pinsB;

	/**
	 * Create the frame.
	 */
	public GUI() {
		/**
		 * Spawn Layout
		 */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 774, 537);
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
		btn_start = new JButton("Start");
		btn_start.addActionListener(new ActionListener() {
			public MyThread startThread;
			public void actionPerformed(ActionEvent arg0) {
				if(isRunning == false){
					isRunning = true;
					startThread = new MyThread(){
						public void run(){
							while(! isInterrupted()){
								
								try{
									log.step(); //damit nach BP die Zeile "übersprungen" wird
									ctrl.selectRow();
						            Boolean chk = (Boolean)ctrl.table_source_code.getModel().getValueAt(ctrl.aktuelleZeile,0);
									if (!Boolean.TRUE.equals(chk)){ 
										log.executeCommand();
										updateProgress();
										System.out.println("PC " + sto.getPc());
										Thread.sleep(sto.getWait());
									}
									else{
										interrupt();
										isRunning = false;
									}
								}
								catch(InterruptedException e){
									interrupt();
								}
							}
						}
					};
					startThread.start();
				}
				else{
					isRunning = false;
					startThread.interrupt();
				}
			}
		});
		btn_start.setBounds(208, 11, 89, 23);
		contentPane.add(btn_start);
		
		/**
		 *  Spawn Source Code Table
		 */
		scrollPane_source_code = new JScrollPane();
		scrollPane_source_code.setBounds(10, 310, 540, 121);
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
		scrollPane_special_register.setBounds(220, 45, 128, 250);
		contentPane.add(scrollPane_special_register);
		
		table_special_register = new JTable(model_special_register);
		scrollPane_special_register.setViewportView(table_special_register);
		
		/**
		 * Spawn PinA Table
		 */
		scrollPane_pinsA = new JScrollPane();
		scrollPane_pinsA.setBounds(358, 46, 228, 55);
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
		scrollPane_pinsB.setBounds(358, 112, 228, 55);
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
		
		lbl_ProgramTime = new JLabel("0 us");
		lbl_ProgramTime.setBounds(10, 458, 89, 14);
		contentPane.add(lbl_ProgramTime);
		
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
		

		
	}

	public void updateProgress(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				updateStorage();
				updateSpecialRegister();
				updatePinsA();
				updatePinsB();
				updateProgramTime();
				ctrl.selectRow();
			}
		});
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
	
	void updateProgramTime() {
		lbl_ProgramTime.setText(sto.getTime() + " us");
	}
	
	void initializeSpecialRegister() { 
		 
		 model_special_register.addColumn("RegName");
		 model_special_register.addColumn("Value");
		 
		 updateSpecialRegister();
	}
	
	void updateSpecialRegister() {
		model_special_register.getDataVector().removeAllElements();
		model_special_register.fireTableDataChanged(); // notifies the JTable that the model has changed

		model_special_register.addRow(new Object[]{"W", Integer.toHexString(sto.getW())});
		model_special_register.addRow(new Object[]{"PC", Integer.toHexString(sto.getPC())});
		model_special_register.addRow(new Object[]{"C", Boolean.toString(sto.getC())});
		model_special_register.addRow(new Object[]{"Z", Boolean.toString(sto.getZ())});
		
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
	public GUI gui;
	private logic log = logic.getInstance();
	@Override
	public void run(){
		
	}
	public void programStart(){
		//log.run();
	}
}
