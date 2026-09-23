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
     * Matricula o aluno em uma turma ofertada no currículo atual e notifica
     * o sistema de cobranças (RF06, RF08, RF09, RF11).
     */
    public boolean matricular(Aluno aluno, Turma turma, TipoMatricula tipo) {
        if (curriculoAtual == null || !curriculoAtual.periodoAberto()) {
            return false; // fora do período de matrículas (RN05)
        }
        if (!curriculoAtual.getTurmas().contains(turma)) {
            return false; // turma não ofertada neste semestre
        }
        boolean ok = aluno.matricular(turma, tipo);
        if (ok) {
            sistemaCobrancas.notificarMatricula(aluno.buscarMatricula(turma));
            repositorio.salvar(this);
        }
        return ok;
    }

    /** Cancela uma matrícula dentro do período (RF07, RF08). */
    public boolean cancelarMatricula(Aluno aluno, Turma turma) {
        if (curriculoAtual == null || !curriculoAtual.periodoAberto()) {
            return false;
        }
        if (aluno.buscarMatricula(turma) == null) {
            return false;
        }
        aluno.cancelarMatricula(turma);
        repositorio.salvar(this);
        return true;
    }

    /** Gera o currículo do semestre com as turmas ofertadas (RF05). */
    public Curriculo gerarCurriculo(String semestre, LocalDate inicio, LocalDate fim,
            List<Turma> ofertadas) {
        Curriculo c = new Curriculo(semestre, inicio, fim);
        c.getTurmas().addAll(ofertadas);
        this.curriculoAtual = c;
        repositorio.salvar(this);
        return c;
    }

    /**
     * Encerra o período de matrículas: cada turma é ativada (>= MIN_ALUNOS)
     * ou cancelada individualmente (RF10, RN03, RN07) e as matrículas das
     * turmas ativas são lançadas no histórico dos alunos (RN09).
     */
    public void encerrarPeriodo() {
        if (curriculoAtual == null || !curriculoAtual.periodoAberto()) {
            return; // evita encerrar (e lançar histórico) duas vezes
        }
        curriculoAtual.encerrarAgora();
        for (Turma t : curriculoAtual.getTurmas()) {
            t.verificarAtivacao();
            if (t.isAtiva()) {
                for (Matricula m : t.getMatriculas()) {
                    m.getAluno().getHistorico().add(new ItemHistorico(
                            t.getDisciplina(), t.getCodigo(), curriculoAtual.getSemestre()));
                }
            }
        }
        repositorio.salvar(this);
    }

    // ----- Cadastros mantidos pela secretaria (RF02-RF04, RF14) -----

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

    public Disciplina cadastrarDisciplina(String codigo, String nome, int creditos) {
        Disciplina d = new Disciplina(codigo, nome, creditos);
        disciplinas.add(d);
        if (!cursos.isEmpty()) {
            cursos.get(0).getDisciplinas().add(d);
        }
        repositorio.salvar(this);
        return d;
    }

    /** Cria uma turma de uma disciplina, com professor próprio (RF14, RN07). */
    public Turma cadastrarTurma(Disciplina disciplina, String codigoTurma, Professor professor) {
        if (disciplina.buscarTurma(codigoTurma) != null) {
            return null; // já existe turma com esse código na disciplina
        }
        Turma t = new Turma(codigoTurma, disciplina, professor);
        repositorio.salvar(this);
        return t;
    }

    /** Remove uma turma sem matrículas (RF14). */
    public boolean removerTurma(Turma t) {
        if (t.qtdMatriculados() > 0) {
            return false;
        }
        t.getDisciplina().getTurmas().remove(t);
        if (t.getProfessor() != null) {
            t.getProfessor().getTurmas().remove(t);
        }
        if (curriculoAtual != null) {
            curriculoAtual.getTurmas().remove(t);
        }
        repositorio.salvar(this);
        return true;
    }

    /** Remove uma disciplina cujas turmas não tenham matrículas (RF02). */
    public boolean removerDisciplina(Disciplina d) {
        for (Turma t : d.getTurmas()) {
            if (t.qtdMatriculados() > 0) {
                return false;
            }
        }
        for (Turma t : new ArrayList<>(d.getTurmas())) {
            removerTurma(t);
        }
        disciplinas.remove(d);
        for (Curso c : cursos) {
            c.getDisciplinas().remove(d);
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

    /** Busca por identificação "DISCIPLINA/TURMA" (ex.: ENG101/T1). */
    public Turma buscarTurma(String codigoDisciplina, String codigoTurma) {
        Disciplina d = buscarDisciplina(codigoDisciplina);
        return d == null ? null : d.buscarTurma(codigoTurma);
    }

    /** Todas as turmas de todas as disciplinas. */
    public List<Turma> getTurmas() {
        List<Turma> turmas = new ArrayList<>();
        for (Disciplina d : disciplinas) {
            turmas.addAll(d.getTurmas());
        }
        return turmas;
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

    /** Dados iniciais da primeira execução (usuários, turmas e currículo de exemplo). */
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

        Disciplina d1 = new Disciplina("ENG101", "Algoritmos e Estruturas de Dados", 4);
        Disciplina d2 = new Disciplina("ENG102", "Engenharia de Software", 4);
        Disciplina d3 = new Disciplina("ENG103", "Banco de Dados", 4);
        Disciplina d4 = new Disciplina("ENG104", "Arquitetura de Software", 4);
        Disciplina d5 = new Disciplina("ENG105", "Redes de Computadores", 2);
        Disciplina d6 = new Disciplina("ENG106", "Inteligencia Artificial", 2);
        for (Disciplina d : List.of(d1, d2, d3, d4, d5, d6)) {
            disciplinas.add(d);
            es.getDisciplinas().add(d);
        }

        // ENG101 com duas turmas independentes (RN07); demais com uma
        new Turma("T1", d1, p1);
        new Turma("T2", d1, p2);
        new Turma("T1", d2, p2);
        new Turma("T1", d3, p1);
        new Turma("T1", d4, p2);
        new Turma("T1", d5, p1);
        new Turma("T1", d6, p2);

        Curriculo c = new Curriculo("2026/2", LocalDate.now().minusDays(7), LocalDate.now().plusDays(30));
        c.getTurmas().addAll(getTurmas());
        this.curriculoAtual = c;
    }
}
