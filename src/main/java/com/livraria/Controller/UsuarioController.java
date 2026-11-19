package com.livraria.Controller;

import com.livraria.DTO.LoginDTO;
import com.livraria.Model.Usuario;
import com.livraria.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario user = usuarioOpt.get();
            if (user.getSenha().equals(loginRequest.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Login efetuado com sucesso!",
                        "nome", user.getNome(),
                        "perfil", user.getPerfil() != null ? user.getPerfil() : "USUARIO"
                ));
            }
        }

        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "mensagem", "E-mail ou palavra-passe incorretos."
        ));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        if (usuario.getCpf() == null || usuario.getCpf().isBlank()) {
            return ResponseEntity.badRequest().body("O CPF é obrigatório para cadastrar um usuário.");
        }

        usuario.setCpf(usuario.getCpf().replaceAll("\\D", ""));

        if (usuarioRepository.findById(usuario.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário cadastrado com este CPF.");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario novoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(novoUsuario);
    }
}