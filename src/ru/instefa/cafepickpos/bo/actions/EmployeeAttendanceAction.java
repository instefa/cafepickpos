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

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.bo.ui.BackOfficeWindow;
import ru.instefa.cafepickpos.report.AttendanceReportView;

public class EmployeeAttendanceAction extends AbstractAction {

	public EmployeeAttendanceAction() {
		super(Messages.getString("EmployeeAttendanceAction.0")); //$NON-NLS-1$
	}

	public EmployeeAttendanceAction(String name) {
		super(name);
	}

	public EmployeeAttendanceAction(String name, Icon icon) {
		super(name, icon);
	}

	public void actionPerformed(ActionEvent e) {
		BackOfficeWindow window = ru.instefa.cafepickpos.util.POSUtil.getBackOfficeWindow();
		JTabbedPane tabbedPane = window.getTabbedPane();
		
		AttendanceReportView reportView = null;
		int index = tabbedPane.indexOfTab(Messages.getString("EmployeeAttendanceAction.1")); //$NON-NLS-1$
		if (index == -1) {
			reportView = new AttendanceReportView();
			tabbedPane.addTab(Messages.getString("EmployeeAttendanceAction.1"), reportView); //$NON-NLS-1$
		}
		else {
			reportView = (AttendanceReportView) tabbedPane.getComponentAt(index);
		}
		tabbedPane.setSelectedComponent(reportView);
	}

}
