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
package ru.instefa.cafepickpos.extension;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import ru.instefa.cafepickpos.config.ui.ConfigurationView;
import ru.instefa.cafepickpos.config.ui.InginicoConfigurationView;
import ru.instefa.cafepickpos.ui.views.payment.CardProcessor;

@PluginImplementation
public class InginicoPlugin extends PaymentGatewayPlugin {
	InginicoConfigurationView view;

	@Override
	public String getProductName() {
		return "Ingenico IWL220 TGI"; //$NON-NLS-1$
	}

	@Override
	public void initUI() {
	}

	@Override
	public void initBackoffice() {
	}

	@Override
	public void initConfigurationView(JDialog dialog) {

	}

	@Override
	public String getId() {
		return "Inginico"; // //$NON-NLS-1$
	}
	
	@Override
	public String getSecurityCode() {
		return "-1105096101";//$NON-NLS-1$
	}

	@Override
	public String toString() {
		return getProductName();
	}

	@Override
	public ConfigurationView getConfigurationPane() throws Exception {
		if (view == null) {
			view = new InginicoConfigurationView();
			view.initialize();
		}

		return view;
	}

	@Override
	public CardProcessor getProcessor() {
		return null;
	}

	@Override
	public boolean shouldShowCardInputProcessor() {
		return true;
	}

	@Override
	public List<AbstractAction> getSpecialFunctionActions() {
		return null;
	}

	@Override
	public void initLicense() {
	}

	@Override
	public boolean hasValidLicense() {
		return true;
	}

	@Override
	public String getProductVersion() {
		return null;
	}

	@Override
	public Component getParent() {
		return null;
	}
}
