package sistemamatriculas;

import java.util.ArrayList;
import java.util.List;

/**
 * Fachada do sistema: concentra as operações usadas pela interface de linha
 * de comando e faz a ponte com a persistência (RepositorioDados) e com o
 * sistema externo de cobranças (SistemaCobrancas).
 */
public class SistemaMatriculas {

    private List<Aluno> alunos = new ArrayList<>();
    private List<Professor> professores = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private Curriculo curriculoAtual;
    private RepositorioDados repositorio = new RepositorioDados();
    private SistemaCobrancas sistemaCobrancas;

    /** Autentica um usuário por login e senha (RF01). */
    public Usuario login(String login, String senha) {
        // TODO: implementar na Sprint 3
        return null;
    }

    /**
     * Matricula o aluno na disciplina e notifica o sistema de cobranças
     * (RF06, RF11).
     */
    public boolean matricular(Aluno aluno, Disciplina disciplina, TipoMatricula tipo) {
        // TODO: implementar na Sprint 3
        return false;
    }

    /** Cancela uma matrícula dentro do período (RF07, RF08). */
    public void cancelarMatricula(Aluno aluno, Disciplina disciplina) {
        // TODO: implementar na Sprint 3
    }

    /** Encerra o período de matrículas do currículo atual (RF10). */
    public void encerrarPeriodo() {
        // TODO: implementar na Sprint 3
    }

    public Curriculo getCurriculoAtual() {
        return curriculoAtual;
    }
}
