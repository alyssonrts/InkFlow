package br.edu.aesa.inkflow.repository;

import br.edu.aesa.inkflow.domain.model.Sessao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessaoRepository
        extends JpaRepository<Sessao, Integer> {

    // Listar as sessões de um projeto
    List<Sessao> findByProjetoIdOrderByDataHoraInicioAsc(
            Integer projetoId);

    // Listar as sessões de um tatuador
    List<Sessao> findByProjetoTatuadorIdOrderByDataHoraInicioAsc(
            Integer tatuadorId);

    // Listar as sessões de um cliente
    List<Sessao> findByProjetoClienteIdOrderByDataHoraInicioAsc(
            Integer clienteId);

    // Verificar conflitos de horário na sala
    @Query("""
        SELECT COUNT(s)
        FROM Sessao s
        WHERE s.sala.id = :salaId
          AND s.status <> 'CANCELADA'
          AND s.dataHoraInicio < :fim
          AND s.dataHoraFim > :inicio
        """)
    long contarConflitosSala(
            @Param("salaId") Integer salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Verificar conflitos de horário do tatuador
    @Query("""
        SELECT COUNT(s)
        FROM Sessao s
        WHERE s.projeto.tatuador.id = :tatuadorId
          AND s.status <> 'CANCELADA'
          AND s.dataHoraInicio < :fim
          AND s.dataHoraFim > :inicio
        """)
    long contarConflitosTatuador(
            @Param("tatuadorId") Integer tatuadorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // ETAPA 5 - Verificar conflitos de horário do cliente
    @Query("""
        SELECT COUNT(s)
        FROM Sessao s
        WHERE s.projeto.cliente.id = :clienteId
          AND s.status <> 'CANCELADA'
          AND s.dataHoraInicio < :fim
          AND s.dataHoraFim > :inicio
        """)
    long contarConflitosCliente(
            @Param("clienteId") Integer clienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Contar sessões normais não canceladas do projeto
    long countByProjetoIdAndRetoqueFalseAndStatusNot(
            Integer projetoId,
            String status);
}