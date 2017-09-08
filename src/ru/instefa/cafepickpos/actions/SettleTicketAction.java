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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.views.payment.SettleTicketDialog;

public class SettleTicketAction extends AbstractAction {

	private int ticketId;
	private User currentUser;

	public SettleTicketAction(int ticketId) {
		this.ticketId = ticketId;
	}

	public SettleTicketAction(int ticketId, User currentUser) {
		this.ticketId = ticketId;
		this.currentUser = currentUser;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		execute();
	}

	public boolean execute() {
		Ticket ticket = TicketDAO.getInstance().loadFullTicket(ticketId);

		if (ticket.isPaid()) {
			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketAction.0")); //$NON-NLS-1$
			return false;
		}

		SettleTicketDialog posDialog = new SettleTicketDialog(ticket, currentUser);

		if (ticket.isBarTab()) {
			posDialog.getTicketProcessor().doSettleBarTabTicket(ticket);
			return true;

		}
		else {
			posDialog.setSize(Application.getPosWindow().getSize());
			posDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			posDialog.openUndecoratedFullScreen();
			return !posDialog.isCanceled();
		}
	}

}
