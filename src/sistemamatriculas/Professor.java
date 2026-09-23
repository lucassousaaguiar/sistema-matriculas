package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Professor: leciona turmas e consulta os alunos matriculados em cada uma
 * de suas turmas (RF12).
 */
public class Professor extends Usuario {

    private List<Turma> turmas = new ArrayList<>();

    public Professor(String login, String senhaHash, String nome) {
        super(login, senhaHash, nome);
    }

    /** Lista os alunos matriculados em uma turma lecionada por este professor. */
    public List<Aluno> listarAlunos(Turma turma) {
        List<Aluno> alunos = new ArrayList<>();
        if (turma != null && turmas.contains(turma)) {
            for (Matricula m : turma.getMatriculas()) {
                alunos.add(m.getAluno());
            }
        }
        return alunos;
    }

    public List<Turma> getTurmas() {
        return turmas;
    }
}
