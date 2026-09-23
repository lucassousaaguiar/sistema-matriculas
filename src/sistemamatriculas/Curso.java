package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Curso: tem nome, número de créditos e é constituído por diversas
 * disciplinas (RN01).
 */
public class Curso {

    private String nome;
    private int creditos;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String nome, int creditos) {
        this.nome = nome;
        this.creditos = creditos;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
