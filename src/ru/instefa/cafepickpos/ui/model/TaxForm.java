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
 * TaxEditor.java
 *
 * Created on August 3, 2006, 1:49 AM
 */

package ru.instefa.cafepickpos.ui.model;

import ru.instefa.cafepickpos.model.Tax;
import ru.instefa.cafepickpos.model.dao.TaxDAO;
import ru.instefa.cafepickpos.swing.DoubleTextField;
import ru.instefa.cafepickpos.swing.MessageDialog;
import ru.instefa.cafepickpos.ui.BeanEditor;
import ru.instefa.cafepickpos.util.POSUtil;

/**
 *
 * @author  MShahriar
 */
public class TaxForm extends BeanEditor {
    
    /** Creates new form TaxEditor */
    public TaxForm() {
        this(new Tax());
    }
    
    public TaxForm(Tax tax) {
    	initComponents();
    	
    	setBean(tax);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfName = new ru.instefa.cafepickpos.swing.FixedLengthTextField();
        tfRate = new DoubleTextField();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText(ru.instefa.cafepickpos.POSConstants.NAME + ":"); //$NON-NLS-1$

        jLabel2.setText(ru.instefa.cafepickpos.POSConstants.RATE + ":"); //$NON-NLS-1$

        tfRate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("%"); //$NON-NLS-1$

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(tfRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3))
                    .add(tfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(tfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(tfRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private ru.instefa.cafepickpos.swing.FixedLengthTextField tfName;
    private DoubleTextField tfRate;
    // End of variables declaration//GEN-END:variables
	@Override
	public boolean save() {
		
		try {
			if(!updateModel()) return false;
			
			Tax tax = (Tax) getBean();
			TaxDAO dao = new TaxDAO();
			dao.saveOrUpdate(tax);
		} catch (Exception e) {
			MessageDialog.showError(e);
			return false;
		}
		
		return true;
	}

	@Override
	protected void updateView() {
		Tax tax = (Tax) getBean();
		tfName.setText(tax.getName());
		tfRate.setText("" + tax.getRate()); //$NON-NLS-1$
	}

	@Override
	protected boolean updateModel() {
		Tax tax = (Tax) getBean();
		
		String name = tfName.getText();
    	if(POSUtil.isBlankOrNull(name)) {
    		MessageDialog.showError(ru.instefa.cafepickpos.POSConstants.NAME_REQUIRED);
    		return false;
    	}
		
		tax.setName(name);
		tax.setRate(tfRate.getDouble());
		
		return true;
	}
    
	public String getDisplayText() {
    	Tax tax = (Tax) getBean();
    	if(tax.getId() == null) {
    		return ru.instefa.cafepickpos.POSConstants.NEW_TAX_RATE;
    	}
    	return ru.instefa.cafepickpos.POSConstants.EDIT_TAX_RATE;
    }
}
