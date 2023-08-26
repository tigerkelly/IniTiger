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
	
import java.io.IOException;

import com.rkw.IniFile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {
	private Pane mainPane = null;
	private ItGlobal ig = ItGlobal.getInstance();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			System.setProperty("java.net.preferIPv4Stack" , "true");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/CuteTiger.png")));
			primaryStage.setTitle("IniTiger by Kelly Wiles");
			primaryStage.setScene(createScene(loadMainPane()));
//			primaryStage.setMinWidth(750);
//			primaryStage.setMinHeight(700);
//			primaryStage.setOnCloseRequest((e) -> e.consume());		// disable Stage close button.
			primaryStage.setOnCloseRequest((e) -> { 
				for (String s : ig.files.keySet()) {
		    		IniFile ini = ig.files.get(s).getIni();
		    		if (ini.getChangedFlag() == true) {
						ButtonType bt = ig.yesNoCancelAlert(mainPane, "Quit IniTiger", 
								"You have unsaved changes.\n" +
								"Select 'No' to abort all changes and exit.\n" +
								"select 'Yes' to close dialog, then use\n" +
								"Menu File -> 'Save Project' or 'Save all Projects'.\n", null);
//						System.out.println(bt);
						if (bt.getButtonData() == ButtonData.YES) {
							e.consume();
							return;
						}
					}
		    	}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setWidth(Double width) {
		mainPane.setPrefWidth(width);
	}

	@Override
	public void stop() {
//		System.out.println("*** TigerScan is Ending. ***");

		SceneInfo si = ig.sceneNav.fxmls.get(ig.scenePeek());
		if (si != null && si.controller instanceof RefreshScene) {
			RefreshScene c = (RefreshScene) si.controller;
			c.leaveScene();
		}
	}

	/**
	 * Loads the main fxml layout.
	 *
	 * @return the loaded pane.
	 * @throws IOException if the pane could not be loaded.
	 */
//    @SuppressWarnings("resource")
	private Pane loadMainPane() throws IOException {
//		System.out.println("*** TigerScan is Starting. ***");

		FXMLLoader loader = new FXMLLoader();

		mainPane = (Pane) loader.load(getClass().getResourceAsStream(SceneNav.MAIN)); // SceneNav

		SceneNavController mainController = loader.getController();

		ig.sceneNav.setMainController(mainController);
		ig.sceneNav.loadScene(SceneNav.INITIGER);

		return mainPane;
	}

	/**
	 * Creates the main application scene.
	 *
	 * @param mainPane the main application layout.
	 *
	 * @return the created scene.
	 */
	private Scene createScene(Pane mainPane) {
		Scene scene = new Scene(mainPane);

//		scene.getStylesheets().setAll(getClass().getResource("application.css").toExternalForm());

		return scene;
	}
}
