import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class GUI extends JFrame {

	private JPanel contentPane;

	public control ctrl;
	private JTable table;
	private JTable table_1;
	
	

	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 774, 537);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Open File");
		btnNewButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				
				ctrl.readFile();		// Datei einlesen
				
			}
		});
		btnNewButton.setBounds(10, 11, 89, 23);
		contentPane.add(btnNewButton);
		
		/**
		 *  |						   |
		 *  | TEST für Programmausgabe |
		 *  v 						   v
		 */
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 68, 251, 302);
		contentPane.add(scrollPane);
		

		String[] columnNames = {"BP","Program"};
		
		Object[][] data = {
				{"", "Line1"},
				{"", "Line2"}
		};
		
		
		table_1 = new JTable(data, columnNames);
		scrollPane.setViewportView(table_1);
		table_1.setBounds(0, 0, 100, 100);
		
		
		
	}
}
