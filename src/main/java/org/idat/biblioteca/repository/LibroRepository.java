package org.idat.biblioteca.repository;

import org.idat.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro,Integer> {
   List<Libro> findByTituloContaining(String titulo);
}
