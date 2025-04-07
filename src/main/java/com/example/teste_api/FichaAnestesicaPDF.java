package com.example.teste_api;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class FichaAnestesicaPDF {

    private static final String DEST = "ficha_anestesica.pdf";
    private static final String[] PARAMETROS = {
        "Pressão Arterial", 
        "Frequência Cardíaca", 
        "Frequência Respiratória"
    };
    private static final Color[] CORES = {
        ColorConstants.BLUE,
        ColorConstants.RED,
        ColorConstants.GREEN
    };
    private static final String[] SIMBOLOS = {"quadrado", "circulo", "triangulo"};
    
    public static void gerarPdf(String dest) throws IOException {
        File file = new File(DEST);
        criarDiretorioSeNaoExistir(file);

        try (PdfWriter writer = new PdfWriter(DEST);
             PdfDocument pdf = new PdfDocument(writer)) {

            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(30, 20, 40, 20); // Margens ajustadas

            adicionarTitulo(document);
            adicionarTabelaParametros(document);
            desenharGraficos(pdf, document);
            adicionarLegenda(document);

            document.close();
            System.out.println("PDF gerado com sucesso em: " + DEST);
        }
    }

    private static void criarDiretorioSeNaoExistir(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Falha ao criar diretório: " + parent.getAbsolutePath());
        }
    }

    private static void adicionarTitulo(Document document) {
        document.add(new Paragraph("Ficha de Monitorização Anestésica")
//        		.setBold()
        		.setFontSize(16)
        		.setTextAlignment(TextAlignment.CENTER)
        		.setMarginBottom(20));
    }

    private static void adicionarTabelaParametros(Document document) {
    	UnitValue[] columnWidths = new UnitValue[10];
    	for (int i = 0; i < 10; i++) {
    	    columnWidths[i] = UnitValue.createPercentValue(8f); // 10 colunas x 8% = 80% (deixa 20% para margens)
    	}

    	Table table = new Table(UnitValue.createPercentArray(new float[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8}));
    	table.setWidth(UnitValue.createPercentValue(90)); // 90% da largura útil
    	table.setFixedLayout(); // Layout fixo para melhor controle
    	
    	/*
    	table.setPadding(2);
    	table.setMarginTop(5);
    	table.setMarginBottom(5);
    	*/

        // Cabeçalho
    	/*
        table.addHeaderCell(new Cell().add(new Paragraph("Parâmetro")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        for (int i = 0; i < columnWidths.length; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph((i * 5) + "m")));
        }
        */
    	// 2. Configurar células do cabeçalho
    	for (int i = 0; i < 10; i++) {
    	    Cell cell = new Cell()
    	        .add(new Paragraph((i * 5) + " min")
    	            .setFontSize(8) // Fonte menor
    	            .setPadding(1)); // Espaçamento reduzido
    	    table.addHeaderCell(cell);
    	}

        // Linhas
    	/*
        for (String parametro : PARAMETROS) {
            table.addCell(new Cell().add(new Paragraph(parametro))
            		//.setBold()
            		);
            for (int i = 1; i < columnWidths.length; i++) {
                table.addCell(new Cell().add(new Paragraph("")));
            }
            
        }
        */
    	// 3. Configurar células de conteúdo
    	for (String parametro : PARAMETROS) {
    	    table.addCell(new Cell()
    	        .add(new Paragraph(parametro)
    	            .setFontSize(8)
    	            .setPadding(1)));
    	    
    	    for (int i = 0; i < 9; i++) { // Note que são 9 células agora
    	        table.addCell(new Cell()
    	            .add(new Paragraph(" "))
    	            .setPadding(1));
    	    }
    	}

        document.add(table);
        document.add(new LineSeparator(new SolidLine()).setMarginTop(10).setMarginBottom(15));
    }

    private static void adicionarLegenda(Document document) {
        Paragraph legenda = new Paragraph("Legenda: ")
  //          .setBold()
            .setFontSize(8);
        
        for (int i = 0; i < PARAMETROS.length; i++) {
            legenda.add(new Text(" ")
                .setFontColor(CORES[i]))
                .add(PARAMETROS[i] + (i < PARAMETROS.length - 1 ? " | " : ""));
        }
        
        document.add(legenda.setMarginBottom(15));
    }

    private static void desenharGraficos(PdfDocument pdf, Document document) throws IOException {
        PdfPage page = pdf.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle pageSize = page.getPageSize();

        // Configurações do gráfico
        float graphXStart = 50;
        float graphYStart = pageSize.getHeight() / 2;
        float graphWidth = pageSize.getWidth() - 100;
        float graphHeight = 200;
        float columnSpacing = graphWidth / 10;

        // Dados simulados com limites realistas
        int[][] dados = {
            new Random().ints(10, 90, 140).toArray(),  // Pressão
            new Random().ints(10, 60, 100).toArray(),   // FC
            new Random().ints(10, 12, 25).toArray()     // FR
        };

        // Desenhar eixos com rótulos
        desenharEixos(canvas, graphXStart, graphYStart, graphWidth, graphHeight);

        // Desenhar séries de dados
        for (int i = 0; i < PARAMETROS.length; i++) {
            desenharSerie(
                canvas, 
                graphXStart, 
                graphYStart, 
                columnSpacing, 
                graphHeight,
                dados[i], 
                CORES[i], 
                SIMBOLOS[i],
                i == 0 // Apenas adicionar rótulos Y na primeira série
            );
        }

        // Rótulo do eixo X
        canvas.beginText()
            .setFontAndSize(PdfFontFactory.createFont(), 10)
            .moveText(graphXStart + graphWidth / 2 - 30, graphYStart - 25)
            .showText("Tempo (minutos)")
            .endText();
    }

    private static void desenharEixos(PdfCanvas canvas, float x, float y, float width, float height) throws IOException {
        canvas.setStrokeColor(ColorConstants.BLACK)
            .setLineWidth(1f)
            .moveTo(x, y)
            .lineTo(x, y + height)
            .lineTo(x + width, y + height)
            .stroke();

        // Marcas do eixo X
        for (int i = 0; i <= 10; i++) {
            float posX = x + (i * (width / 10));
            canvas.moveTo(posX, y + height)
                .lineTo(posX, y + height + 5)
                .stroke();
            
            if (i % 2 == 0) {
                canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(), 8)
                    .moveText(posX - 5, y + height - 15)
                    .showText(String.valueOf(i * 5))
                    .endText();
            }
        }
    }

    private static void desenharSerie(PdfCanvas canvas, float xStart, float yStart, 
                                    float spacing, float height, int[] dados, 
                                    Color cor, String simbolo, boolean addEixoY) throws IOException {
        
        // Calcular escala dinâmica
        int min = Arrays.stream(dados).min().getAsInt();
        int max = Arrays.stream(dados).max().getAsInt();
        float scaleY = height / (max - min + 10); // +10 para margem

        canvas.setStrokeColor(cor).setFillColor(cor);

        // Desenhar rótulos do eixo Y (apenas na primeira série)
        if (addEixoY) {
            for (int val = min; val <= max; val += (max - min) / 3) {
                float yPos = yStart + (val - min) * scaleY;
                canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(), 8)
                    .moveText(xStart - 25, yPos - 5)
                    .showText(String.valueOf(val))
                    .endText();
                
                canvas.setStrokeColor(ColorConstants.LIGHT_GRAY)
                    .moveTo(xStart, yPos)
                    .lineTo(xStart + spacing * 10, yPos)
                    .stroke()
                    .setStrokeColor(cor);
            }
        }

        // Desenhar pontos e linhas
        float prevX = 0, prevY = 0;
        for (int i = 0; i < dados.length; i++) {
            float x = xStart + (i * spacing);
            float y = yStart + (dados[i] - min) * scaleY;

            switch (simbolo) {
                case "circulo":
                    canvas.circle(x, y, 3).fill();
                    break;
                case "quadrado":
                    canvas.rectangle(x - 2.5f, y - 2.5f, 5, 5).fill();
                    break;
                case "triangulo":
                    canvas.moveTo(x, y + 3)
                        .lineTo(x - 3, y - 2)
                        .lineTo(x + 3, y - 2)
                        .closePathFillStroke();
                    break;
            }

            if (i > 0) {
                canvas.moveTo(prevX, prevY).lineTo(x, y).stroke();
            }

            prevX = x;
            prevY = y;
        }
    }
}
