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

import ru.instefa.cafepickpos.ITicketList;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.services.PosTransactionService;
import ru.instefa.cafepickpos.services.TicketService;
import ru.instefa.cafepickpos.ui.dialog.NumberSelectionDialog2;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.CurrencyUtil;

public class RefundAction extends PosAction {
	private ITicketList ticketList;

	public RefundAction(ITicketList ticketList) {
		super(Messages.getString("RefundAction.0"), UserPermission.REFUND); //$NON-NLS-1$

		this.ticketList = ticketList;
	}

	@Override
	public void execute() {
		try {
			Ticket ticket = ticketList.getSelectedTicket();

			if (ticket == null) {
				int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("RefundAction.1")); //$NON-NLS-1$
				if(ticketId == -1) return;
				
				ticket = TicketService.getTicket(ticketId);
			}
			
			if(!ticket.isPaid()) {
				POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.2")); //$NON-NLS-1$
				return;
			}
			
			if(ticket.isRefunded()) {
				POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.3")); //$NON-NLS-1$
				return;
			}
			
			Double paidAmount = ticket.getPaidAmount();
			
			String message = CurrencyUtil.getCurrencySymbol() + paidAmount + " " + Messages.getString("RefundAction.4"); //$NON-NLS-1$
			
			ticket = TicketDAO.getInstance().loadFullTicket(ticket.getId());
			
			message = "<html>" + //$NON-NLS-1$
					Messages.getString("RefundAction.6") + ticket.getId() + "<br/>" + Messages.getString("RefundAction.7") + ticket.getPaidAmount(); //$NON-NLS-1$ //$NON-NLS-2$
			
			if(ticket.getGratuity() != null) {
				message += ", " + Messages.getString("RefundAction.8") + ticket.getGratuity().getAmount(); //$NON-NLS-1$
			}
			
			message += "</html>"; //$NON-NLS-1$
			
			double refundAmount = NumberSelectionDialog2.takeDoubleInput(message, Messages.getString("RefundAction.10"), paidAmount); //$NON-NLS-1$
			if(Double.isNaN(refundAmount)) {
				return;
			}
			
			if(refundAmount > paidAmount) {
				POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("RefundAction.11")); //$NON-NLS-1$
				return;
			}

			PosTransactionService.getInstance().refundTicket(ticket, refundAmount);
			
			POSMessageDialog.showMessage(Messages.getString("RefundAction.12") + CurrencyUtil.getCurrencySymbol() + refundAmount); //$NON-NLS-1$
			
			ticketList.updateTicketList();
			
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
		}
	}

}
