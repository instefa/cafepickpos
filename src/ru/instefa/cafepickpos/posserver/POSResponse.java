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
package ru.instefa.cafepickpos.posserver;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "POSResponse")
public class POSResponse {

	Ident ident;
	POSDefaultInfo posDefaultInfo;
	Checks checks;
	
	List<PrintText> printTexts; 

	public POSResponse() {
		super();
	}

	@XmlElement(name = "Ident")
	public Ident getIdent() {
		return ident;
	}

	public void setIdent(Ident ident) {
		this.ident = ident;
	}

	@XmlElement(name = "POSDefaultInfo")
	public POSDefaultInfo getPosDefaultInfo() {
		return posDefaultInfo;
	}

	public void setPosDefaultInfo(POSDefaultInfo posDefaultInfo) {
		this.posDefaultInfo = posDefaultInfo;
	}

	@XmlElement(name = "Checks")
	public Checks getChecks() {
		return checks;
	}

	public void setChecks(Checks checks) {
		this.checks = checks;
	}
	
	@XmlElement(name = "PrintText")
	public List<PrintText> getPrintText() {
		return printTexts;
	}

	public void setPrintChecks(List<PrintText> printTexts) {
		this.printTexts = printTexts;
	}
}
