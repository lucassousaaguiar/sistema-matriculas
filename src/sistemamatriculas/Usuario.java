package sistemamatriculas;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        return senhaHash.equals(gerarHash(senha));
    }

    /** Gera o hash SHA-256 (em hexadecimal) de uma senha (RNF04). */
    public static String gerarHash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
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
