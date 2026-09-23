package sistemamatriculas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Currículo do semestre: conjunto de disciplinas ofertadas e o período de
 * matrículas definido pela secretaria (RF05, RN05).
 */
public class Curriculo {

    private String semestre; // ex.: "2026/2"
    private LocalDate inicioMatriculas;
    private LocalDate fimMatriculas;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curriculo(String semestre, LocalDate inicioMatriculas, LocalDate fimMatriculas) {
        this.semestre = semestre;
        this.inicioMatriculas = inicioMatriculas;
        this.fimMatriculas = fimMatriculas;
    }

    /** Matrículas e cancelamentos só são permitidos com o período aberto (RF08, RN05). */
    public boolean periodoAberto() {
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(inicioMatriculas) && !hoje.isAfter(fimMatriculas);
    }

    /** Antecipa o fim do período para "ontem", encerrando as matrículas (RF10). */
    void encerrarAgora() {
        this.fimMatriculas = LocalDate.now().minusDays(1);
    }

    public String getSemestre() {
        return semestre;
    }

    public LocalDate getInicioMatriculas() {
        return inicioMatriculas;
    }

    public LocalDate getFimMatriculas() {
        return fimMatriculas;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
