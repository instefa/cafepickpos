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
package ru.instefa.cafepickpos.bo.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDialog;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.LanguageSelectionDialog;
import ru.instefa.cafepickpos.util.POSUtil;

public class LanguageSelectionAction extends AbstractAction {

	public LanguageSelectionAction() {
		super(Messages.getString("LanguageSelectionAction.0"));
	}

	public LanguageSelectionAction(String name) {
		super(name);
	}

	public LanguageSelectionAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {

		LanguageSelectionDialog dialog = new LanguageSelectionDialog();
		dialog.setTitle(Messages.getString("LanguageSelectionAction.1"));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setSize((PosUIManager.getSize(600, 400)));
		dialog.setLocationRelativeTo(POSUtil.getFocusedWindow());
		dialog.setVisible(true);
	}

}
