package com.livraria.Repository;

import com.livraria.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Procura pedidos pelo CPF do cliente
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.cliente c LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.livro l WHERE c.cpf = :cpf")
    List<Pedido> findByClienteDocumento(@Param("cpf") String cpf);

    // Procura pedidos pelo ID do cliente
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.cliente c LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.livro l WHERE c.idPessoa = :idCliente")
    List<Pedido> findByClienteId(@Param("idCliente") Long idCliente);

    // Pesquisa pedidos/faturas pelo título do livro ou nome do cliente
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.cliente c LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.livro l " +
            "WHERE (:termo IS NULL OR :termo = '' OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Pedido> pesquisarPorLivroOuCliente(@Param("termo") String termo);

    // Procura pedidos do vendedor por CPF ou Matrícula com todos os relacionamentos
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.cliente c LEFT JOIN FETCH p.vendedor vd LEFT JOIN FETCH p.itens i LEFT JOIN FETCH i.livro l " +
            "WHERE vd.cpf = :identificador OR vd.matricula = :identificador")
    List<Pedido> findByVendedorCpfOuMatricula(@Param("identificador") String identificador);
}