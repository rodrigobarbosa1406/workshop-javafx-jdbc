package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {
	private Vendedor entidade;
	private VendedorService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpAniversario;
	
	@FXML
	private TextField txtSalarioBase;
	
	@FXML
	private Label lblErroNome;

	@FXML
	private Label lblErroEmail;
	
	@FXML
	private Label lblErroAniversario;
	
	@FXML
	private Label lblErroSalarioBase;
	
	@FXML
	private Button btnSalvar;
	
	@FXML
	private Button btnCancelar;
	
	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}
	
	public void setVendedorService(VendedorService service) {
		this.service = service;
	}
	
	public void inscreverDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		
		if (service == null) {
			throw new IllegalStateException("Service está nulo");
		}
		
		try {
			entidade = getFormData();
			service.salvarOuAtualizar(entidade);
			notificarDataChangeListeners();
			Utils.stageAtual(event).close();
		} catch (ValidationException e) {
			setMsgErro(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Erro ao salvar o objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notificarDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Vendedor getFormData() {
		Vendedor vendedor = new Vendedor();
		
		ValidationException excecao = new ValidationException("Erro de validação");
		
		vendedor.setId(Utils.tentarConverterParaInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.addError("nome", "O campo não pode ser vazio");
		}
		
		vendedor.setNome(txtNome.getText());
		
		if (excecao.getErrors().size() > 0) {
			throw excecao;
		}
		
		return vendedor;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		System.out.println("onBtCancelarAction");
		Utils.stageAtual(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpAniversario, "dd/MM/yyyy");
	}
	
	public void atualizarDadosForm() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		
		Locale.setDefault(Locale.US);
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));
		
		if (entidade.getAniversario() != null) {
			dpAniversario.setValue(LocalDate.ofInstant(entidade.getAniversario().toInstant(), ZoneId.systemDefault()));
		}
	}
	
	private void setMsgErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if (campos.contains("nome")) {
			lblErroNome.setText(erros.get("nome"));
		}
	}
} 
