package application;

import java.net.URL;
import java.util.ResourceBundle;

import com.rkw.IniFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EditController implements Initializable {
	
	private ItGlobal ig = ItGlobal.getInstance();
	private String sec = null;
	private String key = null;
	private String value = null;
	private IniFile ini = null;

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblTitle;

    @FXML
    private TextField tfValue;

    @FXML
    private TextField tfKey;
    
    @FXML
    private TextField tfComment;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	ig.kvChanged = false;
    	ig.secKeyValue = null;
    	ig.commentNew = null;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	String k = tfKey.getText();
    	String v = tfValue.getText();
    	String c = tfComment.getText();
    	
    	if (k == null || k.isBlank() == true) {
    		return;
    	}
    	if (v == null || v.isBlank() == true) {
    		return;
    	}
    	
    	if (ini == null)
    		return;
    	
    	ini.addValuePair(sec, k, v);
    	
    	if (c != null && c.isBlank() == false) {
    		ini.addValuePair(sec + "-Comments", k, c);
    		ig.commentNew = sec + "-Comments~" + c;
    	} else {
    		ini.removeValuePair(sec + "-Comments", k);
    	}
    	
    	ig.kvChanged = true;
    	
    	ig.secKeyValue = sec + "~" + k + "~" + v;
    	
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tfKey.setOnKeyPressed((e) -> {
			if( e.getCode() == KeyCode.ENTER )
				doBtnSave(null);
		});
		tfValue.setOnKeyPressed((e) -> {
			if( e.getCode() == KeyCode.ENTER )
				doBtnSave(null);
		});
		tfComment.setOnKeyPressed((e) -> {
			if( e.getCode() == KeyCode.ENTER )
				doBtnSave(null);
		});
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}
	
	public void setData(String[] data) {
		if (data.length > 0)
			sec = data[0];
		if (data.length > 1)
			key = data[1];
		if (data.length > 2)
			value = data[2];
	}
	
	public void setIni(IniFile ini) {
		this.ini = ini;
		
		if (key != null && key.isBlank() == false)
			tfKey.setText(key);
		if (value != null && value.isBlank() == false)
			tfValue.setText(value);
		
		String c = ini.getString(sec + "-Comments", key);
		
		if (c != null && c.isBlank() == false)
			tfComment.setText(c);
		
//		System.out.println(sec + ", " + key + ", " + value);
	}

}
