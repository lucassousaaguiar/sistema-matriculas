package sistemamatriculas;

/**
 * Classe base de todos os usuários do sistema (RF01).
 * Aluno, Professor e Secretaria herdam desta classe.
 */
public abstract class Usuario {

    private String login;
    private String senhaHash;
    private String nome;

    public Usuario(String login, String senhaHash, String nome) {
        this.login = login;
        this.senhaHash = senhaHash;
        this.nome = nome;
    }

    /** Valida a senha informada contra o hash armazenado (RNF04). */
    public boolean autenticar(String senha) {
        // TODO: implementar na Sprint 3 (comparar hash da senha)
        return false;
    }

    public String getLogin() {
        return login;
    }

    public String getNome() {
        return nome;
    }

    protected String getSenhaHash() {
        return senhaHash;
    }
}
