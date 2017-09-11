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
 * UserListDialog.java
 *
 * Created on September 8, 2006, 2:04 AM
 */

package ru.instefa.cafepickpos.ui.dialog;

import java.util.List;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.swing.ListComboBoxModel;

/**
 *
 * @author  MShahriar
 */
public class UserListDialog extends POSDialog {
    
    /** Creates new form UserListDialog */
    public UserListDialog() {
        initComponents();
        setTitle(ru.instefa.cafepickpos.POSConstants.USER_LIST);
        
        List<User> userList = UserDAO.instance.findAll();
        cbUserList.setModel(new ListComboBoxModel(userList));
        
        cbUserList.setFocusable(false);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        titlePanel1 = new ru.instefa.cafepickpos.ui.TitlePanel();
        transparentPanel1 = new ru.instefa.cafepickpos.swing.TransparentPanel();
        transparentPanel2 = new ru.instefa.cafepickpos.swing.TransparentPanel();
        btnOk = new ru.instefa.cafepickpos.swing.PosButton();
        btnCancel = new ru.instefa.cafepickpos.swing.PosButton();
        jSeparator1 = new javax.swing.JSeparator();
        transparentPanel3 = new ru.instefa.cafepickpos.swing.TransparentPanel();
        cbUserList = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        titlePanel1.setTitle(ru.instefa.cafepickpos.POSConstants.SELECT_USER);
        getContentPane().add(titlePanel1, java.awt.BorderLayout.NORTH);

        transparentPanel1.setLayout(new java.awt.BorderLayout());

        btnOk.setIcon(IconFactory.getIcon("finish.png")); //$NON-NLS-1$ //$NON-NLS-2$
        btnOk.setText(ru.instefa.cafepickpos.POSConstants.OK);
        btnOk.setPreferredSize(new java.awt.Dimension(120, 50));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doOk(evt);
            }
        });

        transparentPanel2.add(btnOk);

        btnCancel.setIcon(IconFactory.getIcon("cancel.png")); //$NON-NLS-1$ //$NON-NLS-2$
        btnCancel.setText(ru.instefa.cafepickpos.POSConstants.CANCEL);
        btnCancel.setPreferredSize(new java.awt.Dimension(120, 50));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancel(evt);
            }
        });

        transparentPanel2.add(btnCancel);

        transparentPanel1.add(transparentPanel2, java.awt.BorderLayout.CENTER);

        transparentPanel1.add(jSeparator1, java.awt.BorderLayout.NORTH);

        getContentPane().add(transparentPanel1, java.awt.BorderLayout.SOUTH);

        cbUserList.setFont(new java.awt.Font("Tahoma", 1, 18)); //$NON-NLS-1$

        org.jdesktop.layout.GroupLayout transparentPanel3Layout = new org.jdesktop.layout.GroupLayout(transparentPanel3);
        transparentPanel3.setLayout(transparentPanel3Layout);
        transparentPanel3Layout.setHorizontalGroup(
            transparentPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transparentPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(cbUserList, 0, 486, Short.MAX_VALUE)
                .addContainerGap())
        );
        transparentPanel3Layout.setVerticalGroup(
            transparentPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transparentPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(cbUserList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        getContentPane().add(transparentPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doOk
    	setCanceled(false);
    	dispose();
    }//GEN-LAST:event_doOk

    private void doCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancel
    	setCanceled(true);
    	dispose();
    }//GEN-LAST:event_doCancel
    
    public User getSelectedUser() {
    	return (User) cbUserList.getSelectedItem();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ru.instefa.cafepickpos.swing.PosButton btnCancel;
    private ru.instefa.cafepickpos.swing.PosButton btnOk;
    private javax.swing.JComboBox cbUserList;
    private javax.swing.JSeparator jSeparator1;
    private ru.instefa.cafepickpos.ui.TitlePanel titlePanel1;
    private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel1;
    private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel2;
    private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel3;
    // End of variables declaration//GEN-END:variables
    
}