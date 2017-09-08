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
/*
 * CurrencyEditor.java
 *
 * Created on August 3, 2006, 1:49 AM
 */

package ru.instefa.cafepickpos.ui.model;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.model.Currency;
import ru.instefa.cafepickpos.model.dao.CurrencyDAO;
import ru.instefa.cafepickpos.swing.DoubleTextField;
import ru.instefa.cafepickpos.swing.FixedLengthTextField;
import ru.instefa.cafepickpos.swing.IntegerTextField;
import ru.instefa.cafepickpos.swing.MessageDialog;
import ru.instefa.cafepickpos.ui.BeanEditor;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.CurrencyUtil;
import ru.instefa.cafepickpos.util.POSUtil;

/**
 *
 * @author  MShahriar
 */
public class CurrencyForm extends BeanEditor {

	private FixedLengthTextField tfCode;
	private FixedLengthTextField tfName;
	private JTextField tfSymbol;
	private DoubleTextField tfExchangeRate;
	private DoubleTextField tfTolerance;
	private JCheckBox chkMain;
	private IntegerTextField tfDecimalPlaces = new IntegerTextField(5);
	private JLabel lblTolerance;

	public CurrencyForm() {
		this(new Currency());
	}

	public CurrencyForm(Currency currency) {
		initComponents();

		setBean(currency);
	}

	private void initComponents() {
		JPanel contentPanel = new JPanel(new MigLayout("hidemode 3,fill"));

		JLabel lblCode = new JLabel(Messages.getString("CurrencyForm.0"));
		tfCode = new FixedLengthTextField();

		JLabel lblName = new JLabel(ru.instefa.cafepickpos.POSConstants.NAME + ":");
		tfName = new FixedLengthTextField();

		JLabel lblExchangeRate = new JLabel(Messages.getString("CurrencyForm.1"));
		tfExchangeRate = new DoubleTextField();

		lblTolerance = new JLabel(Messages.getString("CurrencyForm.2"));
		tfTolerance = new DoubleTextField();

		JLabel lblSymbol = new JLabel(Messages.getString("CurrencyForm.3"));
		tfSymbol = new JTextField();

		chkMain = new JCheckBox(Messages.getString("CurrencyForm.4"));

		contentPanel.add(lblName, "cell 0 0");
		contentPanel.add(tfName, "cell 1 0");
		contentPanel.add(lblCode, "cell 0 1");
		contentPanel.add(tfCode, "cell 1 1");
		contentPanel.add(lblSymbol, "cell 0 2");
		contentPanel.add(tfSymbol, "grow,cell 1 2");
		contentPanel.add(lblExchangeRate, "cell 0 3");
		contentPanel.add(tfExchangeRate, "grow,cell 1 3,grow,split 3");
		contentPanel.add(new JLabel(Messages.getString("CurrencyForm.9")));
		contentPanel.add(tfDecimalPlaces);
		contentPanel.add(lblTolerance, "cell 0 4");
		contentPanel.add(tfTolerance, "grow,cell 1 4");
		contentPanel.add(chkMain, "cell 1 5");

		add(contentPanel);
	}

	@Override
	public boolean save() {

		try {
			if (!updateModel())
				return false;

			Currency currency = (Currency) getBean();
			CurrencyDAO dao = new CurrencyDAO();
			dao.saveOrUpdate(currency);
			CurrencyUtil.populateCurrency();
		} catch (Exception e) {
			MessageDialog.showError(e);
			return false;
		}

		return true;
	}

	@Override
	protected void updateView() {
		Currency currency = (Currency) getBean();
		if (currency == null) {
			return;
		}
		tfCode.setText(currency.getCode());
		tfName.setText(currency.getName());
		tfSymbol.setText(currency.getSymbol());
		tfExchangeRate.setText("" + currency.getExchangeRate()); //$NON-NLS-1$
		tfTolerance.setText("" + currency.getTolerance()); //$NON-NLS-1$
		chkMain.setSelected(currency.isMain());
		tfDecimalPlaces.setText(String.valueOf(currency.getDecimalPlaces()));
	}

	@Override
	protected boolean updateModel() {
		Currency currency = (Currency) getBean();

		String code = tfCode.getText();
		String name = tfName.getText();
		if (POSUtil.isBlankOrNull(code)) {
			POSMessageDialog.showError(POSUtil.getFocusedWindow(), Messages.getString("CurrencyForm.5"));
			return false;
		}
		
		double exchangeRate = tfExchangeRate.getDouble();
		if (chkMain.isSelected()) {
			if (exchangeRate != 1.0) {
				POSMessageDialog.showMessage(POSUtil.getFocusedWindow(),
											 Messages.getString("CurrencyForm.6"));
				return false;
			}
		}
		double tolerance = tfTolerance.getDoubleOrZero();
		if (tolerance > 0 && !chkMain.isSelected()) {
			POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("CurrencyForm.10"));
			tfTolerance.setText("0");
			return false;
		}
		currency.setCode(code);
		currency.setName(name);
		currency.setSymbol(tfSymbol.getText());
		currency.setMain(chkMain.isSelected());
		currency.setTolerance(tolerance);
		currency.setExchangeRate(exchangeRate);
		currency.setDecimalPlaces(tfDecimalPlaces.getInteger());

		if (chkMain.isSelected()) {
			CurrencyDAO dao = new CurrencyDAO();
			List<Currency> currencyList = dao.findAll();

			Session session = null;
			Transaction transaction = null;
			try {
				session = CurrencyDAO.getInstance().createNewSession();
				transaction = session.beginTransaction();

				for (Currency curr : currencyList) {
					curr.setMain(false);
					session.saveOrUpdate(curr);
				}
				transaction.commit();
			} catch (Exception ex) {
				transaction.rollback();
				BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, ex);
			} finally {
				session.close();
			}
		}
		return true;
	}

	public String getDisplayText() {
		Currency currency = (Currency) getBean();
		if (currency.getId() == null) {
			return Messages.getString("CurrencyForm.7");
		}
		return Messages.getString("CurrencyForm.8");
	}
}
