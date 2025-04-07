package com.example.teste_api;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.io.font.constants.StandardFonts;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class FichaAnestesicaCompletaPDF {

    private static final String[] PARAMETROS = {
        "Medicamentos", 
        "Observações", 
        "240",
        " ",
        "210",
        " ",
        "180",
        " ",
        "150",
        " ",
        "120",
        " ",
        "90",
        " ",
        "60",
        " ",
        "30",
        " ",
    };
    
    private static final Color[] CORES = {
        ColorConstants.BLUE,
        ColorConstants.RED,
        ColorConstants.GREEN, 
        ColorConstants.YELLOW
    };
    
    private static int quantidadeDeColunas= 46;

    public static void gerarPdf(String dest) throws IOException {
        // Configuração inicial do documento
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(30, 20, 30, 20);

        // Adicionar título
        document.add(new Paragraph("Ficha de Monitorização Anestésica")
            .setBold()
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(15));

        // Criar e adicionar tabela
        Table table = criarTabela();
        int totalLinhas = PARAMETROS.length + 1; // Dados + cabeçalho
        float alturaLinha = calcularAlturaLinhasTabela(table, totalLinhas);
        document.add(table);
       
        // Adicionar espaço entre tabela e legenda
        document.add(new Paragraph("\n")); 

        // Adicionar legenda (agora como elemento do documento)
        document.add(adicionarLegenda());
        
        // 4. Desenhar gráfico sobre a tabela
        float temp = desenharGraficosSobrepostos(pdf,alturaLinha);

        // Adicionar espaço entre tabela e legenda
        document.add(new Paragraph("\n" + String.valueOf(temp))); 

        document.close();
    }

 // Novo método de legenda (retorna Paragraph)
    private static Paragraph adicionarLegenda() {
        Paragraph legenda = new Paragraph("Legenda: ")
            .setFontSize(10)
            .setMarginTop(10);
        
        // Adiciona itens da legenda com cores
        legenda.add(new Text("■ ").setFontColor(CORES[0]))
              .add("PAS   ")
              .add(new Text("▲ ").setFontColor(CORES[3]))
              .add("PAD   ")
              .add(new Text("● ").setFontColor(CORES[1]))
              .add("Frequência Cardíaca   ")
              .add(new Text("▲ ").setFontColor(CORES[2]))
              .add("Frequência Respiratória");
        return legenda;
    }

    private static Table criarTabela() {
    	float alturaFixa = 13f;
        // Configurar larguras das colunas (10 colunas de 8% cada)
        UnitValue[] columnWidths = new UnitValue[quantidadeDeColunas + 1];
        columnWidths[0] = UnitValue.createPointValue(80);
        for (int i = 1; i< columnWidths.length; i++) {
        	columnWidths[i] = UnitValue.createPercentValue(100f / quantidadeDeColunas);
        }

        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(90));
        table.setFixedLayout();

        // Cabeçalho da tabela
        table.addHeaderCell(
            new Cell().add(new Paragraph("Parâmetro").setBold().setFontSize(6))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
        );

        for (int i = 0; i < quantidadeDeColunas; i++) {
            table.addHeaderCell(
                new Cell().add(new Paragraph((i * 5) + "").setFontSize(5))
            );
        }

        // Conteúdo da tabela
        for (String parametro : PARAMETROS) {
            table.addCell(
                new Cell().add(new Paragraph(parametro).setBold().setFontSize(9)).setHeight(alturaFixa)
            );
            
            for (int i = 0; i < quantidadeDeColunas; i++) {
                table.addCell(new Cell().add(new Paragraph(" ")).setHeight(alturaFixa));
            }
        }

        return table;
    }

    public static float calcularAlturaLinhasTabela(Table table, int numLinhasEsperadas) {
        try {
            // Criar renderizador temporário
            TableRenderer renderer = (TableRenderer)table.createRendererSubTree();
            
            // Layout em área simulada
            LayoutResult result = renderer.layout(new LayoutContext(
                new LayoutArea(0, new Rectangle(PageSize.A4.getWidth(), PageSize.A4.getHeight()))
            ));
            
            Rectangle bounds = result.getOccupiedArea().getBBox();
            return bounds.getHeight() / numLinhasEsperadas;
            
        } catch (Exception e) {
            // Valor padrão se não for possível calcular
            return 13f; 
        }
    }
    
    private static float desenharGraficosSobrepostos(PdfDocument pdf, float alturaLinhas) {
        PdfPage page = pdf.getFirstPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle pageSize = page.getPageSize();

        // Configurações do gráfico (ajuste estas coordenadas)
        float graphXStart = 109f;
        float graphYStart = pageSize.getHeight() - 404f; // Distância do topo 349
        float graphWidth = pageSize.getWidth() - 128f;
        float graphHeight = 279f; // 223
        float columnSpacing = graphWidth / quantidadeDeColunas;
        // Fixando o valor máximo da escala pra ver qtos pontos fica individualmente
        //float rowScale = (graphYStart - graphHeight)/240f;
        float rowScale = (graphHeight/240f);

        // Dados simulados
        /*
        Random random = new Random();
        int[] pas = random.ints(9, 30, 150).toArray();
        int[] pad = random.ints(9, 30, 100).toArray();
        int[] freqCardiaca = random.ints(9, 60, 140).toArray();
        int[] freqRespiratoria = random.ints(9, 10, 70).toArray();
        */
        int[] pas = {125,120,125,120,125,120,125,120,239};
        int[] pad = {80,80,80,80,80,80,80,80,80};
        int[] freqCardiaca = {60,60,60,60,60,60,60,60,60};
        int[] freqRespiratoria = {0,0,12,12,12,12,12,30,30};
        

        // Desenhar eixos
        canvas.saveState()
            .setStrokeColor(ColorConstants.GREEN)
            .setLineWidth(0.5f)
            .moveTo(graphXStart, graphYStart)
            .lineTo(graphXStart, graphYStart + graphHeight)
            .lineTo(graphXStart + graphWidth, graphYStart + graphHeight)
            .stroke();

        // Desenhar séries de dados
        desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
        		pas, CORES[0], "quadrado", rowScale, 1);
        desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
        		pad, CORES[3], "triangulo", rowScale, 2);
        desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
        		freqCardiaca, CORES[1], "circulo", rowScale, 3);
        desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
        		freqRespiratoria, CORES[2], "triangulo", rowScale, 4);

        canvas.restoreState();
        
        return(rowScale);
    }

    private static void desenharSerie(PdfCanvas canvas, float xStart, float yStart, 
                                    float spacing, float height, int[] dados, 
                                    Color cor, String simbolo, float rowScale, int tipoSerie) {
        
        // Calcular escala dinâmica
    	/*
        int min = Arrays.stream(dados).min().getAsInt();
        int max = Arrays.stream(dados).max().getAsInt();
        */
    	int min = 0;
    	int max = 231;
        //float scaleY = height / (max - min + 10);
        // float scaleY = height / (max - min);
        //float scaleY = 0.9f;
    	float scaleY = rowScale;

        canvas.saveState()
            .setStrokeColor(cor)
            .setFillColor(cor)
            .setLineWidth(1);

        float prevX = 0, prevY = 0;
        for (int i = 0; i < dados.length; i++) {
        	float valor = dados[i];
            float x = xStart + (i * spacing);
            float y = 0f;
            // float y = yStart + (dados[i] - min) * scaleY;
            //float y = (yStart) + (valor * scaleY);
        	y = (240f + (valor * rowScale)) > 245 ? 240f + (valor * rowScale) : 245f;
        	y = yStart + (valor * rowScale);
        	/*
            switch(tipoSerie) {
            	case 1:
            		//y = yStart; // 245f;
            		simbolo = "circulo";
            		break;
            	case 2:
            		y = 260f;
            		simbolo = "circulo";
            		break;
            	case 3:
            		y = 277f;
            		simbolo = "circulo";
            		break;
            	case 4:
            		y = 469f;
            		simbolo = "circulo";
            		break;
            }
            */

            // Desenhar símbolo
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

            // Conectar pontos
            /*
            if (i > 0) {
                canvas.moveTo(prevX, prevY).lineTo(x, y).stroke();
            }
            */

            prevX = x;
            prevY = y;
        }
        canvas.restoreState();
    }

    /*
    private static void adicionarLegenda(PdfCanvas canvas, float xStart, float yStart, float height) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        canvas.beginText()
            .setFontAndSize(font, 10)
            .moveText(xStart + 10, yStart + height + 20)
            .showText("Legenda: ")
            .setColor(CORES[0], true)
            .showText("Pressão Arterial  ")
            .setColor(CORES[1], true)
            .showText("Frequência Cardíaca  ")
            .setColor(CORES[2], true)
            .showText("Frequência Respiratória")
            .endText();
    }
    */
}