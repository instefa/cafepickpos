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
package ru.instefa.cafepickpos.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXCollapsiblePane;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;

/**
* Visual Virtual Keyboard
* Local keyboard layout simple support implemented.
* Cyrillic keyboard layout added.
* Default active layout depends on current Locale.
* Second layout always Latin, NOOP if localization is off.
* To not to much affect existing code determining button 
* localized counterpart by comparing current button
* text with current layout's items then using it's index
* to obtain new value. Using array with keyboard row
* length (kbRowLength) instead of bunch of arrays for speed.
*/
public class QwertyKeyPad extends JXCollapsiblePane implements ActionListener, ChangeListener {
	Font buttonFont = getFont().deriveFont(Font.BOLD, PosUIManager.getFontSize(24));

	// buttons number in each virtual keyboard's row
	private int kbRowLength = 10;
	// tracking Caps Lock state
	private boolean capsLocked = false;
	// localization code as active layout key
	private String localeCode = Locale.getDefault().getLanguage();
	// locale dependent keyboard layouts
	private HashMap<String, String[]> layouts = new HashMap<>();

	private ArrayList<PosButton> buttons = new ArrayList<PosButton>();
	Dimension size = PosUIManager.getSize(60, 60);

	public QwertyKeyPad() {
		createUI();

		//Dimension size = PosUIManager.getSize(500, 200);
		//setMinimumSize(size);
		//setPreferredSize(size);
	}

	private void createUI() {
		setLayout(new BorderLayout(0, 0));

		// default layout (english, latin)
		String[] enLayout = new String[] {
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
			"q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
			"a", "s", "d", "f", "g", "h", "j", "k", "l", ";",
			"z", "x", "c", "v", "b", "n", "m", "-", ",", ".",
			"!", "@", "#", "$", "%", "^", "&", "+", "=", "*" 
		};
		
		// use ISO 639 old style locale codes as keys
		layouts.put("en", enLayout);
		// cyrrilic layout (russian)
		if (localeCode.equals(new Locale("ru").getLanguage())) {
			String[] ruLayout = new String[] {
				"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
				"а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и",
				"й", "к", "л", "м", "н", "о", "п", "р", "с", ";",
				"т", "у", "ф", "х", "ц", "ч", "ш", "-", ",", ".",
				"щ", "ъ", "ы", "ь", "э", "ю", "я", "+", "=", "*"
			};
			layouts.put(localeCode, ruLayout);
		}
		
		TransparentPanel centerPanel = new TransparentPanel(new GridLayout(0, 1, 2, 2));

		String[] kbRow = new String[]{};
		for (int i=0; i < layouts.get(localeCode).length; i += kbRowLength) {
			kbRow = Arrays.copyOfRange(layouts.get(localeCode), i, i + kbRowLength);
			centerPanel.add(addButtonsToPanel(kbRow));
		}
		
		add(centerPanel, BorderLayout.CENTER);

		JPanel eastPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		PosButton button = new PosButton();
		button.setText(Messages.getString("QwertyKeyPad.0")); //$NON-NLS-1$
		button.setFocusable(false);
		button.addActionListener(this);
		eastPanel.add(button);

		POSToggleButton toggleCaps = new POSToggleButton();
		toggleCaps.setText(Messages.getString("QwertyKeyPad.2")); //$NON-NLS-1$
		toggleCaps.setFocusable(false);
		toggleCaps.addChangeListener(this);
		eastPanel.add(toggleCaps);

		button = new PosButton();
		button.setText(ru.instefa.cafepickpos.POSConstants.CLEAR);
		button.setFocusable(false);
		button.addActionListener(this);
		eastPanel.add(button);

		button = new PosButton();
		button.setFocusable(false);
		button.setText(ru.instefa.cafepickpos.POSConstants.CLEAR_ALL);
		button.addActionListener(this);
		eastPanel.add(button);

		POSToggleButton toggleLang = new POSToggleButton();
		toggleLang.setText(Messages.getString("QwertyKeyPad.3")); //$NON-NLS-1$
		toggleLang.setFocusable(false);
		toggleLang.addChangeListener(this);
		eastPanel.add(toggleLang);

		eastPanel.setPreferredSize(PosUIManager.getSize(90, 0));
		add(eastPanel, BorderLayout.EAST);
	}

	private TransparentPanel addButtonsToPanel(String[] buttonText) {
		TransparentPanel panel = new TransparentPanel(new GridLayout(0, kbRowLength, 2, 2));
		for (int i = 0; i < buttonText.length; i++) {
			String s = buttonText[i];
			PosButton button = new PosButton();
			button.setText(s);
			button.setMinimumSize(size);
			button.addActionListener(this);
			button.setFont(buttonFont);
			button.setFocusable(false);
			buttons.add(button);
			panel.add(button);
		}
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

		JTextComponent note = null;

		if (!(focusOwner instanceof JTextComponent)) {
			return;
		}

		note = (JTextComponent) focusOwner;

		String s = e.getActionCommand();
		if (s.equals(ru.instefa.cafepickpos.POSConstants.CLEAR)) {
			String str = note.getText();
			if (str.length() > 0) {
				str = str.substring(0, str.length() - 1);
			}
			note.setText(str);
		}
		else if (s.equals(ru.instefa.cafepickpos.POSConstants.CLEAR_ALL)) {
			note.setText(""); //$NON-NLS-1$
		}
		else if (s.equals(Messages.getString("QwertyKeyPad.0"))) { //$NON-NLS-1$
			String str = note.getText();
			if (str == null) {
				str = ""; //$NON-NLS-1$
			}
			note.setText(str + " "); //$NON-NLS-1$
		}
		else {
			String str = note.getText();
			if (str == null) {
				str = ""; //$NON-NLS-1$
			}
			note.setText(str + s);
		}
	}

	public void stateChanged(ChangeEvent e) {
		JToggleButton b = (JToggleButton) e.getSource();
		
		String s = b.getActionCommand();
		
		if (s.equals(Messages.getString("QwertyKeyPad.2"))) {
			// Caps Lock handler
			if (b.isSelected()) {
				capsLocked = true;
				for (PosButton button : buttons) {
					button.setText(button.getText().toUpperCase());
				}
			}
			else {
				capsLocked = false;
				for (PosButton button : buttons) {
					button.setText(button.getText().toLowerCase());
				}
			}
		}
		else if (s.equals(Messages.getString("QwertyKeyPad.3")) && layouts.size() == 2) {
			List layout = new ArrayList();
			String buttonText = new String();
			int buttonIndex = -1;
			// Keyboard layout handler
			if (b.isSelected()) {
				layout = Arrays.asList(layouts.get(localeCode));
				for (PosButton button : buttons) {
					buttonText = button.getText().toLowerCase();
					buttonIndex = layout.indexOf(buttonText);
					if (buttonIndex >= 0) {
						buttonText = layouts.get("en")[buttonIndex];
					}
					if (capsLocked == true) {
						buttonText = buttonText.toUpperCase();
					}
					button.setText(buttonText);
				}
			}
			else {
				layout = Arrays.asList(layouts.get("en"));
				for (PosButton button : buttons) {
					buttonText = button.getText().toLowerCase();
					buttonIndex = layout.indexOf(buttonText);
					if (buttonIndex >= 0) {
						buttonText = layouts.get(localeCode)[buttonIndex];
					}
					if (capsLocked == true) {
						buttonText = buttonText.toUpperCase();
					}
					button.setText(buttonText);
				}
			}
		}
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final QwertyKeyPad comp = new QwertyKeyPad();
		frame.add(comp);
		comp.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				PosLog.info(QwertyKeyPad.class, "" + comp.getSize());

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});

		frame.pack();
		frame.setVisible(true);
	}

}
