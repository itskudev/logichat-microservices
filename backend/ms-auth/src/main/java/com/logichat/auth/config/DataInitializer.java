package com.logichat.auth.config;

import com.logichat.auth.client.PersonasClient;
import com.logichat.auth.model.Rol;
import com.logichat.auth.model.Usuario;
import com.logichat.auth.repository.RolRepository;
import com.logichat.auth.repository.UsuarioRepository;
import com.logichat.auth.security.JwtService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private Usuario sembrar(UsuarioRepository repo, PasswordEncoder encoder,
                            String nombre, String apellido, String email,
                            String rawPassword, Rol rol) {
        Usuario existente = repo.findByEmail(email).orElse(null);
        if (existente != null) {
            existente.setNombre(nombre);
            existente.setApellido(apellido);
            existente.setRol(rol);
            existente.setPassword(encoder.encode(rawPassword));
            existente.setEstado("ACTIVO");
            return repo.save(existente);
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        u.setRol(rol);
        u.setEstado("ACTIVO");
        return repo.save(u);
    }

    private Rol rol(RolRepository repo, String nombre) {
        return repo.findByNombre(nombre).orElseGet(() -> repo.save(new Rol(nombre)));
    }

    @Bean
    public CommandLineRunner initSeed(UsuarioRepository usuarioRepository,
                                      RolRepository rolRepository,
                                      PasswordEncoder passwordEncoder,
                                      JwtService jwtService,
                                      PersonasClient personasClient) {
        return args -> {
            Rol admin = rol(rolRepository, "ADMIN");
            rol(rolRepository, "COORDINADOR");
            Rol conductorRol = rol(rolRepository, "CONDUCTOR");
            Rol clienteRol = rol(rolRepository, "CLIENTE");

            Usuario adminU = sembrar(usuarioRepository, passwordEncoder,
                    "Administroncio", "Lopez Jimenez", "admin@logichat.com", "123456", admin);
            Usuario conductorU = sembrar(usuarioRepository, passwordEncoder,
                    "Conductoncio", "Ramos Delgado", "conductor@logichat.com", "123456", conductorRol);
            Usuario clienteU = sembrar(usuarioRepository, passwordEncoder,
                    "Clientoncio", "Flores Paredes", "cliente@logichat.com", "123456", clienteRol);
            sembrar(usuarioRepository, passwordEncoder,
                    "Coordinoncio", "Huamán Torres", "coordinador@logichat.com", "123456",
                    rolRepository.findByNombre("COORDINADOR").orElseThrow());

            // Perfiles en ms-personas con token propio (server-to-server).
            String bearer = "Bearer " + jwtService.generateToken(adminU);
            try {
                Map<String, Object> c = new HashMap<>();
                c.put("usuarioId", conductorU.getId());
                c.put("licencia", "SEED-LIC-001");
                c.put("telefono", "900000001");
                personasClient.crearConductor(bearer, c);
            } catch (Exception ignored) {
                // Si el perfil ya existe, personas responde 409 y se ignora en el seed.
            }
            try {
                Map<String, Object> c = new HashMap<>();
                c.put("usuarioId", clienteU.getId());
                c.put("telefono", "900000002");
                c.put("direccion", "Av. Seed 123");
                personasClient.crearCliente(bearer, c);
            } catch (Exception ignored) {
                // Si el perfil ya existe, personas responde 409 y se ignora en el seed.
            }
        };
    }
}
