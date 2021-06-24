package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {
	
	public List<Departamento> buscarTudo(){
		List<Departamento> lstDepartamento = new ArrayList<>();
		
		lstDepartamento.add(new Departamento(1, "Livros"));
		lstDepartamento.add(new Departamento(2, "Computadores"));
		lstDepartamento.add(new Departamento(3, "Eletrônicos"));
		
		return lstDepartamento;
	}
}
