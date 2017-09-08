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
package ru.instefa.cafepickpos;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class IconFactory {
	private static final String iconsPath = "/icons/";
	private static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
	
	public static ImageIcon getIcon(String iconName) {
		return getIcon(iconsPath, iconName);
	}
	
	public static ImageIcon getIcon(String iconName, Dimension size) {
		return getIcon(iconsPath, iconName, size);
	}

	public static ImageIcon getIcon(String path, String iconName) {
		ImageIcon icon = iconCache.get(iconName);

		if (icon == null) {
			try {
				icon = new ImageIcon(IconFactory.class.getResource(path + iconName)); //$NON-NLS-1$
				iconCache.put(iconName, icon);
			} catch (Exception x) {
				return getDefaultIcon(iconName);
			}
		}
		return icon;
	}
	
	public static ImageIcon getIcon(String path, String iconName, Dimension size) {
		ImageIcon icon = iconCache.get(iconName);

		if (icon == null) {
			try {
				icon = new ImageIcon(IconFactory.class.getResource(path + iconName)); //$NON-NLS-1$
				icon = new ImageIcon(icon.getImage().getScaledInstance(size.width, size.height,  java.awt.Image.SCALE_SMOOTH));
				iconCache.put(iconName, icon);
			} catch (Exception x) {
				return getIcon(iconName);
			}
		}
		return icon;
	}

	public static ImageIcon getDefaultIcon(String iconName) {
		ImageIcon icon = iconCache.get(iconName);
		String iconPath = "/images/" + iconName;
		if (icon == null) {
			try {
				icon = new ImageIcon(IconFactory.class.getResource(iconPath)); //$NON-NLS-1$
				iconCache.put(iconName, icon);
			} catch (Exception e) {
				PosLog.error(IconFactory.class, iconPath + " not found");
				throw e;
			}
		}
		return icon;
	}
}
