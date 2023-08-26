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
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HelpController implements Initializable {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnClose;
    
    @FXML
    private TextArea taHelp;

    @FXML
    void doBtnClose(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		taHelp.setStyle("-fx-font-family: Monospaced; -fx-font-size: 16px;");
		
		taHelp.setText("");
		
		int indent = 0;
		int start = 0;
		String spaces = "                ";
		
		try (InputStream in = getClass().getResourceAsStream("/resources/help.txt");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			    
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isBlank() == true) {
					taHelp.appendText("\n");
					continue;
				}
				
				if (line.charAt(0) == '-') {
					indent = 4;
					taHelp.appendText(spaces.substring(0, indent));
				} else if (line.charAt(0) == '+') {
					indent = 8;
					taHelp.appendText(spaces.substring(0, indent));
				} else if (line.charAt(0) == '=') {
					indent = 12;
					taHelp.appendText(spaces.substring(0, indent));
				} else if (line.charAt(0) == '*') {
					taHelp.appendText(spaces.substring(0, indent+2));
					start = 2;
				}
				
				taHelp.appendText(line.substring(start) + "\n");
				start = 0;
			}
			reader.close();
			in.close();
			
			taHelp.selectHome();
			taHelp.deselect();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
