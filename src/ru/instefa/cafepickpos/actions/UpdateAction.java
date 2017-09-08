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
package ru.instefa.cafepickpos.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.services.PosWebService;
import ru.instefa.cafepickpos.ui.dialog.UpdateDialog;

public class UpdateAction extends AbstractAction {

	public UpdateAction() {
		super(Messages.getString("UpdateAction.7")); //$NON-NLS-1$
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		openUpdateDialog();
	}

	private void openUpdateDialog() {
		PosWebService service = new PosWebService();
		try {
			String newVersion = service.getAvailableNewVersions();

			UpdateDialog dialog = new UpdateDialog(newVersion);
			dialog.setTitle(Messages.getString("UpdateAction.7")); //$NON-NLS-1$
			dialog.pack();
			dialog.open();
		} catch (Exception ex) {
			PosLog.error(getClass(), ex);
		}
	}
}
