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
package ru.instefa.cafepickpos.ui.views;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.demo.KitchenDisplayWindow;
import ru.instefa.cafepickpos.extension.ExtensionManager;
import ru.instefa.cafepickpos.extension.FloorLayoutPlugin;
import ru.instefa.cafepickpos.extension.TicketImportPlugin;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.ManagerDialog;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.PayoutDialog;
import ru.instefa.cafepickpos.ui.views.payment.AuthorizableTicketBrowser;

public class SwitchboardOtherFunctionsDialog extends POSDialog implements ActionListener {
	private SwitchboardView switchboardView;
	
	private PosButton btnManager = new PosButton(POSConstants.MANAGER_BUTTON_TEXT);
	private PosButton btnAuthorize = new PosButton(POSConstants.AUTHORIZE_BUTTON_TEXT);
	private PosButton btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
	private PosButton btnPayout = new PosButton(POSConstants.PAYOUT_BUTTON_TEXT);
	private PosButton btnTableManage = new PosButton(POSConstants.TABLE_MANAGE_BUTTON_TEXT);
	private PosButton btnOnlineTickets = new PosButton(POSConstants.ONLINE_TICKET_BUTTON_TEXT);
 
	
	private FloorLayoutPlugin floorLayoutPlugin;
	private TicketImportPlugin ticketImportPlugin;
	
	public SwitchboardOtherFunctionsDialog(SwitchboardView switchboardView) {
		super();
		this.switchboardView = switchboardView;
		
		setTitle(Messages.getString("SwitchboardOtherFunctionsDialog.0")); //$NON-NLS-1$
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 400);
		
		JPanel contentPane = new JPanel(new GridLayout(3, 0, 10, 10));
		contentPane.add(btnManager);
		contentPane.add(btnAuthorize);
		contentPane.add(btnKitchenDisplay);
		contentPane.add(btnPayout);
		
		floorLayoutPlugin = (FloorLayoutPlugin) ExtensionManager.getPlugin(FloorLayoutPlugin.class);
		if (floorLayoutPlugin != null) {
			contentPane.add(btnTableManage);
		}

		ticketImportPlugin = (TicketImportPlugin) ExtensionManager.getPlugin(TicketImportPlugin.class);
		if (ticketImportPlugin != null) {
			contentPane.add(btnOnlineTickets);
		}
		
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		
		btnManager.addActionListener(this);
		btnAuthorize.addActionListener(this);
		btnKitchenDisplay.addActionListener(this);
		btnPayout.addActionListener(this);
		btnTableManage.addActionListener(this);
		btnOnlineTickets.addActionListener(this);
		
		setupPermission();
	}

	private void setupPermission() {
		User user = Application.getCurrentUser();
		UserType userType = user.getType();
		if (userType != null) {
			Set<UserPermission> permissions = userType.getPermissions();
			if (permissions != null) {
				for (UserPermission permission : permissions) {
					if (permission.equals(UserPermission.PAY_OUT)) {
						btnPayout.setEnabled(true);
					}
					else if (permission.equals(UserPermission.PERFORM_MANAGER_TASK)) {
						btnManager.setEnabled(true);
					}
				}
			}
		}
	}
	
	private void doShowKitchenDisplay() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if(window instanceof KitchenDisplayWindow) {
				window.toFront();
				return;
			}
		}
		
		KitchenDisplayWindow window = new KitchenDisplayWindow();
		window.setVisible(true);
	}
	
	private void doShowTicketImportDialog() {
		TicketImportPlugin ticketImportPlugin = (TicketImportPlugin) ExtensionManager.getPlugin(TicketImportPlugin.class);
		if(ticketImportPlugin != null) {
			ticketImportPlugin.startService();
		}
	}
	
	private void doShowAuthorizeTicketDialog() {
		AuthorizableTicketBrowser dialog = new AuthorizableTicketBrowser(Application.getPosWindow());
    	dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    	dialog.setSize(PosUIManager.getSize(800, 600));
    	dialog.setLocationRelativeTo(Application.getPosWindow());
    	dialog.setVisible(true);
	}
	
	private void doShowManagerWindow() {
		ManagerDialog dialog = new ManagerDialog();
		dialog.open();

		switchboardView.updateTicketList();
	}
	
	private void doPayout() {
		PayoutDialog dialog = new PayoutDialog();
		dialog.open();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		private PosButton btnBackOffice = new PosButton(POSConstants.BACK_OFFICE_BUTTON_TEXT);
//		private PosButton btnManager = new PosButton(POSConstants.MANAGER_BUTTON_TEXT);
//		private PosButton btnAuthorize = new PosButton(POSConstants.AUTHORIZE_BUTTON_TEXT);
//		private PosButton btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
//		private PosButton btnPayout = new PosButton(POSConstants.PAYOUT_BUTTON_TEXT);
//		private PosButton btnTableManage = new PosButton(POSConstants.TABLE_MANAGE_BUTTON_TEXT);
//		private PosButton btnOnlineTickets = new PosButton(POSConstants.ONLINE_TICKET_BUTTON_TEXT);
		
		Object source = e.getSource();
		dispose();
		
		if(source == btnManager) {
			doShowManagerWindow();
		}
		else if(source == btnAuthorize) {
			doShowAuthorizeTicketDialog();
		}
		else if(source == btnKitchenDisplay) {
			doShowKitchenDisplay();
		}
		else if(source == btnPayout) {
			doPayout();
		}
		else if(source == btnTableManage) {
			if(floorLayoutPlugin != null) {
				floorLayoutPlugin.openTicketsAndTablesDisplay();
			}
		}
		else if(source == btnOnlineTickets) {
			doShowTicketImportDialog();
		}
	}
}
