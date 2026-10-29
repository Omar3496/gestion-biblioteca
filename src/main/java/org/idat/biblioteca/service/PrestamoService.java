package org.idat.biblioteca.service;

import org.idat.biblioteca.dto.PrestamoRequest;
import org.idat.biblioteca.model.EstadoPrestamo;
import org.idat.biblioteca.model.Prestamo;
import org.idat.biblioteca.repository.PrestamoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private final PrestamoRepository repository;

    public PrestamoService(PrestamoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Prestamo crear(PrestamoRequest datos) {

        validarFechas(datos);
        validarDuplicado(datos, null);

        Prestamo prestamo = new Prestamo();

        copiarDatos(datos, prestamo);

        prestamo.setEstado(EstadoPrestamo.PENDIENTE);
        prestamo.setFechaDevolucionReal(null);

        return repository.save(prestamo);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listar() {

        return repository.findAll(
                Sort.by("idPrestamo").ascending()
        );
    }

    @Transactional(readOnly = true)
    public Prestamo buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Préstamo no encontrado"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPendientes() {

        return repository.findByEstado(
                EstadoPrestamo.PENDIENTE
        );
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPorLector(Integer idLector) {

        return repository.findByIdLector(idLector);
    }

    @Transactional
    public Prestamo actualizar(
            Integer id,
            PrestamoRequest datos
    ) {

        Prestamo prestamo = buscarPorId(id);

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede modificar un préstamo devuelto"
            );
        }

        validarFechas(datos);
        validarDuplicado(datos, id);

        copiarDatos(datos, prestamo);

        return repository.save(prestamo);
    }

    @Transactional
    public Prestamo devolver(Integer id) {

        Prestamo prestamo = buscarPorId(id);

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El préstamo ya fue devuelto"
            );
        }

        if (LocalDate.now().isBefore(
                prestamo.getFechaPrestamo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede devolver un préstamo futuro"
            );
        }

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        return repository.save(prestamo);
    }

    @Transactional
    public void eliminar(Integer id) {

        Prestamo prestamo = buscarPorId(id);

        repository.delete(prestamo);
    }

    private void validarFechas(PrestamoRequest datos) {

        if (datos.fechaPrestamo() != null
                && datos.fechaDevolucionPactada() != null
                && datos.fechaDevolucionPactada()
                .isBefore(datos.fechaPrestamo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de devolución no puede ser anterior"
                            + " a la fecha del préstamo"
            );
        }
    }

    private void validarDuplicado(
            PrestamoRequest datos,
            Integer idIgnorado
    ) {

        boolean existe;

        if (idIgnorado == null) {
            existe = repository
                    .existsByIdLectorAndIdLibroAndEstado(
                            datos.idLector(),
                            datos.idLibro(),
                            EstadoPrestamo.PENDIENTE
                    );
        } else {
            existe = repository
                    .existsByIdLectorAndIdLibroAndEstadoAndIdPrestamoNot(
                            datos.idLector(),
                            datos.idLibro(),
                            EstadoPrestamo.PENDIENTE,
                            idIgnorado
                    );
        }

        if (existe) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El lector ya tiene pendiente este libro"
            );
        }
    }

    private void copiarDatos(
            PrestamoRequest datos,
            Prestamo prestamo
    ) {

        prestamo.setIdLector(datos.idLector());
        prestamo.setIdLibro(datos.idLibro());
        prestamo.setIdUsuario(datos.idUsuario());

        prestamo.setFechaPrestamo(datos.fechaPrestamo());
        prestamo.setFechaDevolucionPactada(
                datos.fechaDevolucionPactada()
        );
    }
}