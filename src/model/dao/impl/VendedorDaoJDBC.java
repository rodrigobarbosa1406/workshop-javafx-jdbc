package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {

	private Connection conn;

	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void incluir(Vendedor obj) {
		PreparedStatement stIncluir = null;
		
		try {
			stIncluir = conn.prepareStatement(
						"INSERT INTO seller "
						+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
						+ "VALUES "
						+ "(?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
			
			stIncluir.setString(1, obj.getNome());
			stIncluir.setString(2, obj.getEmail());
			stIncluir.setDate(3, new java.sql.Date(obj.getAniversario().getTime()));
			stIncluir.setDouble(4, obj.getSalarioBase());
			stIncluir.setInt(5, obj.getDepartamento().getId());
			
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
	public void atualizar(Vendedor obj) {
		PreparedStatement stAtualizar = null;
		
		try {
			stAtualizar = conn.prepareStatement(
						"UPDATE seller "
						+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " 
						+ "WHERE Id = ?");
			
			stAtualizar.setString(1, obj.getNome());
			stAtualizar.setString(2, obj.getEmail());
			stAtualizar.setDate(3, new java.sql.Date(obj.getAniversario().getTime()));
			stAtualizar.setDouble(4, obj.getSalarioBase());
			stAtualizar.setInt(5, obj.getDepartamento().getId());
			stAtualizar.setInt(6, obj.getId());
			
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
			stExcluir = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
			stExcluir.setInt(1, id);
			
			stExcluir.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stExcluir);
		}
	}

	@Override
	public Vendedor buscarPorId(Integer id) {
		PreparedStatement stBuscaPorId = null;
		ResultSet rsBuscaPorId = null;
		
		try {
			stBuscaPorId = conn.prepareStatement(
						"SELECT seller.*, department.Name as DepName "
								+ "FROM seller "
								+ "INNER JOIN department "
								+ "ON seller.DepartmentId = department.Id "
								+ "WHERE seller.id = ?");
			
			stBuscaPorId.setInt(1, id);
			rsBuscaPorId = stBuscaPorId.executeQuery();
			
			if (rsBuscaPorId.next()) {
				return instanciarVendedor(rsBuscaPorId, instanciarDepartamento(rsBuscaPorId));
			}
			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stBuscaPorId);
			DB.closeResultSet(rsBuscaPorId);
		}
	}

	private Vendedor instanciarVendedor(ResultSet rsBuscaPorId, Departamento departamento) throws SQLException {
		return new Vendedor(rsBuscaPorId.getInt("Id"), rsBuscaPorId.getString("Name"), rsBuscaPorId.getString("Email"), rsBuscaPorId.getDate("BirthDate"), rsBuscaPorId.getDouble("BaseSalary"), departamento);
	}

	private Departamento instanciarDepartamento(ResultSet rsBuscaPorId) throws SQLException {
		return new Departamento(rsBuscaPorId.getInt("DepartmentId"), rsBuscaPorId.getString("DepName"));
	}

	@Override
	public List<Vendedor> buscarTudo() {
		PreparedStatement stBuscaTudo = null;
		ResultSet rsBuscaTudo = null;
		
		try {
			stBuscaTudo = conn.prepareStatement(
						"SELECT seller.*,department.Name as DepName "
								+ "FROM seller "
								+ "INNER JOIN department "
								+ "ON seller.DepartmentId = department.Id "
								+ "ORDER BY Name");
			
			rsBuscaTudo = stBuscaTudo.executeQuery();
			
			List<Vendedor> lstVendedor = new ArrayList<>();
			Map<Integer, Departamento> mapDepto = new HashMap<>();
			
			while (rsBuscaTudo.next()) {
				Departamento deptoMap = mapDepto.get(rsBuscaTudo.getInt("DepartmentId"));
				
				if (deptoMap == null) {
					deptoMap = instanciarDepartamento(rsBuscaTudo);
					mapDepto.put(rsBuscaTudo.getInt("DepartmentId"), deptoMap);
				}
				
				lstVendedor.add(instanciarVendedor(rsBuscaTudo, deptoMap));
			}
			
			return lstVendedor;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stBuscaTudo);
			DB.closeResultSet(rsBuscaTudo);
		}
	}

	@Override
	public List<Vendedor> buscarPorDepartamento(Departamento departamento) {
		PreparedStatement stBuscaPorDepto = null;
		ResultSet rsBuscaPorDepto = null;
		
		try {
			stBuscaPorDepto = conn.prepareStatement(
						"SELECT seller.*,department.Name as DepName "
								+ "FROM seller "
								+ "INNER JOIN department "
								+ "ON seller.DepartmentId = department.Id "
								+ "WHERE DepartmentId = ? "
								+ "ORDER BY Name");
			
			stBuscaPorDepto.setInt(1, departamento.getId());
			rsBuscaPorDepto = stBuscaPorDepto.executeQuery();
			
			List<Vendedor> lstVendedor = new ArrayList<>();
			Map<Integer, Departamento> mapDepto = new HashMap<>();
			
			while (rsBuscaPorDepto.next()) {
				Departamento deptoMap = mapDepto.get(rsBuscaPorDepto.getInt("DepartmentId"));
				
				if (deptoMap == null) {
					deptoMap = instanciarDepartamento(rsBuscaPorDepto);
					mapDepto.put(rsBuscaPorDepto.getInt("DepartmentId"), deptoMap);
				}
				
				lstVendedor.add(instanciarVendedor(rsBuscaPorDepto, deptoMap));
			}
			
			return lstVendedor;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(stBuscaPorDepto);
			DB.closeResultSet(rsBuscaPorDepto);
		}
	}

}
