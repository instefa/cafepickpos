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
package ru.instefa.cafepickpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.config.AppProperties;
import ru.instefa.cafepickpos.model.GuestCheckPrint;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.dao.GuestCheckPrintDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.swing.BeanTableModel;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.TitlePanel;

public class GuestCheckTktFirstOpenDialog extends POSDialog {

	private BeanTableModel<Ticket> tableModel;

	public GuestCheckTktFirstOpenDialog() {
		initComponents();
	}

	private void initComponents() {
		setTitle(AppProperties.getAppName());
		setLayout(new MigLayout("fill"));
		JPanel mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, "grow");

		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("GuestCheckTktFirstOpenDialog.0"));

		JTable table = new JTable();
		tableModel = new BeanTableModel<Ticket>(Ticket.class);
		tableModel.addColumn(Messages.getString("GuestChkBillDialog.1"), "id");
		tableModel.addColumn(Messages.getString("GuestChkBillDialog.2"), "tableNumbers");
		tableModel.addColumn(Messages.getString("GuestChkBillDialog.3"), "owner");
		tableModel.addColumn(Messages.getString("GuestCheckTktFirstOpenDialog.1"), "createDate");
		tableModel.addColumn(Messages.getString("GuestChkBillDialog.4"), "diffWithCrntTime");

		table.setModel(tableModel);
		table.setRowHeight(60);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());

		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof Integer) {
					lbl.setHorizontalAlignment(JLabel.LEFT);
					return lbl;
				}

				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});

		table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				if (value instanceof Date) {
					Date date = (Date) value;
					// 20170906, pymancer, 24 hour clock format option
					SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.getDatePattern("hh:mm a"));
					return super.getTableCellRendererComponent(table, dateFormat.format(date), isSelected, hasFocus, row, column);
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});

		//		resizeColumnWidth(table);

		JPanel bottomPanel = new JPanel(new MigLayout("center"));
		PosButton btnDone = new PosButton(Messages.getString("GuestChkBillDialog.5"));
		btnDone.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		bottomPanel.add(btnDone);

		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

	}

	public void setData(List<Ticket> tickets) {
		if (tickets != null)
			tableModel.setRows(tickets);
	}

	public void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			columnModel.getColumn(column).setPreferredWidth((Integer) getColumnWidth().get(column));
		}
	}

	private List getColumnWidth() {
		List<Integer> columnWidth = new ArrayList();
		columnWidth.add(100);
		columnWidth.add(200);
		columnWidth.add(200);

		return columnWidth;
	}

}