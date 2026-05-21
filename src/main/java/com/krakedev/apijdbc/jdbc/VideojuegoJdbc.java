package com.krakedev.apijdbc.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krakedev.apijdbc.entidades.Videojuego;
import com.krakedev.apijdbc.excepciones.VideojuegoDuplicadoExcepcion;
import com.krakedev.apijdbc.excepciones.VideojuegoNoEncontradoExcepcion;


public class VideojuegoJdbc {
	
	private static final Logger log = LoggerFactory.getLogger(VideojuegoJdbc.class);
	
	 private static final String SQL_INSERT =
		        "INSERT INTO videojuegos (codigo,nombre,plataforma,precio,disponible,genero) " +
		        "VALUES (?, ?, ?, ?, ?, ?)";
	 private static final String SQL_LISTAR = "SELECT * FROM videojuegos";
	 private static final String SQL_BUSCAR= "SELECT * FROM videojuegos WHERE codigo = ?";
	 private static final String SQL_UPDATE =
		        "UPDATE videojuegos SET nombre=?, plataforma=?, precio=?, disponible=?, genero=? " +
		        "WHERE codigo=?";
	 private static final String SQL_DELETE = "DELETE FROM videojuegos WHERE codigo = ?";
	 
	 private void cerrar(PreparedStatement ps, Connection conn) {
	        try { if (ps   != null) ps.close();   } catch (SQLException e) { log.error(e.getMessage()); }
	        try { if (conn != null) conn.close();  } catch (SQLException e) { log.error(e.getMessage()); }
	    }
	 
	 //INSERTAR
	 public boolean insertar(Videojuego v) {
	        Connection         conn = null;
	        PreparedStatement  ps   = null;

	        try {
	        	
	        	Videojuego vExistente = buscar(v.getCodigo());
	        	
	        	if(vExistente == null) {
	        		
	        		conn = Conexion.getConexion();
		            ps   = conn.prepareStatement(SQL_INSERT);

		            ps.setString (1, v.getCodigo());
		            ps.setString (2, v.getNombre());
		            ps.setString (3, v.getPlataforma());
		            ps.setDouble (4, v.getPrecio());
		            ps.setBoolean(5, v.isDisponible());
		            ps.setString(6, v.getGenero());

		            int filas = ps.executeUpdate();
		            log.info("Videojuego insertado. Filas afectadas: {}", filas);
	        	
		            return filas > 0 ? true : false;
		            
	        	}else {
	        		throw new VideojuegoDuplicadoExcepcion("Videojuego con ese código ya existe");
	        	}   
	        } catch (SQLException e) {
	            log.error("Error al insertar videojuego: {}", e.getMessage());
	            throw new VideojuegoDuplicadoExcepcion("No se puede instertar el videojuego");
	        } finally {
	            cerrar(ps, conn);
	        }
	    }

	 //LISTAR
	 public List<Videojuego> listar() {
	        List<Videojuego>    lista = new ArrayList<>();
	        Connection        conn  = null;
	        PreparedStatement ps    = null;
	        ResultSet         rs    = null;

	        try {
	            conn = Conexion.getConexion();
	            ps   = conn.prepareStatement(SQL_LISTAR);
	            rs   = ps.executeQuery();

	            while (rs.next()) {
	            	Videojuego v = new Videojuego(
	                    rs.getString ("codigo"),
	                    rs.getString ("nombre"),
	                    rs.getString ("plataforma"),
	                    rs.getDouble ("precio"),
	                    rs.getBoolean("disponible"),
	                    rs.getString("genero")
	                );
	                lista.add(v);
	                System.out.println(v);
	            }
	            
	            if(lista.isEmpty()) {
	            	throw new VideojuegoNoEncontradoExcepcion("No existe ningun videojuego");
	            }
	            
	            log.info("Total de videojuegos listados: {}", lista.size());

	        } catch (SQLException e) {
	            log.error("Error al listar videojuegos: {}", e.getMessage());
	            throw new VideojuegoNoEncontradoExcepcion("Error general al listar los videojuegos existentes");
	        } finally {
	            try { if (rs   != null) rs.close();   } catch (SQLException e) { log.error(e.getMessage()); }
	            try { if (ps   != null) ps.close();   } catch (SQLException e) { log.error(e.getMessage()); }
	            try { if (conn != null) conn.close();  } catch (SQLException e) { log.error(e.getMessage()); }
	        }
	        return lista;
	    }
	 
	 //BUSCAR
	 public Videojuego buscar(String codigo) {
	        Connection        conn = null;
	        PreparedStatement ps   = null;
	        ResultSet         rs   = null;
	        Videojuego          v    = null;

	        try {
	            conn = Conexion.getConexion();
	            ps   = conn.prepareStatement(SQL_BUSCAR);
	            ps.setString(1, codigo);
	            rs   = ps.executeQuery();

	            if (rs.next()) {
	                v = new Videojuego(
	                		rs.getString ("codigo"),
		                    rs.getString ("nombre"),
		                    rs.getString ("plataforma"),
		                    rs.getDouble ("precio"),
		                    rs.getBoolean("disponible"),
		                    rs.getString("genero")
	                );
	                log.info("Videojuego encontrado con código: {}", codigo);
	            } else {
	                log.warn("No se encontró ningún videojuego con código: {}", codigo);
	            }

	        } catch (SQLException e) {
	            log.error("Error al buscar videojuego por código: {}", e.getMessage());
	        } finally {
	            try { if (rs   != null) rs.close();   } catch (SQLException e) { log.error(e.getMessage()); }
	            try { if (ps   != null) ps.close();   } catch (SQLException e) { log.error(e.getMessage()); }
	            try { if (conn != null) conn.close();  } catch (SQLException e) { log.error(e.getMessage()); }
	        }
	        return v;
	    }
	 
	 //ACTUALIZAR
	 public void actualizar(String codigo, String nombre, String plataforma,
              double precio, boolean disponible,String genero) {
		 Connection        conn = null;
		 PreparedStatement ps   = null;

		 try {
			 conn = Conexion.getConexion();
			 ps   = conn.prepareStatement(SQL_UPDATE);

			 ps.setString (1, nombre);
			 ps.setString (2, plataforma);
			 ps.setDouble (3, precio);
			 ps.setBoolean(4, disponible);
			 ps.setString(5, genero);
			 ps.setString (6, codigo);   // WHERE

			 int filas = ps.executeUpdate();
			 log.info("Videojuego actualizado. Filas afectadas: {}", filas);

		 } catch (SQLException e) {
			 log.error("Error al actualizar Videojuego: {}", e.getMessage());
		 } finally {
			 try { if (ps   != null) ps.close();  } catch (SQLException e) { log.error(e.getMessage()); }
			 try { if (conn != null) conn.close(); } catch (SQLException e) { log.error(e.getMessage()); }
		 }
	 }
	 
	 //ELIMINAR
	 public void eliminar(String codigo) {
	        Connection        conn = null;
	        PreparedStatement ps   = null;

	        try {
	            conn = Conexion.getConexion();
	            ps   = conn.prepareStatement(SQL_DELETE);
	            ps.setString(1, codigo);

	            int filas = ps.executeUpdate();
	            log.info("Videojuego eliminado. Filas afectadas: {}", filas);

	        } catch (SQLException e) {
	            log.error("Error al eliminar vehículo: {}", e.getMessage());
	        } finally {
	            try { if (ps   != null) ps.close();  } catch (SQLException e) { log.error(e.getMessage()); }
	            try { if (conn != null) conn.close(); } catch (SQLException e) { log.error(e.getMessage()); }
	        }
	    }
}
