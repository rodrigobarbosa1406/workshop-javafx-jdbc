package model.services;

import java.util.List;

import model.dao.FabricaDao;
import model.dao.VendedorDao;
import model.entities.Vendedor;

public class VendedorService {
	private VendedorDao vendedorDao = FabricaDao.criarVendedorDao();
	
	public List<Vendedor> buscarTudo(){
		return vendedorDao.buscarTudo();
	}
	
	public void salvarOuAtualizar(Vendedor vendedor) {
		if (vendedor.getId() == null) {
			vendedorDao.incluir(vendedor);
		} else {
			vendedorDao.atualizar(vendedor);
		}
	}
	
	public void excluir(Vendedor vendedor) {
		vendedorDao.excluirPorId(vendedor.getId());
	}
}
