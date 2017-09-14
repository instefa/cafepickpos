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
package ru.instefa.cafepickpos.ui.tableselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.actions.NewBarTabAction;
import ru.instefa.cafepickpos.bo.ui.explorer.QuickMaintenanceExplorer;
import ru.instefa.cafepickpos.extension.OrderServiceFactory;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.ShopTableStatus;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.dao.ShopTableDAO;
import ru.instefa.cafepickpos.model.dao.ShopTableStatusDAO;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.swing.POSToggleButton;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.ScrollableFlowPanel;
import ru.instefa.cafepickpos.swing.ShopTableButton;
import ru.instefa.cafepickpos.ui.dialog.NumberSelectionDialog2;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.views.order.OrderView;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.ui.views.payment.SplittedTicketSelectionDialog;
import ru.instefa.cafepickpos.exceptions.TicketAlreadyExistsException;
import com.jidesoft.swing.JideScrollPane;

public class DefaultTableSelectionView extends TableSelector implements ActionListener {

	private DefaultListModel<ShopTableButton> addedTableListModel = new DefaultListModel<ShopTableButton>();
	private DefaultListModel<ShopTableButton> removeTableListModel = new DefaultListModel<ShopTableButton>();
	private Map<ShopTable, ShopTableButton> tableButtonMap = new HashMap<ShopTable, ShopTableButton>();

	private ScrollableFlowPanel buttonsPanel;
	private BarTabSelectionView barTab;

	private POSToggleButton btnGroup;
	private POSToggleButton btnUnGroup;

	private static PosButton btnCancelDialog;

	private PosButton btnDone;
	private PosButton btnCancel;
	private PosButton btnRefresh;

	private PosButton btnNewBarTab;

	private ButtonGroup btnGroups;
	private JTabbedPane tabbedPane;

	public DefaultTableSelectionView() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());

		buttonsPanel = new ScrollableFlowPanel(FlowLayout.CENTER);
		barTab = new BarTabSelectionView();

		setLayout(new java.awt.BorderLayout(10, 10));

		TitledBorder titledBorder1 = BorderFactory.createTitledBorder(null, POSConstants.TABLES, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);

		JPanel leftPanel = new JPanel(new java.awt.BorderLayout(5, 5));
		leftPanel.setBorder(new CompoundBorder(titledBorder1, new EmptyBorder(2, 2, 2, 2)));

		//redererTable();

		JideScrollPane scrollPane = new JideScrollPane(buttonsPanel, JideScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JideScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(PosUIManager.getSize(60, 0));

		tabbedPane = new JTabbedPane();

		leftPanel.add(scrollPane, java.awt.BorderLayout.CENTER);
		tabbedPane.addTab(Messages.getString("DefaultTableSelectionView.0"), leftPanel);

		add(tabbedPane, java.awt.BorderLayout.CENTER);

		createButtonActionPanel();
	}

	private void createButtonActionPanel() {
		TitledBorder titledBorder2 = BorderFactory.createTitledBorder(null, "-", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION); //$NON-NLS-1$

		JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
		rightPanel.setPreferredSize(PosUIManager.getSize(120, 0));
		rightPanel.setBorder(new CompoundBorder(titledBorder2, new EmptyBorder(2, 2, 6, 2)));

		JPanel actionBtnPanel = new JPanel(new MigLayout("ins 2 2 0 2, hidemode 3, flowy", "sg fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		btnGroups = new ButtonGroup();

		btnGroup = new POSToggleButton(POSConstants.GROUP); //$NON-NLS-1$
		btnUnGroup = new POSToggleButton(POSConstants.UNGROUP); //$NON-NLS-1$
		btnDone = new PosButton(POSConstants.SAVE_BUTTON_TEXT); //$NON-NLS-1$
		btnCancel = new PosButton(POSConstants.CANCEL); //$NON-NLS-1$

		btnGroup.addActionListener(this);
		btnUnGroup.addActionListener(this);
		btnDone.addActionListener(this);
		btnCancel.addActionListener(this);

		btnDone.setVisible(false);
		btnCancel.setVisible(false);

		btnGroup.setIcon(IconFactory.getDefaultIcon("plus.png")); //$NON-NLS-1$
		btnUnGroup.setIcon(IconFactory.getDefaultIcon("minus_24.png")); //$NON-NLS-1$

		btnGroups.add(btnGroup);
		btnGroups.add(btnUnGroup);

		btnNewBarTab = new PosButton(Messages.getString("DefaultTableSelectionView.1"));
		btnNewBarTab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<ShopTable> selectedTables = getSelectedTables();
				new NewBarTabAction(orderType, selectedTables, Application.getPosWindow()).actionPerformed(e);
			}
		});

		actionBtnPanel.add(btnGroup, "grow"); //$NON-NLS-1$
		actionBtnPanel.add(btnUnGroup, "grow"); //$NON-NLS-1$
		actionBtnPanel.add(btnDone, "grow"); //$NON-NLS-1$
		actionBtnPanel.add(btnCancel, "grow"); //$NON-NLS-1$
		actionBtnPanel.add(btnNewBarTab, "grow"); //$NON-NLS-1$

		rightPanel.add(actionBtnPanel);

		JPanel southbuttonPanel = new JPanel(new MigLayout("ins 2 2 0 2, hidemode 3, flowy", "grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		btnRefresh = new PosButton(POSConstants.REFRESH);
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				redererTables();
			}
		});
		southbuttonPanel.add(btnRefresh, "grow");

		btnCancelDialog = new PosButton(POSConstants.CANCEL);
		btnCancelDialog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog(true);
			}
		});

		southbuttonPanel.add(btnCancelDialog, "grow");
		rightPanel.add(southbuttonPanel, BorderLayout.SOUTH);
		add(rightPanel, java.awt.BorderLayout.EAST);

	}

	public synchronized void redererTables() {
		clearSelection();
		buttonsPanel.getContentPane().removeAll();

		checkTables();

		List<ShopTable> tables = new ArrayList<>();
		tables.addAll(ShopTableDAO.getInstance().findAll());
		if (RootView.getInstance().isMaintenanceMode()) {
			tables.add(new ShopTable(null, 0, 0, null));
		}

		for (ShopTable shopTable : tables) {
			ShopTableButton tableButton = new ShopTableButton(shopTable);
			tableButton.setPreferredSize(PosUIManager.getSize(157, 138));
			tableButton.setFont(new Font(tableButton.getFont().getName(), Font.BOLD, PosUIManager.getTableNumberFontSize()));
			if (shopTable.getId() == null) {
				tableButton.setIcon(IconFactory.getIcon("add-user.png"));
			}
			else
				tableButton.setText(tableButton.getText());
			tableButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (RootView.getInstance().isMaintenanceMode()) {
						ShopTableButton button = (ShopTableButton) e.getSource();
						QuickMaintenanceExplorer.quickMaintain(button.getShopTable());
						return;
					}
					addTable(e);
				}
			});

			tableButton.update();
			buttonsPanel.add(tableButton);
			tableButtonMap.put(shopTable, tableButton);
		}
		barTab.updateView(orderType);
		buttonsPanel.getContentPane().revalidate();
		buttonsPanel.getContentPane().repaint();
	}

	private void addTable(ActionEvent e) {

		ShopTableButton button = (ShopTableButton) e.getSource();
		int tableNumber = button.getId();

		ShopTable shopTable = ShopTableDAO.getInstance().getByNumber(tableNumber);

		if (shopTable == null) {
			POSMessageDialog.showError(this, Messages.getString("TableSelectionDialog.2") + e
                + Messages.getString("TableSelectionDialog.3")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		ShopTableStatus shopTableStatus = shopTable.getShopTableStatus();

		if (btnGroup.isSelected()) {
			if (addedTableListModel.contains(button)) {
				return;
			}
			if (button.getShopTable().isServing()) {
				return;
			}

			button.getShopTable().setServing(true);
			button.setBackground(Color.green);
			button.setForeground(Color.black);
			addedTableListModel.addElement(button);
			return;
		}

		if (btnUnGroup.isSelected()) {
			if (removeTableListModel.contains(button)) {
				return;
			}
			Ticket ticket = button.getTicket();
			if (ticket == null) {
				return;
			}

			int ticketId = ticket.getId();

			Enumeration<ShopTableButton> elements = removeTableListModel.elements();
			while (elements.hasMoreElements()) {
				ShopTableButton shopTableButton = (ShopTableButton) elements.nextElement();
				if (shopTableButton.getTicket().getId() != ticketId) {
					return;
				}
			}

			if (removeTableListModel.size() >= ticket.getTableNumbers().size() - 1) {
				return;
			}

			button.getShopTable().setServing(true);
			button.setBackground(Color.white);
			button.setForeground(Color.black);
			this.removeTableListModel.addElement(button);
			return;
		}
		if (shopTableStatus.hasMultipleTickets() && !btnUnGroup.isSelected()) {
			if (isCreateNewTicket()) {
				showSplitTickets(shopTableStatus, shopTable);
			}
			return;
		}
		if (shopTable.isServing() && !btnGroup.isSelected()) {
			if (!button.hasUserAccess()) {
				return;
			}
			if (isCreateNewTicket()) {
				editTicket(button.getTicket());
				closeDialog(false);
			}
			return;
		}

		if (!btnGroup.isSelected() && !btnGroup.isSelected()) {
			addedTableListModel.clear();
			if (!addedTableListModel.contains(button)) {
				addedTableListModel.addElement(button);
			}

			if (isCreateNewTicket()) {
				doCreateNewTicket();
			}
			closeDialog(false);
		}
	}

	private void showSplitTickets(ShopTableStatus shopTaleStatus, ShopTable table) {
		List<Ticket> splitTickets = new ArrayList<>();
		for (Integer ticketId : shopTaleStatus.getListOfTicketNumbers()) {
			Ticket ticket = TicketDAO.getInstance().get(ticketId);
			if (ticket == null)
				continue;
			splitTickets.add(ticket);
		}
		String action = POSConstants.EDIT;
		SplittedTicketSelectionDialog posDialog = new SplittedTicketSelectionDialog(splitTickets);
		List<ShopTable> selectedTables = getSelectedTables();
		if (selectedTables.isEmpty()) {
			selectedTables.add(table);
		}
		posDialog.setSelectedTables(selectedTables);
		posDialog.setOrderType(getOrderType());
		posDialog.setSelectedAction(action);
		posDialog.setSize(800, 600);
		posDialog.open();
		if (!posDialog.isCanceled()) {
			for (ShopTable shopTable : selectedTables) {
				ShopTableButton tableButton = tableButtonMap.get(shopTable);
				ShopTableStatus shopTableStatus = tableButton.getShopTable().getStatus();
				ShopTableStatusDAO.getInstance().refresh(shopTableStatus);
				tableButton.update();
			}
		}
		if (action.equals(POSConstants.EDIT) || posDialog.createNewTicket())
			closeDialog(false);

		clearSelection();
	}

	private void closeDialog(boolean canceled) {
		Window windowAncestor = SwingUtilities.getWindowAncestor(DefaultTableSelectionView.this);
		if (windowAncestor instanceof POSDialog) {
			((POSDialog) windowAncestor).setCanceled(false);
			windowAncestor.dispose();
		}
	}

	@Override
	public List<ShopTable> getSelectedTables() {
		Enumeration<ShopTableButton> elements = addedTableListModel.elements();
		List<ShopTable> tables = new ArrayList<ShopTable>();

		while (elements.hasMoreElements()) {
			ShopTableButton shopTableButton = (ShopTableButton) elements.nextElement();
			tables.add(shopTableButton.getShopTable());
		}

		return tables;
	}

	private void clearSelection() {
		if (isCreateNewTicket()) {
			addedTableListModel.clear();
		}
		removeTableListModel.clear();
		btnGroups.clearSelection();
		btnGroup.setVisible(true);
		btnUnGroup.setVisible(true);
		btnDone.setVisible(false);
		btnCancel.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		if (object == btnGroup) {
			if (isCreateNewTicket()) {
				addedTableListModel.clear();
			}
			btnUnGroup.setVisible(false);
			btnDone.setVisible(true);
			btnCancel.setVisible(true);
		}
		else if (object == btnUnGroup) {
			removeTableListModel.clear();
			btnGroup.setVisible(false);
			btnDone.setVisible(true);
			btnCancel.setVisible(true);
		}
		else if (object == btnDone) {
			if (btnGroup.isSelected()) {
				doGroupAction();
				clearSelection();
			}
			else if (btnUnGroup.isSelected()) {
				doUnGroupAction();
				clearSelection();
			}
		}
		else if (object == btnCancel) {
			clearSelection();
			redererTables();
		}
	}

	private void doCreateNewTicket() {
		try {
			List<ShopTable> selectedTables = getSelectedTables();

			if (selectedTables.isEmpty()) {
				clearSelection();
				return;
			}

			OrderServiceFactory.getOrderService().createNewTicket(getOrderType(), selectedTables, null);
			clearSelection();

		} catch (TicketAlreadyExistsException e) {
			PosLog.error(getClass(), e);

		}
	}

	private boolean editTicket(Ticket ticket) {
		if (ticket == null) {
			return false;

		}
		closeDialog(false);

		Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());

		OrderView.getInstance().setCurrentTicket(ticketToEdit);
		RootView.getInstance().showView(OrderView.VIEW_NAME);
		OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
		return true;
	}

	private void doGroupAction() {
		if (isCreateNewTicket()) {
			doCreateNewTicket();
		}
		closeDialog(false);
	}

	private void doUnGroupAction() {
		if (removeTableListModel == null || removeTableListModel.isEmpty()) {
			return;
		}

		Enumeration<ShopTableButton> elements = this.removeTableListModel.elements();

		if (!removeTableListModel.elementAt(0).hasUserAccess()) {
			return;
		}
		while (elements.hasMoreElements()) {
			ShopTableButton button = (ShopTableButton) elements.nextElement();
			if (addedTableListModel.contains(button)) {
				addedTableListModel.removeElement(button);
			}
		}
		redererTables();
		if (!isCreateNewTicket()) {
			closeDialog(false);
		}
	}

	public void setTicket(Ticket ticket) {
		if (ticket == null) {
			return;
		}
		this.ticket = ticket;

		List<ShopTable> tables = ShopTableDAO.getInstance().getTables(ticket);
		if (tables == null)
			return;

		addedTableListModel.clear();

		for (ShopTable shopTable : tables) {
			ShopTableButton shopTableButton = tableButtonMap.get(shopTable);
			if (shopTableButton != null) {
				shopTableButton.getShopTable().setServing(false);
			}
			addedTableListModel.addElement(shopTableButton);
		}
		redererTables();
	}

	private void checkTables() {
		boolean hasTable = ShopTableDAO.getInstance().hasTable();
		if (hasTable)
			return;

			int userInput = 0;

			int result = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(),
					Messages.getString("TableSelectionView.0"), Messages.getString("TableSelectionView.1")); //$NON-NLS-1$ //$NON-NLS-2$

			if (result == JOptionPane.YES_OPTION) {

				userInput = NumberSelectionDialog2.takeIntInput(Messages.getString("TableSelectionView.2")); //$NON-NLS-1$

				if (userInput == 0) {
					POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("TableSelectionView.3")); //$NON-NLS-1$
					return;
				}

				if (userInput != -1) {
					ShopTableDAO.getInstance().createNewTables(userInput);
			}
		}
	}

	@Override
	public void setOrderType(OrderType orderType) {
		super.setOrderType(orderType);
		btnNewBarTab.setVisible(orderType.isBarTab());
		if (orderType.isBarTab())
			tabbedPane.addTab("Bar Tab", barTab);
	}

	@Override
	public void updateView(boolean update) {
		btnCancelDialog.setVisible(update);
	}
}