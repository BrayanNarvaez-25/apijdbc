package com.krakedev.apijdbc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.apijdbc.entidades.Videojuego;
import com.krakedev.apijdbc.excepciones.VideojuegoDuplicadoExcepcion;
import com.krakedev.apijdbc.excepciones.VideojuegoNoEncontradoExcepcion;
import com.krakedev.apijdbc.services.VideojuegoService;

@RestController
@RequestMapping("/api/videojuegos")
public class VideojuegoController {

    private static final Logger log = LoggerFactory.getLogger(VideojuegoController.class);

    @Autowired
    private VideojuegoService videojuegoService;

    // ── POST /api/videojuegos ──────────────────────────────────────
    @PostMapping
    public ResponseEntity<String> crear(@RequestBody Videojuego videojuego) {
        log.info("POST - Crear videojuego: {}", videojuego.getCodigo());
        try {
            boolean creado = videojuegoService.crear(videojuego);
            if (creado) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                     .body("Videojuego creado correctamente: " + videojuego.getCodigo());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("No se pudo crear el videojuego");
            }
        } catch (VideojuegoDuplicadoExcepcion e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // ── GET /api/videojuegos ───────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        log.info("GET - Listar todos los videojuegos");
        try {
            return ResponseEntity.ok(videojuegoService.listarTodos());
        } catch (VideojuegoNoEncontradoExcepcion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── GET /api/videojuegos/{codigo} ──────────────────────────────
    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        log.info("GET - Buscar videojuego por código: {}", codigo);
        Videojuego videojuego = videojuegoService.buscarPorCodigo(codigo);
        if (videojuego == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("No se encontró videojuego con código: " + codigo);
        }
        return ResponseEntity.ok(videojuego);
    }

    // ── PUT /api/videojuegos/{codigo} ──────────────────────────────
    @PutMapping("/{codigo}")
    public ResponseEntity<String> actualizar(@PathVariable String codigo,
                                             @RequestBody Videojuego videojuego) {
        log.info("PUT - Actualizar videojuego: {}", codigo);
        videojuegoService.actualizar(
            codigo,
            videojuego.getNombre(),
            videojuego.getPlataforma(),
            videojuego.getPrecio(),
            videojuego.isDisponible(),
            videojuego.getGenero()
        );
        return ResponseEntity.ok("Videojuego actualizado correctamente: " + codigo);
    }

    // ── DELETE /api/videojuegos/{codigo} ───────────────────────────
    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> eliminar(@PathVariable String codigo) {
        log.info("DELETE - Eliminar videojuego: {}", codigo);
        videojuegoService.eliminar(codigo);
        return ResponseEntity.ok("Videojuego eliminado correctamente: " + codigo);
    }
}