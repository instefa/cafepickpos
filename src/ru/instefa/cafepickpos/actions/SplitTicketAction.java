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

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.ui.views.SplitTicketDialog;

public class SplitTicketAction extends PosAction {
	private Ticket ticket;

	public SplitTicketAction() {
		super(POSConstants.SPLIT_TICKET);
	}

	public SplitTicketAction(Ticket ticket) {
		super(POSConstants.SPLIT_TICKET);
		this.ticket = ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Ticket getTicket() {
		return ticket;
	}

	@Override
	public void execute() {
		if (ticket == null) {
			return;
		}

		Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());

		try {
			SplitTicketDialog dialog = new SplitTicketDialog();
			dialog.setTicket(ticketToEdit);
			dialog.setLocationRelativeTo(Application.getPosWindow());
			dialog.setVisible(true);
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		}
	}

}
