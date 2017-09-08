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
 * * Contributor(s): pymancer <pymancer@gmail.com>.
 * ************************************************************************
 */
package ru.instefa.cafepickpos.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ru.instefa.cafepickpos.swing.FixedLengthDocument;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.QwertyKeyPad;
import ru.instefa.cafepickpos.swing.TransparentPanel;

import net.miginfocom.swing.MigLayout;

/**
* Scrollable Text Field with Visual Virtual Keyboard.
* Virtual Keyboard unification.
*/
public class NoteView extends JPanel{
	Font buttonFont = getFont().deriveFont(Font.BOLD, PosUIManager.getFontSize(24));
	JTextArea note = new JTextArea();
	
	public NoteView() {
		setLayout(new MigLayout("inset 0, fillx", "", ""));

		note.setWrapStyleWord(true);
		note.setLineWrap(true);
		note.setDocument(new FixedLengthDocument(255));
		
		TransparentPanel northPanel = new TransparentPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(note);
		northPanel.setPreferredSize(new Dimension(100, 60));
		northPanel.add(scrollPane);
		add(northPanel, "newline, grow, span, gaptop 10");
		
		QwertyKeyPad keyPad = new QwertyKeyPad();
		add(keyPad, "newline, grow, span, gaptop 10");
	}
	
	public String getNote() {
		return note.getText();
	}
	
	public void setNoteLength(int length) {
		note.setDocument(new FixedLengthDocument(length));
	}
}
