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
package ru.instefa.cafepickpos.ui;

import java.awt.HeadlessException;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;

public class TicketFilterDialog extends POSDialog {

	public TicketFilterDialog() throws HeadlessException {
		super(Application.getPosWindow(), Messages.getString("TicketFilterDialog.0"), true); //$NON-NLS-1$
	}

}
