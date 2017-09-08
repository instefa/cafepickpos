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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.model.Tax;
import ru.instefa.cafepickpos.model.TaxGroup;
import ru.instefa.cafepickpos.model.dao.TaxDAO;
import ru.instefa.cafepickpos.model.dao.TaxGroupDAO;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.dialog.TaxSelectionDialog;
import ru.instefa.cafepickpos.ui.model.TaxForm;
import ru.instefa.cafepickpos.util.NumberUtil;
import ru.instefa.cafepickpos.util.POSUtil;

public class TaxGroupExplorer extends TransparentPanel implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;
	private JXTreeTable treeTable;
	private TaxGroupTreeTableModel noRootTreeTableModel;
	private List<TaxGroup> rootGroupList;

	public TaxGroupExplorer() {
		setLayout(new BorderLayout(5, 5));
		treeTable = new JXTreeTable();
		treeTable.setRowHeight(PosUIManager.getSize(30));
		treeTable.setRootVisible(false);
		rootGroupList = TaxGroupDAO.getInstance().findAll();
		createTree();
		treeTable.expandAll();
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				int col = treeTable.columnAtPoint(me.getPoint());
				if (me.getClickCount() == 2 && (col == 0)) {

					treeTable.expandPath(treeTable.getPathForRow(treeTable.getSelectedRow()));
				}
				else if (me.getClickCount() == 2 && (col != 0)) {
					editSelectedRow();
				}
			}
		});

		add(new JScrollPane(treeTable), BorderLayout.CENTER);
		createButtonPanel();
		treeTable.setDefaultRenderer(Object.class, new PosTableRenderer());
	}

	private void createTree() {
		if (rootGroupList != null) {
			TaxGroup demo = new TaxGroup();
			demo.setId("0");
			demo.setName("Root");
			DefaultMutableTreeTableNode rootNode = new DefaultMutableTreeTableNode(demo);
			rootNode.setUserObject(demo);

			for (TaxGroup inventoryGroup : rootGroupList) {
				DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(inventoryGroup);
				rootNode.add(node);
				buildTaxTree(node);
			}

			noRootTreeTableModel = new TaxGroupTreeTableModel(rootNode);
			treeTable.setTreeTableModel(noRootTreeTableModel);
		}
	}

	private void buildTaxTree(DefaultMutableTreeTableNode defaultMutableTreeTableNode) {
		TaxGroup attributeGroup = (TaxGroup) defaultMutableTreeTableNode.getUserObject();
		if (attributeGroup == null) {
			return;
		}

		defaultMutableTreeTableNode.setAllowsChildren(true);
		List<Tax> attributeList = attributeGroup.getTaxes();
		for (Tax tax : attributeList) {
			DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(tax);
			defaultMutableTreeTableNode.add(node);
		}
	}

	class TaxGroupTreeTableModel extends DefaultTreeTableModel {
		private final String[] COLUMN_NAMES = {
            Messages.getString("LABEL_GROUP"),
            Messages.getString("LABEL_NAME"),
            POSConstants.RATE
        };

		public TaxGroupTreeTableModel(DefaultMutableTreeTableNode rootTax) {
			super(rootTax);
		}

		@Override
		public void setRoot(TreeTableNode root) {
			super.setRoot(root);
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public boolean isCellEditable(Object node, int column) {
			return false;
		}

		@Override
		public Object getValueAt(Object node, int column) {
			if (node instanceof DefaultMutableTreeTableNode) {
				if (((DefaultMutableTreeTableNode) node).getUserObject() instanceof TaxGroup) {
					TaxGroup inventoryTax = (TaxGroup) ((DefaultMutableTreeTableNode) node).getUserObject();
					if (inventoryTax == null) {
						return "";
					}
					switch (column) {
						case 0:
							return inventoryTax.getName();
					}
				}
				else if (((DefaultMutableTreeTableNode) node).getUserObject() instanceof Tax) {
					Tax tax = (Tax) ((DefaultMutableTreeTableNode) node).getUserObject();
					if (tax == null) {
						return "";
					}
					switch (column) {
						case 1:
							return tax.getName();
						case 2:
							return NumberUtil.trimDecilamIfNotNeeded(tax.getRate()) + "% ";
					}
				}
			}

			return null;
		}
	}

	private void createButtonPanel() {
		ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();

		JButton btn_newGroup = new JButton(Messages.getString("TaxGroupExplorer.0"));
		JButton btn_newTax = new JButton(Messages.getString("TaxGroupExplorer.1"));
		JButton editButton = explorerButton.getEditButton();
		JButton deleteButton = explorerButton.getDeleteButton();

		btn_newGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String groupName = JOptionPane.showInputDialog(POSUtil.getFocusedWindow(),
                    Messages.getString("TaxGroupExplorer.2"));
				if (groupName == null) {
					return;
				}
				if (groupName.equals("")) {
					BOMessageDialog.showError(POSUtil.getFocusedWindow(),
                        Messages.getString("TaxGroupExplorer.3"));
					return;
				}

				if (groupName.length() > 30) {
					BOMessageDialog.showError(POSUtil.getFocusedWindow(),
                        Messages.getString("TaxGroupExplorer.4"));
					return;
				}

				TaxGroup inventoryGroup = new TaxGroup();
				inventoryGroup.setName(groupName);

				TaxGroupDAO inventoryGroupDAO = new TaxGroupDAO();
				inventoryGroupDAO.saveOrUpdate(inventoryGroup);

				if (inventoryGroup != null) {
					MutableTreeTableNode root = (MutableTreeTableNode) noRootTreeTableModel.getRoot();
					noRootTreeTableModel.insertNodeInto(new DefaultMutableTreeTableNode(inventoryGroup), root, root.getChildCount());
				}

				treeTable.expandAll();
			}
		});

		btn_newTax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TaxGroup selectedGroup = getSelectedGroup();
				if (selectedGroup == null) {
					Tax tax = new Tax();
					TaxForm editor = new TaxForm(tax);
					BeanEditorDialog dialog = new BeanEditorDialog(editor);
					dialog.setSize(PosUIManager.getSize(500, 400));
					dialog.open();
					if (dialog.isCanceled())
						return;
				}
				else {
					TaxSelectionDialog dialog = new TaxSelectionDialog(selectedGroup);
					dialog.setSize(500, 400);
					dialog.open();
					if (dialog.isCanceled())
						return;
					List<Tax> taxes = dialog.getSelectedTaxList();
					selectedGroup.setTaxes(taxes);
					TaxGroupDAO.getInstance().saveOrUpdate(selectedGroup);
					rootGroupList = TaxGroupDAO.getInstance().findAll();
					createTree();
					treeTable.expandAll();
				}
			}

		});

		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editSelectedRow();

			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteInventoryTax();
			}

			private void deleteInventoryTax() {
				try {
					int index = treeTable.getSelectedRow();
					if (index < 0)
						return;

					TreePath treePath = treeTable.getPathForRow(index);
					DefaultMutableTreeTableNode lastPathComponent = (DefaultMutableTreeTableNode) treePath.getLastPathComponent();

					if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof TaxGroup) {

						TaxGroup attributeGroup = (TaxGroup) lastPathComponent.getUserObject();

						if (POSMessageDialog.showYesNoQuestionDialog(getRootPane(), POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
							return;
						}

						TaxGroupDAO inventoryGroupDAO = new TaxGroupDAO();
						inventoryGroupDAO.delete(attributeGroup);

						MutableTreeTableNode tableNode = findTreeNodeForTax((MutableTreeTableNode) noRootTreeTableModel.getRoot(), attributeGroup.getId());
						if (tableNode.getParent() != null) {
							noRootTreeTableModel.removeNodeFromParent(tableNode);
						}

					}
					else if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof Tax) {

						Tax tax = (Tax) lastPathComponent.getUserObject();
						if (POSMessageDialog.showYesNoQuestionDialog(getRootPane(), POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
							return;
						}

						MutableTreeTableNode tableNode = findTreeNodeForTax((MutableTreeTableNode) noRootTreeTableModel.getRoot(), String.valueOf(tax.getId()));
						if (tableNode.getParent() != null) {
							noRootTreeTableModel.removeNodeFromParent(tableNode);
						}

						TaxDAO taxDAO = new TaxDAO();
						taxDAO.delete(tax);

					}

					treeTable.repaint();
					treeTable.revalidate();

				} catch (Throwable x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}

			}

		});

		TransparentPanel panel = new TransparentPanel();

		panel.add(btn_newGroup);
		panel.add(btn_newTax);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

	}

	private void editSelectedRow() {
		try {
			int index = treeTable.getSelectedRow();
			if (index < 0)
				return;
			TreePath treePath = treeTable.getPathForRow(index);

			DefaultMutableTreeTableNode lastPathComponent = (DefaultMutableTreeTableNode) treePath.getLastPathComponent();

			if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof TaxGroup) {

				TaxGroup attributeGroup = (TaxGroup) lastPathComponent.getUserObject();

				String groupName = JOptionPane.showInputDialog(POSUtil.getFocusedWindow(), 
                    Messages.getString("TaxGroupExplorer.5"),
                    attributeGroup.getName());
				if (groupName == null) {
					return;
				}
				if (groupName.isEmpty()) {
					BOMessageDialog.showError(POSUtil.getFocusedWindow(),
                        Messages.getString("TaxGroupExplorer.3"));
					return;
				}
				if (groupName.length() > 30) {
					BOMessageDialog.showError(POSUtil.getFocusedWindow(),
                        Messages.getString("TaxGroupExplorer.4"));
					return;
				}

				attributeGroup.setName(groupName);
				TaxGroupDAO inventoryGroupDAO = new TaxGroupDAO();
				inventoryGroupDAO.saveOrUpdate(attributeGroup);
			}
			else if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof Tax) {

				Tax tax = (Tax) lastPathComponent.getUserObject();

				TaxForm taxFormTree = new TaxForm(tax);
				BeanEditorDialog dialog = new BeanEditorDialog(taxFormTree);
				dialog.setPreferredSize(PosUIManager.getSize(500, 400));
				dialog.open();
				if (dialog.isCanceled())
					return;
			}

			rootGroupList = TaxGroupDAO.getInstance().findAll();
			createTree();
			treeTable.expandAll();

		} catch (Throwable x) {
			BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
		}
	}

	public Tax getSelectedAtrribute() {
		try {
			int index = treeTable.getSelectedRow();
			if (index < 0)
				return null;
			TreePath treePath = treeTable.getPathForRow(index);

			DefaultMutableTreeTableNode lastPathComponent = (DefaultMutableTreeTableNode) treePath.getLastPathComponent();

			if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof TaxGroup) {
				POSMessageDialog.showMessage(POSUtil.getFocusedWindow(), Messages.getString("TaxGroupExplorer.6"));
				return null;
			}
			else if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof Tax) {
				return (Tax) lastPathComponent.getUserObject();
			}
		} catch (Throwable x) {
			BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
		}
		return null;
	}

	public TaxGroup getSelectedGroup() {
		try {
			int index = treeTable.getSelectedRow();
			if (index < 0)
				return null;
			TreePath treePath = treeTable.getPathForRow(index);

			DefaultMutableTreeTableNode lastPathComponent = (DefaultMutableTreeTableNode) treePath.getLastPathComponent();

			if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof TaxGroup) {
				return ((TaxGroup) lastPathComponent.getUserObject());
			}
			else if (((DefaultMutableTreeTableNode) lastPathComponent).getUserObject() instanceof Tax) {
				return ((TaxGroup) lastPathComponent.getParent().getUserObject());
			}
		} catch (Throwable x) {
			BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
		}
		return null;
	}

	public MutableTreeTableNode findTreeNodeForTax(MutableTreeTableNode attributeNode, String attributeId) {
		TaxGroup groupTax;
		Tax attributeTax;
		if (((DefaultMutableTreeTableNode) attributeNode).getUserObject() instanceof TaxGroup) {
			groupTax = (TaxGroup) attributeNode.getUserObject();
			if (attributeId.equals(groupTax.getId())) {
				return attributeNode;
			}

			Enumeration<? extends TreeTableNode> children = attributeNode.children();
			while (children.hasMoreElements()) {
				MutableTreeTableNode treeTableNode = (MutableTreeTableNode) children.nextElement();
				MutableTreeTableNode findAttById = findTreeNodeForTax(treeTableNode, attributeId);
				if (findAttById != null) {
					return findAttById;
				}
			}
		}
		else if (((DefaultMutableTreeTableNode) attributeNode).getUserObject() instanceof Tax) {
			attributeTax = (Tax) attributeNode.getUserObject();
			if (attributeId.equals(String.valueOf(attributeTax.getId()))) {
				return attributeNode;
			}

			Enumeration<? extends TreeTableNode> children = attributeNode.children();
			while (children.hasMoreElements()) {
				MutableTreeTableNode treeTableNode = (MutableTreeTableNode) children.nextElement();
				MutableTreeTableNode findAttById = findTreeNodeForTax(treeTableNode, attributeId);
				if (findAttById != null) {
					return findAttById;
				}
			}
		}

		return null;
	}
}
