import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GUI extends JFrame {

	private JPanel contentPane;

	public control ctrl;
	public storage sto;
	public JTable table_source_code;
	public JScrollPane scrollPane_source_code;
	public JScrollPane scrollPane_storage;
	
	DefaultTableModel model = new DefaultTableModel(); 
	
	public String[] columnNames = {"BP","Program"};	
	public Object[][] tempData = new Object[1][2];
	private JTable table_storage;

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
		 * Spawn Start Button
		 */
		JButton btn_start = new JButton("Start");
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateStorage();
			}
		});
		btn_start.setBounds(109, 11, 89, 23);
		contentPane.add(btn_start);
		
		/**
		 *  Spawn Source Code Table
		 */
		scrollPane_source_code = new JScrollPane();
		scrollPane_source_code.setBounds(10, 310, 663, 121);
		contentPane.add(scrollPane_source_code);
		
		table_source_code = new JTable(tempData, columnNames);
		scrollPane_source_code.setViewportView(table_source_code);
		table_source_code.setBounds(0, 0, 100, 100);
		
		/**
		 * Spawn Storage Table
		 */
		scrollPane_storage = new JScrollPane();
		scrollPane_storage.setBounds(10, 45, 200, 250);
		contentPane.add(scrollPane_storage);
		
		table_storage = new JTable(model);
		scrollPane_storage.setViewportView(table_storage);

	}
	

	
	void initializeStorage() {
		 model.addColumn(""); 
		 model.addColumn("00"); 
		 model.addColumn("01"); 
		 model.addColumn("02"); 
		 model.addColumn("03"); 
		 model.addColumn("04"); 
		 model.addColumn("05"); 
		 model.addColumn("06"); 
		 model.addColumn("07"); 
		 
		 updateStorage();
	}
	
	void updateStorage() {
		model.getDataVector().removeAllElements();
		model.fireTableDataChanged(); // notifies the JTable that the model has changed

		String[] a = new String[9];
		 
		for(int j = 0; j < 32; j++) {
			a[0]=Integer.toHexString(j * 8);
			for(int k = 0; k < 8; k++){
				a[k+1] = Integer.toHexString(sto.dataStorage[(j * 8) + k]);
			}
			model.addRow(new Object[]{ a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8]});
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
