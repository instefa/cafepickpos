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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.UpdateCheckInterval;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.util.POSUtil;

public class UpdateDialog extends POSDialog {
	private String newVersion;
	private JComboBox<String> cbCheckUpdateStatus;
	private Boolean shouldBeUpdated = null;

	public UpdateDialog(String newVersion) {
		/**
		 * If new version is available render link for the latest update;
		 * Show 'up to date' message if current version is latest;
		 * Show general updates link if new version is null.
		 */
		super(POSUtil.getBackOfficeWindow(), Messages.getString("UpdateAction.7")); //$NON-NLS-1$
		this.newVersion = newVersion;
		
		setIconImage(Application.getApplicationIcon().getImage());
		setResizable(false);
		initComponent();
        // 20170713, pymancer, localized update check intervals
		cbCheckUpdateStatus.setSelectedItem(UpdateCheckInterval.fromString(TerminalConfig.getCheckUpdateStatus()).toString());
	}

	protected void initComponent() {
		JPanel panel = new JPanel(new MigLayout("fillx"));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		JLabel logoLabel = new JLabel(IconFactory.getDefaultIcon("logo_128x128.png")); //$NON-NLS-1$ //$NON-NLS-2$
		panel.add(logoLabel, "cell 0 0 0 2");

		JLabel l = new JLabel("<html><h1>" + POSConstants.MDS_POS + "</h1><h4>" + Messages.getString("UpdateDialog.0") +
							  " " + Application.VERSION +
							  "</h4></html>"); //$NON-NLS-1$ //$NON-NLS-2$
		panel.add(l, "cell 0 2");

		String text;
		shouldBeUpdated = POSUtil.shouldBeUpdated(newVersion);
		if (shouldBeUpdated == null) {
			text = "<h2>" + Messages.getString("UpdateDialog.10") + "</h2>" +
					"<h4><a href='#'>" + Messages.getString("UpdateDialog.2") + "</a></h4>";
		} else if (shouldBeUpdated) {
			text = "<h2>" + Messages.getString("UpdateDialog.3") + "</h2>" +
					"<h4>" + newVersion + "&nbsp;<a href='#'>" + Messages.getString("UpdateDialog.4") + "</a></h4>";
		} else {
			text = "<h2>" + Messages.getString("UpdateDialog.1") + "</h2>";
		}

		JLabel lblVersion = new JLabel("<html>" + text + "</html>"); //$NON-NLS-1$ //$NON-NLS-2$
		panel.add(lblVersion, "cell 1 0,right");
		lblVersion.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				String link = TerminalConfig.getPosDownloadUrl();
				if (shouldBeUpdated != null && shouldBeUpdated) {
					link += "v" + newVersion + "/";
				}
				try {
					openBrowser(link);
				} catch (Exception ex) {
					PosLog.info(UpdateDialog.class, ex.getMessage());
				}
			}
		});

		cbCheckUpdateStatus = new JComboBox<String>();
		cbCheckUpdateStatus.addItem(UpdateCheckInterval.DAILY.toString());
		cbCheckUpdateStatus.addItem(UpdateCheckInterval.WEEKLY.toString());
		cbCheckUpdateStatus.addItem(UpdateCheckInterval.MONTHLY.toString());
		cbCheckUpdateStatus.addItem(UpdateCheckInterval.NEVER.toString());
		panel.add(new JLabel(Messages.getString("UpdateDialog.9")), "split 2,cell 1 2,aligny top,right");
		panel.add(cbCheckUpdateStatus, "growx,aligny top,right");

		JPanel buttonPanel = new JPanel(new MigLayout("fill"));
		PosButton btnOk = new PosButton(Messages.getString("AboutDialog.5")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String status = (String) cbCheckUpdateStatus.getSelectedItem();
				TerminalConfig.setCheckUpdateStatus(UpdateCheckInterval.getTypeByLocalName(status).name());
				dispose();
			}
		});

		buttonPanel.add(new JSeparator(), "newline, grow");
		buttonPanel.add(btnOk, "newline,center");
		add(buttonPanel, BorderLayout.SOUTH);
		add(panel);
	}

	private void openBrowser(String link) throws Exception {
		URI uri = new URI(link);
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(uri);
		}
	}
}
