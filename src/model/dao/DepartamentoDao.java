package model.dao;

import java.util.List;

import model.entities.Departamento;

public interface DepartamentoDao {
	void incluir(Departamento obj);
	void atualizar(Departamento obj);
	void excluirPorId(Integer id);
	Departamento buscarPorId(Integer id);
	List<Departamento> buscarTudo();
}