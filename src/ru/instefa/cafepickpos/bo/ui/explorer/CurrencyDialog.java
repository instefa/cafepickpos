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
package ru.instefa.cafepickpos.bo.ui.explorer;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.Currency;
import ru.instefa.cafepickpos.model.dao.CurrencyDAO;
import ru.instefa.cafepickpos.ui.dialog.OkCancelOptionDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.POSUtil;

public class CurrencyDialog extends OkCancelOptionDialog {
	private CurrencyExplorer currencyExplorer;

	public CurrencyDialog() {
		JPanel contentPanel = getContentPanel();
		this.getContentPane();
		setTitle(Messages.getString("CurrencyDialog.0")); //$NON-NLS-1$
		setTitlePaneText(Messages.getString("CurrencyDialog.0")); //$NON-NLS-1$
		currencyExplorer = new CurrencyExplorer();
		contentPanel.add(currencyExplorer);
	}

	@Override
	public void doOk() {
		List<Currency> currencyList = currencyExplorer.getModel().getRows();

		Currency mainCurrency = null;
		boolean isMainSelected = false;
		for (Currency currency : currencyList) {
			if (currency.isMain()) {
				isMainSelected = true;
				mainCurrency = currency;
			}
		}

		if (!isMainSelected) {
			POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("CurrencyDialog.2")); //$NON-NLS-1$
			return;
		}
		else {
			if (mainCurrency.getExchangeRate() != 1) {
				if (POSMessageDialog.showYesNoQuestionDialog(this, Messages.getString("CurrencyDialog.3"), Messages.getString("CurrencyDialog.4")) == JOptionPane.OK_OPTION) { //$NON-NLS-1$ //$NON-NLS-2$
					mainCurrency.setExchangeRate(1.0);
				}
				else {
					return;
				}
			}
		}

		Session session = null;
		Transaction tx = null;
		try {
			session = CurrencyDAO.getInstance().createNewSession();
			tx = session.beginTransaction();

			for (Currency currency : currencyList) {
				session.saveOrUpdate(currency);
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			return;
		} finally {
			session.close();
		}

		setCanceled(true);
		dispose();
	}
}
