package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Disciplina do currículo. A oferta em um semestre acontece através de suas
 * turmas (RN07) — professor, vagas e ativação ficam na classe Turma.
 */
public class Disciplina {

    private String codigo;
    private String nome;
    private int creditos;
    private List<Turma> turmas = new ArrayList<>();

    public Disciplina(String codigo, String nome, int creditos) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
    }

    public Turma buscarTurma(String codigoTurma) {
        for (Turma t : turmas) {
            if (t.getCodigo().equalsIgnoreCase(codigoTurma)) {
                return t;
            }
        }
        return null;
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

    public List<Turma> getTurmas() {
        return turmas;
    }
}
