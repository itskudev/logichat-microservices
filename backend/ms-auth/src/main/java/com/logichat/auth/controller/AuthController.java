package com.logichat.auth.controller;

import com.logichat.auth.dto.LoginRequest;
import com.logichat.auth.dto.LoginResponse;
import com.logichat.auth.dto.RegistroRequest;
import com.logichat.auth.dto.UsuarioResponse;
import com.logichat.auth.model.Usuario;
import com.logichat.auth.security.JwtService;
import com.logichat.auth.service.UsuarioService;
import com.logichat.auth.utils.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final ModelMapper mapper;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioService usuarioService,
                          ModelMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }

    private UsuarioResponse toDTO(Usuario usuario) {
        UsuarioResponse dto = mapper.map(usuario, UsuarioResponse.class);
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<?>> registrar(@Valid @RequestBody RegistroRequest request) {
		Usuario usuario = usuarioService.registrar(request);
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Usuario registrado",
				toDTO(usuario));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword()));
		Usuario usuario = (Usuario) authentication.getPrincipal();
		LoginResponse bean = new LoginResponse(jwtService.generateToken(usuario), toDTO(usuario));
		ApiResponse<LoginResponse> response = new ApiResponse<>(true, "Login correcto", bean);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Gestión de usuarios (solo ADMIN en una fase posterior con @PreAuthorize; por
	// ahora autenticado).
    // Gestión de usuarios: solo ADMIN, salvo ver el propio perfil.
    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<?>> listar() {
        List<UsuarioResponse> lista = usuarioService.listar().stream().map(this::toDTO).toList();
		ApiResponse<List<UsuarioResponse>> response = new ApiResponse<>(true, "Listado de usuarios", lista);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR') or #id == authentication.tokenAttributes['uid']")
    @GetMapping("/usuarios/{id}")
	public ResponseEntity<ApiResponse<?>> obtener(@PathVariable Long id) {
		UsuarioResponse bean = toDTO(usuarioService.obtener(id));
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Usuario existe", bean);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/usuarios")
	public ResponseEntity<ApiResponse<?>> crear(@RequestBody Map<String, String> body) {
		Usuario usuario = usuarioService.crearConRol(body.getOrDefault("nombre", ""), body.getOrDefault("apellido", ""),
				body.get("email"), body.get("password"), body.getOrDefault("rol", "CLIENTE"));
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Usuario registrado",
				toDTO(usuario));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/usuarios/{id}/rol")
	public ResponseEntity<ApiResponse<?>> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
		UsuarioResponse bean = toDTO(usuarioService.cambiarRol(id, body.get("rol")));
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Rol actualizado", bean);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/usuarios/{id}/bloquear")
	public ResponseEntity<ApiResponse<?>> bloquear(@PathVariable Long id) {
		UsuarioResponse bean = toDTO(usuarioService.bloquear(id, true));
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Usuario bloqueado", bean);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/usuarios/{id}/desbloquear")
	public ResponseEntity<ApiResponse<?>> desbloquear(@PathVariable Long id) {
		UsuarioResponse bean = toDTO(usuarioService.bloquear(id, false));
		ApiResponse<UsuarioResponse> response = new ApiResponse<>(true, "Usuario activado", bean);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/usuarios/{id}")
	public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		ApiResponse<Void> response = new ApiResponse<>(true, "Usuario eliminado", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
