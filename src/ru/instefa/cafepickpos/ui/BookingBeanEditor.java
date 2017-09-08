/*
========================================================
This Source Code Form is subject to the terms
of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
========================================================
*/
/*
 * BookingBeanEditor.java
 *
 * Created as empty abstract class by OROCUBE LLC.
 * @author pymancer <pymancer@gmail.com>
 * @since 2017.0.1
 */

package ru.instefa.cafepickpos.ui;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.ui.BeanEditor;
import ru.instefa.cafepickpos.model.TableBookingInfo;
import ru.instefa.cafepickpos.model.dao.TableBookingInfoDAO;
import ru.instefa.cafepickpos.swing.MessageDialog;

public class BookingBeanEditor<E> extends BeanEditor<Object> {
	/**
	 * Editable booking object.
	 * No UI to edit bookings implemented.
	 */
	@Override
    public String getDisplayText() {
		TableBookingInfo booking = (TableBookingInfo) getBean();
		if (booking.getId() == null) {
			return Messages.getString("BookingBeanEditor.0");
		}
		return Messages.getString("BookingBeanEditor.1");
	}
    
    @Override
    protected boolean updateModel() {
		return true;
	}
    
    @Override
    protected void updateView() {
	}
    
    @Override
	public boolean save() {
		try {

			if (!updateModel())
				return false;

			TableBookingInfo booking = (TableBookingInfo) getBean();
			TableBookingInfoDAO.getInstance().saveOrUpdate(booking);

			return true;

		} catch (Exception x) {
			MessageDialog.showError(x);
			return false;
		}
	}
}
