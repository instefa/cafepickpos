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
package ru.instefa.cafepickpos.util;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import ru.instefa.cafepickpos.PosLog;

public class SerialPortUtil {
	public static String readWeight(String comPort) throws SerialPortException {
		final SerialPort serialPort = new SerialPort(comPort);
		serialPort.openPort();//Open serial port
		serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_7, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);
		serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT | SerialPort.FLOWCONTROL_XONXOFF_IN
				| SerialPort.FLOWCONTROL_XONXOFF_OUT);

		final StringBuilder messageBuilder = new StringBuilder();

		serialPort.addEventListener(new SerialPortEventListener() {
			@Override
			public void serialEvent(SerialPortEvent event) {
				try {
					if (event.isRXCHAR() && event.getEventValue() > 0) {
						byte buffer[] = serialPort.readBytes();
						for (byte b : buffer) {
							if ((b == '\r' || b == '\n') && messageBuilder.length() > 0) {
								synchronized (messageBuilder) {
									messageBuilder.notify();
								}
								break;
							}
							else {
								messageBuilder.append((char) b);
							}
						}
					}
				} catch (Exception e) {
					PosLog.error(getClass(), e);
				}
			}
		});
		byte[] data = new byte[] { 0x57, 0x0D, 0 };
		serialPort.writeBytes(data);
		
		synchronized (messageBuilder) {
			try {
				messageBuilder.wait(2000);
			} catch (InterruptedException e) {
				serialPort.closePort();
				return messageBuilder.toString();
			}
		}
		
		serialPort.closePort();
		return messageBuilder.toString();
	}
}
