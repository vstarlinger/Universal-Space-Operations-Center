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
package com.ksatstuttgart.usoc.data;

import com.ksatstuttgart.usoc.controller.DataController;
import javax.mail.Address;

/**
 *
 * @author valentinstarlinger
 */
public class MailEvent {
    
    private Address[] from;
    private String subject, filename, text;
    private DataController iridiumPackage;
    private long timeStampGmail;
    private long timeStamp;

    public MailEvent(Address[] from, String subject, String filename, String text, long timeStamp, long timeStampGmail) {
        this.from = from;
        this.subject = subject;
        this.filename = filename;
        this.text = text;
        this.iridiumPackage = new DataController(text);
        this.timeStampGmail = timeStampGmail;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Address[] getFrom() {
        return from;
    }

    public void setFrom(Address[] from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFilename() {
        return filename;
    }

    public long getTimeStampGmail() {
        return timeStampGmail;
    }

    public void setTimeStampGmail(long timeStampGmail) {
        this.timeStampGmail = timeStampGmail;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DataController getDataController() {
        return iridiumPackage;
    }

    public void setIridiumPackage(DataController iridiumPackage) {
        this.iridiumPackage = iridiumPackage;
    }
    
    public String getText(){
        return text;
    }

    @Override
    public String toString(){
        return timeStamp+"\n"+from.toString()+"\n"+subject+"\n"+filename+"\n"+text.toString()+"\n"+"\n";
    }
    
    
}