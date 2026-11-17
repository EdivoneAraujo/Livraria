package com.livraria.Service;

import com.livraria.Model.Cliente;
import com.livraria.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // CORRIGIDO: de Long para String (pois o ID/CPF do Cliente é String)
    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // CORRIGIDO: de Long para String
    public void eliminar(String id) {
        clienteRepository.deleteById(id);
    }
}