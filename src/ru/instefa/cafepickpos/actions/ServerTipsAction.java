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

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.TipsCashoutReport;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.dao.GratuityDAO;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.swing.ListComboBoxModel;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.dialog.TipsCashoutReportDialog;
import ru.instefa.cafepickpos.ui.util.UiUtil;

public class ServerTipsAction extends PosAction {

	public ServerTipsAction() {
		super(POSConstants.SERVER_TIPS, UserPermission.DRAWER_PULL); //$NON-NLS-1$
	}

	@Override
	public void execute() {
		try {
			JPanel panel = new JPanel(new MigLayout());
			List<User> users = UserDAO.getInstance().findAll();

			JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
			JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();

			panel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.SELECT_USER + ":"), "grow"); //$NON-NLS-1$ //$NON-NLS-2$
			JComboBox userCombo = new JComboBox(new ListComboBoxModel(users));
			panel.add(userCombo, "grow, wrap"); //$NON-NLS-1$
			panel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.FROM + ":"), "grow"); //$NON-NLS-1$ //$NON-NLS-2$
			panel.add(fromDatePicker, "wrap"); //$NON-NLS-1$
			panel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.TO_), "grow"); //$NON-NLS-1$
			panel.add(toDatePicker);

			int option = JOptionPane.showOptionDialog(Application.getPosWindow(), panel, ru.instefa.cafepickpos.POSConstants.SELECT_CRITERIA, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (option != JOptionPane.OK_OPTION) {
				return;
			}

			GratuityDAO gratuityDAO = new GratuityDAO();
			TipsCashoutReport report = gratuityDAO.createReport(fromDatePicker.getDate(), toDatePicker.getDate(), (User) userCombo.getSelectedItem());

			TipsCashoutReportDialog dialog = new TipsCashoutReportDialog(report);
			dialog.setSize(400, 600);
			dialog.open();
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
		}
	}

}
