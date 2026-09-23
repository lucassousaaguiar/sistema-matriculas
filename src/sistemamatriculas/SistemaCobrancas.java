package sistemamatriculas;

/**
 * Sistema externo de cobranças (ator «sistema externo» do diagrama de casos
 * de uso). É notificado a cada matrícula para cobrar o aluno pelas
 * disciplinas do semestre (RF11, RN06).
 */
public interface SistemaCobrancas {

    void notificarMatricula(Matricula matricula);
}
