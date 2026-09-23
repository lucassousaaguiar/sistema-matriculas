package sistemamatriculas;

import java.time.LocalDate;

/**
 * Matrícula de um aluno em uma disciplina, como obrigatória ou optativa (RN02).
 */
public class Matricula {

    private Aluno aluno;
    private Disciplina disciplina;
    private TipoMatricula tipo;
    private LocalDate data;

    public Matricula(Aluno aluno, Disciplina disciplina, TipoMatricula tipo, LocalDate data) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.data = data;
    }

    /** Desfaz esta matrícula, liberando a vaga na disciplina (RF07). */
    public void cancelar() {
        // TODO: implementar na Sprint 3
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public LocalDate getData() {
        return data;
    }
}
