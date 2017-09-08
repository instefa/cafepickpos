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

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.DrawerUtil;

public class DrawerKickAction extends PosAction {
	//String executableFileName = "drawer-kick.bat";
	
	public DrawerKickAction() {
		super(Messages.getString("DrawerKickAction.0"), UserPermission.DRAWER_PULL); //$NON-NLS-1$
		
		setEnabled(Application.getInstance().getTerminal().isHasCashDrawer());
		
//		String osName = System.getProperty("os.name").toLowerCase();
//		if(osName.contains("linux")) {
//			executableFileName = "drawer-kick.sh";
//		}
	}

	@Override
	public void execute() {
		try {
//			File file = new File(Application.getInstance().getLocation(), executableFileName); //$NON-NLS-1$
//			if (file.exists()) {
//				Runtime.getRuntime().exec(file.getAbsolutePath());
//			}
			
			DrawerUtil.kickDrawer();
			
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(),e.getMessage(), e);
		}
	}

}
