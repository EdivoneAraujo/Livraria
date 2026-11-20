package com.livraria.Controller;

import com.livraria.DTO.LoginDTO;
import com.livraria.Model.Administrador;
import com.livraria.Model.Cliente;
import com.livraria.Repository.AdministradorRepository;
import com.livraria.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AdministradorController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        String ident = loginRequest.getIdentificador();
        Optional<Administrador> administradorOpt = (ident != null && ident.contains("@"))
                ? administradorRepository.findByEmail(ident.trim())
                : administradorRepository.findByCpf(ident != null ? ident.replaceAll("\\D", "") : "");

        if (administradorOpt.isPresent()) {
            Administrador user = administradorOpt.get();
            if (user.getSenha().equals(loginRequest.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Login efetuado com sucesso!",
                        "idPessoa", user.getIdPessoa(),
                        "nome", user.getNome(),
                        "perfil", user.getPerfil() != null ? user.getPerfil() : "ADMIN"
                ));
            }
        }

        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "mensagem", "E-mail/CPF ou senha incorretos."
        ));
    }

    @PostMapping("/login-cliente")
    public ResponseEntity<?> loginCliente(@RequestBody LoginDTO loginRequest) {
        String ident = loginRequest.getIdentificador();
        Optional<Cliente> clienteOpt = (ident != null && ident.contains("@"))
                ? clienteRepository.findByEmail(ident.trim())
                : clienteRepository.findByCpf(ident != null ? ident.replaceAll("\\D", "") : "");

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            if (cliente.getSenha().equals(loginRequest.getSenha())) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Bem-vindo(a), " + cliente.getNome() + "!",
                        "cliente", cliente
                ));
            }
        }

        return ResponseEntity.status(401).body(Map.of(
                "sucesso", false,
                "mensagem", "CPF/E-mail ou senha do cliente incorretos."
        ));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Administrador administrador) {
        if (administrador.getCpf() == null || administrador.getCpf().isBlank()) {
            return ResponseEntity.badRequest().body("O CPF é obrigatório para cadastrar um administrador.");
        }

        administrador.setCpf(administrador.getCpf().replaceAll("\\D", ""));

        if (administradorRepository.findByCpf(administrador.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um administrador cadastrado com este CPF.");
        }

        if (administradorRepository.findByEmail(administrador.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um administrador cadastrado com este e-mail.");
        }

        Administrador novoAdministrador = administradorRepository.save(administrador);
        return ResponseEntity.ok(novoAdministrador);
    }
}