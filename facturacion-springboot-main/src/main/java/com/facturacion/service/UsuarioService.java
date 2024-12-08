package com.facturacion.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facturacion.entity.Usuario;
import com.facturacion.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Object[]> buscarPorUsuarioYContrasenia(String usuario, String password) {
        return this.usuarioRepository.findByUsernameAndPasswords(usuario, password);
    }

    public Optional<Integer> obtenerIntentosFallidos(String username) {
        return this.usuarioRepository.obtenerIntentosFallidos(username);
    }
    public boolean estaBloqueado(String username) {
        LOGGER.info("Verificando si el usuario {} esta bloqueado", username);
        Optional<Byte> bloqueado = this.usuarioRepository.estaBloqueado(username);
        LOGGER.info("Verificando si el usuario {} esta bloqueado", bloqueado);
        return bloqueado.orElse((byte) 0) == 1; // Si no se encuentra el usuario, se asume que no está bloqueado
    }

    public void incrementarIntentosFallidos(String username) {
        this.usuarioRepository.incrementarIntentosFallidos(username);
    }

    public void bloquearUsuario(String username) {
        this.usuarioRepository.bloquearUsuario(username);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

}
