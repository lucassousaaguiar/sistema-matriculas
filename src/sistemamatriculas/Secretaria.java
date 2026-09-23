package sistemamatriculas;

/**
 * Secretaria: papel de acesso ao sistema. As operações administrativas
 * (manter cadastros, gerar currículo, encerrar período) são executadas pela
 * fachada SistemaMatriculas através do menu da secretaria na CLI — ajuste
 * registrado no diagrama de classes v2.
 */
public class Secretaria extends Usuario {

    public Secretaria(String login, String senhaHash, String nome) {
        super(login, senhaHash, nome);
    }
}
