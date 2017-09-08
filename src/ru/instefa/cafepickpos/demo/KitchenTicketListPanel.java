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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.KitchenTicket;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.dao.KitchenTicketDAO;
import ru.instefa.cafepickpos.swing.PaginatedListModel;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class KitchenTicketListPanel extends JPanel implements ComponentListener {
	private final static int HORIZONTAL_GAP = 5;
	private final static int VERTICAL_GAP = 5;

	protected PaginatedListModel dataModel = new PaginatedListModel();
	protected Dimension buttonSize = PosUIManager.getSize(350, 240);
	protected JPanel selectionButtonsPanel;

	protected TitledBorder border;
	protected JPanel actionButtonPanel = new JPanel(new MigLayout("center,fillx,hidemode 3, ins 0 8 0 5", "sg, fill", "[30]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	protected ru.instefa.cafepickpos.swing.PosButton btnNext;
	protected ru.instefa.cafepickpos.swing.PosButton btnPrev;
	private String kdsPrinter;
	private int horizontalPanelCount = 4;
	private OrderType orderType;

	public KitchenTicketListPanel() {
		this.selectionButtonsPanel = new JPanel(new MigLayout("fillx")) { //$NON-NLS-1$
			@Override
			public void remove(Component comp) {
				updateKDSView(kdsPrinter, orderType);
			}
		};
		setLayout(new BorderLayout(HORIZONTAL_GAP, VERTICAL_GAP));
		border = new TitledBorder(""); //$NON-NLS-1$
		border.setTitleJustification(TitledBorder.CENTER);
		setBorder(new CompoundBorder(border, new EmptyBorder(2, 2, 2, 2)));
		add(selectionButtonsPanel);

		btnPrev = new PosButton();
		btnPrev.setText(POSConstants.CAPITAL_PREV + "<<"); //$NON-NLS-1$
		actionButtonPanel.add(btnPrev, "grow, align center"); //$NON-NLS-1$

		btnNext = new PosButton();
		btnNext.setText(POSConstants.CAPITAL_NEXT + ">>"); //$NON-NLS-1$
		actionButtonPanel.add(btnNext, "grow, align center"); //$NON-NLS-1$

		add(actionButtonPanel, BorderLayout.SOUTH);

		ScrollAction action = new ScrollAction();
		btnPrev.addActionListener(action);
		btnNext.addActionListener(action);

		addComponentListener(this);
		btnNext.setEnabled(false);
		btnPrev.setEnabled(false);
	}

	public void checkNewKitchenTicket(KitchenTicket ticket) {
	}

	public void updateKDSView(String selectedPrinter, OrderType orderType) {
		this.kdsPrinter = selectedPrinter;
		this.orderType = orderType;
		reset();
		try {
			dataModel.setPageSize(TerminalConfig.getKDSTicketsPerPage());
			dataModel.setNumRows(KitchenTicketDAO.getInstance().getRowCount(selectedPrinter, orderType));
			KitchenTicketDAO.getInstance().loadKitchenTickets(selectedPrinter, orderType, dataModel);
			setDataModel(dataModel);
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
		}
	}

	public void setTitle(String title) {
		border.setTitle(title);
	}

	public void setDataModel(PaginatedListModel items) {
		this.dataModel = items;
		renderItems();
	}

	protected void updateButton() {
		boolean hasNext = dataModel.hasNext();
		boolean hasPrevious = dataModel.hasPrevious();
		btnNext.setVisible(hasNext || hasPrevious);
		btnPrev.setVisible(hasNext || hasPrevious);
		btnPrev.setEnabled(hasPrevious);
		btnNext.setEnabled(hasNext);
	}

	public PaginatedListModel getDataModel() {
		return dataModel;
	}

	public Dimension getButtonSize() {
		return buttonSize;
	}

	public void setButtonSize(Dimension buttonSize) {
		this.buttonSize = buttonSize;
	}

	public void reset() {
		Component[] components = selectionButtonsPanel.getComponents();
		for (Component component : components) {
			if (component instanceof KitchenTicketView) {
				KitchenTicketView kitchenTicketView = (KitchenTicketView) component;
				kitchenTicketView.stopTimer();
			}
		}
		selectionButtonsPanel.removeAll();
		selectionButtonsPanel.repaint();
		btnNext.setEnabled(false);
		btnPrev.setEnabled(false);
	}

	protected int getCount(int containerSize, int itemSize) {
		int panelCount = containerSize / itemSize;
		return panelCount;
	}

	protected int countPanels() {
		Dimension size = Application.getInstance().getRootView().getSize();
		horizontalPanelCount = getCount(size.width, 330);
		int verticalPanelCount = getCount(size.height, 280);
		int totalItem = horizontalPanelCount * verticalPanelCount;
		return totalItem;
	}

	protected void renderItems() {
		try {
			reset();

			if (this.dataModel == null || dataModel.getSize() == 0) {
				btnNext.setEnabled(dataModel.hasNext());
				btnPrev.setEnabled(dataModel.hasPrevious());
				return;
			}
			String parentConstraints = "filly"; //$NON-NLS-1$
			if (horizontalPanelCount >= 3 && dataModel.getSize() >= 3) {
				parentConstraints = "fill"; //$NON-NLS-1$
			}
			selectionButtonsPanel.setLayout(new MigLayout(parentConstraints + ", wrap " + horizontalPanelCount, "sg, fill", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			for (int i = 0; i < dataModel.getSize(); i++) {
				Object item = dataModel.getElementAt(i);
				JPanel itemPanel = createKitchenTicket(item, i);
				if (itemPanel == null) {
					continue;
				}
				String contraints = "growy"; //$NON-NLS-1$
				if (horizontalPanelCount >= 3 && dataModel.getSize() >= 3) {
					contraints = "grow"; //$NON-NLS-1$
				}
				selectionButtonsPanel.add(itemPanel, contraints);
			}
			btnNext.setEnabled(dataModel.hasNext());
			btnPrev.setEnabled(dataModel.hasPrevious());
			revalidate();
			repaint();
		} catch (Exception e) {
			POSMessageDialog.showError(this, e.getMessage(), e);
		}
	}

	private class ScrollAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			try {
				Object source = e.getSource();
				if (source == btnPrev) {
					scrollUp();
				}
				else if (source == btnNext) {
					scrollDown();
				}
			} catch (Exception e2) {
				POSMessageDialog.showError(Application.getPosWindow(), e2.getMessage(), e2);
			}
		}

	}

	public JPanel getButtonsPanel() {
		return selectionButtonsPanel;
	}

	public AbstractButton getFirstItemButton() {
		int componentCount = selectionButtonsPanel.getComponentCount();
		if (componentCount == 0) {
			return null;
		}
		return (AbstractButton) selectionButtonsPanel.getComponent(0);
	}

	protected int getButtonCount(int containerSize, int itemSize) {
		int buttonCount = containerSize / (itemSize + 10);
		return buttonCount;
	}

	protected void scrollDown() {
		dataModel.setCurrentRowIndex(dataModel.getNextRowIndex());
		updateKDSView(kdsPrinter, orderType);
	}

	protected void scrollUp() {
		dataModel.setCurrentRowIndex(dataModel.getPreviousRowIndex());
		updateKDSView(kdsPrinter, orderType);
	}

	protected JPanel createKitchenTicket(Object item, int index) {
		KitchenTicketView kitchenTicketView = new KitchenTicketView((KitchenTicket) item);
		kitchenTicketView.putClientProperty("key", (index + 1)); //$NON-NLS-1$
		return kitchenTicketView;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (!KitchenDisplayView.getInstance().isVisible())
			return;
		countPanels();
		updateKDSView(kdsPrinter, orderType);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
}
