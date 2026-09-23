package sistemamatriculas;

/**
 * Registro do histórico do aluno (RF15): disciplina cursada, turma em que
 * cursou e semestre. Lançado no encerramento do período para as matrículas
 * de turmas ativas (RN09).
 */
public class ItemHistorico {

    private Disciplina disciplina;
    private String turma; // código da turma, ex.: "T1"
    private String semestre;

    public ItemHistorico(Disciplina disciplina, String turma, String semestre) {
        this.disciplina = disciplina;
        this.turma = turma;
        this.semestre = semestre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public String getTurma() {
        return turma;
    }

    public String getSemestre() {
        return semestre;
    }
}
