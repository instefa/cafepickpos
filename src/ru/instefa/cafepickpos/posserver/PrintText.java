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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PrintText")
public class PrintText {
	public static final String CENTER = "center";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	String text = "";
	int lengthLimit = 35;

	public PrintText() {
		super();
	}

	public PrintText(String txt) {
		super();
		if (txt.length() > 42) {
			txt = txt.substring(0, 42);
		}

		int space = (lengthLimit - txt.length());
		for (int i = 1; i < space; i++) {
			txt = txt + " ";
		}
		this.text = txt;
	}

	public PrintText(String txt, String align) {
		super();
		if (txt.length() > 42) {
			txt = txt.substring(0, 42);
		}

		if (align.equals(CENTER)) {
			int space = (lengthLimit - txt.length()) / 2;
			for (int i = 1; i < space; i++) {
				txt = " " + txt + " ";
			}
			this.text = txt;
		}
		else if (align.equals(RIGHT)) {
			int space = (lengthLimit - txt.length());
			for (int i = 1; i < space; i++) {
				txt = " " + txt;
			}
			this.text = txt;
		}
		else if (align.equals(LEFT)) {
			int space = (lengthLimit - txt.length());
			for (int i = 1; i < space; i++) {
				txt = txt + " ";
			}
			this.text = txt;
		}
	}

	@XmlAttribute(name = "text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
