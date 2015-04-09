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
	public JTable table_1;
	public JScrollPane scrollPane;

	public String[] columnNames = {"BP","Program"};	
	public Object[][] tempData = new Object[1][2];

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
		
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 68, 251, 302);
		contentPane.add(scrollPane);
		
		
		table_1 = new JTable(tempData, columnNames);
		scrollPane.setViewportView(table_1);
		table_1.setBounds(0, 0, 100, 100);
		
		
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
