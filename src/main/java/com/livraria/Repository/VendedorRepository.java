package com.livraria.Repository;

import com.livraria.Model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    // Encontra o vendedor quer pelo CPF, quer pela Matrícula
    @Query("SELECT v FROM Vendedor v WHERE v.cpf = :id OR v.matricula = :id")
    Optional<Vendedor> findByCpfOuMatricula(@Param("id") String identificador);
}