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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class FichaAnestesicaDeepSeek {

    private static final String[] PARAMETROS = {
    	"Tempo Cirúrgico",
    	"Tempo Anestésico", 
    	"Oxigênio (l/m)", 
    	"Ar Comprimido (l/m)", 
    	"Cisatracúrio (mg)", 
    	"XXX", 
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
        " ",
    };
    
    private static final Color[] CORES = {
        ColorConstants.BLUE,
        ColorConstants.RED,
        ColorConstants.GREEN, 
        ColorConstants.YELLOW,
        ColorConstants.BLACK
    };
    
    private static int quantidadeDeColunas= 45;
    
    private static int quantidadeDeLinhasGrafico = 16;
    
    private static int quantidadeDeLinhasFixas = 6;
    
    private static LocalTime horaReferencia = LocalTime.of(5, 0);
    
    static float larguraDaPagina, alturaDaPagina, inicioX, inicioY, larguraGrafico, alturaGrafico, escalaColuna, escalaLinha;

    public static void gerarPdf(String dest) throws IOException {
        // Configuração inicial do documento
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(10, 10, 10, 10);
        
        // Criar cabeçalho
        criarCabecalho(document);
        criarCabecalhoDadosPessoais(document);
        criarCabecalhoDadosProcedimento(document);
        
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        
        PdfPage paginaAtual = pdf.getLastPage();
        
        criarCabecalho(document);
        criarCabecalhoDadosPessoais(document);
        
        // Adicionar título
        /*
        document.add(new Paragraph("Ficha de Monitorização Anestésica")
            .setBold()
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(15));
        */

        // Criar e adicionar tabela
        Table table = criarTabela();
        document.add(table);
        //float alturaTabela = PARAMETROS.length * 25f + 50f; // 25px por linha + margem
       
        // Adicionar espaço entre tabela e legenda
        //document.add(new Paragraph("\n"));
        
        // 4. Desenhar gráfico sobre a tabela
        desenharGraficosSobrepostos(pdf, paginaAtual);

        // Adicionar legenda (agora como elemento do documento)
        document.add(adicionarLegenda(pdf));

        document.close();
    }
    
    private static void gerarCodigoDeBarras(PdfDocument document) {
    	Barcode39 codigoBarras = new Barcode39(document);
    	codigoBarras.setCode("*9999999*");
    }
    
    private static void criarCabecalho(Document document) throws IOException {
	    PdfFont titulo = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
	    PdfFont codbarra = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	    
    	String logoPath= "src/main/resources/png/hsm.png"; 
		Image logo = new Image(ImageDataFactory.create(logoPath))
		.setWidth(UnitValue.createPercentValue(80))
		.setHorizontalAlignment(HorizontalAlignment.CENTER);
		
	    Table tableHeader = new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
	    tableHeader.setBorder(Border.NO_BORDER);
	    Cell cell = new Cell(3,1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);
	
	    cell = new Cell(1,7).add(
	    		new Paragraph("Associação Piauiense de Combate ao Câncer Alcenor Almeida")
	    		.setFont(titulo)
	    		.setFontSize(18)
	    		).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);
	
	    cell = new Cell(2,2).add(
	    		new Paragraph("*9999999*")
	    		.setFont(codbarra)
	    		.setFontSize(18)
	    		.setTextAlignment(TextAlignment.CENTER)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,5).add(
	    		new Paragraph("Hospital São Marcos")
	    		.setFont(titulo)
	    		.setFontSize(18)
	    		).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,2).add(
	    		new Paragraph("Internação Nº ")
	    		.setTextAlignment(TextAlignment.RIGHT)
	    		.setFont(titulo)
	    		.setFontSize(12)).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,7).add(
	    		new Paragraph("Boletim de Anestesia")
	    		.setFont(titulo)
	    		.setBold()
	    		.setFontSize(12)
	    		.setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,2).add(
	    		new Paragraph("Sala: ______")
	    		.setFont(titulo)
	    		.setFontSize(12)).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    LineSeparator ls = new LineSeparator(new SolidLine()).setMarginTop(2f).setMarginBottom(2f);

	    document.add(tableHeader);
	    document.add(ls);
    }

    private static void criarCabecalhoDadosPessoais(Document document) throws IOException {
	    PdfFont base = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
	    LineSeparator ls = new LineSeparator(new SolidLine()).setMarginTop(2f).setMarginBottom(2f);

	    Table tableDadosPessoais= new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
	    tableDadosPessoais.setBorder(Border.NO_BORDER);
	    Cell cell = new Cell(1,5).add(
	    		new Paragraph("Nome: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Leito: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,3).add(
	    		new Paragraph("Convênio: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,5).add(
	    		new Paragraph("Filiação: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,4).add(
	    		new Paragraph("Nascimento: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell().add(
	    		new Paragraph("Sexo: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);

	    document.add(tableDadosPessoais);
	    document.add(ls);
    }

    private static void criarCabecalhoDadosProcedimento(Document document) throws IOException {
	    PdfFont base = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
	    Table tableDadosProcedimento= new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
	    tableDadosProcedimento.setBorder(Border.NO_BORDER);
	    Cell cell = new Cell(1,7).add(
	    		new Paragraph("Cirurgião: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,3).add(
	    		new Paragraph("Cód. Procedimento: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,10).add(
	    		new Paragraph("Cirurgia Realizada: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,10).add(
	    		new Paragraph("Condições Pré-Operatórias: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,8).add(
	    		new Paragraph("Exames Clínicos: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Risco ASA: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,8).add(
	    		new Paragraph("Premedicação: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Hora: ")
	    		.setFont(base)
	    		.setFontSize(10)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);

	    document.add(tableDadosProcedimento);
    }
    
 // Novo método de legenda (retorna Paragraph)
    private static Paragraph adicionarLegenda(PdfDocument pdfDoc) throws IOException {
        Paragraph legenda = new Paragraph("Legenda: ").setFontSize(6);
        
        String svgPAS = "src/main/resources/svg/pas.svg";
        String svgPAD = "src/main/resources/svg/pad.svg";
        String svgFC = "src/main/resources/svg/fc.svg";
        String svgFR = "src/main/resources/svg/fr.svg";
        String svgSPO2 = "src/main/resources/svg/spo2.svg";
        String svgETCO2 = "src/main/resources/svg/etco2.svg";
        
        URL urlPAS = new File(svgPAS).toURI().toURL();
        URL urlPAD = new File(svgPAD).toURI().toURL();
        URL urlFC = new File(svgFC).toURI().toURL();
        URL urlFR = new File(svgFR).toURI().toURL();
        URL urlSPO2 = new File(svgSPO2).toURI().toURL();
        URL urlETCO2 = new File(svgETCO2).toURI().toURL();

        Image imgPAS = SvgConverter.convertToImage(urlPAS.openStream(), pdfDoc);
        Image imgPAD = SvgConverter.convertToImage(urlPAD.openStream(), pdfDoc);
        Image imgFC = SvgConverter.convertToImage(urlFR.openStream(), pdfDoc);
        Image imgFR = SvgConverter.convertToImage(urlFC.openStream(), pdfDoc);
        Image imgSPO2 = SvgConverter.convertToImage(urlSPO2.openStream(), pdfDoc);
        Image imgETCO2 = SvgConverter.convertToImage(urlETCO2.openStream(), pdfDoc);
       
        legenda.add(new Text("P.A.S.: "))
        	.add(imgPAS)
        	.add(new Text(" \\ P.A.D.: "))
        	.add(imgPAD)
        	.add(new Text(" \\ Freq. Cardíaca: "))
        	.add(imgFC)
        	.add(new Text(" \\ Freq. Resp.: "))
        	.add(imgFR)
        	.add(new Text(" \\ SpO2: "))
        	.add(imgSPO2)
        	.add(new Text(" \\ ETCO2: "))
        	.add(imgETCO2);
        
        return legenda;
    }

    private static Table criarTabela() {
    	float alturaFixa = 8f; //13f;
        // Configurar larguras das colunas (10 colunas de 8% cada)
        UnitValue[] columnWidths = new UnitValue[quantidadeDeColunas + 1];
        columnWidths[0] = UnitValue.createPointValue(80);
        for (int i = 1; i< columnWidths.length; i++) {
        	columnWidths[i] = UnitValue.createPercentValue(100f / quantidadeDeColunas);
        }

        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(90));
        table.setFixedLayout();
        
        // Estilo especial para primeira coluna
        Border solidBorder = new SolidBorder(1f); // Borda normal
        Border noBottomBorder = new SolidBorder(ColorConstants.WHITE, 1f); // Borda invisível inferior

        LocalTime horaAtual = horaReferencia;
        // Cabeçalho da tabela
        table.addHeaderCell(
            new Cell().add(new Paragraph("Parâmetro").setBold().setFontSize(6))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setBorder(solidBorder)
        );
        
        String horaFormatada = "";
        int controleImpressaoCabecalho = 0;

        for (int i = 0; i < quantidadeDeColunas; i++) {
        	horaFormatada = controleImpressaoCabecalho == 0 ? horaAtual.toString().trim() : "";
        	horaFormatada = "";
            table.addHeaderCell(
                // new Cell().add(new Paragraph((i * 5) + "").setFontSize(5))
                new Cell().add(new Paragraph(horaFormatada).setFontSize(4).setBold())
            );
            horaAtual = horaAtual.plusMinutes(5);
            //Imprime uma coluna sim e 2 não
            controleImpressaoCabecalho = controleImpressaoCabecalho > 1 ? 0 : controleImpressaoCabecalho + 1;
        }

        // Conteúdo da tabela
        int contador = 1;
        for (String parametro : PARAMETROS) {
            table.addCell(
                new Cell().add(new Paragraph(parametro).setBold().setFontSize(6)).setHeight(alturaFixa)
                .setBorderBottom(parametro.trim().isEmpty() && contador< PARAMETROS.length ? noBottomBorder : solidBorder)
            );
            
            for (int i = 0; i < quantidadeDeColunas; i++) {
                table.addCell(new Cell().add(new Paragraph(" ")).setHeight(alturaFixa).setBorder(solidBorder));
            }
        	contador++;
        }

        return table;
    }

    public static float calcularAlturaLinhasTabela(Table table, int numLinhasEsperadas) {
        try {
            // Criar renderizador temporário
            TableRenderer renderer = (TableRenderer) table.createRendererSubTree();
            
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
    
    private static void desenharGraficosSobrepostos(PdfDocument pdf, PdfPage paginaAtual) {
        PdfPage page = paginaAtual; //pdf.getFirstPage();
        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle pageSize = page.getPageSize();

        // Configurações do gráfico (ajuste estas coordenadas)
        float margemTopo = 438f;	//474f //437f //verificar novamente
        float alturaLinhaTabela = 13f;	//18f; 
        
        float graphXStart = 90f;
        // float graphYStart = pageSize.getHeight() - 524f;
        float graphYStart = pageSize.getHeight() - margemTopo - (alturaLinhaTabela * quantidadeDeLinhasFixas);
        float tableYStart = pageSize.getHeight() - margemTopo;
        float graphWidth = pageSize.getWidth() - 102f;
        float graphHeight = 279f;
        
        larguraDaPagina= pageSize.getWidth();
        alturaDaPagina= pageSize.getHeight();
        inicioX= graphXStart;
        inicioY= graphYStart;
        larguraGrafico= graphWidth;
        alturaGrafico= graphHeight;
        escalaColuna= (graphWidth / quantidadeDeColunas);
        escalaLinha= (graphHeight/240f);
        
        float columnSpacing = graphWidth / quantidadeDeColunas;
        try {
			desenharEscalaLateral(canvas, graphXStart, graphYStart, graphHeight);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			// desenharCabecalhoHorarios(canvas, graphXStart + 3, graphYStart + graphHeight + 43, graphWidth);
			desenharCabecalhoHorarios(canvas, graphXStart + 3, tableYStart + graphHeight + 8, graphWidth);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Dados simulados
        Random random = new Random();
        //int[] pas = random.ints(quantidadeDeColunas, 100, 240).toArray();
        int[] pas = {120,110,130,125,122,125,125,110,110,120,120,128,120,122,122,122,115,120,125,110,120,122,120,120};
        int[] pad = {80,90,85,80,88,85,80,80,90,80,85,85,80,77,75,75,80,80,75,90,80,88,76,85};
        int[] freqCardiaca = {60,60,60,60,60,60,60,60,60};
        int[] freqRespiratoria = {12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12};
        int[] spo2 = {93,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99};
        int[] etco2 = {30,35,30,30,30,30,30,30,30,30,31,30,30,30,30,30,30,30,32,30,30,30,30,30};
        

        // Desenhar eixos
        /*
        canvas.saveState()
            .setStrokeColor(ColorConstants.GREEN)
            .setLineWidth(0.5f)
            .moveTo(graphXStart, graphYStart)
            .lineTo(graphXStart, graphYStart + graphHeight)
	        .lineTo(graphXStart + graphWidth, graphYStart)
            .stroke();
         */
        canvas.saveState();
        
        String svgPAS = "src/main/resources/svg/pas.svg";
        String svgPAD = "src/main/resources/svg/pad.svg";
        String svgFC = "src/main/resources/svg/fc.svg";
        String svgFR = "src/main/resources/svg/fr.svg";
        String svgSPO2 = "src/main/resources/svg/spo2.svg";
        String svgETCO2 = "src/main/resources/svg/etco2.svg";

        // Desenhar séries de dados
        //desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight,pas, CORES[0], "quadrado", rowScale, 1, svgPAS);
        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					pas, CORES[0], svgPAS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight,pad, CORES[3], "triangulo", rowScale, 2);
        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					pad, CORES[0], svgPAD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight,freqCardiaca, CORES[4], "circulo", rowScale, 3);
        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					freqCardiaca, CORES[0], svgFC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //desenharSerie(canvas, graphXStart, graphYStart, columnSpacing, graphHeight,freqRespiratoria, CORES[2], "triangulo", rowScale, 4);
        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					freqRespiratoria, CORES[0], svgFR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					spo2, CORES[0], svgSPO2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			desenharSerieComSVG(canvas, graphXStart, graphYStart, columnSpacing, graphHeight, 
					etco2, CORES[0], svgETCO2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        canvas.restoreState();
    }

    private static void adicionarSVG(PdfCanvas canvas, String svgPath, float x, float y, float width, float height) throws IOException {
        // 1. Carregar o SVG e converter para PDF
        PdfFormXObject svg = SvgConverter.convertToXObject(
            new FileInputStream(svgPath), 
            canvas.getDocument()
        );
                
        // 2. Desenhar na posição correta
        canvas.addXObjectAt(
            svg, 
            x, //x - width/2,  // Centralizado no ponto X
            y - height/2 // Centralizado no ponto Y
        );
    }
    
    private static void desenharSerieComSVG(PdfCanvas canvas, float xStart, float yStart, 
            float spacing, float height, int[] dados, 
            Color cor, String svgPath) throws IOException {

		// Tamanho dos ícones SVG
		float iconWidth = 12f;
		float iconHeight = 12f;
		
		float rowScale = height / 240f;
		
		for (int i = 0; i < dados.length; i++) {
        	float valor = dados[i];
			// float x = xStart + (i * spacing);
			float x = xStart + (i * spacing);
        	float y = yStart + (valor * rowScale);

			// Aplica a cor ao canvas antes de desenhar
			canvas.saveState().setFillColor(cor);
		
			adicionarSVG(canvas, svgPath, x, y, iconWidth, iconHeight);
		
			canvas.restoreState();
		}
	}
    
    private static void desenharEscalaLateral(PdfCanvas canvas, float xStart, float yStart, float height) throws IOException {
        canvas.saveState()
              .setStrokeColor(ColorConstants.BLACK)
              .setLineWidth(0.5f);
        
        // Valores da escala (ajuste conforme sua necessidade)
        float[] valoresEscala = {0, 30, 60, 90, 120, 150, 180, 210, 240};
        float espacamento = height / (valoresEscala[valoresEscala.length - 1] - valoresEscala[0]);
        
        // Linha vertical da escala
        canvas.moveTo(xStart - 10, yStart)
              .lineTo(xStart - 10, yStart + height)
              .stroke();
        
        // Marcas e valores da escala
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        for (float valor : valoresEscala) {
            float y = yStart + (valor * espacamento);
            
            // Marca da escala
            canvas.moveTo(xStart - 15, y)
                  .lineTo(xStart - 10, y)
                  .stroke();
            
            // Texto do valor
            canvas.beginText()
                  .setFontAndSize(font, 8)
                  .moveText(xStart - 30, y - 3)
                  .showText(String.valueOf((int)valor))
                  .endText();
        }
        
        canvas.restoreState();
    }

    private static void desenharCabecalhoHorarios(PdfCanvas canvas, float xStart, float yStart, float width) throws IOException {
    	String[] horarios = {"00:00","00:15","00:30","00:45","01:00","01:15","01:30","01:45","02:00","02:15","02:30","02:45","03:00","03:15","03:30"};
    	float espacamentoHorizontal = width / horarios.length;
    	
        canvas.saveState()
              .setStrokeColor(ColorConstants.LIGHT_GRAY)
              .setLineWidth(0.5f);
        
        // Marcas e valores da escala
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        float x = xStart;
        for (String horario : horarios) {
            // Texto do valor
            canvas.saveState()
            	.setFillColor(ColorConstants.LIGHT_GRAY)
            	.rectangle(x-1, yStart-1, font.getWidth(horario,8) + 3, 9)
      	  		.fill()
      	  		.restoreState();

            canvas.beginText()
                  .setFontAndSize(font, 8)
                  .moveText(x, yStart)
                  .showText(horario)
                  .endText()
                  .stroke();

            x = x + espacamentoHorizontal; 
        }
        
        canvas.restoreState();
    }
    
    private static void desenharSerie(PdfCanvas canvas, float xStart, float yStart, 
                                    float spacing, float height, int[] dados, 
                                    Color cor, String simbolo, float rowScale, int tipoSerie) {
        
        canvas.saveState()
            .setStrokeColor(cor)
            .setFillColor(cor)
            .setLineWidth(1);

        for (int i = 0; i < dados.length; i++) {
        	float valor = dados[i];
            float x = xStart + (i * spacing);
            float x2 = xStart + (i * spacing) + (spacing/2);
        	float y = yStart + (valor * rowScale);

            // Desenhar símbolo
            switch (simbolo) {
                case "circulo":
                    canvas.circle(x2, y, 4).fill();
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
        }
        canvas.restoreState();
    }
}