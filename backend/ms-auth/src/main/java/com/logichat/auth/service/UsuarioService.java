package com.logichat.auth.service;

import com.logichat.auth.client.PersonasClient;
import com.logichat.auth.dto.RegistroRequest;
import com.logichat.auth.model.Rol;
import com.logichat.auth.model.Usuario;
import com.logichat.auth.repository.RolRepository;
import com.logichat.auth.repository.UsuarioRepository;
import com.logichat.auth.security.JwtService;
import com.logichat.auth.utils.BusinessException;
import com.logichat.auth.utils.ModeloNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PersonasClient personasClient;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder, JwtService jwtService,
                          PersonasClient personasClient) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.personasClient = personasClient;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Usuario no encontrado"));
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ModeloNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public Usuario registrar(RegistroRequest request) {
        String email = request.getEmail().trim();
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        Rol rol = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE. Importe 03-auth.sql."));
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado("ACTIVO");
        try {
            usuario = usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("El email ya está registrado");
        }
        // Opción A: autocrea el perfil cliente en ms-personas con token propio.
        String bearer = "Bearer " + jwtService.generateToken(usuario);
        Map<String, Object> perfil = new java.util.HashMap<>();
        perfil.put("usuarioId", usuario.getId());
        perfil.put("telefono", request.getTelefono());
        perfil.put("direccion", request.getDireccion());
        personasClient.crearCliente(bearer, perfil);
        return usuario;
    }

    @Transactional
    public Usuario crearConRol(String nombre, String apellido, String email, String password, String rolNombre) {
        if (usuarioRepository.findByEmail(email.trim()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email.trim());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setEstado("ACTIVO");
        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarRol(Long id, String rolNombre) {
        Usuario u = obtener(id);
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));
        u.setRol(rol);
        return usuarioRepository.save(u);
    }

    public Usuario bloquear(Long id, boolean bloqueado) {
        Usuario u = obtener(id);
        u.setEstado(bloqueado ? "BLOQUEADO" : "ACTIVO");
        return usuarioRepository.save(u);
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(obtener(id));
    }
}
