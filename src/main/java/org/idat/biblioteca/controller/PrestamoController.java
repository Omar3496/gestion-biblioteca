package org.idat.biblioteca.controller;

import jakarta.validation.Valid;
import org.idat.biblioteca.dto.PrestamoRequest;
import org.idat.biblioteca.model.Prestamo;
import org.idat.biblioteca.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints REST del módulo de préstamos. */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestamo crear(@Valid @RequestBody PrestamoRequest datos) {
        return prestamoService.crear(datos);
    }

    @GetMapping
    public List<Prestamo> listar() {
        return prestamoService.listar();
    }

    @GetMapping("/pendientes")
    public List<Prestamo> pendientes() {
        return prestamoService.listarPendientes();
    }

    @GetMapping("/lector/{idLector}")
    public List<Prestamo> porLector(@PathVariable Integer idLector) {
        return prestamoService.listarPorLector(idLector);
    }

    @GetMapping("/{id}")
    public Prestamo buscar(@PathVariable Integer id) {
        return prestamoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Prestamo actualizar(@PathVariable Integer id, @Valid @RequestBody PrestamoRequest datos) {
        return prestamoService.actualizar(id, datos);
    }

    @PatchMapping("/{id}/devolver")
    public Prestamo devolver(@PathVariable Integer id) {
        return prestamoService.devolver(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        prestamoService.eliminar(id);
    }
}
