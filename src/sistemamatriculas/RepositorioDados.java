package sistemamatriculas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistência dos dados do sistema em arquivos CSV (RNF03), na pasta dados/:
 * usuarios.csv, cursos.csv, disciplinas.csv, turmas.csv, curriculo.csv,
 * matriculas.csv e historico.csv.
 */
public class RepositorioDados {

    private static final Path PASTA = Path.of("dados");
    private static final String SEP = ";";

    /** Grava todo o estado do sistema em arquivos. */
    public void salvar(SistemaMatriculas s) {
        try {
            Files.createDirectories(PASTA);

            List<String> usuarios = new ArrayList<>();
            for (Secretaria u : s.getSecretarias()) {
                usuarios.add(String.join(SEP, "SECRETARIA", u.getLogin(), u.getSenhaHash(), u.getNome(), ""));
            }
            for (Professor u : s.getProfessores()) {
                usuarios.add(String.join(SEP, "PROFESSOR", u.getLogin(), u.getSenhaHash(), u.getNome(), ""));
            }
            for (Aluno u : s.getAlunos()) {
                usuarios.add(String.join(SEP, "ALUNO", u.getLogin(), u.getSenhaHash(), u.getNome(), u.getMatriculaAcad()));
            }
            Files.write(PASTA.resolve("usuarios.csv"), usuarios, StandardCharsets.UTF_8);

            List<String> disciplinas = new ArrayList<>();
            for (Disciplina d : s.getDisciplinas()) {
                disciplinas.add(String.join(SEP, d.getCodigo(), d.getNome(), String.valueOf(d.getCreditos())));
            }
            Files.write(PASTA.resolve("disciplinas.csv"), disciplinas, StandardCharsets.UTF_8);

            List<String> turmas = new ArrayList<>();
            for (Turma t : s.getTurmas()) {
                String prof = t.getProfessor() == null ? "" : t.getProfessor().getLogin();
                turmas.add(String.join(SEP, t.getDisciplina().getCodigo(), t.getCodigo(),
                        prof, String.valueOf(t.isAtiva())));
            }
            Files.write(PASTA.resolve("turmas.csv"), turmas, StandardCharsets.UTF_8);

            List<String> cursos = new ArrayList<>();
            for (Curso c : s.getCursos()) {
                List<String> codigos = new ArrayList<>();
                for (Disciplina d : c.getDisciplinas()) {
                    codigos.add(d.getCodigo());
                }
                cursos.add(String.join(SEP, c.getNome(), String.valueOf(c.getCreditos()), String.join(",", codigos)));
            }
            Files.write(PASTA.resolve("cursos.csv"), cursos, StandardCharsets.UTF_8);

            List<String> curriculo = new ArrayList<>();
            Curriculo c = s.getCurriculoAtual();
            if (c != null) {
                List<String> ids = new ArrayList<>();
                for (Turma t : c.getTurmas()) {
                    ids.add(t.getDisciplina().getCodigo() + ":" + t.getCodigo());
                }
                curriculo.add(String.join(SEP, c.getSemestre(), c.getInicioMatriculas().toString(),
                        c.getFimMatriculas().toString(), String.join(",", ids)));
            }
            Files.write(PASTA.resolve("curriculo.csv"), curriculo, StandardCharsets.UTF_8);

            List<String> matriculas = new ArrayList<>();
            for (Aluno a : s.getAlunos()) {
                for (Matricula m : a.getMatriculas()) {
                    matriculas.add(String.join(SEP, a.getLogin(), m.getDisciplina().getCodigo(),
                            m.getTurma().getCodigo(), m.getTipo().name(), m.getData().toString()));
                }
            }
            Files.write(PASTA.resolve("matriculas.csv"), matriculas, StandardCharsets.UTF_8);

            List<String> historico = new ArrayList<>();
            for (Aluno a : s.getAlunos()) {
                for (ItemHistorico h : a.getHistorico()) {
                    historico.add(String.join(SEP, a.getLogin(), h.getDisciplina().getCodigo(),
                            h.getTurma(), h.getSemestre()));
                }
            }
            Files.write(PASTA.resolve("historico.csv"), historico, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("[Erro] Falha ao salvar dados: " + e.getMessage());
        }
    }

    /** Carrega o estado dos arquivos; devolve false se ainda não existem dados. */
    public boolean carregar(SistemaMatriculas s) {
        Path usuarios = PASTA.resolve("usuarios.csv");
        if (!Files.exists(usuarios)) {
            return false;
        }
        try {
            for (String linha : Files.readAllLines(usuarios, StandardCharsets.UTF_8)) {
                String[] c = linha.split(SEP, -1);
                if (c.length < 4) {
                    continue;
                }
                switch (c[0]) {
                    case "SECRETARIA" -> s.getSecretarias().add(new Secretaria(c[1], c[2], c[3]));
                    case "PROFESSOR" -> s.getProfessores().add(new Professor(c[1], c[2], c[3]));
                    case "ALUNO" -> s.getAlunos().add(new Aluno(c[1], c[2], c[3], c[4]));
                    default -> { }
                }
            }

            Path disciplinas = PASTA.resolve("disciplinas.csv");
            if (Files.exists(disciplinas)) {
                for (String linha : Files.readAllLines(disciplinas, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 3) {
                        continue;
                    }
                    s.getDisciplinas().add(new Disciplina(c[0], c[1], Integer.parseInt(c[2])));
                }
            }

            Path turmas = PASTA.resolve("turmas.csv");
            if (Files.exists(turmas)) {
                for (String linha : Files.readAllLines(turmas, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 4) {
                        continue;
                    }
                    Disciplina d = s.buscarDisciplina(c[0]);
                    if (d == null) {
                        continue;
                    }
                    Turma t = new Turma(c[1], d, s.buscarProfessor(c[2]));
                    t.setAtiva(Boolean.parseBoolean(c[3]));
                }
            }

            Path cursos = PASTA.resolve("cursos.csv");
            if (Files.exists(cursos)) {
                for (String linha : Files.readAllLines(cursos, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 3) {
                        continue;
                    }
                    Curso curso = new Curso(c[0], Integer.parseInt(c[1]));
                    for (String codigo : c[2].split(",")) {
                        Disciplina d = s.buscarDisciplina(codigo);
                        if (d != null) {
                            curso.getDisciplinas().add(d);
                        }
                    }
                    s.getCursos().add(curso);
                }
            }

            Path curriculo = PASTA.resolve("curriculo.csv");
            if (Files.exists(curriculo)) {
                for (String linha : Files.readAllLines(curriculo, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 4) {
                        continue;
                    }
                    Curriculo cur = new Curriculo(c[0], LocalDate.parse(c[1]), LocalDate.parse(c[2]));
                    for (String id : c[3].split(",")) {
                        String[] partes = id.split(":");
                        if (partes.length == 2) {
                            Turma t = s.buscarTurma(partes[0], partes[1]);
                            if (t != null) {
                                cur.getTurmas().add(t);
                            }
                        }
                    }
                    s.setCurriculoAtual(cur);
                }
            }

            Path matriculas = PASTA.resolve("matriculas.csv");
            if (Files.exists(matriculas)) {
                for (String linha : Files.readAllLines(matriculas, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 5) {
                        continue;
                    }
                    Aluno a = s.buscarAluno(c[0]);
                    Turma t = s.buscarTurma(c[1], c[2]);
                    if (a != null && t != null) {
                        Matricula m = new Matricula(a, t, TipoMatricula.valueOf(c[3]), LocalDate.parse(c[4]));
                        a.getMatriculas().add(m);
                        t.getMatriculas().add(m);
                    }
                }
            }

            Path historico = PASTA.resolve("historico.csv");
            if (Files.exists(historico)) {
                for (String linha : Files.readAllLines(historico, StandardCharsets.UTF_8)) {
                    String[] c = linha.split(SEP, -1);
                    if (c.length < 4) {
                        continue;
                    }
                    Aluno a = s.buscarAluno(c[0]);
                    Disciplina d = s.buscarDisciplina(c[1]);
                    if (a != null && d != null) {
                        a.getHistorico().add(new ItemHistorico(d, c[2], c[3]));
                    }
                }
            }
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("[Erro] Falha ao carregar dados: " + e.getMessage());
            return false;
        }
    }
}
