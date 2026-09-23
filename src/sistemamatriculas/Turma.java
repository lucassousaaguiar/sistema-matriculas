package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Turma de uma disciplina (RN07): cada turma pertence a exatamente uma
 * disciplina, tem professor, vagas e ativação próprios — turmas da mesma
 * disciplina são independentes entre si. As regras de lotação valem por
 * turma: mínimo de 3 alunos para ativação (RN03) e máximo de 60 (RN04).
 */
public class Turma {

    public static final int MAX_ALUNOS = 60;
    public static final int MIN_ALUNOS = 3;

    private String codigo; // ex.: "T1"
    private boolean ativa;
    private Disciplina disciplina;
    private Professor professor;
    private List<Matricula> matriculas = new ArrayList<>();

    public Turma(String codigo, Disciplina disciplina, Professor professor) {
        this.codigo = codigo;
        this.disciplina = disciplina;
        this.professor = professor;
        if (disciplina != null && !disciplina.getTurmas().contains(this)) {
            disciplina.getTurmas().add(this);
        }
        if (professor != null && !professor.getTurmas().contains(this)) {
            professor.getTurmas().add(this);
        }
    }

    /** Há vaga enquanto o número de matriculados for menor que MAX_ALUNOS (RF09). */
    public boolean temVaga() {
        return qtdMatriculados() < MAX_ALUNOS;
    }

    public int qtdMatriculados() {
        return matriculas.size();
    }

    /**
     * Ao final do período: ativa a turma se tiver pelo menos MIN_ALUNOS
     * matriculados; caso contrário, cancela a turma e desfaz suas
     * matrículas (RF10, RN03).
     */
    public void verificarAtivacao() {
        if (qtdMatriculados() >= MIN_ALUNOS) {
            ativa = true;
        } else {
            ativa = false;
            for (Matricula m : new ArrayList<>(matriculas)) {
                m.cancelar();
            }
        }
    }

    /** Identificação exibida ao usuário, ex.: "ENG101/T1". */
    public String getIdentificacao() {
        return disciplina.getCodigo() + "/" + codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isAtiva() {
        return ativa;
    }

    void setAtiva(boolean ativa) {
        this.ativa = ativa; // usado pelo RepositorioDados ao carregar
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
