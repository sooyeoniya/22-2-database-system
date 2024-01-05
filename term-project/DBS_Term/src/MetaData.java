import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sun.prism.paint.Color;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MetaData {
	private Connection con = MyConnection.makeConnection();
	private TreeView<String> tree;
	private Label[] labels;
	private TextField[] txt;

	public void getStage() {
		Stage stage = new Stage();
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		vbox.setMinSize(800, 600);
		vbox.setMaxSize(1000, 800);
		vbox.setPadding(new Insets(15, 15, 15, 15));

		// add Top Pane
		BorderPane tbox = addTopPane();
		tbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(tbox);

		// add Center box
		HBox cbox = addCenterPane();
		cbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(cbox);

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.setTitle("Database Meta Data");
		stage.show();
	}

	private BorderPane addTopPane() {
		BorderPane bPane = new BorderPane();

		bPane.setPadding(new Insets(15, 15, 50, 15));
		Text text = new Text("About my Database");
		text.setFont(Font.font("Arial", FontWeight.BOLD, 36));

		bPane.setCenter(text);

		return bPane;
	}

	private HBox addCenterPane() {

		HBox hb1 = new HBox();
		
		VBox vb = new VBox();

		// Add Labels and TextFields
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(50, 15, 15, 20));
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setStyle("-fx-border-color: Blue;");
		// gp.prefHeightProperty().bind(tree.prefHeightProperty());
		txt = new TextField[10];
		labels = new Label[10];

		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label("Label..");
			labels[i].setFont(Font.font("Arial", FontWeight.BOLD, 14));
			labels[i].setMinSize(150, 40);
			txt[i] = new TextField(" Text.. ");
			txt[i].setMinSize(300, 40);
			txt[i].setEditable(false);
			txt[i].setFont(Font.font("Arial", FontWeight.BOLD, 14));
			gp.addRow(i, labels[i], txt[i]);
			labels[i].prefHeightProperty().bind(gp.widthProperty());
			txt[i].prefHeightProperty().bind(gp.widthProperty());
		}

		// you will write this function
		fillGrid();

		vb.getChildren().addAll(gp);

		// Add TreeView
		StackPane stack = new StackPane();

		// Create the tree
		tree = addNodestoTree(); // you will write
		tree.setShowRoot(true);

		tree.setMaxWidth(400);
		tree.prefWidthProperty().bind(stack.prefWidthProperty());
		stack.getChildren().add(tree);

		hb1.getChildren().addAll(stack, vb);
		hb1.setStyle("-fx-border-color:black;");
		hb1.setSpacing(20);
		hb1.prefHeightProperty().bind(vb.prefWidthProperty());

		return hb1;
	}

	private TreeView<String> addNodestoTree() {

		TreeView<String> tree = new TreeView<String>();
		TreeItem<String> root, tables, sfunctions, tfunctions, sprocedures, triggers;

		root = new TreeItem<String>("Lecture");
		tree.setRoot(root);

		tables = new TreeItem<String>(Nodes02.Tables.toString());

		try {
			String sql1 = "select name as tn from getTableList()";
			ResultSet rs1 = (con.prepareStatement(sql1)).executeQuery();
			int i = 0;
			while (rs1.next()) {
				String tname = rs1.getString("tn");
				makeChild(tname, tables);

				String sql2 = "select * from getColumnList(?)";
				PreparedStatement pst = con.prepareStatement(sql2);
				pst.setString(1, tname);
				ResultSet rs2 = pst.executeQuery();

				while (rs2.next()) {

					String cnames = rs2.getString("column_name") + "(" + rs2.getString("column_type") + ","
							+ rs2.getString("max_length") + ")";

					makeChild(cnames, tables.getChildren().get(i));

				}
				i++;
				rs2.close();
			}
			rs1.close();
			
			
			sfunctions = new TreeItem<String>(Nodes02.Scalar_Functions.toString());
    		makeChild(Nodes02.CalculateArea.toString(), sfunctions);
    		makeChild(Nodes02.GetAge.toString(), sfunctions);
    		makeChild(Nodes02.getSum.toString(), sfunctions);
    		makeChild(Nodes02.rectangleArea.toString(), sfunctions);
    		makeChild(Nodes02.reverseCustName.toString(), sfunctions);
    		
			// add Scalar valued function and their parameters
			
			tfunctions = new TreeItem<String>(Nodes02.Table_Functions.toString());
			// add Table valued function and their parameters
    		makeChild(Nodes02.getColunmList.toString(), tfunctions);
    		makeChild(Nodes02.getFunctionList.toString(), tfunctions);
    		makeChild(Nodes02.getParameters.toString(), tfunctions);
    		makeChild(Nodes02.getPKFK.toString(), tfunctions);
    		makeChild(Nodes02.getProcedure.toString(), tfunctions);
    		makeChild(Nodes02.getRCtable.toString(), tfunctions);
    		makeChild(Nodes02.getTableList.toString(), tfunctions);
    		makeChild(Nodes02.getTriggers1.toString(), tfunctions);
    		makeChild(Nodes02.getTriggers2.toString(), tfunctions);
    		makeChild(Nodes02.getUDtrigger.toString(), tfunctions);
			
    		
			sprocedures = new TreeItem<String>(Nodes02.Stored_Procedures.toString());
			// add Stored procedures and their parameters
			
    		makeChild(Nodes02.addDiscountCoupon.toString(), sprocedures);
    		makeChild(Nodes02.addInstructor.toString(), sprocedures);
    		makeChild(Nodes02.addLecture.toString(), sprocedures);
    		makeChild(Nodes02.addLectureRoom.toString(), sprocedures);
    		makeChild(Nodes02.addPossess.toString(), sprocedures);
    		makeChild(Nodes02.addStudent.toString(), sprocedures);
    		makeChild(Nodes02.addTake.toString(), sprocedures);
    		
    		makeChild(Nodes02.deleteDiscountCoupon.toString(), sprocedures);
    		makeChild(Nodes02.deleteInstructor.toString(), sprocedures);
    		makeChild(Nodes02.deleteLecture.toString(), sprocedures);
    		makeChild(Nodes02.deleteLectureRoom.toString(), sprocedures);
    		makeChild(Nodes02.deletePossess.toString(), sprocedures);
    		makeChild(Nodes02.deleteStudent.toString(), sprocedures);
    		makeChild(Nodes02.deleteTake.toString(), sprocedures);
    		
    		makeChild(Nodes02.updateDiscountCoupon.toString(), sprocedures);
    		makeChild(Nodes02.updateInstructor.toString(), sprocedures);
    		makeChild(Nodes02.updateLecture.toString(), sprocedures);
    		makeChild(Nodes02.updateLectureRoom.toString(), sprocedures);
    		makeChild(Nodes02.updatePossess.toString(), sprocedures);
    		makeChild(Nodes02.updateStudent.toString(), sprocedures);
    		makeChild(Nodes02.updateTake.toString(), sprocedures);

			triggers = new TreeItem<String>(Nodes02.Triggers.toString());
			// add Trigger

    		makeChild(Nodes02.myTrigger_D.toString(), triggers);
    		makeChild(Nodes02.myTrigger_I.toString(), triggers);
    		makeChild(Nodes02.myTrigger_L.toString(), triggers);
    		makeChild(Nodes02.myTrigger_LR.toString(), triggers);
    		makeChild(Nodes02.myTrigger_P.toString(), triggers);
    		makeChild(Nodes02.myTrigger_S.toString(), triggers);
    		makeChild(Nodes02.myTrigger_T.toString(), triggers);
    		makeChild(Nodes02.tr_deleteDiscountCoupon.toString(), triggers);
    		makeChild(Nodes02.tr_deleteInstructor.toString(), triggers);
    		makeChild(Nodes02.tr_deleteLecture.toString(), triggers);
    		makeChild(Nodes02.tr_deleteLectureRoom.toString(), triggers);
    		makeChild(Nodes02.tr_deletePossess.toString(), triggers);
    		makeChild(Nodes02.tr_deleteStudent.toString(), triggers);
    		makeChild(Nodes02.tr_deleteTake.toString(), triggers);
    		makeChild(Nodes02.tr_updateDiscountCoupon.toString(), triggers);
    		makeChild(Nodes02.tr_updateInstructor.toString(), triggers);
    		makeChild(Nodes02.tr_updateLecture.toString(), triggers);
    		makeChild(Nodes02.tr_updateLectureRoom.toString(), triggers);
    		makeChild(Nodes02.tr_updatePossess.toString(), triggers);
    		makeChild(Nodes02.tr_updateStudent.toString(), triggers);
    		makeChild(Nodes02.tr_updateTake.toString(), triggers);

			root.getChildren().addAll(tables, sfunctions, tfunctions, sprocedures, triggers);

		} 
		
		catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		finally {
		}
		
		return tree;
	}

	// Create child
	private TreeItem<String> makeChild(String title, TreeItem<String> parent) {
		TreeItem<String> item = new TreeItem<>(title);
		item.setExpanded(false);
		parent.getChildren().add(item);
		return item;
	}

	private void fillGrid() {
		try {
			DatabaseMetaData dm = (DatabaseMetaData) con.getMetaData();
			
			labels[0].setText("Database name ");
			String sql0 = "select db_name() as dn";
			PreparedStatement ps0 = con.prepareStatement(sql0);
			ResultSet rs0 = ps0.executeQuery();
			rs0.next();
			txt[0].setText(rs0.getString("dn"));
            /*=============================================================*/
			labels[1].setText("Number of Tables ");
			String sql1 = "select count(*) as c from getTableList()";
			PreparedStatement ps1 = con.prepareStatement(sql1);
			ResultSet rs1 = ps1.executeQuery();
			rs1.next();
			txt[1].setText(rs1.getString(1));
			rs1.close();
			/*=============================================================*/
			labels[2].setText("Number of Scalar Valued Functions ");
			String sql2 = "select count(*) as c from getFunctionList() where type='FN'";
			PreparedStatement ps2 = con.prepareStatement(sql2);
			ResultSet rs2 = ps2.executeQuery();
			rs2.next();
			txt[2].setText(rs2.getString(1));
			rs2.close();
			/*=============================================================*/
			labels[3].setText("Number of Table-Valued Functions");
			String sql3 = "select count(*) as c from getFunctionList() where type='IF'";
			PreparedStatement ps3 = con.prepareStatement(sql3);
			ResultSet rs3 = ps3.executeQuery();
			rs3.next();
			txt[3].setText(rs3.getString(1));
			rs3.close();
			/*=============================================================*/
			labels[4].setText("Number of Stored procedures ");
			String sql4 = "select count(*) as c from getProcedure()";
			PreparedStatement ps4 = con.prepareStatement(sql4);
			ResultSet rs4 = ps4.executeQuery();
			rs4.next();
			txt[4].setText(rs4.getString(1));
			rs4.close();
			/*=============================================================*/
			labels[5].setText("Number of Triggers ");
			String sql5 = "select count(*) as c from getUDtrigger()";
			PreparedStatement ps5 = con.prepareStatement(sql5);
			ResultSet rs5 = ps5.executeQuery();
			rs5.next();
			txt[5].setText(rs5.getString(1));
			rs5.close();
			/*=============================================================*/
			labels[6].setText("DBMS name: ");
			txt[6].setText(dm.getDatabaseProductName());

			labels[7].setText("DBMS Version: ");
			txt[7].setText(dm.getDatabaseProductVersion());

			labels[8].setText("JDBC Driver name: ");
			txt[8].setText(dm.getDriverName());

			labels[9].setText("JDBC Driver Version: ");
			txt[9].setText(dm.getDriverVersion());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
		}

	}

}