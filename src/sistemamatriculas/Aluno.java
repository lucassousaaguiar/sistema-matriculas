package sistemamatriculas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aluno: matricula-se em turmas — até 4 disciplinas obrigatórias e 2
 * optativas (RN02), no máximo uma turma por disciplina (RN08) — e mantém o
 * histórico de disciplinas cursadas com a respectiva turma (RF15).
 */
public class Aluno extends Usuario {

    public static final int MAX_OBRIGATORIAS = 4;
    public static final int MAX_OPTATIVAS = 2;

    private String matriculaAcad;
    private List<Matricula> matriculas = new ArrayList<>();
    private List<ItemHistorico> historico = new ArrayList<>();

    public Aluno(String login, String senhaHash, String nome, String matriculaAcad) {
        super(login, senhaHash, nome);
        this.matriculaAcad = matriculaAcad;
    }

    /** Matricula o aluno na turma, respeitando RN02, RN04 e RN08. */
    public boolean matricular(Turma turma, TipoMatricula tipo) {
        if (buscarMatriculaPorDisciplina(turma.getDisciplina()) != null) {
            return false; // já matriculado em uma turma desta disciplina (RN08)
        }
        if (contarPorTipo(tipo) >= limiteDoTipo(tipo)) {
            return false; // estourou o limite de obrigatórias/optativas (RN02)
        }
        if (!turma.temVaga()) {
            return false; // turma lotada (RN04)
        }
        Matricula m = new Matricula(this, turma, tipo, LocalDate.now());
        matriculas.add(m);
        turma.getMatriculas().add(m);
        return true;
    }

    /** Cancela uma matrícula feita anteriormente (RF07). */
    public void cancelarMatricula(Turma turma) {
        Matricula m = buscarMatricula(turma);
        if (m != null) {
            m.cancelar();
        }
    }

    public Matricula buscarMatricula(Turma turma) {
        for (Matricula m : matriculas) {
            if (m.getTurma().equals(turma)) {
                return m;
            }
        }
        return null;
    }

    /** RN08 — o aluno só pode ter uma turma por disciplina. */
    public Matricula buscarMatriculaPorDisciplina(Disciplina disciplina) {
        for (Matricula m : matriculas) {
            if (m.getDisciplina().equals(disciplina)) {
                return m;
            }
        }
        return null;
    }

    public int contarPorTipo(TipoMatricula tipo) {
        int qtd = 0;
        for (Matricula m : matriculas) {
            if (m.getTipo() == tipo) {
                qtd++;
            }
        }
        return qtd;
    }

    private int limiteDoTipo(TipoMatricula tipo) {
        return tipo == TipoMatricula.OBRIGATORIA ? MAX_OBRIGATORIAS : MAX_OPTATIVAS;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public List<ItemHistorico> getHistorico() {
        return historico;
    }

    public String getMatriculaAcad() {
        return matriculaAcad;
    }
}
