package sistemamatriculas;

import java.time.LocalDate;
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
    private List<Secretaria> secretarias = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Disciplina> disciplinas = new ArrayList<>();
    private Curriculo curriculoAtual;

    private RepositorioDados repositorio = new RepositorioDados();
    private SistemaCobrancas sistemaCobrancas;

    public SistemaMatriculas(SistemaCobrancas sistemaCobrancas) {
        this.sistemaCobrancas = sistemaCobrancas;
    }

    /** Carrega os dados dos arquivos; na primeira execução, cria os dados iniciais. */
    public void inicializar() {
        boolean carregou = repositorio.carregar(this);
        if (!carregou) {
            seedInicial();
            repositorio.salvar(this);
        }
    }

    /** Autentica um usuário por login e senha (RF01). */
    public Usuario login(String login, String senha) {
        for (Usuario u : todosUsuarios()) {
            if (u.getLogin().equals(login) && u.autenticar(senha)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Matricula o aluno em uma disciplina do currículo atual e notifica o
     * sistema de cobranças (RF06, RF08, RF09, RF11).
     */
    public boolean matricular(Aluno aluno, Disciplina disciplina, TipoMatricula tipo) {
        if (curriculoAtual == null || !curriculoAtual.periodoAberto()) {
            return false; // fora do período de matrículas (RN05)
        }
        if (!curriculoAtual.getDisciplinas().contains(disciplina)) {
            return false; // disciplina não ofertada neste semestre
        }
        boolean ok = aluno.matricular(disciplina, tipo);
        if (ok) {
            sistemaCobrancas.notificarMatricula(aluno.buscarMatricula(disciplina));
            repositorio.salvar(this);
        }
        return ok;
    }

    /** Cancela uma matrícula dentro do período (RF07, RF08). */
    public boolean cancelarMatricula(Aluno aluno, Disciplina disciplina) {
        if (curriculoAtual == null || !curriculoAtual.periodoAberto()) {
            return false;
        }
        if (aluno.buscarMatricula(disciplina) == null) {
            return false;
        }
        aluno.cancelarMatricula(disciplina);
        repositorio.salvar(this);
        return true;
    }

    /** Gera o currículo do semestre com as disciplinas ofertadas (RF05). */
    public Curriculo gerarCurriculo(String semestre, LocalDate inicio, LocalDate fim,
            List<Disciplina> ofertadas) {
        Curriculo c = new Curriculo(semestre, inicio, fim);
        c.getDisciplinas().addAll(ofertadas);
        this.curriculoAtual = c;
        repositorio.salvar(this);
        return c;
    }

    /**
     * Encerra o período de matrículas: ativa disciplinas com pelo menos
     * MIN_ALUNOS matriculados e cancela as demais (RF10, RN03).
     */
    public void encerrarPeriodo() {
        if (curriculoAtual == null) {
            return;
        }
        curriculoAtual.encerrarAgora();
        for (Disciplina d : curriculoAtual.getDisciplinas()) {
            d.verificarAtivacao();
        }
        repositorio.salvar(this);
    }

    // ----- Cadastros mantidos pela secretaria (RF02-RF04) -----

    public Aluno cadastrarAluno(String login, String senha, String nome, String matriculaAcad) {
        Aluno a = new Aluno(login, Usuario.gerarHash(senha), nome, matriculaAcad);
        alunos.add(a);
        repositorio.salvar(this);
        return a;
    }

    public Professor cadastrarProfessor(String login, String senha, String nome) {
        Professor p = new Professor(login, Usuario.gerarHash(senha), nome);
        professores.add(p);
        repositorio.salvar(this);
        return p;
    }

    public Disciplina cadastrarDisciplina(String codigo, String nome, int creditos, Professor professor) {
        Disciplina d = new Disciplina(codigo, nome, creditos, professor);
        disciplinas.add(d);
        if (!cursos.isEmpty()) {
            cursos.get(0).getDisciplinas().add(d);
        }
        repositorio.salvar(this);
        return d;
    }

    /** Remove uma disciplina sem matrículas; devolve false se houver matriculados. */
    public boolean removerDisciplina(Disciplina d) {
        if (d.qtdMatriculados() > 0) {
            return false;
        }
        disciplinas.remove(d);
        if (d.getProfessor() != null) {
            d.getProfessor().getDisciplinas().remove(d);
        }
        for (Curso c : cursos) {
            c.getDisciplinas().remove(d);
        }
        if (curriculoAtual != null) {
            curriculoAtual.getDisciplinas().remove(d);
        }
        repositorio.salvar(this);
        return true;
    }

    // ----- Buscas e acesso -----

    public Disciplina buscarDisciplina(String codigo) {
        for (Disciplina d : disciplinas) {
            if (d.getCodigo().equalsIgnoreCase(codigo)) {
                return d;
            }
        }
        return null;
    }

    public Professor buscarProfessor(String login) {
        for (Professor p : professores) {
            if (p.getLogin().equals(login)) {
                return p;
            }
        }
        return null;
    }

    public Aluno buscarAluno(String login) {
        for (Aluno a : alunos) {
            if (a.getLogin().equals(login)) {
                return a;
            }
        }
        return null;
    }

    private List<Usuario> todosUsuarios() {
        List<Usuario> todos = new ArrayList<>();
        todos.addAll(alunos);
        todos.addAll(professores);
        todos.addAll(secretarias);
        return todos;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public List<Secretaria> getSecretarias() {
        return secretarias;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public Curriculo getCurriculoAtual() {
        return curriculoAtual;
    }

    void setCurriculoAtual(Curriculo c) {
        this.curriculoAtual = c; // usado pelo RepositorioDados ao carregar
    }

    /** Dados iniciais da primeira execução (usuários e currículo de exemplo). */
    private void seedInicial() {
        secretarias.add(new Secretaria("secretaria", Usuario.gerarHash("admin123"), "Secretaria Academica"));

        Professor p1 = new Professor("mrezende", Usuario.gerarHash("prof123"), "Marcos Rezende");
        Professor p2 = new Professor("cassia", Usuario.gerarHash("prof123"), "Cassia Oliveira");
        professores.add(p1);
        professores.add(p2);

        alunos.add(new Aluno("lucas", Usuario.gerarHash("aluno123"), "Lucas Aguiar", "826571"));
        alunos.add(new Aluno("maria", Usuario.gerarHash("aluno123"), "Maria Souza", "826572"));
        alunos.add(new Aluno("joao", Usuario.gerarHash("aluno123"), "Joao Lima", "826573"));

        Curso es = new Curso("Engenharia de Software", 240);
        cursos.add(es);

        Disciplina d1 = new Disciplina("ENG101", "Algoritmos e Estruturas de Dados", 4, p1);
        Disciplina d2 = new Disciplina("ENG102", "Engenharia de Software", 4, p2);
        Disciplina d3 = new Disciplina("ENG103", "Banco de Dados", 4, p1);
        Disciplina d4 = new Disciplina("ENG104", "Arquitetura de Software", 4, p2);
        Disciplina d5 = new Disciplina("ENG105", "Redes de Computadores", 2, p1);
        Disciplina d6 = new Disciplina("ENG106", "Inteligencia Artificial", 2, p2);
        for (Disciplina d : List.of(d1, d2, d3, d4, d5, d6)) {
            disciplinas.add(d);
            es.getDisciplinas().add(d);
        }

        Curriculo c = new Curriculo("2026/2", LocalDate.now().minusDays(7), LocalDate.now().plusDays(30));
        c.getDisciplinas().addAll(disciplinas);
        this.curriculoAtual = c;
    }
}
