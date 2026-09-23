package sistemamatriculas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interface de linha de comando do protótipo (Lab02S03).
 * Saida do console sem acentos para compatibilidade com o terminal do Windows.
 */
public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final SistemaMatriculas sistema = new SistemaMatriculas(new CobrancasArquivo());

    public static void main(String[] args) {
        sistema.inicializar();
        System.out.println("==============================================");
        System.out.println("  SISTEMA DE MATRICULAS - PUC Minas (prototipo)");
        System.out.println("==============================================");

        while (true) {
            System.out.println();
            String login = ler("Login (ou 'sair'): ");
            if (login.equalsIgnoreCase("sair")) {
                System.out.println("Ate logo!");
                return;
            }
            String senha = ler("Senha: ");
            Usuario u = sistema.login(login, senha);
            if (u == null) {
                System.out.println("Login ou senha invalidos.");
            } else {
                System.out.println("Bem-vindo(a), " + u.getNome() + "!");
                if (u instanceof Aluno a) {
                    menuAluno(a);
                } else if (u instanceof Professor p) {
                    menuProfessor(p);
                } else if (u instanceof Secretaria) {
                    menuSecretaria();
                }
            }
        }
    }

    // ---------------- Menu do aluno ----------------

    private static void menuAluno(Aluno aluno) {
        while (true) {
            System.out.println();
            System.out.println("--- MENU DO ALUNO ---");
            System.out.println("1. Ver turmas ofertadas");
            System.out.println("2. Matricular em turma");
            System.out.println("3. Cancelar matricula");
            System.out.println("4. Minhas matriculas");
            System.out.println("5. Meu historico");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> listarOfertadas();
                case "2" -> matricular(aluno);
                case "3" -> cancelar(aluno);
                case "4" -> minhasMatriculas(aluno);
                case "5" -> meuHistorico(aluno);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private static void listarOfertadas() {
        Curriculo c = sistema.getCurriculoAtual();
        if (c == null) {
            System.out.println("Nenhum curriculo gerado ainda.");
            return;
        }
        System.out.printf("Curriculo %s | matriculas de %s a %s | periodo %s%n",
                c.getSemestre(), c.getInicioMatriculas(), c.getFimMatriculas(),
                c.periodoAberto() ? "ABERTO" : "ENCERRADO");
        for (Turma t : c.getTurmas()) {
            System.out.printf("  %-10s %-33s %d creditos | prof. %-16s | %2d/%d matriculados%s%n",
                    t.getIdentificacao(), t.getDisciplina().getNome(), t.getDisciplina().getCreditos(),
                    t.getProfessor() == null ? "-" : t.getProfessor().getNome(),
                    t.qtdMatriculados(), Turma.MAX_ALUNOS,
                    c.periodoAberto() ? "" : (t.isAtiva() ? " [ATIVA]" : " [CANCELADA]"));
        }
    }

    private static void matricular(Aluno aluno) {
        Turma t = pedirTurma();
        if (t == null) {
            return;
        }
        String tipoTxt = ler("Tipo - (O)brigatoria ou o(P)tativa: ").trim().toUpperCase();
        TipoMatricula tipo = tipoTxt.startsWith("P") ? TipoMatricula.OPTATIVA : TipoMatricula.OBRIGATORIA;
        if (sistema.matricular(aluno, t, tipo)) {
            System.out.println("Matricula realizada em " + t.getIdentificacao() + " como " + tipo + ".");
        } else {
            System.out.println("Nao foi possivel matricular. Verifique: periodo aberto, limites "
                    + "(4 obrigatorias / 2 optativas), vagas da turma e se ja ha matricula em "
                    + "outra turma desta disciplina.");
        }
    }

    private static void cancelar(Aluno aluno) {
        Turma t = pedirTurma();
        if (t == null) {
            return;
        }
        if (sistema.cancelarMatricula(aluno, t)) {
            System.out.println("Matricula em " + t.getIdentificacao() + " cancelada.");
        } else {
            System.out.println("Nao foi possivel cancelar (sem matricula nessa turma ou periodo encerrado).");
        }
    }

    private static void minhasMatriculas(Aluno aluno) {
        if (aluno.getMatriculas().isEmpty()) {
            System.out.println("Voce nao possui matriculas.");
            return;
        }
        System.out.printf("Obrigatorias: %d/%d | Optativas: %d/%d%n",
                aluno.contarPorTipo(TipoMatricula.OBRIGATORIA), Aluno.MAX_OBRIGATORIAS,
                aluno.contarPorTipo(TipoMatricula.OPTATIVA), Aluno.MAX_OPTATIVAS);
        for (Matricula m : aluno.getMatriculas()) {
            System.out.printf("  %-10s %-33s %-12s em %s%n", m.getTurma().getIdentificacao(),
                    m.getDisciplina().getNome(), m.getTipo(), m.getData());
        }
    }

    private static void meuHistorico(Aluno aluno) {
        if (aluno.getHistorico().isEmpty()) {
            System.out.println("Historico vazio (nenhuma disciplina cursada ainda).");
            return;
        }
        System.out.println("Semestre | Disciplina                          | Turma");
        for (ItemHistorico h : aluno.getHistorico()) {
            System.out.printf("  %-7s | %-7s %-27s | %s%n", h.getSemestre(),
                    h.getDisciplina().getCodigo(), h.getDisciplina().getNome(), h.getTurma());
        }
    }

    // ---------------- Menu do professor ----------------

    private static void menuProfessor(Professor professor) {
        while (true) {
            System.out.println();
            System.out.println("--- MENU DO PROFESSOR ---");
            System.out.println("1. Minhas turmas");
            System.out.println("2. Alunos matriculados em uma turma");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> {
                    if (professor.getTurmas().isEmpty()) {
                        System.out.println("Voce nao leciona nenhuma turma.");
                    }
                    for (Turma t : professor.getTurmas()) {
                        System.out.printf("  %-10s %-33s %d matriculados%n",
                                t.getIdentificacao(), t.getDisciplina().getNome(), t.qtdMatriculados());
                    }
                }
                case "2" -> {
                    Turma t = pedirTurma();
                    if (t == null) {
                        break;
                    }
                    if (!professor.getTurmas().contains(t)) {
                        System.out.println("Voce nao leciona essa turma.");
                        break;
                    }
                    List<Aluno> alunos = professor.listarAlunos(t);
                    if (alunos.isEmpty()) {
                        System.out.println("Nenhum aluno matriculado.");
                    } else {
                        for (Aluno a : alunos) {
                            System.out.printf("  %-10s %-25s (matricula %s)%n",
                                    a.getLogin(), a.getNome(), a.getMatriculaAcad());
                        }
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    // ---------------- Menu da secretaria ----------------

    private static void menuSecretaria() {
        while (true) {
            System.out.println();
            System.out.println("--- MENU DA SECRETARIA ---");
            System.out.println("1. Listar turmas ofertadas");
            System.out.println("2. Cadastrar disciplina");
            System.out.println("3. Remover disciplina");
            System.out.println("4. Cadastrar turma");
            System.out.println("5. Remover turma");
            System.out.println("6. Listar/cadastrar professores");
            System.out.println("7. Listar/cadastrar alunos");
            System.out.println("8. Gerar curriculo do semestre");
            System.out.println("9. Encerrar periodo de matriculas");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> listarOfertadas();
                case "2" -> cadastrarDisciplina();
                case "3" -> removerDisciplina();
                case "4" -> cadastrarTurma();
                case "5" -> removerTurma();
                case "6" -> professoresMenu();
                case "7" -> alunosMenu();
                case "8" -> gerarCurriculo();
                case "9" -> encerrarPeriodo();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private static void cadastrarDisciplina() {
        String codigo = ler("Codigo (ex.: ENG107): ").trim().toUpperCase();
        if (sistema.buscarDisciplina(codigo) != null) {
            System.out.println("Ja existe disciplina com esse codigo.");
            return;
        }
        String nome = ler("Nome: ");
        int creditos = lerInteiro("Creditos: ");
        sistema.cadastrarDisciplina(codigo, nome, creditos);
        System.out.println("Disciplina cadastrada. Cadastre turmas dela para poder oferta-la.");
    }

    private static void removerDisciplina() {
        Disciplina d = sistema.buscarDisciplina(ler("Codigo da disciplina: ").trim());
        if (d == null) {
            System.out.println("Disciplina nao encontrada.");
            return;
        }
        if (sistema.removerDisciplina(d)) {
            System.out.println("Disciplina e suas turmas removidas.");
        } else {
            System.out.println("Nao e possivel remover: ha alunos matriculados em alguma turma.");
        }
    }

    private static void cadastrarTurma() {
        Disciplina d = sistema.buscarDisciplina(ler("Codigo da disciplina: ").trim());
        if (d == null) {
            System.out.println("Disciplina nao encontrada.");
            return;
        }
        String codigo = ler("Codigo da turma (ex.: T1): ").trim().toUpperCase();
        Professor prof = sistema.buscarProfessor(ler("Login do professor da turma: ").trim());
        if (prof == null) {
            System.out.println("Professor nao encontrado.");
            return;
        }
        Turma t = sistema.cadastrarTurma(d, codigo, prof);
        if (t == null) {
            System.out.println("Ja existe turma " + codigo + " nessa disciplina.");
            return;
        }
        if (sistema.getCurriculoAtual() != null && simNao("Incluir no curriculo atual (s/n)? ")) {
            sistema.getCurriculoAtual().getTurmas().add(t);
        }
        System.out.println("Turma " + t.getIdentificacao() + " cadastrada.");
    }

    private static void removerTurma() {
        Turma t = pedirTurma();
        if (t == null) {
            return;
        }
        if (sistema.removerTurma(t)) {
            System.out.println("Turma removida.");
        } else {
            System.out.println("Nao e possivel remover: ha alunos matriculados na turma.");
        }
    }

    private static void professoresMenu() {
        for (Professor p : sistema.getProfessores()) {
            System.out.printf("  %-10s %-25s %d turma(s)%n",
                    p.getLogin(), p.getNome(), p.getTurmas().size());
        }
        if (simNao("Cadastrar novo professor (s/n)? ")) {
            String login = ler("Login: ").trim();
            String senha = ler("Senha: ");
            String nome = ler("Nome: ");
            sistema.cadastrarProfessor(login, senha, nome);
            System.out.println("Professor cadastrado.");
        }
    }

    private static void alunosMenu() {
        for (Aluno a : sistema.getAlunos()) {
            System.out.printf("  %-10s %-25s matricula %s | %d matricula(s) | %d no historico%n",
                    a.getLogin(), a.getNome(), a.getMatriculaAcad(),
                    a.getMatriculas().size(), a.getHistorico().size());
        }
        if (simNao("Cadastrar novo aluno (s/n)? ")) {
            String login = ler("Login: ").trim();
            String senha = ler("Senha: ");
            String nome = ler("Nome: ");
            String matricula = ler("Numero de matricula: ");
            sistema.cadastrarAluno(login, senha, nome, matricula);
            System.out.println("Aluno cadastrado.");
        }
    }

    private static void gerarCurriculo() {
        String semestre = ler("Semestre (ex.: 2027/1): ");
        LocalDate inicio = lerData("Inicio das matriculas (AAAA-MM-DD, vazio = hoje): ", LocalDate.now());
        LocalDate fim = lerData("Fim das matriculas (AAAA-MM-DD, vazio = hoje+30): ", LocalDate.now().plusDays(30));
        sistema.gerarCurriculo(semestre, inicio, fim, sistema.getTurmas());
        System.out.println("Curriculo " + semestre + " gerado com "
                + sistema.getTurmas().size() + " turmas ofertadas.");
    }

    private static void encerrarPeriodo() {
        Curriculo c = sistema.getCurriculoAtual();
        if (c == null) {
            System.out.println("Nenhum curriculo gerado.");
            return;
        }
        if (!c.periodoAberto()) {
            System.out.println("O periodo de matriculas ja esta encerrado.");
            return;
        }
        if (!simNao("Encerrar o periodo de matriculas de " + c.getSemestre() + " (s/n)? ")) {
            return;
        }
        sistema.encerrarPeriodo();
        System.out.println("Periodo encerrado. Resultado por turma (minimo " + Turma.MIN_ALUNOS + " alunos):");
        for (Turma t : c.getTurmas()) {
            System.out.printf("  %-10s %-33s %2d matriculados -> %s%n", t.getIdentificacao(),
                    t.getDisciplina().getNome(), t.qtdMatriculados(), t.isAtiva() ? "ATIVA" : "CANCELADA");
        }
        System.out.println("Historico lancado para os alunos das turmas ativas.");
    }

    // ---------------- Utilitarios de entrada ----------------

    private static String ler(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine() : "sair";
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(ler(prompt).trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro.");
            }
        }
    }

    private static LocalDate lerData(String prompt, LocalDate padrao) {
        while (true) {
            String txt = ler(prompt).trim();
            if (txt.isEmpty()) {
                return padrao;
            }
            try {
                return LocalDate.parse(txt);
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida. Use o formato AAAA-MM-DD.");
            }
        }
    }

    private static boolean simNao(String prompt) {
        return ler(prompt).trim().toLowerCase().startsWith("s");
    }

    /**
     * Pede uma turma no formato DISCIPLINA/TURMA (ex.: ENG101/T1). Se o
     * usuario informar so a disciplina e ela tiver uma unica turma, usa essa.
     */
    private static Turma pedirTurma() {
        String entrada = ler("Turma (ex.: ENG101/T1): ").trim().toUpperCase();
        String[] partes = entrada.split("/");
        Disciplina d = sistema.buscarDisciplina(partes[0]);
        if (d == null) {
            System.out.println("Disciplina nao encontrada.");
            return null;
        }
        if (partes.length >= 2) {
            Turma t = d.buscarTurma(partes[1]);
            if (t == null) {
                System.out.println("Turma nao encontrada nessa disciplina.");
            }
            return t;
        }
        if (d.getTurmas().size() == 1) {
            return d.getTurmas().get(0);
        }
        System.out.println("Essa disciplina tem mais de uma turma. Informe no formato DISCIPLINA/TURMA:");
        for (Turma t : d.getTurmas()) {
            System.out.println("  " + t.getIdentificacao());
        }
        return null;
    }
}
