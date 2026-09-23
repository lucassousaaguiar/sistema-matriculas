package sistemamatriculas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aluno: matricula-se em até 4 disciplinas obrigatórias e 2 optativas (RN02)
 * e pode cancelar matrículas dentro do período (RF06, RF07).
 */
public class Aluno extends Usuario {

    public static final int MAX_OBRIGATORIAS = 4;
    public static final int MAX_OPTATIVAS = 2;

    private String matriculaAcad;
    private List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String login, String senhaHash, String nome, String matriculaAcad) {
        super(login, senhaHash, nome);
        this.matriculaAcad = matriculaAcad;
    }

    /** Matricula o aluno na disciplina, respeitando RN02 e RN04. */
    public boolean matricular(Disciplina disciplina, TipoMatricula tipo) {
        if (buscarMatricula(disciplina) != null) {
            return false; // já matriculado nesta disciplina
        }
        if (contarPorTipo(tipo) >= limiteDoTipo(tipo)) {
            return false; // estourou o limite de obrigatórias/optativas (RN02)
        }
        if (!disciplina.temVaga()) {
            return false; // disciplina lotada (RN04)
        }
        Matricula m = new Matricula(this, disciplina, tipo, LocalDate.now());
        matriculas.add(m);
        disciplina.getMatriculas().add(m);
        return true;
    }

    /** Cancela uma matrícula feita anteriormente (RF07). */
    public void cancelarMatricula(Disciplina disciplina) {
        Matricula m = buscarMatricula(disciplina);
        if (m != null) {
            m.cancelar();
        }
    }

    public Matricula buscarMatricula(Disciplina disciplina) {
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

    public String getMatriculaAcad() {
        return matriculaAcad;
    }
}
