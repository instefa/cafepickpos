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
package ru.instefa.cafepickpos.swing;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class TouchScrollHandler extends MouseAdapter implements AWTEventListener {

	private Point origin;
	private boolean wasDragging;

	public TouchScrollHandler() {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		origin = new Point(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (wasDragging) {
			e.getComponent().setCursor(Cursor.getDefaultCursor());
			wasDragging = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (origin != null) {
			JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, e.getComponent());
			if (viewPort != null) {
				

				Rectangle view = viewPort.getViewRect();
				//MouseEvent convertMouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, viewPort);
				//PosLog.debug(getClass(),convertMouseEvent.getY());
				
				//if (convertMouseEvent.getY() >= 0) {
					int deltaX = origin.x - e.getX();
					int deltaY = origin.y - e.getY();
					
					view.x += deltaX;
					view.y += deltaY;

					e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					((JComponent) e.getComponent()).scrollRectToVisible(view);
					wasDragging = true;
					e.consume();
				//}
			}
		}
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		switch (event.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				mousePressed((MouseEvent) event);
				break;

			case MouseEvent.MOUSE_RELEASED:
				mouseReleased((MouseEvent) event);
				break;

			case MouseEvent.MOUSE_DRAGGED:
				mouseDragged((MouseEvent) event);
				break;

			default:
				break;
		}
	}
}