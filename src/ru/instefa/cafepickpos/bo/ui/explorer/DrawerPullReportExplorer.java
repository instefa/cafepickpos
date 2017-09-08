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
package ru.instefa.cafepickpos.bo.ui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.model.DrawerPullReport;
import ru.instefa.cafepickpos.model.dao.DrawerPullReportDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.print.PosPrintService;
import ru.instefa.cafepickpos.swing.ListTableModel;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.util.UiUtil;

public class DrawerPullReportExplorer extends TransparentPanel {
	private JXDatePicker fromDatePicker = UiUtil.getCurrentMonthStart();
	private JXDatePicker toDatePicker = UiUtil.getCurrentMonthEnd();
	private JButton btnGo = new JButton(ru.instefa.cafepickpos.POSConstants.GO);
	private JButton btnEditActualAmount = new JButton(ru.instefa.cafepickpos.POSConstants.EDIT_ACTUAL_AMOUNT);
	private JButton btnPrint = new JButton(Messages.getString("DrawerPullReportExplorer.0")); //$NON-NLS-1$
    // 20170714, pymancer 24 hour clock format option
	private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DateUtil.getDatePattern("dd MMM, yyyy hh:mm a")); //$NON-NLS-1$
	private TableColumnModelExt columnModel;
	private JXTable table;

	public DrawerPullReportExplorer() {
		super(new BorderLayout());
		add(new JScrollPane(table = new JXTable(new DrawerPullExplorerTableModel(null))));
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setColumnControlVisible(true);

		resizeColumnWidth(table);

		restoreTableColumnsVisibility();
		addTableColumnListener();
		JPanel topPanel = new JPanel(new MigLayout());

		topPanel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.FROM), "grow"); //$NON-NLS-1$
		topPanel.add(fromDatePicker, "wrap"); //$NON-NLS-1$
		topPanel.add(new JLabel(ru.instefa.cafepickpos.POSConstants.TO), "grow"); //$NON-NLS-1$
		topPanel.add(toDatePicker, "wrap"); //$NON-NLS-1$
		topPanel.add(btnGo, "skip 1, al right"); //$NON-NLS-1$
		add(topPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(btnEditActualAmount);
		bottomPanel.add(btnPrint);
		add(bottomPanel, BorderLayout.SOUTH);

		btnPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow < 0) {
					BOMessageDialog.showError(DrawerPullReportExplorer.this, Messages.getString("DrawerPullReportExplorer.1")); //$NON-NLS-1$
					return;
				}
				DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel) table.getModel();
				DrawerPullReport report = (DrawerPullReport) model.getRowData(selectedRow);

				PosPrintService.printDrawerPullReport(report, report.getTerminal());
			}
		});

		btnGo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					viewReport();
					resizeColumnWidth(table);
				} catch (Exception e1) {
					BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}

		});
		btnEditActualAmount.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					int selectedRow = table.getSelectedRow();
					if (selectedRow < 0) {
						BOMessageDialog.showError(DrawerPullReportExplorer.this, ru.instefa.cafepickpos.POSConstants.SELECT_DRAWER_PULL_TO_EDIT);
						return;
					}

					String amountString = JOptionPane.showInputDialog(DrawerPullReportExplorer.this,
							ru.instefa.cafepickpos.POSConstants.ENTER_ACTUAL_AMOUNT + ":"); //$NON-NLS-1$
					if (amountString == null) {
						return;
					}
					double amount = 0;
					try {
						amount = Double.parseDouble(amountString);
					} catch (Exception x) {
						BOMessageDialog.showError(DrawerPullReportExplorer.this, ru.instefa.cafepickpos.POSConstants.INVALID_AMOUNT);
						return;
					}

					DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel) table.getModel();
					DrawerPullReport report = (DrawerPullReport) model.getRowData(selectedRow);
					report.setCashToDeposit(amount);

					DrawerPullReportDAO dao = new DrawerPullReportDAO();
					dao.saveOrUpdate(report);
					model.updateItem(selectedRow);
				} catch (Exception e1) {
					BOMessageDialog.showError(DrawerPullReportExplorer.this, POSConstants.ERROR_MESSAGE, e1);
				}
			}

		});
	}

	private void restoreTableColumnsVisibility() {
		String recordedSelectedColumns = TerminalConfig.getDrawerPullReportHiddenColumns();
		TableColumnModelExt columnModel = (TableColumnModelExt) table.getColumnModel();

		if (recordedSelectedColumns.isEmpty()) {
			return;
		}
		String str[] = recordedSelectedColumns.split("\\*"); //$NON-NLS-1$
		for (int i = 0; i < str.length; i++) {
			Integer columnIndex = Integer.parseInt(str[i]);
			columnModel.getColumnExt((columnIndex - i)).setVisible(false);
		}
	}

	private void viewReport() {
		try {
			Date fromDate = fromDatePicker.getDate();
			Date toDate = toDatePicker.getDate();

			fromDate = DateUtil.startOfDay(fromDate);
			toDate = DateUtil.endOfDay(toDate);

			List<DrawerPullReport> list = new DrawerPullReportDAO().findReports(fromDate, toDate);
			DrawerPullExplorerTableModel model = (DrawerPullExplorerTableModel) table.getModel();
			model.setRows(list);
		} catch (Exception e) {
			BOMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
		}
	}

	class DrawerPullExplorerTableModel extends ListTableModel {
		String[] columnNames = { ru.instefa.cafepickpos.POSConstants.ID.toLowerCase(),
			ru.instefa.cafepickpos.POSConstants.TIME.toLowerCase(),
			Messages.getString("DPRE.2"), ru.instefa.cafepickpos.POSConstants.DRAWER_PULL_AMOUNT.toLowerCase(),
			Messages.getString("DPRE.3"), Messages.getString("DPRE.4"), Messages.getString("DPRE.5"),
			Messages.getString("DPRE.6"), Messages.getString("DPRE.7"), Messages.getString("DPRE.8"),
			Messages.getString("DPRE.9"), Messages.getString("DPRE.10"),
			Messages.getString("DPRE.11"), Messages.getString("DPRE.12"),
			Messages.getString("DPRE.13"), Messages.getString("DPRE.14"),
			Messages.getString("DPRE.15"), Messages.getString("DPRE.16"),
			Messages.getString("DPRE.17"), Messages.getString("DPRE.18"),
			Messages.getString("DPRE.19"), Messages.getString("DPRE.20"),
			Messages.getString("DPRE.21"), Messages.getString("DPRE.22"), Messages.getString("DPRE.23"),
			Messages.getString("DPRE.24"), Messages.getString("DPRE.25"), Messages.getString("DPRE.26"),
			Messages.getString("DPRE.27"), Messages.getString("DPRE.28"), Messages.getString("DPRE.29"),
			Messages.getString("DPRE.30"), Messages.getString("DPRE.31"),
			ru.instefa.cafepickpos.POSConstants.ACTUAL_AMOUNT.toLowerCase(), Messages.getString("DPRE.32"),
			Messages.getString("DPRE.33"), Messages.getString("DPRE.34"), Messages.getString("DPRE.35"),
			Messages.getString("DPRE.36"), Messages.getString("DPRE.37"),
			Messages.getString("DPRE.38"), Messages.getString("DPRE.39"),
			Messages.getString("DPRE.40"), Messages.getString("DPRE.41"),
			Messages.getString("DPRE.42") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		DrawerPullExplorerTableModel(List<DrawerPullReport> list) {
			setRows(list);
			setColumnNames(columnNames);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			DrawerPullReport report = (DrawerPullReport) rows.get(rowIndex);

			switch (columnIndex) {

				case 0:
					return report.getId().toString();

				case 1:
					return dateTimeFormatter.format(report.getReportTime());

				case 2:
					return report.getTicketCount();

				case 3:
					return report.getDrawerAccountable();

				case 4:
					return report.getAssignedUser().getUserId();

				case 5:
					return report.getTerminal().getId();

				case 6:
					return report.getBeginCash();

				case 7:
					return report.getNetSales();

				case 8:
					return report.getSalesTax();

				case 9:
					return report.getCashTax();

				case 10:
					return report.getTotalRevenue();

				case 11:
					return report.getGrossReceipts();

				case 12:
					return report.getGiftCertReturnCount();

				case 13:
					return report.getGiftCertReturnAmount();

				case 14:
					return report.getGiftCertChangeAmount();

				case 15:
					return report.getCashReceiptNumber();

				case 16:
					return report.getCashReceiptAmount();

				case 17:
					return report.getCreditCardReceiptNumber();

				case 18:
					return report.getCreditCardReceiptAmount();

				case 19:
					return report.getDebitCardReceiptNumber();

				case 20:
					return report.getDebitCardReceiptAmount();

				case 21:
					return report.getRefundReceiptCount();

				case 22:
					return report.getRefundAmount();

				case 23:
					return report.getReceiptDifferential();

				case 24:
					return report.getCashBack();

				case 25:
					return report.getCashTips();

				case 26:
					return report.getChargedTips();

				case 27:
					return report.getTipsPaid();

				case 28:
					return report.getTipsDifferential();

				case 29:
					return report.getPayOutNumber();

				case 30:
					return report.getPayOutAmount();

				case 31:
					return report.getDrawerBleedNumber();

				case 32:
					return report.getDrawerBleedAmount();

				case 33:
					return report.getCashToDeposit();

				case 34:
					return report.getVariance();

				case 35:
					return report.getTotalVoidWst();

				case 36:
					return report.getTotalVoid();

				case 37:
					return report.getTotalDiscountCount();

				case 38:
					return report.getTotalDiscountAmount();

				case 39:
					return report.getTotalDiscountSales();

				case 40:
					return report.getTotalDiscountGuest();

				case 41:
					return report.getTotalDiscountPartySize();

				case 42:
					return report.getTotalDiscountCheckSize();

				case 43:
					return report.getTotalDiscountPercentage();

				case 44:
					return report.getTotalDiscountRatio();

			}
			return null;
		}
	}

	public void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {

			int columnWidthByComponent = getColumnWidthByComponentLenght(table, column);
			int columnWidthByHeader = getColumnWidthByHeaderLenght(table, column);
			if (columnWidthByComponent > columnWidthByHeader) {
				columnModel.getColumn(column).setPreferredWidth(columnWidthByComponent);
			}
			else {
				columnModel.getColumn(column).setPreferredWidth(columnWidthByHeader);
			}
		}
	}

	private int getColumnWidthByHeaderLenght(JTable table, int column) {
		int width = 50;
		TableColumn tcolumn = table.getColumnModel().getColumn(column);
		TableCellRenderer headerRenderer = tcolumn.getHeaderRenderer();

		if (headerRenderer == null) {
			headerRenderer = table.getTableHeader().getDefaultRenderer();
		}
		Object headerValue = tcolumn.getHeaderValue();
		Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, column);

		width = Math.max(width, headerComp.getPreferredSize().width);

		return width + 20;
	}

	private int getColumnWidthByComponentLenght(JTable table, int column) {
		int width = 50;
		for (int row = 0; row < table.getRowCount(); row++) {
			TableCellRenderer renderer = table.getCellRenderer(row, column);
			Component comp = table.prepareRenderer(renderer, row, column);
			width = Math.max(comp.getPreferredSize().width + 1, width);
		}
		return width + 20;
	}

	private void saveHiddenColumns() {
		List<TableColumn> columns = columnModel.getColumns(true);
		List<Integer> indices = new ArrayList<Integer>();
		for (TableColumn tableColumn : columns) {
			TableColumnExt c = (TableColumnExt) tableColumn;
			if (!c.isVisible()) {
				indices.add(c.getModelIndex());
			}
		}
		saveTableColumnsVisibility(indices);
	}

	private void saveTableColumnsVisibility(List indices) {
		String selectedColumns = ""; //$NON-NLS-1$
		for (Iterator iterator = indices.iterator(); iterator.hasNext();) {
			String newSelectedColumn = String.valueOf(iterator.next());
			selectedColumns += newSelectedColumn;

			if (iterator.hasNext()) {
				selectedColumns += "*"; //$NON-NLS-1$
			}
		}
		TerminalConfig.setDrawerPullReportHiddenColumns(selectedColumns);
	}

	private void addTableColumnListener() {
		columnModel = (TableColumnModelExt) table.getColumnModel();
		columnModel.addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				saveHiddenColumns();
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {

			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
				saveHiddenColumns();
			}
		});

	}
}
