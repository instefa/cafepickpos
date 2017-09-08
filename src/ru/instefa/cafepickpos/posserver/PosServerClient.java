/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package ru.instefa.cafepickpos.posserver;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.ui.TitlePanel;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;

public class PosServerClient extends POSDialog {
	private static JLabel lblStatus;

	private JTextField txtServerId;
	private JTextField txtTable;

	private JTextArea txtReqMsg;
	private JTextArea txtRespMsg;

	private JButton btnSend;
	private JButton btnRequest;
	
	
	
	public PosServerClient() {
		intializeComponents(); 
	}

	public void intializeComponents() {
		setLayout(new BorderLayout()); 
		
		JPanel container=new JPanel(new BorderLayout());
		
		JPanel headerPanel=new JPanel(new BorderLayout()); 
		
		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle("POS Server Client");
		
		lblStatus=new JLabel(""); 
		
		headerPanel.add(titlePanel, BorderLayout.NORTH);
		headerPanel.add(lblStatus, BorderLayout.SOUTH); 
		
		JLabel lblServerId=new JLabel("Server Id:"); 
		JLabel lblTable=new JLabel("Table No:"); 
		
		txtServerId=new JTextField(20); 
		txtTable=new JTextField(20); 
		
		btnSend=new JButton("Send"); 
		btnRequest=new JButton("Request"); 
		
		JPanel centerPanel=new JPanel(new MigLayout()); 
		
		centerPanel.add(lblServerId);
		centerPanel.add(txtServerId); 
		centerPanel.add(btnSend, "wrap"); 
		centerPanel.add(lblTable); 
		centerPanel.add(txtTable,"wrap"); 
		centerPanel.add(new JLabel()); 
		centerPanel.add(btnRequest, "wrap"); 
		
		JPanel msgPanel=new JPanel(new MigLayout());
		
		JLabel lblReq=new JLabel("Request"); 
		JLabel lblRes=new JLabel("Response"); 
		
		txtReqMsg=new JTextArea(5,50); 
		txtRespMsg=new JTextArea(5,50);
		
		txtReqMsg.setLineWrap(true); 
		txtRespMsg.setLineWrap(true); 
		
		msgPanel.add(lblReq, "wrap"); 
		msgPanel.add(txtReqMsg, "grow, wrap"); 
		msgPanel.add(lblRes, "wrap"); 
		msgPanel.add(txtRespMsg, "grow"); 
		
		container.add(headerPanel, BorderLayout.NORTH); 
		container.add(msgPanel, BorderLayout.SOUTH); 
		container.add(centerPanel, BorderLayout.CENTER);
		
		add(container, BorderLayout.CENTER); 
		
		setSize(500,500);
	}

	public static void main(String[] args) throws Exception {

		/*Socket socket = new Socket(InetAddress.getLocalHost(), 5656);
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String s = in.readLine();
		while (s != null) {

		}

		writer.write("e=he");
		writer.flush();*/
		
		new PosServerClient().open(); 
	}
}
