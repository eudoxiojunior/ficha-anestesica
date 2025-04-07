package com.example.teste_api;

import com.example.teste_api.FichaAnestesicaPDF;
import com.example.teste_api.FichaAnestesicaCompletaPDF;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @GetMapping("/gerar-ficha")
    public ResponseEntity<byte[]> gerarFichaAnestesica() throws IOException {
        // Gera o PDF (usando a classe que você já tem)
        String dest = "ficha_anestesica.pdf";
        FichaAnestesicaPDF.gerarPdf(dest);
        
        // Lê o arquivo gerado
        File file = new File(dest);
        byte[] contents = Files.readAllBytes(file.toPath());

        // Configura a resposta para download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", dest);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }
   
    @GetMapping(value = "/gerar-ficha-sobreposta", produces = "application/pdf")
    public ResponseEntity<byte[]> gerarFichaAnestesicaCompleta() throws IOException {
        // Gera o PDF (usando a classe que você já tem)
        //String dest = System.getProperty("java.io.tmpdir") + "/ficha_anestesica.pdf";	// Gera em diretorio temporário
        String dest = "ficha_anestesica.pdf";
        FichaAnestesicaCompletaPDF.gerarPdf(dest);
        
        // Lê o arquivo gerado
        File file = new File(dest);
        byte[] contents = Files.readAllBytes(file.toPath());

        // Configura a resposta para download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", dest);	// Download
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }

    @GetMapping(value = "/deepseek", produces = "application/pdf")
    public ResponseEntity<byte[]> gerarFichaAnestesicaDeepSeek() throws IOException {
        // Gera o PDF (usando a classe que você já tem)
        //String dest = System.getProperty("java.io.tmpdir") + "/ficha_anestesica.pdf";	// Gera em diretorio temporário
        String dest = "ficha_anestesica.pdf";
        FichaAnestesicaDeepSeek.gerarPdf(dest);
        
        // Lê o arquivo gerado
        File file = new File(dest);
        byte[] contents = Files.readAllBytes(file.toPath());

        // Configura a resposta para download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", dest);	// Download
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }

    @GetMapping(value = "/teste")
    public void testePDF() throws Exception {
    	ColspanRowspan.executa();
    }

}
