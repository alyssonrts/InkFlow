
package br.edu.aesa.inkflow.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sessao")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto", nullable = false)
    private ProjetoTatuagem projeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "is_retoque", nullable = false)
    private boolean retoque;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    protected Sessao() {
    }

    public Sessao(
            ProjetoTatuagem projeto,
            Sala sala,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim,
            boolean retoque,
            String observacoes) {

        if (dataHoraInicio == null
                || dataHoraFim == null
                || !dataHoraFim.isAfter(dataHoraInicio)) {

            throw new IllegalArgumentException(
                    "O horário de término deve ser posterior ao início.");
        }

        this.projeto = projeto;
        this.sala = sala;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = "AGENDADA";
        this.retoque = retoque;
        this.observacoes = observacoes;
    }

    public Integer getId() {
        return id;
    }

    public ProjetoTatuagem getProjeto() {
        return projeto;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRetoque() {
        return retoque;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
