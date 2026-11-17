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
        System.out.println("===> Tentativa de login recebida para o e-mail: " + loginRequest.getEmail());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(loginRequest.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario user = usuarioOpt.get();
            System.out.println("===> Utilizador encontrado na BD: " + user.getNome());
            System.out.println("===> Senha na BD: [" + user.getSenha() + "] | Senha enviada: [" + loginRequest.getSenha() + "]");

            if (user.getSenha().equals(loginRequest.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Login efetuado com sucesso!",
                        "nome", user.getNome()
                ));
            } else {
                System.out.println("===> ERRO: A senha não coincide!");
            }
        } else {
            System.out.println("===> ERRO: E-mail não encontrado na base de dados!");
        }

        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "mensagem", "E-mail ou palavra-passe incorretos."
        ));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(novoUsuario);
    }
}