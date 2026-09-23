package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Professor: consulta os alunos matriculados nas disciplinas que leciona (RF12).
 */
public class Professor extends Usuario {

    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(String login, String senhaHash, String nome) {
        super(login, senhaHash, nome);
    }

    /** Lista os alunos matriculados em uma disciplina lecionada por este professor. */
    public List<Aluno> listarAlunos(Disciplina disciplina) {
        // TODO: implementar na Sprint 3
        return new ArrayList<>();
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
