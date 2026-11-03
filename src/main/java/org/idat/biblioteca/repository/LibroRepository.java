package org.idat.biblioteca.repository;

import org.idat.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro,Long> {

   List<Libro> findByTituloContaining(String titulo);
   Optional<Libro> findByIsbn(String isbn);
   List<Libro> findByCategoriaContaining(String categoria);
   List<Libro> findByStockGreaterThan(int stock);
}
