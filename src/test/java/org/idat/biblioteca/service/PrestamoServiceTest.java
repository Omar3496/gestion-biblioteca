package org.idat.biblioteca.service;

import org.idat.biblioteca.dto.PrestamoRequest;
import org.idat.biblioteca.model.Prestamo;
import org.idat.biblioteca.model.EstadoPrestamo;
import org.idat.biblioteca.repository.PrestamoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class PrestamoServiceTest {

    private PrestamoRepository repository;
    private PrestamoService servicio;

    @BeforeEach
    void configurar() {
        repository = mock(PrestamoRepository.class);
        servicio = new PrestamoService(repository);
    }

    @Test
    void registrarPrestamo() {

        PrestamoRequest datos = new PrestamoRequest(
                1,
                2,
                1,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        when(repository.save(any(Prestamo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Prestamo resultado = servicio.crear(datos);

        assertEquals(
                EstadoPrestamo.PENDIENTE,
                resultado.getEstado()
        );

        verify(repository).save(any(Prestamo.class));
    }

    @Test
    void buscarPrestamo() {

        Prestamo prestamo = new Prestamo();
        prestamo.setIdPrestamo(1);

        when(repository.findById(1))
                .thenReturn(Optional.of(prestamo));

        Prestamo resultado = servicio.buscarPorId(1);

        assertEquals(1, resultado.getIdPrestamo());
    }

    @Test
    void devolverPrestamo() {

        Prestamo prestamo = new Prestamo();

        prestamo.setIdPrestamo(1);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.PENDIENTE);

        when(repository.findById(1))
                .thenReturn(Optional.of(prestamo));

        when(repository.save(any(Prestamo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Prestamo resultado = servicio.devolver(1);

        assertEquals(
                EstadoPrestamo.DEVUELTO,
                resultado.getEstado()
        );

        assertNotNull(resultado.getFechaDevolucionReal());
    }
}