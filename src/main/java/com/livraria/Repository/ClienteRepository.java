package com.livraria.Repository;

import com.livraria.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNome(String nome);
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByEmail(String email);

    // Lista todos os clientes que foram indicados por um determinado cliente
    List<Cliente> findByClienteIndicadorIdPessoa(Long idIndicador);
}