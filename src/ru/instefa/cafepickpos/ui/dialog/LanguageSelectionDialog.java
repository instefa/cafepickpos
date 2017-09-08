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
package ru.instefa.cafepickpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Main;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.ui.TitlePanel;
import ru.instefa.cafepickpos.util.POSUtil;

public class LanguageSelectionDialog extends POSDialog {

	private JComboBox cbLang;
	private List<Locale> posLocaleList;

	public LanguageSelectionDialog() {
		super(POSUtil.getBackOfficeWindow(), "");
		init();
		updateModel();
	}

	public LanguageSelectionDialog(JFrame parent) {
		super();

		init();
		updateModel();
	}

	private void init() {
		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("LanguageSelectionDialog.0")); //$NON-NLS-1$
		add(titlePanel, BorderLayout.NORTH);

		posLocaleList = new ArrayList<Locale>();
		int length = countProperties();
		//PosLog.debug(getClass(),length);
		JPanel centerPanel = new JPanel(new MigLayout("fillx,aligny center", "[][]", ""));

		JLabel lblLanguage = new JLabel(Messages.getString("LanguageSelectionDialog.1"));

		cbLang = new JComboBox();
		for (Locale locale : posLocaleList) {
			cbLang.addItem(locale.getDisplayName());
		}

		centerPanel.add(lblLanguage, "cell 0 0, alignx right");
		centerPanel.add(cbLang, "cell 1 0");

		add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new MigLayout("al center", "sg, fill", ""));

		PosButton btnSave = new PosButton(Messages.getString("LanguageSelectionDialog.2"));
		buttonPanel.add(btnSave, "grow");

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();

			}
		});

		PosButton btnCancel = new PosButton(Messages.getString("LanguageSelectionDialog.3"));
		buttonPanel.add(btnCancel, "grow");

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

			}
		});

		buttonPanel.add(btnCancel);

		add(buttonPanel, BorderLayout.SOUTH);

	}

	private void updateModel() {
		if (TerminalConfig.getDefaultLocale() != null) {
			Locale getLocale = TerminalConfig.getDefaultLocale();
			/*String language = "";
			String country = "";
			String variant = "";
			StringTokenizer st = new StringTokenizer(getLocale, "_");
			if (st.hasMoreTokens())
				language = st.nextToken();
			if (st.hasMoreTokens())
				country = st.nextToken();
			if (st.hasMoreTokens())
				variant = st.nextToken();
			Locale disName = new Locale(language, country, variant);*/
			cbLang.setSelectedItem(getLocale.getDisplayName());
		}
		//Locale.setDefault(TerminalConfig.getDefaultLocale());
	}

	public void doSave() {

		if (cbLang.getSelectedItem() != null) {
			String selectedLang = (String) cbLang.getSelectedItem();
			String savedLocal = "";
			for (Locale locale : posLocaleList) {
				if (locale.getDisplayName().equals(selectedLang)) {
					savedLocal += locale;
				}
			}
			TerminalConfig.setDefaultLocale(savedLocal);

			POSMessageDialog.showMessage(this, Messages.getString("LanguageSelectionDialog.4"));
			dispose();
			try {
				Main.restart();
			} catch (Exception e) {
				PosLog.error(getClass(), e);
			}
		}
	}

	private boolean save() {
		return false;
	}

	private int countProperties() {
		int count = 0;

		//Locale l = new Locale("ar", "EG");
		//PosLog.debug(getClass(),l.getDisplayName());

		File[] files = null;
		try {
			files = new File(("i18n")).listFiles();
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		}
		if (files != null) {
			for (File file : files) {
				String fileName = file.getName();
				String languageName = "";
				String countryName = "";

				int lf = fileName.indexOf("_") + 1;
				int ll = fileName.lastIndexOf("_");

				if (lf != 0 && ll != -1 && ll > lf) {
					languageName = fileName.substring(lf, ll);
					//PosLog.debug(getClass(),languageName);
				}

				int start = fileName.lastIndexOf("_") + 1;
				int end = fileName.lastIndexOf(".");
				if (start != 0 && end != -1 && end > start) {
					countryName = fileName.substring(start, end);
					//PosLog.debug(getClass(),countryName);
				}
				if (StringUtils.isEmpty(languageName) && !StringUtils.isEmpty(countryName)) {
					languageName = countryName;
					posLocaleList.add(new Locale(languageName));
				}
				else if (StringUtils.isEmpty(languageName)) {
					posLocaleList.add(new Locale("en"));
				}
				else if (StringUtils.isEmpty(countryName)) {
					posLocaleList.add(new Locale(languageName));
				}
				else {
					posLocaleList.add(new Locale(languageName, countryName));
				}
			}
			return files.length;
		}
		else
			return 0;
	}

}
