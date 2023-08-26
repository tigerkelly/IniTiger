package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SectionController implements Initializable {
	
	private ItGlobal ig = ItGlobal.getInstance();

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblTitle;

    @FXML
    private TextField tfName;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	ig.sectionNew = null;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	ig.sectionNew = tfName.getText();
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tfName.setOnKeyPressed((e) -> {
			if( e.getCode() == KeyCode.ENTER )
				doBtnSave(null);
		});
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}

}
