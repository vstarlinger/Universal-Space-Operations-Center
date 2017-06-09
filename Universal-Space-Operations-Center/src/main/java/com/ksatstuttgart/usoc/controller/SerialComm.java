/*
 * The MIT License
 *
 * Copyright 2017 KSat e.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ksatstuttgart.usoc.controller;

import com.ksatstuttgart.usoc.data.SerialEvent;
import java.awt.event.ActionEvent;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

/**
 * <h1>SerialComm</h1>
 * This class provides methods to communicate via a specified serial port.
 * <p>
 *
 *
 * @author Valentin Starlinger
 * @version 1.0
 */
public class SerialComm {

    private SerialPort serialPort;

    private ArrayList<SerialListener> listener = new ArrayList<>();

    private static SerialComm instance;

    public static SerialComm getInstance() {
        if (instance == null) {
            instance = new SerialComm();
        }
        return instance;
    }

    public void start(String sp, int baudrate) {
        if (!sp.isEmpty()) {
            if (serialPort != null && serialPort.isOpened()) {
                close();
            }
            serialPort = new SerialPort(sp);//"/dev/tty.usbserial-A702WKDS");
            try {
                serialPort.openPort();//Open serial port //serialPort.setParams(9600, 8, 1, 0); 
                serialPort.setParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
                serialPort.setEventsMask(mask);//Set mask
                serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener

            } catch (SerialPortException e) {
                //System.out.println(e);
                error("Error: Couldn't open Serialport.\n");
            }
        }
    }

    public void close() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            error("Error: Couldn't close Serialport.");
        }
    }

    public void send(String msg) {
        try {
            //add a $ to the beginning and a # at the end
            serialPort.writeBytes(("$" + msg + "#").getBytes());
        } catch (SerialPortException ex) {
            error("Error: Couldn't send message: " + msg + ".");
        }
    }

    public static void sendAction(ActionEvent e) {
        if (SerialComm.getInstance().isOpen()) {
            switch (e.getActionCommand()) {
                case "LaunchMacro":
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                SerialComm.getInstance().send("Cameras");
                                sleep(3 * 1000);
                                SerialComm.getInstance().send("Prelaunch");
                                sleep(20 * 1000);
                                SerialComm.getInstance().send("Active");
                                sleep(5 * 1000);
                                SerialComm.getInstance().send("Cameras");
                                sleep(2 * 1000);
                                SerialComm.getInstance().send("Cameras");
                                sleep(2 * 1000);
                                SerialComm.getInstance().send("Prelaunch");
                            } catch (InterruptedException ex) {
                            }

                        }
                    }.start();
                    break;
                default:
                    SerialComm.getInstance().send(e.getActionCommand());
                    break;
            }
        }
    }

    public ArrayList<String> getPorts() {
        ArrayList<String> al = new ArrayList<>();
        for (String s : jssc.SerialPortList.getPortNames()) {
            al.add(s);
        }
        return al;
    }

    public boolean isOpen() {
        return serialPort != null && serialPort.isOpened();
    }

    /*
     * In this class must implement the method serialEvent, through it we learn about 
     * events that happened to our port. But we will not report on all events but only 
     * those that we put in the mask. In this case the arrival of the data and change the 
     * status lines CTS and DSR
     */
    private class SerialPortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {//If data is available
                try {
                    byte buffer[] = serialPort.readBytes(event.getEventValue());
                    String s = "";
                    for (byte c : buffer) {
                        s += (char) c;
                    }
                    messageReceived(new SerialEvent(s, event.getPortName(), System.currentTimeMillis()));
                } catch (SerialPortException ex) {
                    error("Error: Couldn't read incoming Bytes.");
                }
            }
        }
    }

    private void messageReceived(SerialEvent e) {
        for (SerialListener l : listener) {
            l.messageReceived(e);
        }
    }

    private void error(String msg) {
        for (SerialListener l : listener) {
            l.error(msg);
        }
    }

    public void addSerialListener(SerialListener l) {
        listener.add(l);
    }

    public void removeSerialListener(SerialListener l) {
        listener.remove(l);
    }

}