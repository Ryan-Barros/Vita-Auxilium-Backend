package com.vitaauxilium.vitaauxilium.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TemplateEngine templateEngine;

    public byte[] generatePdfReport(String templateName, Map<String, Object> dados) {
        // 1. Preenche os dados no contexto do Thymeleaf
        Context context = new Context();
        context.setVariables(dados);

        // 2. Renderiza o template HTML como String
        String htmlProcessado = templateEngine.process(templateName, context);

        // 3. Converte a String HTML em PDF usando OpenHTMLToPDF
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlProcessado, null);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF!", e);
        }
    }

    public byte generateActivityReport(UUID id) {
        return 0;
    }

    public byte generateSensorialReport(UUID id) {
        return 0;
    }
}
