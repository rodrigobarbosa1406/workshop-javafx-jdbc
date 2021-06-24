package model.dao;

import java.util.List;

import model.entities.Departamento;
import model.entities.Vendedor;

public interface VendedorDao {
	void incluir(Vendedor obj);
	void atualizar(Vendedor obj);
	void excluirPorId(Integer id);
	Vendedor buscarPorId(Integer id);
	List<Vendedor> buscarTudo();
	List<Vendedor> buscarPorDepartamento(Departamento departamento);
}
