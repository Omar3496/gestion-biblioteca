package org.idat.biblioteca.service;

import lombok.RequiredArgsConstructor;
import org.idat.biblioteca.model.Libro;
import org.idat.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LibroService{

    private final LibroRepository libroRepository;

    public Libro buscarPorId(Long id){
        return libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    public Libro guardarLibro(Libro libro){
        return libroRepository.save(libro);
    }

    public Libro buscarPorIsbn(String isbn){
        return libroRepository.findByIsbn(isbn).orElse(null);
    }

    public List<Libro> listarLibros(){
        return libroRepository.findAll();
    }

    public void eliminarLibro(Long id){
       Libro libroEncontrado= libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        libroRepository.delete(libroEncontrado);
    }

    public Libro actualizarLibro(Long id, Libro libro){
        libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        libro.setId(id);
        return libroRepository.save(libro);
    }
}
