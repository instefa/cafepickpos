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
/*
 * KeyStatisticsSalesReportView.java
 *
 * Created on March 10, 2007, 2:53 AM
 */

package ru.instefa.cafepickpos.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.model.dao.SalesSummaryDAO;
import ru.instefa.cafepickpos.model.dao.TerminalDAO;
import ru.instefa.cafepickpos.model.dao.UserTypeDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.report.SalesAnalysisReportModel.SalesAnalysisData;
import ru.instefa.cafepickpos.report.SalesStatistics.ShiftwiseDataTableModel;
import ru.instefa.cafepickpos.swing.ListComboBoxModel;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.util.UiUtil;
import ru.instefa.cafepickpos.util.NumberUtil;

/**
 *
 * @author  mshahriar
 */
public class SalesSummaryReportView extends javax.swing.JPanel {
	public static final int REPORT_KEY_STATISTICS = 1;
	public static final int REPORT_SALES_ANALYSIS = 2;

	private int reportType;

	/** Creates new form KeyStatisticsSalesReportView */
	public SalesSummaryReportView() {
		initComponents();

		UserTypeDAO dao = new UserTypeDAO();
		List<UserType> userTypes = dao.findAll();
		
		Vector list = new Vector();
		list.add(null);
		list.addAll(userTypes);
		
		cbUserType.setModel(new DefaultComboBoxModel(list));

		TerminalDAO terminalDAO = new TerminalDAO();
		List terminals = terminalDAO.findAll();
		terminals.add(0, ru.instefa.cafepickpos.POSConstants.ALL);
		cbTerminal.setModel(new ListComboBoxModel(terminals));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		fromDatePicker = UiUtil.getCurrentMonthStart();
		toDatePicker = UiUtil.getCurrentMonthEnd();
		cbUserType = new javax.swing.JComboBox();
		cbTerminal = new javax.swing.JComboBox();
		btnGo = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		reportPanel = new javax.swing.JPanel();

		jLabel1.setText(ru.instefa.cafepickpos.POSConstants.FROM + ":"); //$NON-NLS-1$

		jLabel2.setText(ru.instefa.cafepickpos.POSConstants.TO + ":"); //$NON-NLS-1$

		jLabel3.setText(ru.instefa.cafepickpos.POSConstants.USER_TYPE + ":"); //$NON-NLS-1$

		jLabel4.setText(ru.instefa.cafepickpos.POSConstants.TERMINAL_LABEL + ":"); //$NON-NLS-1$

		btnGo.setText(ru.instefa.cafepickpos.POSConstants.GO);
		btnGo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				showReport(evt);
			}
		});

		reportPanel.setLayout(new java.awt.BorderLayout());

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE).add(
								layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(jLabel2)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(toDatePicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(fromDatePicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).add(20, 20, 20).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3).add(jLabel4)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(cbTerminal, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(cbUserType, 0, 137, Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(btnGo,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(reportPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3).add(
								layout.createSequentialGroup().add(cbUserType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
										layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel4).add(cbTerminal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(btnGo))).add(
								layout.createSequentialGroup().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(fromDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
												layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel2).add(toDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(reportPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303,
						Short.MAX_VALUE).addContainerGap()));

		layout.linkSize(new java.awt.Component[] { cbTerminal, cbUserType, jLabel3, jLabel4 }, org.jdesktop.layout.GroupLayout.VERTICAL);

		layout.linkSize(new java.awt.Component[] { fromDatePicker, jLabel1, jLabel2, toDatePicker }, org.jdesktop.layout.GroupLayout.VERTICAL);

	}// </editor-fold>//GEN-END:initComponents

	private boolean initCriteria() {
		fromDate = fromDatePicker.getDate();
		toDate = toDatePicker.getDate();

		if (fromDate.after(toDate)) {
			POSMessageDialog.showError(ru.instefa.cafepickpos.util.POSUtil.getFocusedWindow(), ru.instefa.cafepickpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return false;
		}

		dateDiff = (int) ((toDate.getTime() - fromDate.getTime()) * (1.15740741 * Math.pow(10, -8))) + 1;
		userType = (UserType) cbUserType.getSelectedItem();
//		if (userType.equalsIgnoreCase(ru.instefa.cafepickpos.POSConstants.ALL)) {
//			userType = null;
//		}
		terminal = null;
		if (cbTerminal.getSelectedItem() instanceof Terminal) {
			terminal = (Terminal) cbTerminal.getSelectedItem();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(fromDate);

		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		fromDate = calendar.getTime();

		calendar.clear();
		calendar2.setTime(toDate);
		calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar2.get(Calendar.DATE));
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		toDate = calendar.getTime();

		return true;
	}

	private void showReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReport
		try {
			if (!initCriteria()) {
				return;
			}

			if (reportType == REPORT_KEY_STATISTICS) {
				showKeyStatisticsReport();
			}
			else if (reportType == REPORT_SALES_ANALYSIS) {
				showSalesAnalysisReport();
			}
		} catch (Exception e) {
			POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, e);
		}
	}//GEN-LAST:event_showReport

	private void showSalesAnalysisReport() throws Exception {
		SalesSummaryDAO dao = new SalesSummaryDAO();
		List<SalesAnalysisData> datas = dao.findSalesAnalysis(fromDate, toDate, userType, terminal);

		Map properties = new HashMap();
		ReportUtil.populateRestaurantProperties(properties);
		properties.put("subtitle", ru.instefa.cafepickpos.POSConstants.SALES_SUMMARY_REPORT); //$NON-NLS-1$
		properties.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$
		properties.put("fromDate", shortDateFormatter.format(fromDate)); //$NON-NLS-1$
		properties.put("toDate", shortDateFormatter.format(toDate)); //$NON-NLS-1$
		if (userType == null) {
			properties.put("reportType", ru.instefa.cafepickpos.POSConstants.SYSTEM_TOTAL); //$NON-NLS-1$
		}
		else {
			properties.put("reportType", userType.getName()); //$NON-NLS-1$
		}
		properties.put("shift", ru.instefa.cafepickpos.POSConstants.ALL); //$NON-NLS-1$
		properties.put("centre", terminal == null ? ru.instefa.cafepickpos.POSConstants.ALL : terminal.getName()); //$NON-NLS-1$
		properties.put("days", String.valueOf(dateDiff)); //$NON-NLS-1$

		JasperReport report = ReportUtil.getReport("sales_summary_report2"); //$NON-NLS-1$
		JasperPrint print = JasperFillManager.fillReport(report, properties, new JRTableModelDataSource(new SalesAnalysisReportModel(datas)));
		openReport(print);
	}

	private void showKeyStatisticsReport() throws Exception {
		SalesSummaryDAO dao = new SalesSummaryDAO();
		SalesStatistics summary = dao.findKeyStatistics(fromDate, toDate, userType, terminal);

		Map properties = new HashMap();
		ReportUtil.populateRestaurantProperties(properties);
		properties.put("subtitle", ru.instefa.cafepickpos.POSConstants.SALES_SUMMARY_REPORT); //$NON-NLS-1$
		properties.put("GuestPerSeat", NumberUtil.formatNumber(summary.getGuestPerSeat())); //$NON-NLS-1$
		properties.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$
		properties.put("fromDate", shortDateFormatter.format(fromDate)); //$NON-NLS-1$
		properties.put("toDate", shortDateFormatter.format(toDate)); //$NON-NLS-1$
		if (userType == null) {
			properties.put("reportType", ru.instefa.cafepickpos.POSConstants.SYSTEM_TOTAL); //$NON-NLS-1$
		}
		else {
			properties.put("reportType", userType.getName()); //$NON-NLS-1$
		}
		properties.put("shift", ru.instefa.cafepickpos.POSConstants.ALL); //$NON-NLS-1$
		properties.put("centre", terminal == null ? ru.instefa.cafepickpos.POSConstants.ALL : terminal.getName()); //$NON-NLS-1$
		properties.put("days", String.valueOf(dateDiff)); //$NON-NLS-1$

		properties.put("Capacity", String.valueOf(summary.getCapacity())); //$NON-NLS-1$
		properties.put("GuestCount", String.valueOf(summary.getGuestCount())); //$NON-NLS-1$
		properties.put("TableTrnOvr", NumberUtil.formatNumber(summary.getTableTurnOver())); //$NON-NLS-1$
		properties.put("AVGGuest", NumberUtil.formatNumber(summary.getAvgGuest())); //$NON-NLS-1$
		properties.put("OpenChecks", String.valueOf(summary.getOpenChecks())); //$NON-NLS-1$
		properties.put("VOIDChecks", String.valueOf(summary.getVoidChecks())); //$NON-NLS-1$
		properties.put("OPPDChecks", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("TRNGChecks", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("ROPNChecks", String.valueOf(summary.getRopnChecks())); //$NON-NLS-1$
		properties.put("MergeChecks", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("LaborHour", NumberUtil.formatNumber(summary.getLaborHour())); //$NON-NLS-1$
		properties.put("LaborSales", NumberUtil.formatNumber(summary.getGrossSale())); //$NON-NLS-1$
		properties.put("Tables", String.valueOf(summary.getTables())); //$NON-NLS-1$
		properties.put("CheckCount", String.valueOf(summary.getCheckCount())); //$NON-NLS-1$
		properties.put("GuestPerChecks", NumberUtil.formatNumber(summary.getGuestPerCheck())); //$NON-NLS-1$
		properties.put("TrnOvrTime", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("AVGChecks", NumberUtil.formatNumber(summary.getAvgCheck())); //$NON-NLS-1$
		properties.put("OPENAmount", NumberUtil.formatNumber(summary.getOpenAmount())); //$NON-NLS-1$
		properties.put("VOIDAmount", NumberUtil.formatNumber(summary.getVoidAmount())); //$NON-NLS-1$
		properties.put("PAIDChecks", String.valueOf(summary.getPaidChecks())); //$NON-NLS-1$
		properties.put("TRNGAmount", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("ROPNAmount", NumberUtil.formatNumber(summary.getRopnAmount())); //$NON-NLS-1$
		properties.put("NTaxChecks", String.valueOf(summary.getNtaxChecks())); //$NON-NLS-1$
		properties.put("NTaxAmount", NumberUtil.formatNumber(summary.getNtaxAmount())); //$NON-NLS-1$
		properties.put("MergeAmount", String.valueOf(" ")); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("Labor", NumberUtil.formatNumber(summary.getLaborCost())); //$NON-NLS-1$
		properties.put("LaborCost", NumberUtil.formatNumber((summary.getLaborCost() / summary.getGrossSale()) * 100)); //$NON-NLS-1$

		JasperReport report = ReportUtil.getReport("sales_summary_report1"); //$NON-NLS-1$
		JasperPrint print = JasperFillManager.fillReport(report, properties, new JRTableModelDataSource(new ShiftwiseDataTableModel(summary.getSalesTableDataList())));
		openReport(print);

	}

	private void openReport(JasperPrint print) {
		JRViewer viewer = new JRViewer(print);
		reportPanel.removeAll();
		reportPanel.add(viewer);
		reportPanel.revalidate();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnGo;
	private javax.swing.JComboBox cbTerminal;
	private javax.swing.JComboBox cbUserType;
	private org.jdesktop.swingx.JXDatePicker fromDatePicker;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JPanel reportPanel;
	private org.jdesktop.swingx.JXDatePicker toDatePicker;
	// End of variables declaration//GEN-END:variables
    // 20170714, pymancer 24 hour clock format option
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat(DateUtil.getDatePattern("yyyy MMM dd, hh:mm a")); //$NON-NLS-1$
	private SimpleDateFormat shortDateFormatter = new SimpleDateFormat("yyyy MMM dd"); //$NON-NLS-1$
	
	private Date fromDate;
	private Date toDate;
	private int dateDiff;
	private UserType userType;
	private Terminal terminal;

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

}
