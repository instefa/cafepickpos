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
package ru.instefa.cafepickpos.ui.model;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.model.Shift;
import ru.instefa.cafepickpos.model.dao.ShiftDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.ShiftUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class ShiftEntryDialog extends POSDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox startHour;
	private JComboBox startMin;
	private JComboBox startAmPm;
	private JComboBox endHour;
	private JComboBox endAmPm;
	private JComboBox endMin;
	private JTextField tfShiftName;

	private Vector<Integer> hours;
	private Vector<Integer> mins;

	private Shift shift;
	private Date shiftStart, shiftEnd;

	public ShiftEntryDialog() {
		this(null);
	}

	public ShiftEntryDialog(Shift shift) {
		super(POSUtil.getBackOfficeWindow(), true);
		setTitle(ru.instefa.cafepickpos.POSConstants.NEW_SHIFT);

		setContentPane(contentPane);
		getRootPane().setDefaultButton(buttonOK);

		hours = new Vector<Integer>();
        // 20170714, pymancer 24 hour clock format option
		int lastH = TerminalConfig.isUse24HourClockMode() ? 24 : 12;
		for (int i = 1; i <= lastH; i++) {
			hours.add(i);
		}

		mins = new Vector<>();
		for (int i = 0; i < 60; i++) {
			mins.add(i);
		}

		startHour.setModel(new DefaultComboBoxModel(hours));
		endHour.setModel(new DefaultComboBoxModel(hours));

		startMin.setModel(new DefaultComboBoxModel(mins));
		endMin.setModel(new DefaultComboBoxModel(mins));

		buttonOK.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		setSize(375, 250);

		setShift(shift);
	}

	private boolean calculateShifts() {
		int hour1 = ((Integer) startHour.getSelectedItem());
		int hour2 = ((Integer) endHour.getSelectedItem());
		int min1 = ((Integer) startMin.getSelectedItem());
		int min2 = ((Integer) endMin.getSelectedItem());
		if (!TerminalConfig.isUse24HourClockMode()) {
			int ampm1 = startAmPm.getSelectedIndex();
			int ampm2 = endAmPm.getSelectedIndex();
	
			shiftStart = ShiftUtil.buildShiftStartTime(hour1, min1, ampm1, hour2, min2, ampm2);
			shiftEnd = ShiftUtil.buildShiftEndTime(hour1, min1, ampm1, hour2, min2, ampm2);
		}
		else {
			shiftStart = ShiftUtil.buildShiftStartTime(hour1, min1, hour2, min2);
			shiftEnd = ShiftUtil.buildShiftEndTime(hour1, min1, hour2, min2);
		}

		if (!shiftEnd.after(shiftStart)) {
			POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.SHIFT_END_TIME_MUST_BE_GREATER_THAN_SHIFT_START_TIME);
			return false;
		}
		return true;
	}

	private void onOK() {
		try {
			if (!updateModel())
				return;

			ShiftDAO dao = new ShiftDAO();
			if (shift.getId() == null && dao.exists(shift.getName())) {
				POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.SHIFT_NAME_ALREADY_EXISTS);
				return;
			}
			dao.saveOrUpdate(shift);

			setCanceled(false);

			dispose();
		} catch (Exception e) {
			POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.ERROR_SAVING_SHIFT_STATE, e);
		}
	}

	private void onCancel() {
		setCanceled(true);
		dispose();
	}

	public Date getShiftStart() {
		return shiftStart;
	}

	public Date getShiftEnd() {
		return shiftEnd;
	}

	public void updateView() {
		if (shift == null) {
			return;
		}

		tfShiftName.setText(shift.getName());

		Date startTime = shift.getStartTime();
		Date endTime = shift.getEndTime();

		Calendar c = Calendar.getInstance();
		c.setTime(startTime);

		startHour.setSelectedIndex(c.get(DateUtil.getCalendarHour()) - 1);
		startMin.setSelectedIndex(c.get(Calendar.MINUTE));
		//startAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);

		c.setTime(endTime);
		endHour.setSelectedIndex(c.get(DateUtil.getCalendarHour()) - 1);
		endMin.setSelectedIndex(c.get(Calendar.MINUTE));
		//endAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
		if (!TerminalConfig.isUse24HourClockMode()) {
			startAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
			endAmPm.setSelectedIndex(c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
		}
	}

	public boolean updateModel() {
		if (!calculateShifts())
			return false;

		if (shift == null) {
			shift = new Shift();
		}
		shift.setName(tfShiftName.getText());
		shift.setStartTime(shiftStart);
		shift.setEndTime(shiftEnd);

		Calendar c = Calendar.getInstance();
		c.setTime(shiftStart);

		long length = Math.abs(shiftStart.getTime() - shiftEnd.getTime());

		shift.setShiftLength(length);

		return true;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;

		updateView();
	}

	{
        // 20170717, pymancer, form initialization made manual
        // ($$$setupUI$$$ -> setupUI) since IntelliJ is not in use anymore.
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		setupUI();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 * @noinspection ALL
	 */
	private void setupUI() {
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
		contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK
				| GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
		panel1.add(panel2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK
				| GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		buttonOK = new JButton();
		buttonOK.setText(ru.instefa.cafepickpos.POSConstants.OK);
		panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		buttonCancel = new JButton();
		buttonCancel.setText(ru.instefa.cafepickpos.POSConstants.CANCEL);
		panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		panel1.add(separator1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
		contentPane
				.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK
						| GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
						0, false));
		final JLabel label1 = new JLabel();
		label1.setText(ru.instefa.cafepickpos.POSConstants.START_TIME + ":"); //$NON-NLS-1$
		panel3.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator2 = new JSeparator();
		panel3.add(separator2, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText(ru.instefa.cafepickpos.POSConstants.HOUR + ":"); //$NON-NLS-1$
		panel3.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		startHour = new JComboBox();
		panel3.add(startHour, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, 22), null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText(ru.instefa.cafepickpos.POSConstants.MIN + ":"); //$NON-NLS-1$
		panel3.add(label3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		startMin = new JComboBox();
		panel3.add(startMin, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		// 20170717, pymancer, rendering AM_PM info only for 12 Hour Clock
		if (!TerminalConfig.isUse24HourClockMode()) {
			startAmPm = new JComboBox();
			final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
			defaultComboBoxModel1.addElement(ru.instefa.cafepickpos.POSConstants.AM);
			defaultComboBoxModel1.addElement(ru.instefa.cafepickpos.POSConstants.PM);
			startAmPm.setModel(defaultComboBoxModel1);
			panel3.add(startAmPm, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
					GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		}
		
		final JLabel label4 = new JLabel();
		label4.setText(ru.instefa.cafepickpos.POSConstants.END_TIME + ":"); //$NON-NLS-1$
		panel3.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator3 = new JSeparator();
		panel3.add(separator3, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, new Dimension(-1, 2), 0, false));
		final JLabel label5 = new JLabel();
		label5.setText(ru.instefa.cafepickpos.POSConstants.HOUR + ":"); //$NON-NLS-1$
		panel3.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		endHour = new JComboBox();
		panel3.add(endHour, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, 22), null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText(ru.instefa.cafepickpos.POSConstants.MIN + ":"); //$NON-NLS-1$
		panel3.add(label6, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		endMin = new JComboBox();
		panel3.add(endMin, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		// 20170717, pymancer, rendering AM_PM info only for 12 Hour Clock
		if (!TerminalConfig.isUse24HourClockMode()) {
			endAmPm = new JComboBox();
			final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
			defaultComboBoxModel2.addElement(ru.instefa.cafepickpos.POSConstants.AM);
			defaultComboBoxModel2.addElement(ru.instefa.cafepickpos.POSConstants.PM);
			endAmPm.setModel(defaultComboBoxModel2);
			panel3.add(endAmPm, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW,
					GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		}
		
		final Spacer spacer2 = new Spacer();
		panel3.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
				GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText(ru.instefa.cafepickpos.POSConstants.SHIFT_NAME + ":"); //$NON-NLS-1$
		panel3.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tfShiftName = new JTextField();
		panel3.add(tfShiftName, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final Spacer spacer3 = new Spacer();
		contentPane.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
				GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
	}

	/**
     * @return 
	 * @noinspection ALL
	 */
	/*public JComponent $$$getRootComponent$$$() {
		return contentPane;
	}*/
}
