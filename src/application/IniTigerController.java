package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.rkw.IniFile;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class IniTigerController implements Initializable {

	private ItGlobal ig = ItGlobal.getInstance();
	private FileChooser fc = null;
//	private Accordion acc = null;
	
    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnAbout;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblVersion;

    @FXML
    private MenuItem mFileOpen;
    
    @FXML
    private MenuItem mFileNewIni;
    
    @FXML
    private MenuItem mFileSave;
    
    @FXML
    private MenuItem mFileSaveAll;
    
    @FXML
    private Menu mFileRecent;

    @FXML
    private MenuItem mFileQuit;

    @FXML
    private TabPane tabPane;

    @FXML
    void doBtnAbout(ActionEvent event) {
    	ig.centerScene(aPane, "About.fxml", "About IniTiger", null);
    }

    @FXML
    void doFileOpen(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	fc.getExtensionFilters().addAll(
    		new FileChooser.ExtensionFilter("Ini Files", "*.ini"),
		    new FileChooser.ExtensionFilter("All Files", "*.*")
		);
    	File fd = fc.showOpenDialog(stage);
		if (fd != null) {
			FileInfo fi = createTab(fd);
			
			ig.files.put(fd.getAbsolutePath(), fi);
			String files = ig.sysIni.getString("Recents", "files");
			
			List<String> list = new ArrayList<String>();
			if (files != null && files.isBlank() == false) {
				String[] arr = files.split(",");
			
				list.addAll(Arrays.asList(arr));
			}
			
			list.add(fd.getAbsolutePath());
			
			if (list.size() > 10) {
				list.remove(0);
			}
			
			files = null;
			for (String p : list) {
				if (files == null) {
					files = p;
				} else {
					files += "," + p;
				}
			}
			
			ig.sysIni.addValuePair("Recents", "files", files);
			ig.sysIni.writeFile(true);
			
			files = ig.sysIni.getString("Recents", "files");
			if (files != null) {
				ObservableList<MenuItem> items = mFileRecent.getItems();
				
				items.clear();
				
				String[] arr = files.split(",");
				for (String p : arr) {
					MenuItem mi = new MenuItem(p);
					mi.setOnAction((e) -> {
						boolean flag = false;
						ObservableList<Tab> tabs = tabPane.getTabs();
						for (Tab tab : tabs) {
							if (tab.getText().equals(mi.getText()) == true)
								flag = true;
						}
						if (flag == false)
							createTab(new File(mi.getText()));
					});
					items.add(mi);
				}
			}
			
			lblStatus.setText(fd.getAbsolutePath() + " Opened.");
		}
    }
    
    @FXML
    void doFileNewIni(ActionEvent event) {
    	FXMLLoader loader = ig.loadScene(aPane, "NewFile.fxml", "Create New Ini File", null);
    	NewFileController nfc = (NewFileController)loader.getController();
		
		Stage stage = nfc.getStage();
		stage.showAndWait();
		
		if (ig.fileNew != null) {
			File fd = new File(ig.fileNew);
			
			try {
				FileWriter fw = new FileWriter(ig.fileNew, false);
				
				fw.write("# Created by the IniTiger program.\n\n");
				
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			FileInfo fi = createTab(fd);
			
			ig.files.put(fd.getAbsolutePath(), fi);
			
			lblStatus.setText(ig.fileNew + " Created.");
		}
    }
    
    @FXML
    void doFileSave(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	
    	FileInfo fi = (FileInfo)tab.getUserData();
    	
//    	System.out.println(fi);
    	
    	fi.getIni().writeFile(true);
    	
    	ObservableList<TitledPane> panes = fi.getAcc().getPanes();
		if (panes != null) {
			for (TitledPane p : panes) {
				p.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
				
				VBox vbox = (VBox)p.getContent();
				ObservableList<Node> nodes = vbox.getChildren();
				if (nodes != null) {
					for (Node n : nodes) {
						HBox hb = (HBox)n;
						ObservableList<Node> childern = hb.getChildren();
						if (childern != null) {
							if (childern.get(0) instanceof Label) {
								Label l = (Label)childern.get(0);
								TextField t = (TextField)childern.get(1);
								l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: black;");
								t.setStyle("-fx-font-size: 16px; -fx-font-weight: normal; -fx-font-family: SanSerif; -fx-text-fill: black;");
							}
						}
					}
				}
			}
		}
		lblStatus.setText(fi.getIni().getFileName() + " Saved.");
    }
    
    @FXML
    void doFileSaveAll(ActionEvent event) {
    	System.out.println("Save All");
    	
    	for (String s : ig.files.keySet()) {
    		IniFile ini = ig.files.get(s).getIni();
    		
    		if (ini.getChangedFlag() == true) {
				ini.writeFile(true);
			}
    		
    		Accordion acc = ig.files.get(s).getAcc();
    		
    		ObservableList<TitledPane> panes = acc.getPanes();
    		for (TitledPane p : panes) {
    			VBox vbox = (VBox)p.getContent();
    			ObservableList<Node> nodes = vbox.getChildren();
    			if (nodes != null) {
    				for (Node n : nodes) {
    					HBox hb = (HBox)n;
    					ObservableList<Node> childern = hb.getChildren();
    					if (childern != null) {
    						if (childern.get(0) instanceof Label) {
    							Label l = (Label)childern.get(0);
    							TextField t = (TextField)childern.get(1);
    							l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: black;");
    							t.setStyle("-fx-font-size: 16px; -fx-font-weight: normal; -fx-font-family: SanSerif; -fx-text-fill: black;");
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	lblStatus.setText("All Saved.");
    }

    @FXML
    void doFileQuit(ActionEvent event) {

    	for (String s : ig.files.keySet()) {
    		IniFile ini = ig.files.get(s).getIni();
    		if (ini.getChangedFlag() == true) {
				ButtonType bt = ig.yesNoCancelAlert(aPane, "Quit IniTiger", 
						"You have unsaved changes.\n" +
						"Select 'No' to abort all changes and exit.\n" +
						"select 'Yes' to close dialog, then use\n" +
						"Menu File -> 'Save Project' or 'Save all Projects'.\n", null);
//				System.out.println(bt);
				if (bt.getButtonData() == ButtonData.YES) {
					return;
				}
			}
    	}
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doHelpAbout(ActionEvent event) {
    	ig.centerScene(aPane, "About.fxml", "About IniTiger", null);
    }

    @FXML
    void doHelpHelp(ActionEvent event) {
    	ig.centerScene(aPane, "Help.fxml", "IniTiger Help", null);
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fc = new FileChooser();
		
		String files = ig.sysIni.getString("Recents", "files");
		if (files != null && files.isBlank() == false) {
			String[] arr = files.split(",");
			
			for (String p : arr) {
				if (p == null || p.isBlank() == true)
					continue;
				File f = new File(p);
				if (f.exists() == true) {
					MenuItem mi = new MenuItem(p);
					
					mi.setOnAction((e) -> {
						boolean flag = false;
						ObservableList<Tab> tabs = tabPane.getTabs();
						for (Tab tab : tabs) {
							if (tab.getText().equals(mi.getText()) == true)
								flag = true;
						}
						if (flag == false)
							createTab(new File(mi.getText()));
						lblStatus.setText(mi.getText() + " Opened.");
					});
					
					mFileRecent.getItems().add(mi);
				}
			}
		}
	}
	
	private FileInfo createTab(File fd) {
		FileInfo fi = new FileInfo(fd);
		Tab tab = new Tab(fd.getName());
		tab.setUserData(fi);
		tab.setStyle("-fx-font-family: SanSerif; -fx-padding: 0 10 0 10; -fx-font-size: 18px;");
		
		tab.setOnCloseRequest((e) -> {
			if (fi.getIni().getChangedFlag() == true) {
				ButtonType bt = ig.yesNoAlert("Save Changes", "Do you want to save your changes?", AlertType.CONFIRMATION);
				if (bt.getButtonData() == ButtonData.CANCEL_CLOSE) {
					ig.files.remove(tab.getText());
				} else {
					fi.getIni().writeFile(true);
					ig.files.remove(tab.getText());
				}
			}
		});
		
		tabPane.getTabs().add(tab);
		
		tabPane.getSelectionModel().select(tab);
		
		VBox vb = new VBox();
		vb.setSpacing(4.0);
		
		AnchorPane.setBottomAnchor(vb, 2.0);
		AnchorPane.setLeftAnchor(vb, 4.0);
		AnchorPane.setRightAnchor(vb, 4.0);
		AnchorPane.setTopAnchor(vb, 2.0);
		
		Accordion acc = new Accordion();
		acc.setStyle("-fx-font-size: 18px; -fx-font-family: SanSerif;");
		
		vb.getChildren().add(acc);
		
		fi.setAcc(acc);
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem addSec = new MenuItem("Add Section");
		addSec.setUserData(fi);
		
		addSec.setOnAction((e) -> {
			FXMLLoader loader = ig.loadScene(aPane, "Section.fxml", "New Section", null);
			SectionController sc = (SectionController)loader.getController();
			
			Stage stage = sc.getStage();
			stage.showAndWait();
			
			if (ig.sectionNew != null) {
				addSec(fi);
			}
		});
		
		MenuItem delSec = new MenuItem("Del Section");
		delSec.setUserData(fi);
		
		delSec.setOnAction((e) -> {
			ObservableList<TitledPane> panes = acc.getPanes();
			for (TitledPane p : panes) {
				String t = p.getText();
				if (p.isFocused() == true) {
					ButtonType bt = ig.yesNoAlert("Delete Section", "Are you sure you want to delete section '" + t + "'?", AlertType.CONFIRMATION);
					if (bt.getButtonData() == ButtonData.CANCEL_CLOSE)
						return;
					
					fi.getIni().removeSection(t);
					
					fi.getAcc().getPanes().remove(p);
					
					break;
				}
			}
		});
		
		cm.getItems().addAll(addSec, delSec);
		
		acc.setContextMenu(cm);
		
		tab.setContent(vb);
		
		String sep = "~";
		
		Object[] secs = fi.getIni().getSectionNames();
		if (secs != null) {
			for (Object sec : secs) {
				if (((String)sec).endsWith("-Comments") == true)
					continue;
				
				TitledPane tp = new TitledPane((String)sec, vb);
				tp.setStyle("-fx-font-family: SanSerif;");
				tp.setPrefWidth(Double.MAX_VALUE);
				acc.getPanes().add(tp);
				
				VBox vb2 = new VBox();
				AnchorPane.setBottomAnchor(vb2, 2.0);
				AnchorPane.setLeftAnchor(vb2, 0.0);
				AnchorPane.setRightAnchor(vb2, 0.0);
				AnchorPane.setTopAnchor(vb2, 2.0);
				
				tp.setContent(vb2);
				
				Object[] keys = fi.getIni().getSectionKeys(sec);
				if (keys != null) {
					for (Object k : keys) {
						String v = fi.getIni().getString(sec, k);
						HBox hb = new HBox();
						hb.setSpacing(4.0);
						hb.setPadding(new Insets(2.0));
						hb.setAlignment(Pos.CENTER_LEFT);
						
						Tooltip tt = null;
						
						Label lbl = new Label((String)k);
						lbl.setPrefWidth(125.0);
						lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif;");
//						tt = new Tooltip((String)k);
//						tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
//						lbl.setTooltip(tt);
						
						TextField tf = new TextField(v);
						tf.setEditable(false);
						String c = fi.getIni().getString(sec + "-Comments", k);
						
						if (c != null && c.isBlank() == false) {
							tt = new Tooltip(c);
							tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
							tf.setTooltip(tt);
						}
						tf.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif; -fx-font-weight: normal;");
						
						HBox.setHgrow(tf, Priority.ALWAYS);
						
						Button undo = new Button();
						undo.setGraphic(new ImageView(ig.imgUndo));
						undo.setUserData(sec + sep + k + sep + v);
						undo.setPrefWidth(40.0);
						undo.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						tt = new Tooltip("Undo changes");
						tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						undo.setTooltip(tt);
						
						undo.setOnAction((e) -> {
							String kv = (String)undo.getUserData();
							
							HBox hbox = (HBox)undo.getParent();
							Label l = (Label)hbox.getChildren().get(0);
							l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: black;");
							TextField t = (TextField)hbox.getChildren().get(1);
							t.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif; -fx-font-weight: normal;");
							String[] a = kv.split("~");
							l.setText(a[1]);
							t.setText(a[2]);
							
							fi.getIni().addValuePair(a[0], a[1], a[2]);
						});
						
						Button edit = new Button();
						edit.setGraphic(new ImageView(ig.imgEdit));
						edit.setUserData(sec + sep + k + sep + v);
						edit.setPrefWidth(40.0);
						edit.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						tt = new Tooltip("Edit Key/Value part.");
						tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						edit.setTooltip(tt);
						
						edit.setOnAction((e) -> {
							String kv = (String)edit.getUserData();
							
							FXMLLoader loader = ig.loadScene(aPane, "Edit.fxml", "Edit Key/Value", null);
							EditController ec = (EditController)loader.getController();
							
							ec.setData(kv.split(sep));
							ec.setIni(fi.getIni());
							
							Stage stage = ec.getStage();
							stage.showAndWait();
							
							if (ig.kvChanged == true) {
								HBox hbox = (HBox)undo.getParent();
								Label l = (Label)hbox.getChildren().get(0);
								l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: magenta;");
								TextField t = (TextField)hbox.getChildren().get(1);
								t.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif; -fx-font-weight: bold;");
								String[] a = ig.secKeyValue.split("~");
								l.setText(a[1]);
								t.setText(a[2]);
								String comment = fi.getIni().getString(sec + "-Comments", a[1]);
								if (comment != null && comment.isBlank() == false) {
									Tooltip tooltip = t.getTooltip();
									if (tooltip == null) {
										tooltip = new Tooltip(comment);
										t.setTooltip(tooltip);
									} else {
										tooltip.setText(comment);
									}
								} else {
									Tooltip tooltip = t.getTooltip();
									if (tooltip != null)
										t.setTooltip(null);
								}
								
								ObservableList<TitledPane> panes = acc.getPanes();
								if (panes != null) {
									for (TitledPane p : panes) { 
										if (p.getText().equals(sec) == true) {
											p.setStyle("-fx-text-fill: blue; -fx-font-family: SanSerif;");
										}
									}
								}
								lblStatus.setText(a[0] + " " + a[1] + " " + a[2] + " Changed.");
							}
						});
						
						Button delete = new Button();
						delete.setGraphic(new ImageView(ig.imgDelete));
						delete.setUserData(sec + sep + k + sep + v);
						delete.setPrefWidth(40.0);
						delete.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						tt = new Tooltip("Delete Key/Value pair.");
						tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						delete.setTooltip(tt);
						
						delete.setOnAction((e) -> {
							String kv = (String)delete.getUserData();
							String [] a = kv.split("~");
							
							ButtonType bt = ig.yesNoAlert("Delete Key/Value pair",
									"Are you sure you want to delete key '" + a[1] + "'?", AlertType.CONFIRMATION);
							
							if (bt.getButtonData() == ButtonData.CANCEL_CLOSE)
								return;
							
							fi.getIni().removeValuePair(a[1], a[2]);
							
							HBox hb3 = (HBox)delete.getParent();
							VBox vb3 = (VBox)hb3.getParent();
							
							vb3.getChildren().remove(hb3);
							
							lblStatus.setText(a[0] + " " + a[1] + " " + a[2] + " Deleted.");
						});
						
						hb.getChildren().addAll(lbl, tf, undo, edit, delete);
						
						vb2.getChildren().add(hb);
					}
				}
				
				HBox hb = new HBox();
				hb.setSpacing(4.0);
				hb.setPadding(new Insets(2.0));
				hb.setAlignment(Pos.CENTER_LEFT);
				
				Region rg = new Region();
				HBox.setHgrow(rg, Priority.ALWAYS);
				
				Button add = new Button("Key/Value");
				add.setUserData(sec);
				add.setGraphic(new ImageView(ig.imgAdd));
//				add.setPrefWidth(50.0);
				add.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
				Tooltip tt = new Tooltip("Add Key/value pair.");
				tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
				add.setTooltip(tt);
				
				add.setOnAction((e) -> {
					String secName = (String)add.getUserData();
					
					FXMLLoader loader = ig.loadScene(aPane, "Edit.fxml", "Add Key/Value", null);
					EditController ec = (EditController)loader.getController();
					
					ec.setData(secName.split(sep));
					ec.setIni(fi.getIni());
					
					Stage stage = ec.getStage();
					stage.showAndWait();
					
					if (ig.kvChanged == true) {
						addKeyValueBox(fi);
						
						String [] a = ig.secKeyValue.split("~");
						lblStatus.setText(a[0] + "->" + a[1] + "->" + a[2] + " Key/Value Added.");
					}
				});
				
				hb.getChildren().addAll(rg, add);
				
				vb2.getChildren().add(hb);
			}
			
			HBox hb2 = new HBox();
			hb2.setSpacing(4.0);
			hb2.setPadding(new Insets(0.0, 4.0, 0.0, 4.0));
			Region rg2 = new Region();
			HBox.setHgrow(rg2, Priority.ALWAYS);
			Button addSection = new Button("Section");
			addSection.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
			addSection.setGraphic(new ImageView(ig.imgAdd));
			Tooltip tt = new Tooltip("Add Section.");
			tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
			addSection.setTooltip(tt);
			
			addSection.setOnAction((e) -> {
				FXMLLoader loader = ig.loadScene(aPane, "Section.fxml", "New Section", null);
				SectionController sc = (SectionController)loader.getController();
				
				Stage stage = sc.getStage();
				stage.showAndWait();
				
				if (ig.sectionNew != null) {
					addSec(fi);
					
					lblStatus.setText(ig.sectionNew.replaceAll(sep, "->") + " section Added.");
				}
			});
			
			hb2.getChildren().addAll(rg2, addSection);
			
			vb.getChildren().add(hb2);
		}
		
		return fi;
	}
	
	private void addSec(FileInfo fi) {
		String sep = "~";
		
		VBox vb = new VBox();
		AnchorPane.setBottomAnchor(vb, 2.0);
		AnchorPane.setLeftAnchor(vb, 4.0);
		AnchorPane.setRightAnchor(vb, 4.0);
		AnchorPane.setTopAnchor(vb, 2.0);
		
		TitledPane tp = new TitledPane(ig.sectionNew, vb);
		tp.setStyle("-fx-font-family: SanSerif;");
		tp.setPrefWidth(Double.MAX_VALUE);
		fi.getAcc().getPanes().add(tp);
		
		fi.getIni().addSection(ig.sectionNew);
		
		HBox hb = new HBox();
		hb.setSpacing(4.0);
		hb.setPadding(new Insets(2.0));
		hb.setAlignment(Pos.CENTER_LEFT);
		
		Region rg = new Region();
		HBox.setHgrow(rg, Priority.ALWAYS);
		
		Button add = new Button("Key/Value");
		add.setUserData(ig.sectionNew);
		add.setGraphic(new ImageView(ig.imgAdd));
//		add.setPrefWidth(50.0);
		add.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
		Tooltip tt = new Tooltip("Add Key/value pair.");
		tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
		add.setTooltip(tt);
		
		add.setOnAction((e) -> {
			String secName = (String)add.getUserData();
			
			FXMLLoader loader = ig.loadScene(aPane, "Edit.fxml", "Add Key/Value", null);
			EditController ec = (EditController)loader.getController();
			
			ec.setData(secName.split(sep));
			ec.setIni(fi.getIni());
			
			Stage stage = ec.getStage();
			stage.showAndWait();
			
			if (ig.kvChanged == true) {
				addKeyValueBox(fi);
			}
		});
		
		hb.getChildren().addAll(rg, add);
		vb.getChildren().add(hb);
		
		fi.getIni().addSection(ig.sectionNew + "-Comments");
		
	}
	
	private void addKeyValueBox(FileInfo fi) {
		ObservableList<TitledPane> panes = fi.getAcc().getPanes();
		
		String sep = "~";
		
		String[] a = ig.secKeyValue.split(sep);
		String sec = a[0];
		String k = a[1];
		String v = a[2];
		
		if (panes != null) {
			for (TitledPane p : panes) {
				if (p.getText().equals(sec) == true) {
					
					p.setStyle("-fx-text-fill: blue; -fx-font-weight: normal;");
					
					VBox vbox = (VBox)p.getContent();
					
					HBox hb = new HBox();
					hb.setSpacing(4.0);
					hb.setPadding(new Insets(2.0));
					hb.setAlignment(Pos.CENTER_LEFT);
					
					Tooltip tt = null;
					
					Label lbl = new Label(k);
					lbl.setPrefWidth(125.0);
					lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif;");
					tt = new Tooltip(k);
					tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					lbl.setTooltip(tt);
					
					TextField tf = new TextField(v);
					tf.setEditable(false);
					String c = fi.getIni().getString(sec + "-Comments", k);
					
					if (c != null && c.isBlank() == false) {
						tt = new Tooltip(c);
						tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
						tf.setTooltip(tt);
					}

					tf.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					Button undo = new Button();
					undo.setGraphic(new ImageView(ig.imgUndo));
					undo.setUserData(sec + sep + k + sep + v);
					undo.setPrefWidth(40.0);
					undo.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					tt = new Tooltip("Undo changes");
					tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					undo.setTooltip(tt);
					
					undo.setOnAction((e) -> {
						String kv = (String)undo.getUserData();
						
						HBox hbox = (HBox)undo.getParent();
						Label l = (Label)hbox.getChildren().get(0);
						l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: black;");
						TextField t = (TextField)hbox.getChildren().get(1);
						t.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif; -fx-font-weight: normal;");
						String[] a2 = kv.split("~");
						l.setText(a2[1]);
						t.setText(a2[2]);
						
						fi.getIni().addValuePair(a2[0], a2[1], a2[2]);
						lblStatus.setText(a2[0] + " " + a2[1] + " " + a2[2] + " Key/Value Undone.");
					});
					
					Button edit = new Button();
					edit.setGraphic(new ImageView(ig.imgEdit));
					edit.setUserData(sec + sep + k + sep + v);
					edit.setPrefWidth(40.0);
					edit.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					tt = new Tooltip("Edit Key part.");
					tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					edit.setTooltip(tt);
					
					edit.setOnAction((e) -> {
						String kv = (String)edit.getUserData();
						
						FXMLLoader loader = ig.loadScene(aPane, "Edit.fxml", "Edit Key/Value", null);
						EditController ec = (EditController)loader.getController();
						
						ec.setData(kv.split(sep));
						ec.setIni(fi.getIni());
						
						Stage stage = ec.getStage();
						stage.showAndWait();
						
						if (ig.kvChanged == true) {
							HBox hbox = (HBox)undo.getParent();
							Label l = (Label)hbox.getChildren().get(0);
							l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: magenta;");
							TextField t = (TextField)hbox.getChildren().get(1);
							t.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif; -fx-font-weight: bold;");
							String[] a2 = ig.secKeyValue.split("~");
							l.setText(a2[1]);
							t.setText(a2[2]);
							String comment = fi.getIni().getString(sec + "-Comments", a2[1]);
							if (comment != null && comment.isBlank() == false) {
								Tooltip tooltip = t.getTooltip();
								if (tooltip == null) {
									tooltip = new Tooltip(comment);
									t.setTooltip(tooltip);
								} else {
									tooltip.setText(v);
								}
							} else {
								Tooltip tooltip = t.getTooltip();
								if (tooltip != null)
									t.setTooltip(null);
							}
							
							ObservableList<TitledPane> panes2 = fi.getAcc().getPanes();
							if (panes2 != null) {
								for (TitledPane p2 : panes2) { 
									if (p2.getText().equals(sec) == true) {
										p2.setStyle("-fx-text-fill: blue; -fx-font-family: SanSerif;");
									}
								}
							}
							
							lblStatus.setText(a2[0] + " " + a2[1] + " " + a2[2] + " Key/Value Edited.");
						}
					});
					
					Button delete = new Button();
					delete.setGraphic(new ImageView(ig.imgDelete));
					delete.setUserData(sec + sep + k + sep + v);
					delete.setPrefWidth(40.0);
					delete.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					tt = new Tooltip("Delete Key/value pair.");
					tt.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
					delete.setTooltip(tt);
					
					delete.setOnAction((e) -> {
						String kv = (String)delete.getUserData();
						String [] a3 = kv.split("~");
						
						ButtonType bt = ig.yesNoAlert("Delete Key/Value pair",
								"Are you sure you want to delete key '" + a3[1] + "'?", AlertType.CONFIRMATION);
						
						if (bt.getButtonData() == ButtonData.CANCEL_CLOSE)
							return;
						
						fi.getIni().removeValuePair(a3[1], a3[2]);
						
						HBox hb3 = (HBox)delete.getParent();
						VBox vb3 = (VBox)hb3.getParent();
						
						vb3.getChildren().remove(hb3);
					});
					
					hb.getChildren().addAll(lbl, tf, undo, edit, delete);
					
					vbox.getChildren().add(vbox.getChildren().size() - 1, hb);
					
					break;
				}
			}
		}
	}

}