package com.livraria.Service;

import com.livraria.DTO.CadastroClienteDTO;
import com.livraria.Model.Cliente;
import com.livraria.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        String cpfLimpo = cpf != null ? cpf.replaceAll("\\D", "") : "";
        return clienteRepository.findByCpf(cpfLimpo);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public Cliente cadastrarComIndicacao(CadastroClienteDTO dto) {
        String cpfLimpo = dto.getCpf() != null ? dto.getCpf().replaceAll("\\D", "") : "";
        if (cpfLimpo.isEmpty()) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }
        if (clienteRepository.findByCpf(cpfLimpo).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("O e-mail do cliente é obrigatório.");
        }
        if (clienteRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este e-mail.");
        }

        Cliente novo = new Cliente();
        novo.setCpf(cpfLimpo);
        novo.setNome(dto.getNome() != null ? dto.getNome().trim() : "Cliente");
        novo.setEmail(dto.getEmail().trim());
        novo.setSenha((dto.getSenha() != null && !dto.getSenha().isBlank()) ? dto.getSenha() : "123456");
        novo.setSexo(dto.getSexo() != null ? dto.getSexo() : "Outro");
        novo.setTelefones(dto.getTelefones() != null ? dto.getTelefones() : new ArrayList<>());

        novo.setRua(dto.getRua() != null && !dto.getRua().isBlank() ? dto.getRua().trim() : "Não informado");
        novo.setNumero(dto.getNumero() != null && !dto.getNumero().isBlank() ? dto.getNumero().trim() : "S/N");
        novo.setBairro(dto.getBairro() != null && !dto.getBairro().isBlank() ? dto.getBairro().trim() : "Centro");
        novo.setCidade(dto.getCidade() != null && !dto.getCidade().isBlank() ? dto.getCidade().trim() : "Belo Jardim");
        novo.setUf(dto.getUf() != null && !dto.getUf().isBlank() ? dto.getUf().trim().toUpperCase() : "PE");
        novo.setCep(dto.getCep() != null && !dto.getCep().isBlank() ? dto.getCep().trim() : "55150-000");

        if (dto.getCpfIndicador() != null && !dto.getCpfIndicador().isBlank()) {
            String cpfInd = dto.getCpfIndicador().replaceAll("\\D", "");
            if (cpfInd.equals(cpfLimpo)) {
                throw new IllegalArgumentException("Um cliente não pode indicar a si mesmo.");
            }
            Cliente indicador = clienteRepository.findByCpf(cpfInd)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente indicador não encontrado com o CPF informado."));
            novo.setClienteIndicador(indicador);
        }

        return clienteRepository.save(novo);
    }

    @Transactional
    public Cliente registrarIndicacao(Long idIndicador, String cpfIndicado) {
        String cpfLimpo = cpfIndicado != null ? cpfIndicado.replaceAll("\\D", "") : "";
        Cliente indicador = clienteRepository.findById(idIndicador)
                .orElseThrow(() -> new IllegalArgumentException("Cliente indicador não encontrado."));

        Cliente indicado = clienteRepository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum cliente cadastrado encontrado com o CPF informado."));

        if (indicador.getIdPessoa().equals(indicado.getIdPessoa())) {
            throw new IllegalArgumentException("Você não pode indicar a si mesmo.");
        }
        if (indicado.getClienteIndicador() != null) {
            throw new IllegalArgumentException("Este cliente já foi indicado anteriormente por outro cliente.");
        }

        indicado.setClienteIndicador(indicador);
        return clienteRepository.save(indicado);
    }

    public List<Cliente> listarIndicadosPor(Long idIndicador) {
        return clienteRepository.findByClienteIndicadorIdPessoa(idIndicador);
    }
}