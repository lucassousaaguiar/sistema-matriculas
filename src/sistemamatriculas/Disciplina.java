package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Disciplina ofertada em um semestre. Concentra as regras de lotação:
 * mínimo de 3 alunos para ser ativada (RN03) e máximo de 60 (RN04).
 */
public class Disciplina {

    public static final int MAX_ALUNOS = 60;
    public static final int MIN_ALUNOS = 3;

    private String codigo;
    private String nome;
    private int creditos;
    private boolean ativa;
    private Professor professor;
    private List<Matricula> matriculas = new ArrayList<>();

    public Disciplina(String codigo, String nome, int creditos, Professor professor) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.professor = professor;
        if (professor != null && !professor.getDisciplinas().contains(this)) {
            professor.getDisciplinas().add(this);
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
     * Ao final do período: ativa a disciplina se tiver pelo menos MIN_ALUNOS
     * matriculados; caso contrário, cancela a disciplina e desfaz suas
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

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public boolean isAtiva() {
        return ativa;
    }

    void setAtiva(boolean ativa) {
        this.ativa = ativa; // usado pelo RepositorioDados ao carregar
    }

    public Professor getProfessor() {
        return professor;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
