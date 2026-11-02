package org.idat.biblioteca.repository;

import org.idat.biblioteca.model.Prestamo;
import org.idat.biblioteca.model.EstadoPrestamo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository
        extends JpaRepository<Prestamo, Integer> {

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    List<Prestamo> findByIdLector(Integer idLector);

    boolean existsByIdLectorAndIdLibroAndEstado(
            Integer idLector,
            Integer idLibro,
            EstadoPrestamo estado
    );

    boolean existsByIdLectorAndIdLibroAndEstadoAndIdPrestamoNot(
            Integer idLector,
            Integer idLibro,
            EstadoPrestamo estado,
            Integer idPrestamo
    );
}