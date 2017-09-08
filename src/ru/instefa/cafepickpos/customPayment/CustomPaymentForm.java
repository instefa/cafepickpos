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
package ru.instefa.cafepickpos.customPayment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.hibernate.StaleObjectStateException;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.exceptions.IllegalModelStateException;
import ru.instefa.cafepickpos.model.CustomPayment;
import ru.instefa.cafepickpos.model.dao.CustomPaymentDAO;
import ru.instefa.cafepickpos.swing.FixedLengthTextField;
import ru.instefa.cafepickpos.ui.BeanEditor;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.POSUtil;

public class CustomPaymentForm extends BeanEditor<CustomPayment> {

	private JLabel lblName;
	private JLabel lblRefNumberFieldName;

	private FixedLengthTextField txtName;
	private FixedLengthTextField txtRefNumberFieldName;
	private JCheckBox cbRequiredRefNumber;

	public CustomPaymentForm() {
		initComponent();
	}

	private void initComponent() {
		cbRequiredRefNumber = new JCheckBox(Messages.getString("CustomPaymentForm.0")); //$NON-NLS-1$
		lblRefNumberFieldName = new JLabel(Messages.getString("CustomPaymentForm.1")); //$NON-NLS-1$
		lblName = new JLabel(Messages.getString("CustomPaymentForm.2")); //$NON-NLS-1$
		txtName = new FixedLengthTextField(60);
		txtRefNumberFieldName = new FixedLengthTextField(60);

		setLayout(new MigLayout("hidemode 1", "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		add(lblName, "split 2"); //$NON-NLS-1$
		add(txtName, "wrap"); //$NON-NLS-1$
		add(cbRequiredRefNumber, "wrap"); //$NON-NLS-1$
		add(lblRefNumberFieldName, "split 2"); //$NON-NLS-1$
		add(txtRefNumberFieldName);

		cbRequiredRefNumber.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cbRequiredRefNumber.isSelected()) {
					lblRefNumberFieldName.setVisible(true);
					txtRefNumberFieldName.setVisible(true);
				}
				else {
					lblRefNumberFieldName.setVisible(false);
					txtRefNumberFieldName.setVisible(false);
				}
			}
		});

		lblRefNumberFieldName.setVisible(false);
		txtRefNumberFieldName.setVisible(false);
		txtName.setEnabled(false);
		cbRequiredRefNumber.setEnabled(false);
	}

	@Override
	public void setFieldsEnable(boolean enable) {
		txtName.setEnabled(enable);
		cbRequiredRefNumber.setEnabled(enable);
		txtRefNumberFieldName.setEnabled(enable);
	}

	@Override
	public void createNew() {
		setBean(new CustomPayment());
		lblRefNumberFieldName.setVisible(false);
		txtRefNumberFieldName.setVisible(false);
		cbRequiredRefNumber.setSelected(false);
	}

	@Override
	public boolean save() {
		try {
			if (!updateModel())
				return false;

			CustomPayment payment = (CustomPayment) getBean();
			CustomPaymentDAO.getInstance().saveOrUpdate(payment);

			updateView();

			return true;
		} catch (IllegalModelStateException e) {
		} catch (StaleObjectStateException e) {
			BOMessageDialog.showError(this, Messages.getString("CustomPaymentForm.10")); //$NON-NLS-1$
		}
		return false;
	}

	@Override
	protected void updateView() {

		CustomPayment payment = (CustomPayment) getBean();

		txtName.setText(payment.getName());

		if (payment.isRequiredRefNumber()) {
			txtRefNumberFieldName.setVisible(true);
			lblRefNumberFieldName.setVisible(true);
			txtRefNumberFieldName.setText(payment.getRefNumberFieldName());
			cbRequiredRefNumber.setSelected(payment.isRequiredRefNumber());
		}
		else {
			lblRefNumberFieldName.setVisible(false);
			txtRefNumberFieldName.setVisible(false);
			cbRequiredRefNumber.setSelected(payment.isRequiredRefNumber());
		}
	}

	@Override
	protected boolean updateModel() throws IllegalModelStateException {

		CustomPayment payment = (CustomPayment) getBean();

		if (txtName.getText().equals("")) { //$NON-NLS-1$
			POSMessageDialog.showMessage(null, Messages.getString("CustomPaymentForm.12")); //$NON-NLS-1$
			return false;
		}

		payment.setName(txtName.getText());

		if (cbRequiredRefNumber.isSelected()) {
			if (txtRefNumberFieldName.getText().equals("")) { //$NON-NLS-1$
				POSMessageDialog.showMessage(null, Messages.getString("CustomPaymentForm.14")); //$NON-NLS-1$
				return false;
			}
			payment.setRefNumberFieldName(txtRefNumberFieldName.getText());
			payment.setRequiredRefNumber(true);
		}
		else {
			payment.setRefNumberFieldName(""); //$NON-NLS-1$
			payment.setRequiredRefNumber(false);
		}
		return true;
	}

	@Override
	public boolean delete() {
		try {
			CustomPayment payment = (CustomPayment) getBean();
			if (payment == null)
				return false;

			CustomPaymentDAO.getInstance().delete(payment);
			return true;
		} catch (Exception e) {
			POSMessageDialog.showError(POSUtil.getBackOfficeWindow(), e.getMessage(), e);
		}
		return false;
	}

	@Override
	public String getDisplayText() {
		return null;
	}
}
