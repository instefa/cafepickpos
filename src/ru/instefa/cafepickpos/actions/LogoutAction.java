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
package ru.instefa.cafepickpos.actions;

import java.awt.Window;

import javax.swing.Action;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.main.PosWindow;

public class LogoutAction extends ViewChangeAction {

	public LogoutAction() {
		super(Messages.getString("Logout")); //$NON-NLS-1$
	}

	public LogoutAction(boolean showText, boolean showIcon) {
		if (showText) {
			putValue(Action.NAME, Messages.getString("Logout")); //$NON-NLS-1$
		}
		if (showIcon) {
			putValue(Action.SMALL_ICON, IconFactory.getIcon("logout.png")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public void execute() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (!(window instanceof PosWindow)) {
				window.setVisible(false);
				window.dispose();
			}
		}
		Application.getInstance().doLogout();
	}

}
