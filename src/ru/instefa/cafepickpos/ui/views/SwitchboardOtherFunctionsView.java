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
package ru.instefa.cafepickpos.ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.extension.ExtensionManager;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.actions.DrawerAssignmentAction;
import ru.instefa.cafepickpos.actions.DrawerBleedAction;
import ru.instefa.cafepickpos.actions.DrawerKickAction;
import ru.instefa.cafepickpos.actions.DrawerPullAction;
import ru.instefa.cafepickpos.actions.ManageTableLayoutAction;
import ru.instefa.cafepickpos.actions.PayoutAction;
import ru.instefa.cafepickpos.actions.PosAction;
import ru.instefa.cafepickpos.actions.ServerTipsAction;
import ru.instefa.cafepickpos.actions.ShowBackofficeAction;
import ru.instefa.cafepickpos.actions.ShowKitchenDisplayAction;
import ru.instefa.cafepickpos.actions.ShowOnlineTicketManagementAction;
import ru.instefa.cafepickpos.actions.ShowTransactionsAuthorizationsAction;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.ui.views.order.ViewPanel;

public class SwitchboardOtherFunctionsView extends ViewPanel {
	//public static final String VIEW_NAME = "ALL FUNCTIONS"; //$NON-NLS-1$
	// 20170713, pymancer, selectable as default views translation, name unification
	public final static String VIEW_NAME = POSConstants.ALL_FUNCTIONS;
	private static SwitchboardOtherFunctionsView instance;

	private JPanel contentPanel;
	private DrawerAssignmentAction drawerAction;

	public SwitchboardOtherFunctionsView() {
		setLayout(new BorderLayout(5, 5));
		PosButton btnBack = new PosButton(Messages.getString("SwitchboardOtherFunctionsView.1")); //$NON-NLS-1$
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RootView.getInstance().showDefaultView();
			}
		});
		add(btnBack, BorderLayout.SOUTH);

		contentPanel = new JPanel(new MigLayout("hidemode 3,align 50% 50%, wrap 6", "sg fill", ""));

		List<PosAction> actions = new ArrayList();
		actions.add(new ShowBackofficeAction());
		drawerAction = new DrawerAssignmentAction();
		actions.add(drawerAction);
		actions.add(new DrawerPullAction());
		actions.add(new DrawerBleedAction());
		actions.add(new DrawerKickAction());
		actions.add(new PayoutAction());
		actions.add(new ServerTipsAction());
		actions.add(new ShowTransactionsAuthorizationsAction());
		actions.add(new ShowKitchenDisplayAction()
		/*); actions.add(new SwithboardViewAction()*/);
		actions.add(new ManageTableLayoutAction());
		actions.add(new ShowOnlineTicketManagementAction());

		List<FloreantPlugin> plugins = ExtensionManager.getPlugins();
		if (plugins != null) {
			for (FloreantPlugin plugin : plugins) {
				List<AbstractAction> posActions = plugin.getSpecialFunctionActions();
				if (posActions != null) {
					for (AbstractAction action : posActions) {
						actions.add((PosAction) action);
					}
				}
			}
		}

		Dimension size = PosUIManager.getSize(150, 150);
		for (PosAction action : actions) {
			if (action instanceof DrawerAssignmentAction) {
				if (!Application.getInstance().getTerminal().isHasCashDrawer()) {
					continue;
				}
			}

			PosButton button = new PosButton(action);
			contentPanel.add(button, "w " + size.width + "!, h " + size.height + "!");
		}

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(null);
		add(scrollPane);
	}

	public static SwitchboardOtherFunctionsView getInstance() {
		if (instance == null) {
			instance = new SwitchboardOtherFunctionsView();
		}
		instance.updateView();
		return instance;
	}

	private void updateView() {
		drawerAction.updateActionText();
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}
}
