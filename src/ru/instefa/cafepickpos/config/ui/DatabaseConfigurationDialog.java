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
package ru.instefa.cafepickpos.config.ui;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.GenericJDBCException;

import ru.instefa.cafepickpos.Database;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.config.AppConfig;
import ru.instefa.cafepickpos.exceptions.DatabaseConnectionException;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.main.Main;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.swing.POSPasswordField;
import ru.instefa.cafepickpos.swing.POSTextField;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.ui.TitlePanel;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.DatabaseUtil;

public class DatabaseConfigurationDialog extends POSDialog implements ActionListener {

	private static final String CREATE_DATABASE = "CD"; //$NON-NLS-1$
	private static final String UPDATE_DATABASE = "UD"; //$NON-NLS-1$
	private static final String SAVE = "SAVE"; //$NON-NLS-1$
	private static final String CANCEL = "cancel"; //$NON-NLS-1$
	private static final String TEST = "test"; //$NON-NLS-1$
	private POSTextField tfServerAddress;
	private POSTextField tfServerPort;
	private POSTextField tfDatabaseName;
	private POSTextField tfUserName;
	private POSPasswordField tfPassword;
	private PosButton btnTestConnection;
	private PosButton btnCreateDb;
	private PosButton btnUpdateDb;
	private PosButton btnExit;
	private PosButton btnSave;
	private JComboBox<Database> databaseCombo;

	private TitlePanel titlePanel;
	private JLabel lblServerAddress;
	private JLabel lblServerPort;
	private JLabel lblDbName;
	private JLabel lblUserName;
	private JLabel lblDbPassword;

	private boolean connectionSuccess;

	public DatabaseConfigurationDialog() throws HeadlessException {
		super();

		setFieldValues();
		addUIListeners();
	}

	protected void initUI() {
		getContentPane().setLayout(new MigLayout("fill", "[][fill, grow]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		titlePanel = new TitlePanel();
		tfServerAddress = new POSTextField();
		tfServerPort = new POSTextField();
		tfDatabaseName = new POSTextField();
		tfUserName = new POSTextField();
		tfPassword = new POSPasswordField();
		databaseCombo = new JComboBox<>(Database.values());

		String databaseProviderName = AppConfig.getDatabaseProviderName();
		if (StringUtils.isNotEmpty(databaseProviderName)) {
			databaseCombo.setSelectedItem(Database.getByProviderName(databaseProviderName));
		}

		getContentPane().add(titlePanel, "span, grow, wrap"); //$NON-NLS-1$

		getContentPane().add(new JLabel(Messages.getString("DatabaseConfigurationDialog.8"))); //$NON-NLS-1$
		getContentPane().add(databaseCombo, "grow, wrap"); //$NON-NLS-1$
		lblServerAddress = new JLabel(Messages.getString("DatabaseConfigurationDialog.10") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		getContentPane().add(lblServerAddress);
		getContentPane().add(tfServerAddress, "grow, wrap"); //$NON-NLS-1$
		lblServerPort = new JLabel(Messages.getString("DatabaseConfigurationDialog.13") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		getContentPane().add(lblServerPort);
		getContentPane().add(tfServerPort, "grow, wrap"); //$NON-NLS-1$
		lblDbName = new JLabel(Messages.getString("DatabaseConfigurationDialog.16") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		getContentPane().add(lblDbName);
		getContentPane().add(tfDatabaseName, "grow, wrap"); //$NON-NLS-1$
		lblUserName = new JLabel(Messages.getString("DatabaseConfigurationDialog.19") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		getContentPane().add(lblUserName);
		getContentPane().add(tfUserName, "grow, wrap"); //$NON-NLS-1$
		lblDbPassword = new JLabel(Messages.getString("DatabaseConfigurationDialog.22") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		getContentPane().add(lblDbPassword);
		getContentPane().add(tfPassword, "grow, wrap"); //$NON-NLS-1$
		getContentPane().add(new JSeparator(), "span, grow, gaptop 10"); //$NON-NLS-1$

		btnTestConnection = new PosButton(Messages.getString("DatabaseConfigurationDialog.26").toUpperCase()); //$NON-NLS-1$
		btnTestConnection.setActionCommand(TEST);
		btnSave = new PosButton(Messages.getString("DatabaseConfigurationDialog.27").toUpperCase()); //$NON-NLS-1$
		btnSave.setActionCommand(SAVE);
		btnExit = new PosButton(Messages.getString("DatabaseConfigurationDialog.28").toUpperCase()); //$NON-NLS-1$
		btnExit.setActionCommand(CANCEL);

		JPanel buttonPanel = new JPanel(new MigLayout("inset 0, fill", "grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		btnCreateDb = new PosButton(Messages.getString("DatabaseConfigurationDialog.29").toUpperCase()); //$NON-NLS-1$
		btnCreateDb.setActionCommand(CREATE_DATABASE);

		btnUpdateDb = new PosButton(Messages.getString("UPDATE_DATABASE").toUpperCase()); //$NON-NLS-1$
		btnUpdateDb.setActionCommand(UPDATE_DATABASE);

		buttonPanel.add(btnUpdateDb);
		buttonPanel.add(btnCreateDb);
		buttonPanel.add(btnTestConnection);
		buttonPanel.add(btnSave);
		buttonPanel.add(btnExit);

		getContentPane().add(buttonPanel, "span, grow"); //$NON-NLS-1$
	}

	private void addUIListeners() {
		btnTestConnection.addActionListener(this);
		btnCreateDb.addActionListener(this);
		btnSave.addActionListener(this);
		btnExit.addActionListener(this);
		btnUpdateDb.addActionListener(this);

		databaseCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
	    		Database selectedDb = (Database) databaseCombo.getSelectedItem();

				if (selectedDb == Database.DERBY_SINGLE) {
					setFieldsVisible(false);
					return;
				}

				setFieldsVisible(true);

				String databasePort = AppConfig.getDatabasePort();
				if (StringUtils.isEmpty(databasePort)) {
					databasePort = selectedDb.getDefaultPort();
				}

				tfServerPort.setText(databasePort);
	      	}
		});
	}

	private void setFieldValues() {
		Database selectedDb = (Database) databaseCombo.getSelectedItem();

		String databaseURL = AppConfig.getDatabaseHost();
		tfServerAddress.setText(databaseURL);

		String databasePort = AppConfig.getDatabasePort();
		if (StringUtils.isEmpty(databasePort)) {
			databasePort = selectedDb.getDefaultPort();
		}

		tfServerPort.setText(databasePort);
		tfDatabaseName.setText(AppConfig.getDatabaseName());
		tfUserName.setText(AppConfig.getDatabaseUser());
		tfPassword.setText(AppConfig.getDatabasePassword());

		if (selectedDb == Database.DERBY_SINGLE) {
			setFieldsVisible(false);
		}
		else {
			setFieldsVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String command = e.getActionCommand();

			if (CANCEL.equalsIgnoreCase(command)) {
				if (!Application.getInstance().isSystemInitialized()) {
					// proceed with the single viable option - close the application
					Application.getInstance().exitSystem(0);
				} else {
					dispose();
					return;
				}
			}
			
			if (TEST.equalsIgnoreCase(command)) {
				if (checkConnection()) {
					connectionSuccess = true;
					JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.31"));
				} else {
					JOptionPane.showMessageDialog(this, Messages.getString("DatabaseConfigurationDialog.32"));
				}
			}
			else if (UPDATE_DATABASE.equals(command)) {
				int i = JOptionPane.showConfirmDialog(this,
						Messages.getString("DatabaseConfigurationDialog.0"),
						Messages.getString("DatabaseConfigurationDialog.1"), JOptionPane.YES_NO_OPTION);
				if (i != JOptionPane.YES_OPTION) {
					return;
				}

				if (isAuthorizedToPerformDbChange()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					Application.getInstance().setSystemInitialized(false);
	
					if (updateDatabase()) {
						connectionSuccess = true;
						JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this,
								Messages.getString("DatabaseConfigurationDialog.2"));
					}
					else {
						JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this,
								Messages.getString("DatabaseConfigurationDialog.3"));
					}
				}
			}
			else if (CREATE_DATABASE.equals(command)) {

				int i = JOptionPane.showConfirmDialog(this, Messages.getString("DatabaseConfigurationDialog.33"),
						Messages.getString("DatabaseConfigurationDialog.34"), JOptionPane.YES_NO_OPTION);
				if (i != JOptionPane.YES_OPTION) {
					return;
				}

				i = JOptionPane.showConfirmDialog(this, Messages.getString("DatabaseConfigurationDialog.4"),
						Messages.getString("DatabaseConfigurationDialog.5"), JOptionPane.YES_NO_OPTION);
				boolean generateSampleData = false;
				if (i == JOptionPane.YES_OPTION) {
					generateSampleData = true;
				}

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				Application.getInstance().setSystemInitialized(false);

				if (createDatabase(generateSampleData)) {
					JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this,
							Messages.getString("DatabaseConfigurationDialog.6") +
							Messages.getString("DatabaseConfigurationDialog.7"));

					try {
						Main.restart();
					} catch (IOException | InterruptedException | URISyntaxException ex) {
						PosLog.error(getClass(), ex.getMessage());
					}
					
					connectionSuccess = true;
				}
				else {
					JOptionPane.showMessageDialog(DatabaseConfigurationDialog.this,
							Messages.getString("DatabaseConfigurationDialog.36"));
				}
			}
			else if (SAVE.equalsIgnoreCase(command)) {
				if (connectionSuccess) {
					Application.getInstance().initializeSystem();
				} else if (!Application.getInstance().isSystemInitialized()) {
					// proceed with the single viable option - close the application
					Application.getInstance().exitSystem(0);
				}
				Application.getInstance().setSystemInitialized(false);
				dispose();
			}
		} catch (IllegalComponentStateException ex) {
			// TODO: refactor dialog to avoid the possibility of this error,
			// databaseCombo's ActionListener has been replaced with an ItemListener
			// as a workaround, further testing and monitoring are necessary.
			POSMessageDialog.showMessage(this, Messages.getString("DatabaseConfigurationDialog.38"));
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private boolean isAuthorizedToPerformDbChange() {
		try {
			DatabaseUtil.initialize();
			UserDAO.getInstance().findAll();

			String password = JOptionPane.showInputDialog(Messages.getString("DatabaseConfigurationDialog.9"));
			User user2 = UserDAO.getInstance().findUserBySecretKey(password);
			if (user2 == null || !user2.isAdministrator()) {
				POSMessageDialog.showError(this, Messages.getString("DatabaseConfigurationDialog.11"));
				return true;
			}
		} catch (DatabaseConnectionException | GenericJDBCException e) {
			PosLog.error(getClass(), e.getMessage());
			POSMessageDialog.showError(this, Messages.getString("DatabaseConfigurationDialog.39"));
		}
		return false;
	}
	
	private HashMap<String, String> getDbParameters(boolean create) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        final Database selectedDb = (Database) databaseCombo.getSelectedItem();
		final String providerName = selectedDb.getProviderName();
		final String databaseURL = tfServerAddress.getText();
		final String databasePort = tfServerPort.getText();
		final String databaseName = tfDatabaseName.getText();
		
		data.put("user", tfUserName.getText());
		data.put("pass", new String(tfPassword.getPassword()));
		data.put("dialect", selectedDb.getHibernateDialect());
		data.put("driver", selectedDb.getHibernateConnectionDriverClass());
		if (create) {
			data.put("conn", selectedDb.getCreateDbConnectString(databaseURL, databasePort, databaseName));
		} else {
			data.put("conn", selectedDb.getConnectString(databaseURL, databasePort, databaseName));
		}

		AppConfig.setDatabaseProviderName(providerName);
		AppConfig.setConnectString(data.get("conn"));
		AppConfig.setDatabaseHost(databaseURL);
		AppConfig.setDatabasePort(databasePort);
		AppConfig.setDatabaseName(databaseName);
		AppConfig.setDatabaseUser(data.get("user"));
		AppConfig.setDatabasePassword(data.get("pass"));
        
        return data;
	}

	private boolean checkConnection() {
		HashMap<String, String> db = getDbParameters(false);
		
		try {
			DatabaseUtil.checkConnection(db.get("conn"), db.get("dialect"),
					db.get("driver"), db.get("user"), db.get("pass"));
		} catch (DatabaseConnectionException e1) {
			return false;
		}
		return true;
	}

	private boolean updateDatabase() {
		HashMap<String, String> db = getDbParameters(false);
		
		if (DatabaseUtil.updateDatabase(db.get("conn"), db.get("dialect"),
				db.get("driver"), db.get("user"), db.get("pass"))) {
			return true;
		}
		return false;
	}

	private boolean createDatabase(boolean generateSampleData) {
		HashMap<String, String> db = getDbParameters(true);

		if (DatabaseUtil.createDatabase(db.get("conn"), db.get("dialect"),
				db.get("driver"), db.get("user"), db.get("pass"), generateSampleData)) {
			return true;
		}
		return false;
	}

	public void setTitle(String title) {
		super.setTitle(Messages.getString("DatabaseConfigurationDialog.37")); //$NON-NLS-1$

		titlePanel.setTitle(title);
	}

	private void setFieldsVisible(boolean visible) {
		lblServerAddress.setVisible(visible);
		tfServerAddress.setVisible(visible);

		lblServerPort.setVisible(visible);
		tfServerPort.setVisible(visible);

		lblDbName.setVisible(visible);
		tfDatabaseName.setVisible(visible);

		lblUserName.setVisible(visible);
		tfUserName.setVisible(visible);

		lblDbPassword.setVisible(visible);
		tfPassword.setVisible(visible);
	}

	public static DatabaseConfigurationDialog show(Frame parent) {
		DatabaseConfigurationDialog dialog = new DatabaseConfigurationDialog();
		dialog.setTitle(Messages.getString("DatabaseConfigurationDialog.37")); //$NON-NLS-1$
		dialog.pack();
		dialog.open();

		return dialog;
	}
}
