package org.idat.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.idat.biblioteca.model.Libro;
import org.idat.biblioteca.repository.LibroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public Libro buscarPorId(Integer id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
    }

    public Libro guardarLibro(Libro libro) {
        libro.setId(null);
        return libroRepository.save(libro);
    }

    public Libro buscarPorIsbn(String isbn) {
        return libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public void eliminarLibro(Integer id) {
        Libro libro = buscarPorId(id);
        libroRepository.delete(libro);
    }

    public Libro actualizarLibro(Integer id, Libro datos) {
        Libro libro = buscarPorId(id);
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setIsbn(datos.getIsbn());
        libro.setIdCategoria(datos.getIdCategoria());
        libro.setStock(datos.getStock());
        return libroRepository.save(libro);
    }
}
