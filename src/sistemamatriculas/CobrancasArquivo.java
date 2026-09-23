package sistemamatriculas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

/**
 * Implementação do sistema de cobranças (RF11): como é um sistema externo,
 * o protótipo simula a integração registrando cada notificação no arquivo
 * dados/cobrancas.log.
 */
public class CobrancasArquivo implements SistemaCobrancas {

    private static final Path ARQUIVO = Path.of("dados", "cobrancas.log");

    @Override
    public void notificarMatricula(Matricula matricula) {
        String linha = String.format("%s;aluno=%s;disciplina=%s;tipo=%s%n",
                LocalDateTime.now(),
                matricula.getAluno().getLogin(),
                matricula.getDisciplina().getCodigo(),
                matricula.getTipo());
        try {
            Files.createDirectories(ARQUIVO.getParent());
            Files.writeString(ARQUIVO, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("[Cobrancas] Sistema de cobrancas notificado (dados/cobrancas.log).");
        } catch (IOException e) {
            System.out.println("[Cobrancas] Falha ao notificar: " + e.getMessage());
        }
    }
}
