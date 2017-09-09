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
package ru.instefa.cafepickpos.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.model.KitchenTicket;
import ru.instefa.cafepickpos.model.KitchenTicket.KitchenTicketStatus;
import ru.instefa.cafepickpos.model.KitchenTicketItem;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.dao.KitchenTicketDAO;
import ru.instefa.cafepickpos.model.dao.KitchenTicketItemDAO;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.swing.ListTableModel;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.TimerWatch;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class KitchenTicketView extends JPanel {
	private KitchenTicket kitchenTicket;
	private JLabel ticketId = new JLabel();
	private KitchenTicketTableModel tableModel;
	private JTable table;
	private KitchenTicketStatusSelector statusSelector;
	private TimerWatch timerWatch;
	private JScrollPane scrollPane;

	private JPanel headerPanel;

	private JLabel ticketInfo;
	private JLabel tableInfo;
	private JLabel serverInfo;
	private BumpButton btnDone;

	public KitchenTicketView(KitchenTicket ticket) {
		this.kitchenTicket = ticket;
		setLayout(new BorderLayout(1, 1));
		setBackground(Color.black);

		createHeader(ticket);
		createTable(ticket);
		createButtonPanel();

		statusSelector = new KitchenTicketStatusSelector((Frame) SwingUtilities.getWindowAncestor(this), ticket);
		statusSelector.pack();

		setPreferredSize(PosUIManager.getSize(350, 240));

		timerWatch.start();

		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				timerWatch.stop();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}
		});
	}

	public void stopTimer() {
		timerWatch.stop();
	}

	private void createHeader(KitchenTicket ticket) {
		String printerName = ticket.getPrinterName();
		if (printerName == null) {
			printerName = "";
		}

		ticketInfo = new JLabel(Messages.getString("KitchenTicketView.19") + ticket.getTicketId() + "-" +
								ticket.getSequenceNumber() + " " + printerName + "");

		tableInfo = new JLabel();
		if (ticket.getTableNumbers() != null && ticket.getTableNumbers().size() > 0) {
			String tableNumbers = ticket.getTableNumbers().toString();
			tableNumbers = tableNumbers.replace("[", "").replace("]", ""); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$  //$NON-NLS-4$
			tableInfo.setText(Messages.getString("TicketView.51") + " " + tableNumbers); //$NON-NLS-1$
		}
		serverInfo = new JLabel();
		if (ticket.getServerName() != null) {
			serverInfo.setText(Messages.getString("KitchenTicketView.20") + " " + ticket.getServerName());
		}

		Font font = getFont().deriveFont(Font.BOLD, 13f);

		ticketInfo.setFont(font);
		tableInfo.setFont(font);
		serverInfo.setFont(font);

		timerWatch = new TimerWatch(ticket.getCreateDate());
		timerWatch.setBackground(Color.black);
		//timerWatch.setPreferredSize(new Dimension(100, 30));

		headerPanel = new JPanel(new MigLayout("fill", "sg, fill", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		headerPanel.setBackground(Color.black);
		headerPanel.setBorder(BorderFactory.createLineBorder(Color.white));
		headerPanel.add(ticketInfo, "split 2"); //$NON-NLS-1$
		ticketInfo.setFont(ticketInfo.getFont().deriveFont(Font.BOLD, 13f));
		headerPanel.add(timerWatch, "right,wrap"); //$NON-NLS-1$
		headerPanel.add(tableInfo, "split 2, grow"); //$NON-NLS-1$
		headerPanel.add(serverInfo, "right,span"); //$NON-NLS-1$

		updateHeaderView();
		add(headerPanel, BorderLayout.NORTH);
	}

	private void createTable(KitchenTicket ticket) {
		tableModel = new KitchenTicketTableModel(ticket.getTicketItems());
		table = new JTable(tableModel);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setRowHeight(30);
		table.setTableHeader(null);
		table.setOpaque(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setShowGrid(false);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				KitchenTicketItem ticketItem = tableModel.getRowData(row);

				if (ticketItem != null && ticketItem.getStatus() != null) {
					if (ticketItem.getStatus().equalsIgnoreCase(KitchenTicketStatus.DONE.name())) {
						rendererComponent.setBackground(Color.green);
					}
					else if (ticketItem.getStatus().equalsIgnoreCase(KitchenTicketStatus.VOID.name())) {
						rendererComponent.setBackground(new Color(128, 0, 128));
					}
					else {
						rendererComponent.setBackground(Color.black);
						rendererComponent.setForeground(Color.white);
					}
				}
				Font font = getFont().deriveFont(Font.BOLD, 16f);
				rendererComponent.setFont(font);
				if (column == 1) {
					if (ticketItem.getQuantity() <= 0) {
						return new JLabel();
					}
				}

				updateHeaderView();
				return rendererComponent;
			}
		});
		resizeTableColumns();

		AbstractAction action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = Integer.parseInt(e.getActionCommand());
				KitchenTicketItem ticketItem = tableModel.getRowData(row);
				if (!ticketItem.isCookable()) {
					return;
				}
				statusSelector.setTicketItem(ticketItem);
				statusSelector.setLocationRelativeTo(KitchenTicketView.this);
				statusSelector.setVisible(true);
				table.repaint();
			}
		};

		new CustomButtonColumn(table, action, 2) {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				KitchenTicketItem ticketItem = tableModel.getRowData(row);
				if (ticketItem.getQuantity() <= 0) {
					JLabel jLabel = new JLabel();
					jLabel.setOpaque(false);
					return jLabel;
				}
				Component tableCellRendererComponent = (Component) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Font font = getFont().deriveFont(Font.BOLD, 12f);
				tableCellRendererComponent.setFont(font);
				return tableCellRendererComponent;
			}

			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
				KitchenTicketItem ticketItem = tableModel.getRowData(row);
				if (ticketItem.getQuantity() <= 0) {
					return new JLabel();
				}
				Component tableCellEditorComponent = (Component) super.getTableCellEditorComponent(table, value, isSelected, row, column);
				Font font = getFont().deriveFont(Font.BOLD, 12f);
				tableCellEditorComponent.setFont(font);
				return tableCellEditorComponent;
			}

		};
		scrollPane = new JScrollPane(table);
		scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.white));
		scrollPane.getViewport().setBackground(Color.black);
		add(scrollPane);
	}

	private void updateHeaderView() {
		headerPanel.setBackground(timerWatch.backColor);
		ticketInfo.setForeground(timerWatch.textColor);
		tableInfo.setForeground(timerWatch.textColor);
		serverInfo.setForeground(timerWatch.textColor);
	}

	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));

		PosButton btnVoid = new PosButton(Messages.getString("KitchenTicketView.12")); //$NON-NLS-1$
		btnVoid.setPreferredSize(PosUIManager.getSize(100, 40));

		btnVoid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeTicket(KitchenTicketStatus.VOID);
			}
		});
		//buttonPanel.add(btnVoid);

		btnDone = new BumpButton(POSConstants.BUMP);
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		btnDone.setPreferredSize(PosUIManager.getSize(100, 40));

		buttonPanel.add(btnDone);
		buttonPanel.setOpaque(false);

		//		PosButton btnPrint = new PosButton("PRINT");
		//		btnPrint.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				//KitchenDisplay.instance.removeTicket(KitchenTicketView.this);
		//			}
		//		});
		//		buttonPanel.add(btnPrint);
		//		
		//		PosButton btnUp = new PosButton("UP");
		//		btnUp.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		//				scrollBar.setValue(scrollBar.getValue() - 50);
		//			}
		//		});
		//		buttonPanel.add(btnUp);
		//		
		//		PosButton btnDown = new PosButton("DOWN");
		//		btnDown.addActionListener(new ActionListener() {
		//			@Override
		//			public void actionPerformed(ActionEvent e) {
		//				JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		//				scrollBar.setValue(scrollBar.getValue() + 50);
		//			}
		//		});
		//		buttonPanel.add(btnDown);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void resizeTableColumns() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setColumnWidth(1, PosUIManager.getSize(40));
		setColumnWidth(2, PosUIManager.getSize(60));
	}

	private void setColumnWidth(int columnNumber, int width) {
		TableColumn column = table.getColumnModel().getColumn(columnNumber);

		column.setPreferredWidth(width);
		column.setMaxWidth(width);
		column.setMinWidth(width);
	}

	class KitchenTicketTableModel extends ListTableModel<KitchenTicketItem> {

		KitchenTicketTableModel(List<KitchenTicketItem> list) {
			super(new String[] { Messages.getString("KitchenTicketView.13"), Messages.getString("KitchenTicketView.14"), "" }, list); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 2) {
				return true;
			}

			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			KitchenTicketItem ticketItem = getRowData(rowIndex);

			switch (columnIndex) {
				case 0:
					return ticketItem.getMenuItemName();

				case 1:
					if (ticketItem.isFractionalUnit()) {

						double itemQuantity = ticketItem.getFractionalQuantity();

						if (itemQuantity % 1 == 0) {
							return String.valueOf((int) itemQuantity) + ticketItem.getUnitName();
						}
						return String.valueOf(itemQuantity) + ticketItem.getUnitName();
					}
					return String.valueOf(ticketItem.getQuantity());
				case 2:
					return POSConstants.BUMP;
			}
			return null;
		}

	}

	public KitchenTicket getTicket() {
		return kitchenTicket;
	}

	public void setTicket(KitchenTicket ticket) {
		this.kitchenTicket = ticket;
	}

	private void closeTicket(KitchenTicketStatus status) {
		try {
			stopTimer();

			kitchenTicket.setStatus(status.name());
			kitchenTicket.setClosingDate(new Date());

			Ticket parentTicket = TicketDAO.getInstance().load(kitchenTicket.getTicketId());

			Transaction tx = null;
			Session session = null;

			try {
				session = KitchenTicketItemDAO.getInstance().createNewSession();
				tx = session.beginTransaction();
				for (KitchenTicketItem kitchenTicketItem : kitchenTicket.getTicketItems()) {
					kitchenTicketItem.setStatus(status.name());
					//Question: Do we actually need status in original ticket item?
					int itemCount = kitchenTicketItem.getQuantity();

					for (TicketItem item : parentTicket.getTicketItems()) {
						if (kitchenTicketItem.getMenuItemCode() != null && kitchenTicketItem.getMenuItemCode().equals(item.getItemCode())) {
							if (item.getStatus() != null && item.getStatus().equals(Ticket.STATUS_READY)) {
								continue;
							}
							if (itemCount == 0) {
								break;
							}
							if (status.equals(KitchenTicketStatus.DONE)) {
								item.setStatus(Ticket.STATUS_READY);
							}
							else {
								item.setStatus(Ticket.STATUS_VOID);
							}
							itemCount -= item.getItemCount();
						}
					}
					session.saveOrUpdate(parentTicket);
					session.saveOrUpdate(kitchenTicketItem);
				}
				tx.commit();

			} catch (Exception ex) {
				PosLog.error(getClass(), ex);
				tx.rollback();
			} finally {
				session.close();
			}

			KitchenTicketDAO.getInstance().saveOrUpdate(kitchenTicket);
			Container parent = this.getParent();
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		} catch (Exception e) {
			POSMessageDialog.showError(KitchenTicketView.this, e.getMessage(), e);
		}
	}

	private class BumpButton extends PosButton implements ActionListener {
		private Boolean keySelected = false;

		public BumpButton(String string) {
			super(string);
			setOpaque(false);
			addActionListener(this);
			setBackground(Color.white);
			setBorder(BorderFactory.createLineBorder(Color.white));
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());

			Color color1 = getBackground();// UIManager.getColor("control");
			Color color2 = color1.brighter();

			int buttonX = 0;
			int buttonY = 0;
			int width = getWidth();
			int height = getHeight();

			GradientPaint gp = new GradientPaint(buttonX, buttonY, color2, width - 2, height - 2, color1, true);
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(gp);
			g2.fillRect(buttonX, buttonY, width, height);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (keySelected) {
				g2.setColor(new Color(0, 160, 0));
			}
			else {
				g2.setColor(new Color(234, 42, 42));
			}
			int h = getHeight() - 10;
			g2.fillOval(10, 4, h, h);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font(getFont().getName(), Font.BOLD, 20));
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(getKey(), g2);
			int x = ((h + 10 - (int) r.getWidth() + 10) / 2);
			int y = (h - (int) r.getHeight()) / 2 + fm.getAscent();
			g2.drawString(getKey(), x, y + 2);
			g2.setFont(getFont());
			g2.setBackground(getBackground());
			super.paintComponent(g2);
		}

		public Boolean isKeySelected() {
			return keySelected;
		}

		public void setKeySelected(Boolean keySelected) {
			this.keySelected = keySelected;
			repaint();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			closeTicket(KitchenTicketStatus.DONE);
		}
	}

	public boolean keySelected(int key) {
		String keyString = KeyEvent.getKeyText(key);
		if (keyString.contains("NumPad-")) {
			keyString = keyString.split("-")[1];
		}
		Object kitchenKey = getClientProperty("key");
		String keyValue = String.valueOf(kitchenKey);
		boolean keySelected = keyValue.equals(keyString);
		btnDone.setKeySelected(keySelected);
		return keySelected;
	}

	public void setSelected(boolean b) {
		btnDone.setKeySelected(b);
	}

	public boolean isKeySelected() {
		return btnDone.isKeySelected();
	}

	public void fireBumpSelected() {
		btnDone.actionPerformed(null);
	}

	protected String getKey() {
		return String.valueOf(getClientProperty("key"));
	}
}
