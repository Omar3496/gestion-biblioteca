package org.idat.biblioteca.service;

import org.idat.biblioteca.model.Rol;
import org.idat.biblioteca.model.Usuario;
import org.idat.biblioteca.repository.RolRepository;
import org.idat.biblioteca.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtener(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        usuario.setIdUsuario(null);
        usuario.setRol(buscarRol(usuario));
        // TODO: cifrar la contraseña con BCrypt
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Integer id, Usuario datos) {
        Usuario u = obtener(id);
        if (!u.getEmail().equals(datos.getEmail()) && usuarioRepository.existsByEmail(datos.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        u.setNombre(datos.getNombre());
        u.setApellido(datos.getApellido());
        u.setEmail(datos.getEmail());
        u.setRol(buscarRol(datos));
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            u.setPassword(datos.getPassword()); // TODO: cifrar con BCrypt
        }
        return usuarioRepository.save(u);
    }

    public void eliminar(Integer id) {
        usuarioRepository.delete(obtener(id));
    }

    private Rol buscarRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el rol");
        }
        return rolRepository.findById(usuario.getRol().getIdRol())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no existe"));
    }
}
