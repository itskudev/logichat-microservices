package com.logichat.personas.service;

import com.logichat.personas.client.AuthClient;
import com.logichat.personas.model.Cliente;
import com.logichat.personas.repository.ClienteRepository;
import com.logichat.personas.utils.BusinessException;
import com.logichat.personas.utils.ModeloNotFoundException;
import feign.FeignException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuthClient authClient;

    public ClienteService(ClienteRepository clienteRepository, AuthClient authClient) {
        this.clienteRepository = clienteRepository;
        this.authClient = authClient;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Cliente no encontrado"));
    }

    public Cliente obtenerPorUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ModeloNotFoundException("No tienes perfil de cliente"));
    }

    public Cliente crear(Long usuarioId, String telefono, String direccion) {
        try {
            authClient.getUsuario(usuarioId);
        } catch (FeignException.NotFound e) {
            throw new ModeloNotFoundException("Usuario no encontrado");
        }
        if (clienteRepository.existsByUsuarioId(usuarioId)) {
            throw new BusinessException("El usuario ya tiene un perfil de cliente");
        }
        Cliente c = new Cliente();
        c.setUsuarioId(usuarioId);
        c.setTelefono(telefono);
        c.setDireccion(direccion);
        return clienteRepository.save(c);
    }

    public Cliente actualizar(Long id, String telefono, String direccion) {
        Cliente c = obtener(id);
        if (telefono != null) c.setTelefono(telefono);
        if (direccion != null) c.setDireccion(direccion);
        return clienteRepository.save(c);
    }

    public void eliminar(Long id) {
        clienteRepository.delete(obtener(id));
    }
}
