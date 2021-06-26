package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Departamento;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {
	private Departamento entidade;
	private DepartamentoService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private Label lblErroNome;
	
	@FXML
	private Button btnSalvar;
	
	@FXML
	private Button btnCancelar;
	
	public void setDepartamento(Departamento entidade) {
		this.entidade = entidade;
	}
	
	public void setDepartamentoService(DepartamentoService service) {
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

	private Departamento getFormData() {
		Departamento departamento = new Departamento();
		
		ValidationException excecao = new ValidationException("Erro de validação");
		
		departamento.setId(Utils.tentarConverterParaInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.addError("nome", "O campo não pode ser vazio");
		}
		
		departamento.setNome(txtNome.getText());
		
		if (excecao.getErrors().size() > 0) {
			throw excecao;
		}
		
		return departamento;
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
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	
	public void atualizarDadosForm() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula");
		}
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
	
	private void setMsgErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if (campos.contains("nome")) {
			lblErroNome.setText(erros.get("nome"));
		}
	}
} 
