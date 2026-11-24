
package br.edu.aesa.inkflow.service;

import br.edu.aesa.inkflow.domain.exception.RegraDeNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

@Service
public class ArmazenamentoImagemService {

    private static final long LIMITE_BYTES =
            10 * 1024 * 1024;

    private final Path diretorio;

    public ArmazenamentoImagemService(
            @Value("${inkflow.upload.diretorio:./uploads}")
            String diretorio) {

        this.diretorio = Paths.get(diretorio)
                .toAbsolutePath()
                .normalize();
    }

    public String armazenar(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException(
                    "Selecione uma imagem para enviar.");
        }

        if (arquivo.getSize() > LIMITE_BYTES) {
            throw new RegraDeNegocioException(
                    "A imagem deve ter no máximo 10 MB.");
        }

        String extensao = validarImagem(arquivo);

        String nomeArquivo =
                UUID.randomUUID() + extensao;

        try {

            Files.createDirectories(diretorio);

            Path destino = diretorio
                    .resolve(nomeArquivo)
                    .normalize();

            try (var entrada = arquivo.getInputStream()) {
                Files.copy(entrada, destino);
            }

            return nomeArquivo;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Não foi possível armazenar a imagem.", e);
        }
    }

    private String validarImagem(MultipartFile arquivo) {

        try (
            var entrada = arquivo.getInputStream();
            ImageInputStream imagem =
                    ImageIO.createImageInputStream(entrada)
        ) {

            if (imagem == null) {
                throw new RegraDeNegocioException(
                        "Arquivo de imagem inválido.");
            }

            Iterator<ImageReader> leitores =
                    ImageIO.getImageReaders(imagem);

            if (!leitores.hasNext()) {
                throw new RegraDeNegocioException(
                        "Envie uma imagem JPG ou PNG válida.");
            }

            ImageReader leitor = leitores.next();

            try {

                leitor.setInput(imagem);

                String formato = leitor.getFormatName();

                String extensao;

                if ("JPEG".equalsIgnoreCase(formato)) {
                    extensao = ".jpg";
                } else if ("PNG".equalsIgnoreCase(formato)) {
                    extensao = ".png";
                } else {
                    throw new RegraDeNegocioException(
                            "Apenas imagens JPG e PNG são permitidas.");
                }

                long largura = leitor.getWidth(0);
                long altura = leitor.getHeight(0);

                if (largura <= 0 || altura <= 0
                        || largura * altura > 25000000L) {

                    throw new RegraDeNegocioException(
                            "As dimensões da imagem são inválidas ou muito grandes.");
                }

                leitor.read(0);

                return extensao;

            } finally {
                leitor.dispose();
            }

        } catch (IOException e) {

            throw new RegraDeNegocioException(
                    "A imagem está inválida ou corrompida.");
        }
    }

    public Path getDiretorio() {
        return diretorio;
    }
}
