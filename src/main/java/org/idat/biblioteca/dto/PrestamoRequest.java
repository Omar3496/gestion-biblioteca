package org.idat.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/** Campos que debe enviar el cliente al registrar o actualizar un préstamo. */
public record PrestamoRequest(
        @NotNull(message = "El ID del lector es obligatorio")
        @Positive(message = "El ID del lector debe ser positivo") Integer idLector,

        @NotNull(message = "El ID del libro es obligatorio")
        @Positive(message = "El ID del libro debe ser positivo") Integer idLibro,

        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser positivo") Integer idUsuario,

        @NotNull(message = "La fecha del préstamo es obligatoria") LocalDate fechaPrestamo,

        @NotNull(message = "La fecha pactada de devolución es obligatoria")
        LocalDate fechaDevolucionPactada
) {}
