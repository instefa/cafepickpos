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

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.beanutils.PropertyUtils;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.MenuCategory;
import ru.instefa.cafepickpos.model.MenuGroup;
import ru.instefa.cafepickpos.model.MenuItem;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.dao.MenuCategoryDAO;
import ru.instefa.cafepickpos.model.dao.MenuGroupDAO;
import ru.instefa.cafepickpos.model.dao.MenuItemDAO;
import ru.instefa.cafepickpos.model.dao.OrderTypeDAO;
import ru.instefa.cafepickpos.model.dao.ShopTableDAO;
import ru.instefa.cafepickpos.swing.POSToggleButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.table.ShopTableForm;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.model.MenuCategoryForm;
import ru.instefa.cafepickpos.ui.model.MenuGroupForm;
import ru.instefa.cafepickpos.ui.model.MenuItemForm;
import ru.instefa.cafepickpos.ui.model.OrderTypeForm;
import ru.instefa.cafepickpos.ui.views.SwitchboardView;
import ru.instefa.cafepickpos.ui.views.TableMapView;
import ru.instefa.cafepickpos.ui.views.order.OrderView;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.PosGuiUtil;

public class QuickMaintenanceExplorer extends TransparentPanel {
	private static POSToggleButton btnCursor;
	private static POSToggleButton btnEdit;
	private static POSToggleButton btnCopy;
	private static POSToggleButton btnDelete;
	private static POSToggleButton btni18;

	public QuickMaintenanceExplorer() {
		super(new MigLayout("inset 0"));

		int btnSize = PosUIManager.getSize(60);
		btnCursor = new POSToggleButton();
		btnCursor.setIcon(IconFactory.getIcon("cursor_hand.png"));
		btnEdit = new POSToggleButton(Messages.getString("QuickMaintenanceExplorer.0"));
		btnCopy = new POSToggleButton(Messages.getString("QuickMaintenanceExplorer.1"));
		btnDelete = new POSToggleButton(Messages.getString("QuickMaintenanceExplorer.2"));
		btni18 = new POSToggleButton("i18n");

		ButtonGroup group = new ButtonGroup();
		group.add(btnCursor);
		group.add(btnCopy);
		group.add(btnEdit);
		group.add(btnDelete);
		group.add(btni18);

		btnEdit.setSelected(true);

		add(btnCursor, "w " + btnSize + "!, h " + btnSize + "!");
		add(btnEdit, "w " + btnSize + "!, h " + btnSize + "!");
		add(btnCopy, "w " + btnSize + "!, h " + btnSize + "!");
		add(btnDelete, "w " + btnSize + "!, h " + btnSize + "!");
		//add(btni18, "w " + btnSize + "!, h " + btnSize + "!");
	}

	public static void quickMaintain(Object object) {
		if (object instanceof MenuItem) {
			quickMaintainMenuItem((MenuItem) object);
			return;
		}
		if (btnCursor.isSelected())
			return;
		if (object instanceof MenuGroup) {
			quickMaintainMenuGroup((MenuGroup) object);
		}
		else if (object instanceof MenuCategory) {
			quickMaintainMenuCategory((MenuCategory) object);
		}
		else if (object instanceof OrderType) {
			quickMaintainOrderType((OrderType) object);
		}
		else if (object instanceof ShopTable) {
			quickMaintainShopTable((ShopTable) object);
		}
	}

	private static void quickMaintainMenuItem(MenuItem menuItem) {
		try {
			if (menuItem.getId() != null) {
				if (btnCopy.isSelected()) {
					MenuItem newMenuItem = new MenuItem();
					PropertyUtils.copyProperties(newMenuItem, menuItem);
					newMenuItem.setId(null);
					newMenuItem.setFractionalUnit(menuItem.isFractionalUnit());
					newMenuItem.setDisableWhenStockAmountIsZero(menuItem.isDisableWhenStockAmountIsZero());
					newMenuItem.setShowImageOnly(menuItem.isShowImageOnly());
					menuItem = newMenuItem;
				}
				else if (btnDelete.isSelected()) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}
					MenuItemDAO foodItemDAO = new MenuItemDAO();
					if (menuItem.getDiscounts() != null && menuItem.getDiscounts().size() > 0) {
						foodItemDAO.releaseParentAndDelete(menuItem);
					}
					else {
						try {
							foodItemDAO.delete(menuItem.getId());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					OrderView.getInstance().getItemView().updateView(menuItem);
					return;
				}
			}
			else {
				menuItem.addToorderTypeList(OrderView.getInstance().getTicketView().getTicket().getOrderType());
			}
			MenuItemForm editor = new MenuItemForm(menuItem);
			BeanEditorDialog dialog = new BeanEditorDialog(Application.getPosWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;
			OrderView.getInstance().getItemView().updateView(menuItem);
			return;
		} catch (Exception e) {
			return;
		}
	}

	private static void quickMaintainMenuGroup(MenuGroup menuGroup) {
		try {
			if (menuGroup.getId() != null) {
				if (btnCopy.isSelected()) {
					MenuGroup newMenuGroup = new MenuGroup();
					PropertyUtils.copyProperties(newMenuGroup, menuGroup);
					newMenuGroup.setId(null);
					menuGroup = newMenuGroup;
				}
				else if (btnDelete.isSelected()) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}
					MenuItemDAO menuItemDao = new MenuItemDAO();
					List<MenuItem> menuItems = menuItemDao.findByParent(null, menuGroup, true);

					if (menuItems.size() > 0) {
						if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
								PosGuiUtil.getMultilineHtml(Messages.getString("MenuGroupExplorer.0")),
								POSConstants.DELETE) != JOptionPane.YES_OPTION) { //$NON-NLS-1$
							return;
						}
						menuItemDao.releaseParent(menuItems);
					}
					MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
					foodGroupDAO.delete(menuGroup);
					OrderView.getInstance().getGroupView().updateView(menuGroup);
					return;
				}
			}
			MenuGroupForm editor = new MenuGroupForm(menuGroup);
			BeanEditorDialog dialog = new BeanEditorDialog(Application.getPosWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;
			OrderView.getInstance().getGroupView().updateView(menuGroup);
			return;
		} catch (Exception e) {
			return;
		}
	}

	private static void quickMaintainMenuCategory(MenuCategory menuCategory) {
		try {
			if (menuCategory.getId() != null) {
				if (btnCopy.isSelected()) {
					MenuCategory newMenuCategory = new MenuCategory();
					PropertyUtils.copyProperties(newMenuCategory, menuCategory);
					newMenuCategory.setId(null);
					menuCategory = newMenuCategory;
				}
				else if (btnDelete.isSelected()) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}

					MenuGroupDAO menuGroupDao = new MenuGroupDAO();
					List<MenuGroup> menuGroups = menuGroupDao.findByParent(menuCategory);

					if (menuGroups.size() > 0) {
						if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
								PosGuiUtil.getMultilineHtml(Messages.getString("MenuCategoryExplorer.0")),
								POSConstants.DELETE) != JOptionPane.YES_OPTION) { //$NON-NLS-1$
							return;
						}
						menuGroupDao.releaseParent(menuGroups);
					}
					MenuCategoryDAO dao = new MenuCategoryDAO();
					dao.delete(menuCategory);
					OrderView.getInstance().getCategoryView().updateView(menuCategory);
					return;
				}
			}
			MenuCategoryForm editor = new MenuCategoryForm(menuCategory);
			BeanEditorDialog dialog = new BeanEditorDialog(Application.getPosWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;
			OrderView.getInstance().getCategoryView().updateView(menuCategory);
			return;
		} catch (Exception e) {
			return;
		}
	}

	private static void quickMaintainOrderType(OrderType orderType) {
		try {
			if (orderType.getId() != null) {
				if (btnCopy.isSelected()) {
					OrderType newOrderType = new OrderType();
					PropertyUtils.copyProperties(newOrderType, orderType);
					newOrderType.setId(null);
					orderType = newOrderType;
				}
				else if (btnDelete.isSelected()) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}

					OrderTypeDAO orderTypeDAO = new OrderTypeDAO();
					orderTypeDAO.delete(orderType.getId());
					SwitchboardView.getInstance().rendererOrderPanel();
					return;
				}
			}
			OrderTypeForm editor = new OrderTypeForm(orderType);
			BeanEditorDialog dialog = new BeanEditorDialog(Application.getPosWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;
			Application.getInstance().refreshOrderTypes();
			SwitchboardView.getInstance().rendererOrderPanel();
			return;
		} catch (Exception e) {
			return;
		}
	}

	private static void quickMaintainShopTable(ShopTable shopTable) {
		try {
			if (shopTable.getId() != null) {
				if (btnCopy.isSelected()) {
					ShopTable newShopTable = new ShopTable();
					PropertyUtils.copyProperties(newShopTable, shopTable);
					newShopTable.setId(shopTable.getId() + 1);
					newShopTable.setDescription(String.valueOf(shopTable.getId() + 1));
					shopTable = newShopTable;
				}
				else if (btnDelete.isSelected()) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}

					ShopTableDAO shopTableDAO = new ShopTableDAO();
					shopTableDAO.delete(shopTable.getId());
					TableMapView.getInstance().updateView();
					return;
				}
			}
			ShopTableForm editor = new ShopTableForm();
			editor.setBean(shopTable);
			BeanEditorDialog dialog = new BeanEditorDialog(Application.getPosWindow(), editor);
			dialog.open(600, 500);
			if (dialog.isCanceled())
				return;
			TableMapView.getInstance().updateView();
			return;
		} catch (Exception e) {
			return;
		}
	}
}
