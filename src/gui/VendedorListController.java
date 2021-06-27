package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Vendedor;
import model.services.VendedorService;

public class VendedorListController implements Initializable, DataChangeListener {
	private VendedorService service;

	@FXML
	private TableView<Vendedor> tableViewVendedor;

	@FXML
	private TableColumn<Vendedor, Integer> tableColumnId;

	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;
	
	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Vendedor, Date> tableColumnAniversario;
	
	@FXML
	private TableColumn<Vendedor, Double> tableColumnSalarioBase;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEditar;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnExcluir;

	@FXML
	private Button btNovo;

	private ObservableList<Vendedor> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage stagePai = Utils.stageAtual(event);
		Vendedor vendedor = new Vendedor();
		criarFormDialogo(vendedor, "/gui/VendedorForm.fxml", stagePai);
	}

	public void setVendedorService(VendedorService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnAniversario.setCellValueFactory(new PropertyValueFactory<>("aniversario"));
		Utils.formatTableColumnDate(tableColumnAniversario, "dd/MM/yyyy");
		tableColumnSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatTableColumnDouble(tableColumnSalarioBase, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}

		List<Vendedor> lstVendedor = service.buscarTudo();
		obsList = FXCollections.observableArrayList(lstVendedor);
		tableViewVendedor.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void criarFormDialogo(Vendedor departamento, String nomeAbsoluto, Stage stagePai) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
//			Pane pane = loader.load();
//
//			VendedorFormController controller = loader.getController();
//			controller.setVendedor(departamento);
//			controller.setVendedorService(new VendedorService());
//			controller.inscreverDataChangeListener(this);
//			controller.atualizarDadosForm();
//
//			Stage stageDialogo = new Stage();
//			stageDialogo.setTitle("Informe os dados do departamento");
//			stageDialogo.setScene(new Scene(pane));
//			stageDialogo.setResizable(false);
//			stageDialogo.initOwner(stagePai);
//			stageDialogo.initModality(Modality.WINDOW_MODAL);
//			stageDialogo.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Erro ao ler a view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarFormDialogo(obj, "/gui/VendedorForm.fxml", Utils.stageAtual(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnExcluir.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnExcluir.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Excluir");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> excluirEntidade(obj));
			}
		});
	}

	private void excluirEntidade(Vendedor departamento) {
		Optional<ButtonType> resultado = Alerts.exibirConfirmacao("Confirmação", "Tem certeza que deseja excluir?!\nEssa ação não poderá ser desfeita!");
		
		if (resultado.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço está nulo");
			}
			
			try {
				service.excluir(departamento);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
