package sistemamatriculas;

import java.time.LocalDate;

/**
 * Matrícula de um aluno em uma turma, como obrigatória ou optativa (RN02).
 * A disciplina é obtida através da turma.
 */
public class Matricula {

    private Aluno aluno;
    private Turma turma;
    private TipoMatricula tipo;
    private LocalDate data;

    public Matricula(Aluno aluno, Turma turma, TipoMatricula tipo, LocalDate data) {
        this.aluno = aluno;
        this.turma = turma;
        this.tipo = tipo;
        this.data = data;
    }

    /** Desfaz esta matrícula, liberando a vaga na turma (RF07). */
    public void cancelar() {
        aluno.getMatriculas().remove(this);
        turma.getMatriculas().remove(this);
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Turma getTurma() {
        return turma;
    }

    public Disciplina getDisciplina() {
        return turma.getDisciplina();
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public LocalDate getData() {
        return data;
    }
}
