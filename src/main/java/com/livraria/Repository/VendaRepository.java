package com.livraria.Repository;

import com.livraria.Model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Procura vendas pelo CPF do cliente
    @Query("SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.cliente c LEFT JOIN FETCH v.itens i LEFT JOIN FETCH i.livro l WHERE c.cpf = :cpf")
    List<Venda> findByClienteDocumento(@Param("cpf") String cpf);

    // Pesquisa faturas pelo título do livro ou nome do cliente
    @Query("SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.cliente c LEFT JOIN FETCH v.itens i LEFT JOIN FETCH i.livro l " +
            "WHERE (:termo IS NULL OR :termo = '' OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Venda> pesquisarPorLivroOuCliente(@Param("termo") String termo);

    // ESSENCIAL: Procura vendas do vendedor por CPF ou Matrícula com todos os relacionamentos
    @Query("SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.cliente c LEFT JOIN FETCH v.vendedor vd LEFT JOIN FETCH v.itens i LEFT JOIN FETCH i.livro l " +
            "WHERE vd.cpf = :identificador OR vd.matricula = :identificador")
    List<Venda> findByVendedorCpfOuMatricula(@Param("identificador") String identificador);
}