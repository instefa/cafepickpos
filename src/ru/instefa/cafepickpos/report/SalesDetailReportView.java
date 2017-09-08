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
package ru.instefa.cafepickpos.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JRViewer;

import org.jdesktop.swingx.JXDatePicker;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.report.service.ReportService;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.util.UiUtil;

public class SalesDetailReportView extends JPanel {
    // 20170714, pymancer 24 hour clock format option
	private SimpleDateFormat fullDateFormatter = new SimpleDateFormat(DateUtil.getDatePattern("yyyy MMM dd, hh:mm a")); //$NON-NLS-1$
	private SimpleDateFormat shortDateFormatter = new SimpleDateFormat("yyyy MMM dd"); //$NON-NLS-1$
	
	private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
	private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
	private JButton btnGo = new JButton(ru.instefa.cafepickpos.POSConstants.GO);
	private JPanel reportContainer;
	
	public SalesDetailReportView() {
		super(new BorderLayout());
		
		JPanel topPanel = new JPanel(new MigLayout());
		
		topPanel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.FROM + ":"), "grow"); //$NON-NLS-1$ //$NON-NLS-2$
		topPanel.add(fromDatePicker,"wrap"); //$NON-NLS-1$
		topPanel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.TO + ":"), "grow"); //$NON-NLS-1$ //$NON-NLS-2$
		topPanel.add(toDatePicker,"wrap"); //$NON-NLS-1$
		topPanel.add(btnGo, "skip 1, al right"); //$NON-NLS-1$
		add(topPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new EmptyBorder(0, 10,10,10));
		centerPanel.add(new JSeparator(), BorderLayout.NORTH);
		
		reportContainer = new JPanel(new BorderLayout());
		centerPanel.add(reportContainer);
		
		add(centerPanel);
		
		btnGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					viewReport();
				} catch (Exception e1) {
					POSMessageDialog.showError(SalesDetailReportView.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}
			
		});
	}
	
	private void viewReport() throws Exception {
		Date fromDate = fromDatePicker.getDate();
		Date toDate = toDatePicker.getDate();
		
		if(fromDate.after(toDate)) {
			POSMessageDialog.showError(ru.instefa.cafepickpos.util.POSUtil.getFocusedWindow(), ru.instefa.cafepickpos.POSConstants.FROM_DATE_CANNOT_BE_GREATER_THAN_TO_DATE_);
			return;
		}
		
		fromDate = DateUtil.startOfDay(fromDate);
		toDate = DateUtil.endOfDay(toDate);
		
		ReportService reportService = new ReportService();
		SalesDetailedReport report = reportService.getSalesDetailedReport(fromDate, toDate);
		
		JasperReport drawerPullReport = ReportUtil.getReport("sales_summary_balance_detailed__1"); //$NON-NLS-1$
		JasperReport creditCardReport = ReportUtil.getReport("sales_summary_balance_detailed_2"); //$NON-NLS-1$
		
		HashMap map = new HashMap();
		ReportUtil.populateRestaurantProperties(map);
		map.put("fromDate", shortDateFormatter.format(fromDate)); //$NON-NLS-1$
		map.put("toDate", shortDateFormatter.format(toDate)); //$NON-NLS-1$
		map.put("reportTime", fullDateFormatter.format(new Date())); //$NON-NLS-1$
		map.put("giftCertReturnCount", report.getGiftCertReturnCount()); //$NON-NLS-1$
		map.put("giftCertReturnAmount", report.getGiftCertReturnAmount()); //$NON-NLS-1$
		map.put("giftCertChangeCount", report.getGiftCertChangeCount()); //$NON-NLS-1$
		map.put("giftCertChangeAmount", report.getGiftCertChangeAmount()); //$NON-NLS-1$
		map.put("tipsCount", report.getTipsCount()); //$NON-NLS-1$
		map.put("tipsAmount", report.getChargedTips()); //$NON-NLS-1$
		map.put("tipsPaidAmount", report.getTipsPaid()); //$NON-NLS-1$
		map.put("drawerPullReport", drawerPullReport); //$NON-NLS-1$
		map.put("drawerPullDatasource", new JRTableModelDataSource(report.getDrawerPullDataTableModel())); //$NON-NLS-1$
		map.put("creditCardReport", creditCardReport); //$NON-NLS-1$
		map.put("creditCardReportDatasource", new JRTableModelDataSource(report.getCreditCardDataTableModel())); //$NON-NLS-1$
		
		JasperReport jasperReport = ReportUtil.getReport("sales_summary_balace_detail"); //$NON-NLS-1$
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JREmptyDataSource());
		JRViewer viewer = new JRViewer(jasperPrint);
		reportContainer.removeAll();
		reportContainer.add(viewer);
		reportContainer.revalidate();
		
	}
}
