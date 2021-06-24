package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {

	private Connection conn;

	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void incluir(Departamento obj) {
		PreparedStatement stIncluir = null;
		
		try {
			stIncluir = conn.prepareStatement(
						"INSERT INTO department "
						+ "(Name) "
						+ "VALUES "
						+ "(?)",
						Statement.RETURN_GENERATED_KEYS);
			
			stIncluir.setString(1, obj.getNome());
			
			int rowsAffected = stIncluir.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rsIncluir = stIncluir.getGeneratedKeys(); 
				
				if (rsIncluir.next()) {
					obj.setId(rsIncluir.getInt(1));
				}
				
				DB.closeResultSet(rsIncluir);
			} else {
				throw new DbException("Erro inesperado! Nenhuma linha afetada!");
			}			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stIncluir);
		}
	}

	@Override
	public void atualizar(Departamento obj) {
		PreparedStatement stAtualizar = null;
		
		try {
			stAtualizar = conn.prepareStatement(
						"UPDATE department "
						+ "SET Name = ? " 
						+ "WHERE Id = ?");
			
			stAtualizar.setString(1, obj.getNome());
			stAtualizar.setInt(2, obj.getId());
			
			stAtualizar.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stAtualizar);
		}
	}

	@Override
	public void excluirPorId(Integer id) {
		PreparedStatement stExcluir = null;
		
		try {
			stExcluir = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			stExcluir.setInt(1, id);
			
			stExcluir.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stExcluir);
		}
	}

	@Override
	public Departamento buscarPorId(Integer id) {
		PreparedStatement stBuscaPorId = null;
		ResultSet rsBuscaPorId = null;
		
		try {
			stBuscaPorId = conn.prepareStatement("SELECT * FROM department WHERE id = ?");
			
			stBuscaPorId.setInt(1, id);
			rsBuscaPorId = stBuscaPorId.executeQuery();
			
			if (rsBuscaPorId.next()) {
				return instanciarDepartamento(rsBuscaPorId);
			}
			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stBuscaPorId);
			DB.closeResultSet(rsBuscaPorId);
		}
	}

	private Departamento instanciarDepartamento(ResultSet rsBuscaPorId) throws SQLException {
		return new Departamento(rsBuscaPorId.getInt("id"), rsBuscaPorId.getString("Name"));
	}

	@Override
	public List<Departamento> buscarTudo() {
		PreparedStatement stBuscaTudo = null;
		ResultSet rsBuscaTudo = null;
		
		try {
			stBuscaTudo = conn.prepareStatement("SELECT * FROM department ORDER BY Name");
			
			rsBuscaTudo = stBuscaTudo.executeQuery();
			
			List<Departamento> lstDepartamento = new ArrayList<>();
			
			while (rsBuscaTudo.next()) {
				lstDepartamento.add(instanciarDepartamento(rsBuscaTudo));
			}
			
			return lstDepartamento;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stBuscaTudo);
			DB.closeResultSet(rsBuscaTudo);
		}
	}
}
