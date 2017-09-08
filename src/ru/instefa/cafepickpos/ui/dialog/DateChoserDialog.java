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
 * * Contributor(s): pymancer <pymancer@gmail.com>.
 * ************************************************************************
 */

package ru.instefa.cafepickpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.AttendenceHistory;
import ru.instefa.cafepickpos.model.Shift;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.swing.ComboBoxModel;
import ru.instefa.cafepickpos.swing.IntegerTextField;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.ui.util.UiUtil;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.ShiftUtil;

public class DateChoserDialog extends POSDialog {
	private org.jdesktop.swingx.JXDatePicker tbStartDate;
	private org.jdesktop.swingx.JXDatePicker tbEndDate;
	private IntegerTextField tfStartHour;
	private IntegerTextField tfStartMin;
	private JRadioButton rbStartAm;
	private JRadioButton rbStartPm;
	private IntegerTextField tfEndHour;
	private IntegerTextField tfEndMin;
	private JRadioButton rbEndAm;
	private JRadioButton rbEndPm;
	private ButtonGroup btnGroupStartAmPm;
	private ButtonGroup btnGroupEndAmPm;
	private PosButton btnOk;
	private PosButton btnCancel;
	private AttendenceHistory attendenceHistory;
	private JComboBox cbEmployees;

	private JCheckBox chkClockOut;

	// 20170717, pymancer, default - 24 Hour Clock, settings
	private final int lastHour = TerminalConfig.isUse24HourClockMode() ? 24 : 12;
	
	public DateChoserDialog(String title) {
		super(POSUtil.getBackOfficeWindow(), title);
		attendenceHistory = new AttendenceHistory();
		initUi();
	}

	public DateChoserDialog(AttendenceHistory history, String title) {
		super(POSUtil.getBackOfficeWindow(), title);
		this.attendenceHistory = history;
		initUi();
	}

	private void initUi() {
		setIconImage(Application.getApplicationIcon().getImage());
		setResizable(false);
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.setBackground(Color.red);

		List<User> employees = UserDAO.getInstance().findAll();
		cbEmployees = new JComboBox<User>(new ComboBoxModel(employees));

		JPanel topPanel = new JPanel(new MigLayout());
		topPanel.add(new JLabel(Messages.getString("DateChoserDialog.0")));
		topPanel.add(cbEmployees);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel(new MigLayout("wrap 2", " [][][][][][][][][]", "[][]"));
		panel.setBorder(new TitledBorder("-"));

		if (!TerminalConfig.isUse24HourClockMode()) {
			// 12 Hour Clock, controls
			btnGroupStartAmPm = new ButtonGroup();
			rbStartAm = new JRadioButton(POSConstants.AM); //$NON-NLS-1$
			rbStartPm = new JRadioButton(POSConstants.PM); //$NON-NLS-1$
			btnGroupStartAmPm.add(rbStartAm);
			btnGroupStartAmPm.add(rbStartPm);
			rbStartPm.setSelected(true);
	
			btnGroupEndAmPm = new ButtonGroup();
			rbEndAm = new JRadioButton(POSConstants.AM); //$NON-NLS-1$
			rbEndPm = new JRadioButton(POSConstants.PM); //$NON-NLS-1$
			btnGroupEndAmPm.add(rbEndAm);
			btnGroupEndAmPm.add(rbEndPm);
			rbEndPm.setSelected(true);
		}

		tbStartDate = UiUtil.getCurrentMonthStart();

		tbEndDate = UiUtil.getCurrentMonthEnd();

		Vector<Integer> hours;

		hours = new Vector<Integer>();
		for (int i = 1; i <= lastHour; i++) {
			hours.add(Integer.valueOf(i));
		}

		DefaultComboBoxModel stMinModel = new DefaultComboBoxModel();
		stMinModel.addElement(0);
		stMinModel.addElement(15);
		stMinModel.addElement(30);
		stMinModel.addElement(45);

		DefaultComboBoxModel etMinModel = new DefaultComboBoxModel();
		etMinModel.addElement(0);
		etMinModel.addElement(15);
		etMinModel.addElement(30);
		etMinModel.addElement(45);

		tfStartHour = new IntegerTextField();
		tfStartMin = new IntegerTextField();
		tfEndHour = new IntegerTextField();
		tfEndMin = new IntegerTextField();

		tfStartHour.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Integer selectedItem = (Integer) tfStartHour.getInteger();
				if (selectedItem == lastHour) {
					selectedItem = 1;
				}
				else {
					selectedItem = selectedItem + 1;
				}
				tfEndHour.setText(String.valueOf(selectedItem));
			}
		});

		chkClockOut = new JCheckBox(Messages.getString("DateChoserDialog.1"));
		chkClockOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enabledItemsForClockOut();
			}
		});

		JLabel lblClockIn = new JLabel(Messages.getString("DateChoserDialog.2"));
		panel.add(lblClockIn, "cell 0 0,right"); //$NON-NLS-1$
		panel.add(new JLabel(POSConstants.DATE), "cell 1 0"); //$NON-NLS-1$
		panel.add(tbStartDate, "cell 2 0");
		panel.add(new JLabel(POSConstants.HOUR), "cell 3 0"); //$NON-NLS-1$
		panel.add(tfStartHour, "w 40!,cell 4 0");
		panel.add(new JLabel(POSConstants.MIN), "cell 5 0");
		panel.add(tfStartMin, "w 40!,cell 6 0");
		if (!TerminalConfig.isUse24HourClockMode()) {
			panel.add(rbStartAm, "cell 7 0");
			panel.add(rbStartPm, "cell 8 0");
		}

		panel.add(chkClockOut, "cell 0 1"); //$NON-NLS-1$
		panel.add(new JLabel("Date"), "cell 1 1"); //$NON-NLS-1$
		panel.add(tbEndDate, "cell 2 1");
		panel.add(new JLabel("Hour"), "cell 3 1"); //$NON-NLS-1$
		panel.add(tfEndHour, "w 40!,cell 4 1");
		panel.add(new JLabel("Min"), "cell 5 1");
		panel.add(tfEndMin, "w 40!,cell 6 1");
		if (!TerminalConfig.isUse24HourClockMode()) {
			panel.add(rbEndAm, "cell 7 1");
			panel.add(rbEndPm, "cell 8 1");
		}

		JPanel footerPanel = new JPanel(new MigLayout("al center center", "sg", ""));
		btnOk = new PosButton(POSConstants.OK);
		btnCancel = new PosButton(POSConstants.CANCEL);
		btnCancel.setPreferredSize(new Dimension(100, 0));

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setCanceled(true);
				dispose();

			}
		});

		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (updateModel()) {
					setCanceled(false);
					dispose();
				}
			}
		});

		footerPanel.add(btnOk, "grow");
		footerPanel.add(btnCancel, "grow");

		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(footerPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		updateView();
		enabledItemsForClockOut();
	}

	private void enabledItemsForClockOut() {
		boolean selected = chkClockOut.isSelected();
		tbEndDate.setEnabled(selected);
		tfEndHour.setEnabled(selected);
		tfEndMin.setEnabled(selected);
	}

	private void updateView() {

		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();

		if (attendenceHistory.getId() == null) {
			startCalendar.setTime(new Date());
			endCalendar.setTime(new Date());
		}
		else {
			if (attendenceHistory.getClockInTime() != null) {
				startCalendar.setTime(attendenceHistory.getClockInTime());

				if (attendenceHistory.getClockOutTime() != null) {
					endCalendar.setTime(attendenceHistory.getClockOutTime());
				}
			}
			cbEmployees.setSelectedItem(attendenceHistory.getUser());
			chkClockOut.setSelected(attendenceHistory.isClockedOut());
		}

		tbStartDate.setDate(startCalendar.getTime());

		Integer hour = (Integer) startCalendar.get(DateUtil.getCalendarHour());
		if (hour.equals(0)) {
			hour = lastHour;

		}
		tfStartHour.setText(String.valueOf(hour));
		tfStartMin.setText(String.valueOf(startCalendar.get(Calendar.MINUTE)));

		if (!TerminalConfig.isUse24HourClockMode()) {
			if (startCalendar.get(Calendar.AM_PM) == 0) {
				rbStartAm.setSelected(true);
			}
			else {
				rbStartPm.setSelected(true);
			}
		}

		tbEndDate.setDate(endCalendar.getTime());

		Integer endHour = (Integer) endCalendar.get(DateUtil.getCalendarHour());
		if (endHour.equals(0)) {
			endHour = lastHour;

		}
		tfEndHour.setText(String.valueOf(endHour));
		tfEndMin.setText(String.valueOf(endCalendar.get(Calendar.MINUTE)));

		if (!TerminalConfig.isUse24HourClockMode()) {
			if (endCalendar.get(Calendar.AM_PM) == 0) {
				rbEndAm.setSelected(true);
			}
			else {
				rbEndPm.setSelected(true);
			}
		}

	}

	private boolean updateModel() {

		Calendar clockInTime = getStartDate();
		Calendar clockOutTime = getEndDate();

		PosLog.info(DateChoserDialog.class, "" + clockInTime.getTime().getTime());
		PosLog.info(DateChoserDialog.class, "" + clockOutTime.getTime().getTime());

		if (clockInTime.getTime().getTime() > clockOutTime.getTime().getTime()) {
			POSMessageDialog.showMessage(POSUtil.getBackOfficeWindow(), Messages.getString("DateChoserDialog.3"));
			return false;
		}

		attendenceHistory.setClockInTime(clockInTime.getTime());
		attendenceHistory.setClockInHour(Short.valueOf((short) clockInTime.get(Calendar.HOUR_OF_DAY)));

		if (!chkClockOut.isSelected()) {
			attendenceHistory.setClockOutTime(null);
			attendenceHistory.setClockOutHour(null);
		}
		else {
			attendenceHistory.setClockOutTime(clockOutTime.getTime());
			attendenceHistory.setClockOutHour(Short.valueOf((short) clockOutTime.get(Calendar.HOUR_OF_DAY)));
		}
		User employee = (User) cbEmployees.getSelectedItem();
		Shift currentShift = ShiftUtil.getCurrentShift();

		attendenceHistory.setClockedOut(chkClockOut.isSelected());
		attendenceHistory.setUser(employee);
		attendenceHistory.setTerminal(Application.getInstance().getTerminal());
		attendenceHistory.setShift(currentShift);

		return true;
	}

	private Calendar getStartDate() {
		if (tbStartDate.getDate() == null) {
			return null;
		}

		Calendar clStartDate = Calendar.getInstance();
		clStartDate.setTime(tbStartDate.getDate());

		Integer hour = (Integer) tfStartHour.getInteger();
		if (hour == lastHour) {
			hour = 0;
		}

		clStartDate.set(DateUtil.getCalendarHour(), hour);
		clStartDate.set(Calendar.MINUTE, (Integer) tfStartMin.getInteger());

		if (!TerminalConfig.isUse24HourClockMode()) {
			if (rbStartAm.isSelected()) {
				clStartDate.set(Calendar.AM_PM, Calendar.AM);
			}
			else if (rbStartPm.isSelected()) {
				clStartDate.set(Calendar.AM_PM, Calendar.PM);
			}
		}

		return clStartDate;
	}

	private Calendar getEndDate() {
		if (tbEndDate.getDate() == null) {
			return null;
		}

		Calendar clEndDate = Calendar.getInstance();
		clEndDate.setTime(tbEndDate.getDate());

		Integer hour = (Integer) tfEndHour.getInteger();
		if (hour == lastHour) {
			hour = 0;
		}

		clEndDate.set(DateUtil.getCalendarHour(), hour);
		clEndDate.set(Calendar.MINUTE, (Integer) tfEndMin.getInteger());

		if (!TerminalConfig.isUse24HourClockMode()) {
			if (rbEndAm.isSelected()) {
				clEndDate.set(Calendar.AM_PM, Calendar.AM);
			}
			else if (rbEndPm.isSelected()) {
				clEndDate.set(Calendar.AM_PM, Calendar.PM);
			}
		}

		return clEndDate;
	}

	public AttendenceHistory getAttendenceHistory() {
		return attendenceHistory;
	}
}
