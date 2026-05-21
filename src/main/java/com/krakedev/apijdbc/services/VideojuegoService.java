package com.krakedev.apijdbc.services;

import com.krakedev.apijdbc.entidades.Videojuego;
import com.krakedev.apijdbc.jdbc.VideojuegoJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideojuegoService {

    private static final Logger log = LoggerFactory.getLogger(VideojuegoService.class);

    private final VideojuegoJdbc videojuegoJdbc = new VideojuegoJdbc();

    public boolean crear(Videojuego videojuego) {
        log.info("Creando videojuego con código: {}", videojuego.getCodigo());
        return videojuegoJdbc.insertar(videojuego);
    }

    public List<Videojuego> listarTodos() {
        log.info("Listando todos los videojuegos");
        return videojuegoJdbc.listar();
    }

    public Videojuego buscarPorCodigo(String codigo) {
        log.info("Buscando videojuego con código: {}", codigo);
        return videojuegoJdbc.buscar(codigo);
    }

    public void actualizar(String codigo, String nombre, String plataforma,
                           double precio, boolean disponible, String genero) {
        log.info("Actualizando videojuego con código: {}", codigo);
        videojuegoJdbc.actualizar(codigo, nombre, plataforma, precio, disponible, genero);
    }

    public void eliminar(String codigo) {
        log.info("Eliminando videojuego con código: {}", codigo);
        videojuegoJdbc.eliminar(codigo);
    }
}