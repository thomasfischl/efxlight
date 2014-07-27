package efx.test.templateeditor;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public abstract class TemplateEditorControl extends AnchorPane {

	@FXML
	private ListView lbSearchQueryList;
	@FXML
	private Button btnSearchQueryTest;
	@FXML
	private Button btnNameQueryTest;
	@FXML
	private TextField tfName;
	@FXML
	private TextField tfNameQueryTextQuery;
	@FXML
	private Button btnSearchQueryUp;
	@FXML
	private Button btnSearchQueryDown;
	@FXML
	private ListView lvNameQueryList;
	@FXML
	private Button btnNameQueryUp;
	@FXML
	private TextField tfSearchQueryQuery;
	@FXML
	private Button btnSearchQueryAdd;
	@FXML
	private Button btnSearchQueryRemove;
	@FXML
	private Button btnNameQueryRemove;
	@FXML
	private TextField tfNameQueryNodeQuery;
	@FXML
	private Button btnNameQueryAdd;
	@FXML
	private Button btnNameQueryDown;

	@FXML
	private void onNameQueryTest(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onSearchQueryTest(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onNameQueryDown(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onNameQueryUp(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onNameQueryRemove(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onNameQueryAdd(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onSearchQueryDown(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onSearchQueryUp(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onSearchQueryRemove(ActionEvent e) {
		//TODO implement me 
	}

	@FXML
	private void onSearchQueryAdd(ActionEvent e) {
		//TODO implement me 
	}

	public TemplateEditorControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"TemplateEditorControl.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
