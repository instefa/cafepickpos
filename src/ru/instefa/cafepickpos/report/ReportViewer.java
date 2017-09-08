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
/*
 * ReportViewer.java
 *
 * Created on September 17, 2006, 11:38 PM
 */

package ru.instefa.cafepickpos.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.model.dao.TerminalDAO;
import ru.instefa.cafepickpos.model.dao.UserTypeDAO;
import ru.instefa.cafepickpos.swing.ListComboBoxModel;
import ru.instefa.cafepickpos.swing.MessageDialog;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.util.UiUtil;

/**
 *
 * @author  MShahriar
 */
public class ReportViewer extends JPanel {
	private JButton btnRefresh;
	private JComboBox cbReportType;
	private JComboBox cbTerminal;
	private JXDatePicker dpEndDate;
	private JXDatePicker dpStartDate;
	private JLabel lblReportType;
	private JLabel lblFromDate;
	private JLabel lblToDate;
	private JLabel lblTerminal;
	private JCheckBox chkBoxFree;
	private JLabel lblUserType;
	private JComboBox cbUserType;
	private TransparentPanel reportSearchOptionPanel;
	private TransparentPanel reportConstraintPanel;
	private TransparentPanel reportPanel;

	private Report report;

	public ReportViewer() {
		initComponents();
	}

	public ReportViewer(Report report) {
		initComponents();

		TerminalDAO terminalDAO = new TerminalDAO();
		List drawerTerminals = new ArrayList<Terminal>();
		drawerTerminals.add(0, ru.instefa.cafepickpos.POSConstants.ALL);

		List<Terminal> terminals = terminalDAO.findAll();
		for (Terminal terminal : terminals) {
			if (terminal.isHasCashDrawer()) {
				drawerTerminals.add(terminal);
			}
		}

		cbTerminal.setModel(new ListComboBoxModel(drawerTerminals));

		setReport(report);
	}

	private void initComponents() {
		setLayout(new java.awt.BorderLayout(5, 5));

		reportSearchOptionPanel = new TransparentPanel(new BorderLayout());
		reportConstraintPanel = new TransparentPanel();
		reportConstraintPanel.setLayout(new MigLayout());

		lblReportType = new JLabel(Messages.getString("ReportViewer.0") + ":");
		cbReportType = new JComboBox();
		cbReportType.setModel(new DefaultComboBoxModel(new String[] { ru.instefa.cafepickpos.POSConstants.PREVIOUS_SALE_AFTER_DRAWER_RESET_,
				ru.instefa.cafepickpos.POSConstants.SALE_BEFORE_DRAWER_RESET }));
		cbReportType.setSelectedIndex(1);

		lblTerminal = new JLabel(POSConstants.TERMINAL_LABEL);

		cbTerminal = new JComboBox();
		cbTerminal.setPreferredSize(new Dimension(115, 0));

		lblFromDate = new JLabel(ru.instefa.cafepickpos.POSConstants.START_DATE + ":");
		dpStartDate = UiUtil.getCurrentMonthStart();

		lblToDate = new JLabel(ru.instefa.cafepickpos.POSConstants.END_DATE + ":");
		dpEndDate = UiUtil.getCurrentMonthEnd();

		chkBoxFree = new JCheckBox(Messages.getString("ReportViewer.1"));

		lblUserType = new JLabel(POSConstants.USER_TYPE);
		cbUserType = new JComboBox();

		btnRefresh = new JButton();
		reportPanel = new TransparentPanel();

		btnRefresh.setText(ru.instefa.cafepickpos.POSConstants.REFRESH);
		btnRefresh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doRefreshReport(evt);
			}
		});

		reportConstraintPanel.add(lblReportType);
		reportConstraintPanel.add(cbReportType, "gap 0px 20px");
		reportConstraintPanel.add(lblTerminal);
		reportConstraintPanel.add(cbTerminal, "wrap");
		reportConstraintPanel.add(lblFromDate);
		reportConstraintPanel.add(dpStartDate);
		reportConstraintPanel.add(lblToDate);
		reportConstraintPanel.add(dpEndDate, "wrap");
		reportConstraintPanel.add(new JLabel(""));
		reportConstraintPanel.add(chkBoxFree, "wrap");
		reportConstraintPanel.add(new JLabel(""));
		reportConstraintPanel.add(btnRefresh);
		
		reportSearchOptionPanel.add(reportConstraintPanel, BorderLayout.NORTH);
		reportSearchOptionPanel.add(new JSeparator(), BorderLayout.CENTER);

		reportPanel.setLayout(new BorderLayout());

		add(reportSearchOptionPanel, BorderLayout.NORTH);
		add(reportPanel, BorderLayout.CENTER);

	}

	private void doRefreshReport(java.awt.event.ActionEvent evt) {
		Date fromDate = dpStartDate.getDate();
		Date toDate = dpEndDate.getDate();

		if (fromDate.after(toDate)) {
			POSMessageDialog.showError(ru.instefa.cafepickpos.util.POSUtil.getFocusedWindow(), ru.instefa.cafepickpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return;
		}

		try {
			reportPanel.removeAll();
			reportPanel.revalidate();

			if (report != null) {
				int reportType = cbReportType.getSelectedIndex();
				report.setReportType(reportType);

				UserType userType = null;
				if (cbUserType.getSelectedItem() instanceof UserType) {
					userType = (UserType) cbUserType.getSelectedItem();
				}
				report.setUserType(userType);

				Terminal terminal = null;
				if (cbTerminal.getSelectedItem() instanceof Terminal) {
					terminal = (Terminal) cbTerminal.getSelectedItem();
				}
				report.setTerminal(terminal);
				report.setStartDate(fromDate);
				report.setEndDate(toDate);
				report.setIncludeFreeItems(chkBoxFree.isSelected());

				report.refresh();

				if (report != null && report.getViewer() != null) {
					reportPanel.add(report.getViewer());
					reportPanel.revalidate();
				}
			}

		} catch (Exception e) {
			MessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, e);
		}
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;

		if (report instanceof OpenTicketSummaryReport) {
			reportConstraintPanel.removeAll();

			UserTypeDAO dao = new UserTypeDAO();
			List<UserType> userTypes = dao.findAll();

			List list = new ArrayList<UserType>();
			list.add(0, ru.instefa.cafepickpos.POSConstants.ALL);
			list.addAll(userTypes);

			cbUserType.setModel(new ListComboBoxModel(list));
			cbUserType.setPreferredSize(cbTerminal.getPreferredSize());
			reportConstraintPanel.add(lblUserType);
			reportConstraintPanel.add(cbUserType, "wrap");
			reportConstraintPanel.add(lblTerminal);
			reportConstraintPanel.add(cbTerminal, "wrap");
			reportConstraintPanel.add(new JLabel(""));
			reportConstraintPanel.add(btnRefresh, "wrap");
		}

	}
}
