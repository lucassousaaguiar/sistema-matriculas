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
            System.out.println("1. Ver disciplinas ofertadas");
            System.out.println("2. Matricular em disciplina");
            System.out.println("3. Cancelar matricula");
            System.out.println("4. Minhas matriculas");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> listarOfertadas();
                case "2" -> matricular(aluno);
                case "3" -> cancelar(aluno);
                case "4" -> minhasMatriculas(aluno);
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
        for (Disciplina d : c.getDisciplinas()) {
            System.out.printf("  %-7s %-35s %d creditos | prof. %-16s | %2d/%d matriculados%s%n",
                    d.getCodigo(), d.getNome(), d.getCreditos(),
                    d.getProfessor() == null ? "-" : d.getProfessor().getNome(),
                    d.qtdMatriculados(), Disciplina.MAX_ALUNOS,
                    c.periodoAberto() ? "" : (d.isAtiva() ? " [ATIVA]" : " [CANCELADA]"));
        }
    }

    private static void matricular(Aluno aluno) {
        Disciplina d = pedirDisciplina();
        if (d == null) {
            return;
        }
        String t = ler("Tipo - (O)brigatoria ou o(P)tativa: ").trim().toUpperCase();
        TipoMatricula tipo = t.startsWith("P") ? TipoMatricula.OPTATIVA : TipoMatricula.OBRIGATORIA;
        if (sistema.matricular(aluno, d, tipo)) {
            System.out.println("Matricula realizada em " + d.getCodigo() + " como " + tipo + ".");
        } else {
            System.out.println("Nao foi possivel matricular. Verifique: periodo aberto, "
                    + "limites (4 obrigatorias / 2 optativas), vagas e matricula duplicada.");
        }
    }

    private static void cancelar(Aluno aluno) {
        Disciplina d = pedirDisciplina();
        if (d == null) {
            return;
        }
        if (sistema.cancelarMatricula(aluno, d)) {
            System.out.println("Matricula em " + d.getCodigo() + " cancelada.");
        } else {
            System.out.println("Nao foi possivel cancelar (sem matricula nessa disciplina ou periodo encerrado).");
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
            System.out.printf("  %-7s %-35s %-12s em %s%n", m.getDisciplina().getCodigo(),
                    m.getDisciplina().getNome(), m.getTipo(), m.getData());
        }
    }

    // ---------------- Menu do professor ----------------

    private static void menuProfessor(Professor professor) {
        while (true) {
            System.out.println();
            System.out.println("--- MENU DO PROFESSOR ---");
            System.out.println("1. Minhas disciplinas");
            System.out.println("2. Alunos matriculados em uma disciplina");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> {
                    if (professor.getDisciplinas().isEmpty()) {
                        System.out.println("Voce nao leciona nenhuma disciplina.");
                    }
                    for (Disciplina d : professor.getDisciplinas()) {
                        System.out.printf("  %-7s %-35s %d matriculados%n",
                                d.getCodigo(), d.getNome(), d.qtdMatriculados());
                    }
                }
                case "2" -> {
                    Disciplina d = pedirDisciplina();
                    if (d == null) {
                        break;
                    }
                    List<Aluno> alunos = professor.listarAlunos(d);
                    if (!professor.getDisciplinas().contains(d)) {
                        System.out.println("Voce nao leciona essa disciplina.");
                    } else if (alunos.isEmpty()) {
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
            System.out.println("1. Listar disciplinas");
            System.out.println("2. Cadastrar disciplina");
            System.out.println("3. Remover disciplina");
            System.out.println("4. Listar/cadastrar professores");
            System.out.println("5. Listar/cadastrar alunos");
            System.out.println("6. Gerar curriculo do semestre");
            System.out.println("7. Encerrar periodo de matriculas");
            System.out.println("0. Sair (logout)");
            switch (ler("Opcao: ")) {
                case "1" -> listarOfertadas();
                case "2" -> cadastrarDisciplina();
                case "3" -> removerDisciplina();
                case "4" -> professoresMenu();
                case "5" -> alunosMenu();
                case "6" -> gerarCurriculo();
                case "7" -> encerrarPeriodo();
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
        Professor prof = sistema.buscarProfessor(ler("Login do professor responsavel: "));
        if (prof == null) {
            System.out.println("Professor nao encontrado.");
            return;
        }
        sistema.cadastrarDisciplina(codigo, nome, creditos, prof);
        if (sistema.getCurriculoAtual() != null
                && simNao("Incluir no curriculo atual (s/n)? ")) {
            sistema.getCurriculoAtual().getDisciplinas().add(sistema.buscarDisciplina(codigo));
        }
        System.out.println("Disciplina cadastrada.");
    }

    private static void removerDisciplina() {
        Disciplina d = pedirDisciplina();
        if (d == null) {
            return;
        }
        if (sistema.removerDisciplina(d)) {
            System.out.println("Disciplina removida.");
        } else {
            System.out.println("Nao e possivel remover: ha alunos matriculados.");
        }
    }

    private static void professoresMenu() {
        for (Professor p : sistema.getProfessores()) {
            System.out.printf("  %-10s %-25s %d disciplina(s)%n",
                    p.getLogin(), p.getNome(), p.getDisciplinas().size());
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
            System.out.printf("  %-10s %-25s matricula %s | %d disciplina(s)%n",
                    a.getLogin(), a.getNome(), a.getMatriculaAcad(), a.getMatriculas().size());
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
        sistema.gerarCurriculo(semestre, inicio, fim, sistema.getDisciplinas());
        System.out.println("Curriculo " + semestre + " gerado com "
                + sistema.getDisciplinas().size() + " disciplinas ofertadas.");
    }

    private static void encerrarPeriodo() {
        Curriculo c = sistema.getCurriculoAtual();
        if (c == null) {
            System.out.println("Nenhum curriculo gerado.");
            return;
        }
        if (!simNao("Encerrar o periodo de matriculas de " + c.getSemestre() + " (s/n)? ")) {
            return;
        }
        sistema.encerrarPeriodo();
        System.out.println("Periodo encerrado. Resultado (minimo " + Disciplina.MIN_ALUNOS + " alunos):");
        for (Disciplina d : c.getDisciplinas()) {
            System.out.printf("  %-7s %-35s %2d matriculados -> %s%n", d.getCodigo(), d.getNome(),
                    d.qtdMatriculados(), d.isAtiva() ? "ATIVA" : "CANCELADA");
        }
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

    private static Disciplina pedirDisciplina() {
        Disciplina d = sistema.buscarDisciplina(ler("Codigo da disciplina: ").trim());
        if (d == null) {
            System.out.println("Disciplina nao encontrada.");
        }
        return d;
    }
}
