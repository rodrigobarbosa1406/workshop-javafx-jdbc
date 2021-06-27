package model.services;

import java.util.List;

import model.dao.DepartamentoDao;
import model.dao.FabricaDao;
import model.entities.Departamento;

public class DepartamentoService {
	private DepartamentoDao departamentoDao = FabricaDao.criarDepartamentoDao();
	
	public List<Departamento> buscarTudo(){
		return departamentoDao.buscarTudo();
	}
	
	public void salvarOuAtualizar(Departamento departamento) {
		if (departamento.getId() == null) {
			departamentoDao.incluir(departamento);
		} else {
			departamentoDao.atualizar(departamento);
		}
	}
	
	public void excluir(Departamento departamento) {
		departamentoDao.excluirPorId(departamento.getId());
	}
}
