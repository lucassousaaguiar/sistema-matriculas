package sistemamatriculas;

/**
 * Secretaria: gera o currículo do semestre e mantém os cadastros de
 * disciplinas, professores e alunos (RF02-RF05); encerra o período de
 * matrículas (RF10).
 */
public class Secretaria extends Usuario {

    public Secretaria(String login, String senhaHash, String nome) {
        super(login, senhaHash, nome);
    }

    /** Gera o currículo do semestre com as disciplinas ofertadas (RF05). */
    public Curriculo gerarCurriculo(String semestre) {
        // TODO: implementar na Sprint 3
        return null;
    }

    /** CRUD de disciplinas (RF02). */
    public void manterDisciplinas() {
        // TODO: implementar na Sprint 3
    }

    /** CRUD de professores (RF03). */
    public void manterProfessores() {
        // TODO: implementar na Sprint 3
    }

    /** CRUD de alunos (RF04). */
    public void manterAlunos() {
        // TODO: implementar na Sprint 3
    }

    /**
     * Encerra o período de matrículas: ativa disciplinas com pelo menos
     * MIN_ALUNOS matriculados e cancela as demais (RF10, RN03).
     */
    public void encerrarPeriodo(Curriculo curriculo) {
        // TODO: implementar na Sprint 3
    }
}
