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
package ru.instefa.cafepickpos.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.ui.views.order.ViewPanel;

public class KitchenDisplayView extends ViewPanel implements ActionListener {

	//public final static String VIEW_NAME = "KD"; //$NON-NLS-1$
	// 20170713, changes by pymancer, selectable as default views translation
	public final static String VIEW_NAME = POSConstants.KD;

	private static KitchenDisplayView instance;

	private KitchenTicketListPanel ticketPanel;

	private Timer viewUpdateTimer;
	private boolean showHeader;
	private KeyboardDispatcher dispatcher;

	public KitchenDisplayView(boolean showHeader) {
		this.showHeader = showHeader;
		setLayout(new BorderLayout(5, 5));
		setBackground(Color.black);

		ticketPanel = new KitchenTicketListPanel();
		ticketPanel.setBackground(Color.black);
		ticketPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(ticketPanel);
		viewUpdateTimer = new Timer(10 * 1000, this);
		viewUpdateTimer.setRepeats(true);
		dispatcher = new KeyboardDispatcher();
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			RootView.getInstance().getHeaderPanel().setVisible(showHeader);
			String currentView = TerminalConfig.getDefaultView();
			ticketPanel.setBackButtonVisible(currentView != null && !currentView.equals(VIEW_NAME));
			updateTicketView();
			if (!viewUpdateTimer.isRunning()) {
				viewUpdateTimer.start();
			}
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			manager.addKeyEventDispatcher(dispatcher);
		}
		else {
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			manager.removeKeyEventDispatcher(dispatcher);
			cleanup();
		}
	}

	private class KeyboardDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if (viewUpdateTimer.isRunning()) {
					viewUpdateTimer.stop();
				}
				ticketPanel.setSelectedKey(e.getKeyCode());
				viewUpdateTimer.restart();
			}
			else if (e.getID() == KeyEvent.KEY_RELEASED) {
			}
			else if (e.getID() == KeyEvent.KEY_TYPED) {
			}
			return false;
		}
	}

	public synchronized void cleanup() {
		viewUpdateTimer.stop();
		ticketPanel.reset();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() != null && e.getActionCommand().equalsIgnoreCase("log out")) { //$NON-NLS-1$
			Application.getInstance().doLogout();
		}
		updateTicketView();
	}

	private synchronized void updateTicketView() {
		try {
			viewUpdateTimer.stop();
			ticketPanel.updateKDSView();
			revalidate();
			repaint();
		} catch (Exception e2) {
			POSMessageDialog.showError(this, e2.getMessage(), e2);
		} finally {
			viewUpdateTimer.restart();
		}
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public synchronized static KitchenDisplayView getInstance() {
		if (instance == null) {
			instance = new KitchenDisplayView(false);
		}
		return instance;
	}
}
