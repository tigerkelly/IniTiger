/*
 * Copyright (c) 2023 Richard Kelly Wiles (rkwiles@twc.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *  Created on: Aug 6, 2023
 *      Author: Kelly Wiles
 */

package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class StreamGobbler extends Thread {
	InputStream is = null;
	StringBuilder sb =new StringBuilder();
	Object obj = false;
	boolean allTextMode = false;
    
    StreamGobbler(InputStream is, Object obj) {
        this.is = is;
        this.obj = obj;
    }
    
    public void run() {
    	
    	try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
			String line = null;
			
            while ( (line = br.readLine()) != null) {
        		if (obj != null && obj instanceof TextArea) {
        			final String txt = line;
        			Platform.runLater(new Runnable() {
        				@Override
        				public void run() {
//        					OutputInterface oi = (OutputInterface)obj;
//                			oi.addText(txt + "\n", true);
        					TextArea ta = (TextArea)obj;
        					ta.appendText(txt + "\n");
                			System.out.println(">> " + txt);
        				}
        			});
        		} else {
            		sb.append(" " + line + "\n");
//            		System.out.println(line);
        		}
        		try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            br.close();
        } catch (IOException ioe) {
                ioe.printStackTrace();  
        }
    }
    
    public String getOutput() {
    	return sb.toString();
    }
}
