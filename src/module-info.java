module IniTiger {
	requires javafx.controls;
	requires javafx.fxml;
	requires IniFile;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
}
