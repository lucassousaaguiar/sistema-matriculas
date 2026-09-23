package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Aluno: matricula-se em até 4 disciplinas obrigatórias e 2 optativas (RN02)
 * e pode cancelar matrículas dentro do período (RF06, RF07).
 */
public class Aluno extends Usuario {

    private String matriculaAcad;
    private List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String login, String senhaHash, String nome, String matriculaAcad) {
        super(login, senhaHash, nome);
        this.matriculaAcad = matriculaAcad;
    }

    /** Matricula o aluno na disciplina, respeitando RN02, RN04 e RN05. */
    public boolean matricular(Disciplina disciplina, TipoMatricula tipo) {
        // TODO: implementar na Sprint 3
        return false;
    }

    /** Cancela uma matrícula feita anteriormente, dentro do período (RF07). */
    public void cancelarMatricula(Disciplina disciplina) {
        // TODO: implementar na Sprint 3
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public String getMatriculaAcad() {
        return matriculaAcad;
    }
}
