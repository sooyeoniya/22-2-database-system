
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javafx.util.Callback;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DBGUI01 extends Application {

// data members
	private Connection con = MyConnection.makeConnection();
	private TableView table;
	private TreeView<String> tree;
	private Button[] buttons;
	private Label[] labels;
	private TextField[] txt;
	private TextArea txtArea; 
	private final String[] btntext = { "clear", "save", "update", "delete", "print", "search" };
	private Object JasperViewer;
	
// function members
	private HBox addCenterPane() {
		
		HBox hb1 = new HBox();
		
		// Add TableView
		VBox vb = new VBox();

		table = new TableView<>();
		table.setMinSize(700, 150);
		table.setMaxSize(700, 150);
		table.setStyle("-fx-border-color: Black;");
		table.prefWidthProperty().bind(vb.prefWidthProperty());
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.getSelectionModel().setCellSelectionEnabled(false);
		
		table.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv)->{
			if(ov!=nv)
				showFields();
		});
		
		// Add Labels and TextFields
		GridPane  gp = new GridPane (); 
		gp.setPadding(new Insets(15, 15, 15, 125));
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setStyle("-fx-border-color: Blue;");
		gp.prefHeightProperty().bind(table.prefWidthProperty());
		  txt=new TextField[10]; // 최대 입력 개수 10개
		  labels= new Label[10];
		  
		  for (int i = 0; i < labels.length; i++) { 
			  labels[i]= new Label("Label..");
			  labels[i].setMinSize(150, 25);
		      txt[i]= new TextField(" Text.. "); 
		      txt[i].setMinSize(300, 20);
		      gp.addRow(i, labels[i],txt[i] );
		      labels[i].prefHeightProperty().bind(gp.widthProperty());
		      txt[i].prefHeightProperty().bind(gp.widthProperty());
		   }
	 
		  
		 vb.getChildren().addAll(table, gp);
		
		 // Add TreeView
				StackPane stack = new StackPane();

				// Create the tree
				
				tree = addNodestoTree();
				tree.setShowRoot(true);
				
			
				tree.setMaxWidth(150);
				tree.prefWidthProperty().bind(stack.prefWidthProperty());
				
				tree.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv)->{
					if(nv!=ov) {
						String str=mySelectedNode();
						//txtArea.appendText("You have selected " + str + "\n");
						if(str.equals(Nodes.DiscountCoupon.toString())||
								str.equals(Nodes.Instructor.toString()) ||
								str.equals(Nodes.Lecture.toString())||
								str.equals(Nodes.LectureRoom.toString())||
								str.equals(Nodes.Possess.toString())||
								str.equals(Nodes.Student.toString())||
								str.equals(Nodes.Take.toString()) )
							rsToTableView(str);
						else if (str.equals(Nodes.About.toString())) {
							MetaData md = new MetaData();
							md.getStage();
						}
					}
					
				});
				
				stack.getChildren().add(tree);

		hb1.getChildren().addAll(stack,vb);
		hb1.setStyle("-fx-border-color:black;");
		hb1.setSpacing(20);
		hb1.prefHeightProperty().bind(vb.prefWidthProperty());
	

		return hb1;
	}

	private StackPane addBottomPane() {

		StackPane  stack = new StackPane();
		//stack.setMaxHeight(150);
		//stack.setMinHeight(150);
		//stack.setPrefHeight(150);
		
		stack.setStyle("-fx-border-color: #336699;");
		txtArea  = new TextArea();
		txtArea.setMaxHeight(200);
		txtArea.prefHeightProperty().bind(stack.prefWidthProperty());
		stack.getChildren().add(txtArea);
		return stack;
	}
	
	private void addCall() {
		String str = mySelectedNode();
		if (str.equals(Nodes.DiscountCoupon.toString())){
			addDiscountCouponSP();
		}
		else if (str.equals(Nodes.Instructor.toString())){
			addInstructorSP();
		}
		else if (str.equals(Nodes.Lecture.toString())){
			addLectureSP();
		}
		else if (str.equals(Nodes.LectureRoom.toString())){
			addLectureRoomSP();
		}
		else if (str.equals(Nodes.Possess.toString())){
			addPossessSP();
		}
		else if (str.equals(Nodes.Student.toString())){
			addStudentSP();
		}
		else if (str.equals(Nodes.Take.toString())){
			addTakeSP();
		}
	}
	
	private HBox addTopPane() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 15, 15, 15));
		hbox.setSpacing(10); // Gap between nodes
		//hbox.setStyle("-fx-background-color: #336699;");
		hbox.setStyle("-fx-border-color: Blue;");
		

		buttons = new Button[6];
		for (int i = 0; i < buttons.length; i++) {

			buttons[i] = new Button(btntext[i]);
			buttons[i].setPrefSize(80, 20);
			buttons[i].prefHeightProperty().bind(hbox.prefWidthProperty());
		}

		for (int i=0; i<buttons.length; i++) {
			final int j=i;
			buttons[j].setOnAction(e->{
				String str= buttons[j].getText();
				txtArea.appendText(" You have selected " + str +"\n");
				if("clear".equals(str)) {clearTextFields();}
				else if ("save".equals(str)) {
					if(mySelectedNode().equals(Nodes.DiscountCoupon.toString())) {
						//addCall();
						//addDiscountCouponIS();
						//addDiscountCouponRS();
						 addDiscountCouponSP();
					}
					if(mySelectedNode().equals(Nodes.Instructor.toString())) {
						//addCall();
						//addInstructorIS();
						//addInstructorRS();
						 addInstructorSP();
					}
					if(mySelectedNode().equals(Nodes.Lecture.toString())) {
						//addCall();
						//addLectureIS();
						//addLectureRS();
						 addLectureSP();
					}
					if(mySelectedNode().equals(Nodes.LectureRoom.toString())) {
						//addCall();
						//addLectureRoomIS();
						//addLectureRoomRS();
						 addLectureRoomSP();
					}
					if(mySelectedNode().equals(Nodes.Possess.toString())) {
						//addCall();
						//addPossessIS();
						//addPossessRS();
						 addPossessSP();
					}
					if(mySelectedNode().equals(Nodes.Student.toString())) {
						//addCall();
						//addStudentIS();
						//addStudentRS();
						 addStudentSP();
					}
					if(mySelectedNode().equals(Nodes.Take.toString())) {
						//addCall();
						//addTakeIS();
						//addTakeRS();
						 addTakeSP();
					}
				}
				else if ("update".equals(str)) {
					if(mySelectedNode().equals(Nodes.DiscountCoupon.toString())) {
						//updateDiscountCouponUS();
						///updateDiscountCouponRS();
						updateDiscountCouponSP();
					}
					if(mySelectedNode().equals(Nodes.Instructor.toString())) {
						//updateInstructorUS();
						//updateInstructorRS();
						updateInstructorSP();
					}
					if(mySelectedNode().equals(Nodes.Lecture.toString())) {
						//updateLectureUS();
						//updateLectureRS();
						updateLectureSP();
					}
					if(mySelectedNode().equals(Nodes.LectureRoom.toString())) {
						//updateLectureRoomUS();
						//updateLectureRoomRS();
						updateLectureRoomSP();
					}
					if(mySelectedNode().equals(Nodes.Possess.toString())) {
						//updatePossessUS();
						//updatePossessRS();
						updatePossessSP();
					}
					if(mySelectedNode().equals(Nodes.Student.toString())) {
						//updateStudentUS();
						//updateStudentRS();
						updateStudentSP();
					}
					if(mySelectedNode().equals(Nodes.Take.toString())) {
						//updateTakeUS();
						//updateTakeRS();
						updateTakeSP();
					}
				}
				else if ("delete".equals(str)) {
					if(mySelectedNode().equals(Nodes.DiscountCoupon.toString())) {
						//deleteDiscountCouponDS();
						deleteDiscountCouponRS();
						//deleteDiscountCouponSP();
					}
					if(mySelectedNode().equals(Nodes.Instructor.toString())) {
						//deleteInstructorDS();
						deleteInstructorRS();
						//deleteInstructorSP();
					}
					if(mySelectedNode().equals(Nodes.Lecture.toString())) {
						//deleteLectureDS();
						deleteLectureRS();
						//deleteLectureSP();
					}
					if(mySelectedNode().equals(Nodes.LectureRoom.toString())) {
						//deleteLectureRoomDS();
						deleteLectureRoomRS();
						//deleteLectureRoomSP();
					}
					if(mySelectedNode().equals(Nodes.Possess.toString())) {
						//deletePossessDS();
						deletePossessRS();
						//deletePossessSP();
					}
					if(mySelectedNode().equals(Nodes.Student.toString())) {
						//deleteStudentDS();
						deleteStudentRS();
						//deleteStudentSP();
					}
					if(mySelectedNode().equals(Nodes.Take.toString())) {
						//deleteTakeDS();
						deleteTakeRS();
						//deleteTakeSP();
					}
				}
				else if ("print".equals(str)) {
					if(mySelectedNode().equals(Nodes.DiscountCoupon.toString())) {
						printDiscountCoupon();
					}
					if(mySelectedNode().equals(Nodes.Instructor.toString())) {
						printInstructor();
					}
					if(mySelectedNode().equals(Nodes.Lecture.toString())) {
						printLecture();
					}
					if(mySelectedNode().equals(Nodes.LectureRoom.toString())) {
						printLectureRoom();
					}
					if(mySelectedNode().equals(Nodes.Possess.toString())) {
						printPossess();
					}
					if(mySelectedNode().equals(Nodes.Student.toString())) {
						printStudent();
					}
					if(mySelectedNode().equals(Nodes.Take.toString())) {
						printTake();
					}
				}
				else if ("search".equals(str)) {
					/*
					if(mySelectedNode().equals(Nodes.DiscountCoupon.toString()))
					if(mySelectedNode().equals(Nodes.Instructor.toString()))
					if(mySelectedNode().equals(Nodes.Lecture.toString()))
					if(mySelectedNode().equals(Nodes.LectureRoom.toString()))
					if(mySelectedNode().equals(Nodes.Possess.toString()))
					if(mySelectedNode().equals(Nodes.Student.toString()))
					if(mySelectedNode().equals(Nodes.Take.toString()))
					*/
				}
				else
				{txtArea.appendText("Not an approperiate button");}
				
			});
		}
		
		
		hbox.getChildren().addAll(buttons);

		return hbox;
	}

	

private  TreeView<String> addNodestoTree() {
    	TreeView<String> tree = new TreeView<String>();
    	
    	TreeItem<String> root, tables, reports, exit, about;
    	
    		root = new TreeItem<String>("Lecture");
    		
    		tables = new TreeItem<String>("Tables");
    		//테이블(부모)에 자식 테이블 생성하기
    		makeChild(Nodes.DiscountCoupon.toString(), tables);
    		makeChild(Nodes.Instructor.toString(), tables);
    		makeChild(Nodes.Lecture.toString(), tables);
    		makeChild(Nodes.LectureRoom.toString(), tables);
    		makeChild(Nodes.Possess.toString(), tables);
    		makeChild(Nodes.Student.toString(), tables);
    		makeChild(Nodes.Take.toString(), tables);
    		//report 내에 자식 노드 만들기
    		reports = new TreeItem<String>("Reports");
    		makeChild(Nodes.Report01.toString(), reports);
    		makeChild(Nodes.Report02.toString(), reports);
    		makeChild(Nodes.Report03.toString(), reports);
    		
    		exit = new TreeItem<String>(Nodes.Exit.toString());
    		about=  new TreeItem<String>(Nodes.About.toString());
     		root.getChildren().addAll(tables,reports, exit, about);
    	    tree.setRoot(root);
         return tree;

    }	
    
	// Create child
	private TreeItem<String> makeChild(String title, TreeItem<String> parent) {
		TreeItem<String> item = new TreeItem<>(title);
		item.setExpanded(false);
		parent.getChildren().add(item);
		return item;
	}


	@Override
	public void start(Stage stage) {

		// Add controls and Layouts
		
		VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setMinSize(900, 500);
		vbox.setMaxSize(1200, 700);
		vbox.setPadding(new Insets(15, 15, 15, 15));
		vbox.setSpacing(10); // Gap between nodes
		vbox.setStyle("-fx-border-color: Black;");
		// Top Box
		HBox tbox=addTopPane();
		tbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(tbox);
		
		// Center box
		HBox cbox=addCenterPane();
		cbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(cbox);
			
		StackPane bbox=addBottomPane();
		bbox.prefHeightProperty().bind(vbox.prefWidthProperty());
		vbox.getChildren().add(bbox);

		//create and show stage 

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.setTitle("Lecture");
		stage.show();
	}
	
	// 모든 txt와 label 지움
	private void clearFields() {
		for (int i = 0; i < txt.length; i++) {
			txt[i].setText("");
			txt[i].setVisible(false);
			labels[i].setText("");
			labels[i].setVisible(false);
		}

	}
	// text 지우고, label은 지우지 않음
	private void clearTextFields() {
		int noc = table.getColumns().size();
		for (int i = 0; i < noc; i++) {
			txt[i].setText(" ");
		}
	}
	//테이블명 s를 받으면 다음 함수 처리 결과를 TableView에 보여줌
	private void rsToTableView(String s) {
		// 초기화
		table.getColumns().clear();
		for (int i = 0; i < table.getItems().size(); i++) {
			table.getItems().clear();
		}

		ObservableList data = FXCollections.observableArrayList();
		try {
			// 하나의 열을 가져와 string 타입으로 변환 후 저장
			String query = "select * from " + s + "";
			PreparedStatement pst = null;
			pst = con.prepareStatement(query); // 정적으로 prepareStatement 정의
			ResultSet rs = pst.executeQuery(); // Query 날림

			ResultSetMetaData rsmd = rs.getMetaData(); // Query 날린 결과 값으로 메타데이터 제공

			int colCount = rsmd.getColumnCount(); // 컬럼 개수 반환

			// get data rows

			for (int i = 0; i < colCount; i++) {

				int dataType = rsmd.getColumnType(i + 1);

				final int j = i;
				TableColumn col = new TableColumn(rsmd.getColumnName(i + 1));

				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				table.getColumns().addAll(col);
			}
			// 위에는 한 열씩 가져옴, 아래부터는 하나씩 한 행을 가져옴
			while (rs.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int k = 1; k <= colCount; k++) {
					String str1 = rs.getString(k);
					if (str1 == null) str1 = "null";
					row.add(str1);
				}
				data.add(row);
			}

			table.setItems(data);

			table.getSelectionModel().select(0); // 0번째 행부터 테이블 가져옴
			showFields();

		} catch (Exception e) {
			txtArea.appendText(e.getMessage());
		} finally {}
	}
	
	private String mySelectedNode() {
		TreeItem ti = tree.getSelectionModel().selectedItemProperty().getValue();
		return ti.getValue().toString();
	}
	
	private void showFields() {
		clearFields();
		int row=0;
		TablePosition pos = (TablePosition) table.getSelectionModel().getSelectedCells().get(0);
		System.out.println(table.getSelectionModel().getSelectedItem());
		row = pos.getRow();
		int cols = table.getColumns().size();

		for (int j = 0; j < cols; j++) {
			Object ch = ((TableColumnBase) table.getColumns().get(j)).getText();
			Object cell = ((TableColumnBase) table.getColumns().get(j)).getCellData(row).toString();

			if (cell == null) {
				txt[j].setText("");
			} else {
				txt[j].setText(cell.toString());
				txt[j].setVisible(true);
			}
			labels[j].setText(ch.toString());
			labels[j].setVisible(true);
		}
	}
	// insert(IS, RS, SP 중 하나를 고르는 것이 좋음, SP 추천)
	// insert record using insert into statement
	private void addDiscountCouponIS() {
		String sql = "insert into DiscountCoupon values(?,?,?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.setString(4, txt[3].getText());
			st.setString(5, txt[4].getText());
			st.executeUpdate();
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	// insert record using ResultSet functions
	private void addDiscountCouponRS() {
		String sql= "select * from DiscountCoupon";
		try {
			
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.insertRow();
			txtArea.appendText("record is added..");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	// insert record using Stored Procedures
	private void addDiscountCouponSP() {
		String sql="{call sp_addDiscountCoupon(?,?,?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	
	private void addInstructorIS() {
		String sql = "insert into Instructor values(?,?,?,?,?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.setString(4, txt[3].getText());
			st.setString(5, txt[4].getText());
			st.setString(6, txt[5].getText());
			st.setString(7, txt[6].getText());
			st.executeUpdate();
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addInstructorRS() {
		String sql= "select * from Instructor";
		try {
			
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.updateString(6, txt[5].getText());
			rs.updateString(7, txt[6].getText());
			rs.insertRow();
			txtArea.appendText("record is added..");
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addInstructorSP() {
		String sql="{call sp_addInstructor(?,?,?,?,?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			cst.setString(6, txt[5].getText());
			cst.setString(7, txt[6].getText());
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	
	private void addLectureIS() {
		String sql = "insert into Lecture values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.setString(4, txt[3].getText());
			st.setString(5, txt[4].getText());
			st.setString(6, txt[5].getText());
			st.setString(7, txt[6].getText());
			st.setString(8, txt[7].getText());
			st.setString(9, txt[8].getText());
			st.setString(10, txt[9].getText());
			st.setString(11, txt[10].getText());
			st.setString(12, txt[11].getText());
			st.setString(13, txt[12].getText());
			st.setString(14, txt[13].getText());
			st.executeUpdate();
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addLectureRS() {
		String sql= "select * from Lecture";
		try {
			
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.updateString(6, txt[5].getText());
			rs.updateString(7, txt[6].getText());
			rs.updateString(8, txt[7].getText());
			rs.updateString(9, txt[8].getText());
			rs.updateString(10, txt[9].getText());
			rs.updateString(11, txt[10].getText());
			rs.updateString(12, txt[11].getText());
			rs.updateString(13, txt[12].getText());
			rs.updateString(14, txt[13].getText());
			
			rs.insertRow();
			txtArea.appendText("record is added..");
			
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addLectureSP() {
		String sql="{call sp_addLecture(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			cst.setString(6, txt[5].getText());
			cst.setString(7, txt[6].getText());
			cst.setString(8, txt[7].getText());
			cst.setString(9, txt[8].getText());
			cst.setString(10, txt[9].getText());
			cst.setString(11, txt[10].getText());
			cst.setString(12, txt[11].getText());
			cst.setString(13, txt[12].getText());
			cst.setString(14, txt[13].getText());
			
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	
	private void addLectureRoomIS() {
		String sql = "insert into LectureRoom values(?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.executeUpdate();
			
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}	
	private void addLectureRoomRS() {
		String sql= "select * from LectureRoom";
		try {
				
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
				
			rs.insertRow();
			txtArea.appendText("record is added..");
				
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addLectureRoomSP() {
		String sql="{call sp_addLectureRoom(?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
				
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
		
	private void addPossessIS() {
		String sql = "insert into Possess values(?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.executeUpdate();
			
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}	
	private void addPossessRS() {
		String sql= "select * from Possess";
		try {
				
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
				
			rs.insertRow();
			txtArea.appendText("record is added..");
				
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addPossessSP() {
		String sql="{call sp_addPossess(?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
				
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void addStudentIS() {
		String sql = "insert into Student values(?,?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.setString(4, txt[3].getText());
			st.executeUpdate();
			
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addStudentRS() {
		String sql= "select * from Student";
		try {
			
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			
			rs.insertRow();
			txtArea.appendText("record is added..");
			
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addStudentSP() {
		String sql="{call sp_addStudent(?,?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void addTakeIS() {
		String sql = "insert into Take values(?,?,?)";
		try {
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.setString(2, txt[1].getText());
			st.setString(3, txt[2].getText());
			st.executeUpdate();
			
			txtArea.appendText("record is added...");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}	
	private void addTakeRS() {
		String sql= "select * from Take";
		try {
				
			PreparedStatement st= con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			rs.moveToInsertRow();
			rs.updateString(1, txt[0].getText());
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
				
			rs.insertRow();
			txtArea.appendText("record is added..");
				
			rs.close();
			st.close();
				
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void addTakeSP() {
		String sql="{call sp_addTake(?,?,?)}";
		try {
			CallableStatement cst= con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
				
			cst.execute();
			txtArea.appendText("record is added..");
			cst.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	
	// delete
	private void deleteDiscountCouponDS() {
		try {
			String sql="delete from DiscountCoupon where DC_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteDiscountCouponRS() {
		try {
			String sql="select * from DiscountCoupon";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteDiscountCouponSP() {
		try {
			String sql = "{call sp_deleteDiscountCoupon(?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}
	
	private void deleteInstructorDS() {
		try {
			String sql="delete from Instructor where I_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteInstructorRS() {
		try {
			String sql="select * from Instructor";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteInstructorSP() {
		try {
			String sql = "{call sp_deleteInstructor(?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	private void deleteLectureDS() {
		try {
			String sql="delete from Lecture where L_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteLectureRS() {
		try {
			String sql="select * from Lecture";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteLectureSP() {
		try {
			String sql = "{call sp_deleteLecture(?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	private void deleteLectureRoomDS() {
		try {
			String sql="delete from LectureRoom where LR_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteLectureRoomRS() {
		try {
			String sql="select * from LectureRoom";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteLectureRoomSP() {
		try {
			String sql = "{call sp_deleteLectureRoom(?)}";
			System.out.println(txt[0].getText());
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	private void deletePossessDS() {
		try {
			String sql="delete from Possess where DC_id=? and S_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deletePossessRS() {
		try {
			String sql="select * from Possess";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deletePossessSP() {
		try {
			String sql = "{call sp_deletePossess(?,?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	private void deleteStudentDS() {
		try {
			String sql="delete from Student where S_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteStudentRS() {
		try {
			String sql="select * from Student";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteStudentSP() {
		try {
			String sql = "{call sp_deleteStudent(?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	private void deleteTakeDS() {
		try {
			String sql="delete from Take where S_id=? and L_id=?";
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, txt[0].getText());
			st.executeUpdate();
			txtArea.appendText("Record is deleted..\n");
			st.close();
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteTakeRS() {
		try {
			String sql="select * from Take";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.deleteRow();
			rs.first();
			
			txtArea.appendText("Record is deleted..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void deleteTakeSP() {
		try {
			String sql = "{call sp_deleteTake(?,?)}";
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.execute();
			txtArea.appendText("Record is deleted..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
		
	}

	
	// update
	private void updateDiscountCouponUS() {
		try {
			String sql="update DiscountCoupon set Discount_rate=?, Validity_date=?, L_id=?, Registration_date=? where DC_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[1].getText());
			st.setString(2, txt[2].getText());
			st.setString(3, txt[3].getText());
			st.setString(4, txt[4].getText());
			st.setString(5, txt[0].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateDiscountCouponRS() {
		try {
			String sql="select * from DiscountCoupon";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateDiscountCouponSP() {
		try {
			String sql="{call sp_updateDiscountCoupon(?,?,?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	
	private void updateInstructorUS() {
		try {
			String sql="update Instructor set I_name=?, I_office_num=?, I_phone=?, I_email=?, I_career=?, I_field=? where I_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[1].getText());
			st.setString(2, txt[2].getText());
			st.setString(3, txt[3].getText());
			st.setString(4, txt[4].getText());
			st.setString(5, txt[5].getText());
			st.setString(6, txt[6].getText());
			st.setString(7, txt[0].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateInstructorRS() {
		try {
			String sql="select * from Instructor";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.updateString(6, txt[5].getText());
			rs.updateString(7, txt[6].getText());
			
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateInstructorSP() {
		try {
			String sql="{call sp_updateInstructor(?,?,?,?,?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			cst.setString(6, txt[5].getText());
			cst.setString(7, txt[6].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void updateLectureUS() {
		try {
			String sql="update Lecture set L_name=?, L_fee=?, L_startdate=?, L_enddate=?, L_length=?, L_date=?, L_starttime=?, L_endtime=?, L_numberofstudents=?, L_contents=?, I_id=?, LR_id=?, SetUp_date=? where L_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[1].getText());
			st.setString(2, txt[2].getText());
			st.setString(3, txt[3].getText());
			st.setString(4, txt[4].getText());
			st.setString(5, txt[5].getText());
			st.setString(6, txt[6].getText());
			st.setString(7, txt[7].getText());
			st.setString(8, txt[8].getText());
			st.setString(9, txt[9].getText());
			st.setString(10, txt[10].getText());
			st.setString(11, txt[11].getText());
			st.setString(12, txt[12].getText());
			st.setString(13, txt[13].getText());
			st.setString(14, txt[0].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateLectureRS() {
		try {
			String sql="select * from Lecture";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateString(5, txt[4].getText());
			rs.updateString(6, txt[5].getText());
			rs.updateString(7, txt[6].getText());
			rs.updateString(8, txt[7].getText());
			rs.updateString(9, txt[8].getText());
			rs.updateString(10, txt[9].getText());
			rs.updateString(11, txt[10].getText());
			rs.updateString(12, txt[11].getText());
			rs.updateString(13, txt[12].getText());
			rs.updateString(14, txt[13].getText());
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateLectureSP() {
		try {
			String sql="{call sp_updateLecture(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			cst.setString(5, txt[4].getText());
			cst.setString(6, txt[5].getText());
			cst.setString(7, txt[6].getText());
			cst.setString(8, txt[7].getText());
			cst.setString(9, txt[8].getText());
			cst.setString(10, txt[9].getText());
			cst.setString(11, txt[10].getText());
			cst.setString(12, txt[11].getText());
			cst.setString(13, txt[12].getText());
			cst.setString(14, txt[13].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void updateLectureRoomUS() {
		try {
			String sql="update LectureRoom set LR_location=?, LR_maximumstudent=?, where LR_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[1].getText());
			st.setString(2, txt[2].getText());
			st.setString(3, txt[0].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateLectureRoomRS() {
		try {
			String sql="select * from LectureRoom";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			txtArea.appendText(rownum + " is selected .. \n");
			
			rs.absolute(rownum+1);
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateRow();
			rs.first();

			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateLectureRoomSP() {
		try {
			String sql="{call sp_updateLectureRoom(?, ?, ?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void updatePossessUS() {
		try {
			String sql="update Possess set Possession_date=? where DC_id=? and S_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[2].getText());
			st.setString(2, txt[0].getText());
			st.setString(3, txt[1].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updatePossessRS() {
		try {
			String sql="select * from Possess";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+2);
			rs.updateString(3, txt[2].getText());
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updatePossessSP() {
		try {
			String sql="{call sp_updatePossess(?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void updateStudentUS() {
		try {
			String sql="update Student set S_name=?, S_dateofbirth=?, S_phone=? where S_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[1].getText());
			st.setString(2, txt[2].getText());
			st.setString(3, txt[3].getText());
			st.setString(4, txt[0].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateStudentRS() {
		try {
			String sql="select * from Student";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+1);
			rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateString(4, txt[3].getText());
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateStudentSP() {
		try {
			String sql="{call sp_updateStudent(?,?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			cst.setString(4, txt[3].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	
	private void updateTakeUS() {
		try {
			String sql="update Take set take_date=? where S_id=? and L_id=?";
			PreparedStatement st= con.prepareStatement(sql);
			
			st.setString(1, txt[2].getText());
			st.setString(2, txt[0].getText());
			st.setString(3, txt[1].getText());
			
			st.executeUpdate();
			txtArea.appendText("Record is updated..\n");
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateTakeRS() {
		try {
			String sql="select * from Take";
			PreparedStatement st= con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery();
			
			TablePosition pos=(TablePosition) table.getSelectionModel().getSelectedCells().get(0);
			int rownum=pos.getRow();
			
			rs.absolute(rownum+2);
			//rs.updateString(1, txt[0].getText());
			//rs.updateString(2, txt[1].getText());
			rs.updateString(3, txt[2].getText());
			rs.updateRow();
			
			txtArea.appendText("Record is updated..\n");
			rs.close();
			st.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}
	private void updateTakeSP() {
		try {
			String sql="{call sp_updateTake(?,?,?)}";
			
			CallableStatement cst = con.prepareCall(sql);
			cst.setString(1, txt[0].getText());
			cst.setString(2, txt[1].getText());
			cst.setString(3, txt[2].getText());
			
			cst.execute();
			txtArea.appendText("Record is updated..\n");
			cst.close();
			
		}
		catch(Exception e) {txtArea.appendText(e.getMessage()+"\n" );}
		finally {}
	}

	private void printDiscountCoupon() {
		String filename ="reportDiscountCoupon.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}

	private void printInstructor() {
		String filename ="reportInstructor.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}
	
	private void printLecture() {
		String filename ="reportLecture.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}
	
	private void printLectureRoom() {
		String filename ="reportLectureRoom.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}
	
	private void printPossess() {
		String filename ="reportPossess.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}
	
	private void printStudent() {
		String filename ="reportStudent.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}
	
	private void printTake() {
		String filename ="reportTake.jrxml";
		try {
			
			JasperReport jr = JasperCompileManager.compileReport(filename);
			JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
			net.sf.jasperreports.view.JasperViewer.viewReport(jp,false);
		
			//((net.sf.jasperreports.view.JasperViewer) JasperViewer).viewReport(jp,false);
		}
		catch(Exception e) {
			txtArea.appendText("It has any problem...");
			txtArea.appendText(e.getMessage());
		}
		finally {}
	}

	public static void main(String[] args) {
		launch(args);
	}
	

}