import java.awt.BorderLayout;
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


public class GUI extends JFrame {

	private JPanel contentPane;

	public control ctrl;
	private storage sto = storage.getInstance();
	private logic log = logic.getInstance();
	
	
	public JTable table_source_code_temp;
	public JScrollPane scrollPane_source_code;
	public JScrollPane scrollPane_storage;
	
	DefaultTableModel model_storage = new DefaultTableModel(); 
	DefaultTableModel model_special_register = new DefaultTableModel(); 
	DefaultTableModel model_source_code = new DefaultTableModel();
	
	public String[] columnNames = {"BP","Program"};	
	public Object[][] tempData = new Object[1][2];
	private JTable table_storage;
	private JTable table_special_register;
	private JButton btn_start;
	public boolean isRunning = false;

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
				ctrl.readFile();		// Datei einlesen
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
<<<<<<< HEAD
				if(isRunning == false){
					isRunning = true;
					startThread = new MyThread(){
						public void run(){
							while(! isInterrupted()){
								
								try{
									log.executeCommand();
									updateProgress();
									System.out.println("PC " + sto.getPc());
									Thread.sleep(1000);
									
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
=======
				while(true)
				{
					log.step();
>>>>>>> 40239cc4d6e9578a6424fc33034c16f8fdc9d2e2
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
		
		JScrollPane scrollPane_special_register = new JScrollPane();
		scrollPane_special_register.setBounds(220, 45, 128, 250);
		contentPane.add(scrollPane_special_register);
		
		table_special_register = new JTable(model_special_register);
		scrollPane_special_register.setViewportView(table_special_register);
		

		
	}

	public void updateProgress(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				updateStorage();
				updateSpecialRegister();
				ctrl.selectRow();
			}
		});
	}
	
	public void setEnabled(boolean b){
		btn_start.setEnabled(true);
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
