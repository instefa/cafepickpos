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
package ru.instefa.cafepickpos.bo.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

import ru.instefa.cafepickpos.bo.ui.BackOfficeWindow;
import ru.instefa.cafepickpos.bo.ui.explorer.ModifierExplorer;

public class ModifierExplorerAction extends AbstractAction {

	public ModifierExplorerAction() {
		super(ru.instefa.cafepickpos.POSConstants.MENU_MODIFIERS);
	}

	public ModifierExplorerAction(String name) {
		super(name);
	}

	public ModifierExplorerAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow backOfficeWindow = ru.instefa.cafepickpos.util.POSUtil.getBackOfficeWindow();
		JTabbedPane tabbedPane;
		ModifierExplorer modifier;
		tabbedPane = backOfficeWindow.getTabbedPane();
		int index = tabbedPane.indexOfTab(ru.instefa.cafepickpos.POSConstants.MODIFIER_EXPLORER);
		if (index == -1) {
			modifier = new ModifierExplorer();
			tabbedPane.addTab(ru.instefa.cafepickpos.POSConstants.MODIFIER_EXPLORER, modifier);
		}
		else {
			modifier = (ModifierExplorer) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(modifier);
	}

}
