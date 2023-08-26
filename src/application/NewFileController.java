package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NewFileController implements Initializable {
	
	private ItGlobal ig = ItGlobal.getInstance();
	private FileChooser fc = null;

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCrearte;

    @FXML
    private Button btnSelectDir;

    @FXML
    private Label lblTitle;
    
    @FXML
    private TextField tfPath;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	ig.fileNew = null;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnCreate(ActionEvent event) {

    	ig.fileNew = tfPath.getText();
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doSelectDir(ActionEvent event) {
    	File f = fc.showOpenDialog(aPane.getScene().getWindow());
    	if (f != null) {
    		tfPath.setText(f.getAbsolutePath());
    	}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}

}