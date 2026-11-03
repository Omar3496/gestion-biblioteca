package org.idat.biblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.idat.biblioteca.model.Libro;
import org.idat.biblioteca.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro nuevoLibro(@RequestBody Libro libro) {
        return libroService.guardarLibro(libro);
    }

    @GetMapping
    public List<Libro> listarLibros() {
        return libroService.listarLibros();
    }

    @GetMapping("/{id}")
    public Libro buscarLibroPorId(@PathVariable Integer id) {
        return libroService.buscarPorId(id);
    }

    @GetMapping("/isbn/{isbn}")
    public Libro buscarLibroPorIsbn(@PathVariable String isbn) {
        return libroService.buscarPorIsbn(isbn);
    }

    @PutMapping("/{id}")
    public Libro actualizarLibro(@PathVariable Integer id, @RequestBody Libro libro) {
        return libroService.actualizarLibro(id, libro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLibro(@PathVariable Integer id) {
        libroService.eliminarLibro(id);
    }
}
