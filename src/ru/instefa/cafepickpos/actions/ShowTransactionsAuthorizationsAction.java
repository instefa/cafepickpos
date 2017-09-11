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
package ru.instefa.cafepickpos.actions;

import javax.swing.JDialog;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.views.payment.AuthorizableTicketBrowser;

public class ShowTransactionsAuthorizationsAction extends PosAction {

	public ShowTransactionsAuthorizationsAction() {
		super(POSConstants.AUTHORIZE_BUTTON_TEXT, UserPermission.AUTHORIZE_TICKETS); //$NON-NLS-1$
	}

	@Override
	public void execute() {
		AuthorizableTicketBrowser dialog = new AuthorizableTicketBrowser(Application.getPosWindow());
    	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	dialog.setSize((PosUIManager.getSize(800, 600)));
    	dialog.setLocationRelativeTo(Application.getPosWindow());
    	dialog.setVisible(true);
	}

}