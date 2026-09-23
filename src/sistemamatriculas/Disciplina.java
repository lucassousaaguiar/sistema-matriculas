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
    }

    /** Há vaga enquanto o número de matriculados for menor que MAX_ALUNOS (RF09). */
    public boolean temVaga() {
        // TODO: implementar na Sprint 3
        return false;
    }

    public int qtdMatriculados() {
        // TODO: implementar na Sprint 3
        return 0;
    }

    /**
     * Ao final do período: ativa a disciplina se tiver pelo menos MIN_ALUNOS
     * matriculados; caso contrário, cancela (RF10, RN03).
     */
    public void verificarAtivacao() {
        // TODO: implementar na Sprint 3
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

    public Professor getProfessor() {
        return professor;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
