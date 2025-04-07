package com.example.teste_api;
/*
This file is part of the iText (R) project.
Copyright (c) 1998-2023 Apryse Group NV
Authors: Apryse Software.

For more information, please contact iText Software at this address:
sales@itextpdf.com
*/

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;

public class ColspanRowspan {
	public static final String DEST = "c:/temp/colspan_rowspan.pdf";

	public static void executa() throws Exception {
	    File file = new File(DEST);
	    file.getParentFile().mkdirs();
	
	    new ColspanRowspan().manipulatePdf(DEST);
	}

	protected void manipulatePdf(String dest) throws Exception {
		
	    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
	    Document doc = new Document(pdfDoc, PageSize.A4.rotate());
	    doc.setMargins(10, 10, 10, 10);
	    
	    PdfFont titulo = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
	    PdfFont codbarra = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
	    PdfFont base = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	    
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
	    		.setFontSize(12)
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
	    		.setFontSize(10)
	    		).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,2).add(
	    		new Paragraph("Internação Nº ")
	    		.setTextAlignment(TextAlignment.RIGHT)
	    		.setFont(titulo)
	    		.setFontSize(9)).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,7).add(
	    		new Paragraph("Boletim de Anestesia")
	    		.setFont(titulo)
	    		.setBold()
	    		.setFontSize(10)
	    		.setTextAlignment(TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    cell = new Cell(1,2).add(
	    		new Paragraph("Sala: ______")
	    		.setFont(titulo)
	    		.setFontSize(11)).setBorder(Border.NO_BORDER);
	    tableHeader.addCell(cell);

	    LineSeparator ls = new LineSeparator(new SolidLine()).setMarginTop(2f).setMarginBottom(2f);
	    LineSeparator ls1 = new LineSeparator(new SolidLine()).setMarginTop(2f).setMarginBottom(2f);

	    Table tableDadosPessoais= new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
	    tableDadosPessoais.setBorder(Border.NO_BORDER);
	    cell = new Cell(1,5).add(
	    		new Paragraph("Nome: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Leito: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,3).add(
	    		new Paragraph("Convênio: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,5).add(
	    		new Paragraph("Filiação: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell(1,4).add(
	    		new Paragraph("Nascimento: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);
	    cell = new Cell().add(
	    		new Paragraph("Sexo: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosPessoais.addCell(cell);

	    Table tableDadosProcedimento= new Table(UnitValue.createPercentArray(10)).useAllAvailableWidth();
	    tableDadosProcedimento.setBorder(Border.NO_BORDER);
	    cell = new Cell(1,7).add(
	    		new Paragraph("Cirurgião: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,3).add(
	    		new Paragraph("Cód. Procedimento: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,10).add(
	    		new Paragraph("Cirurgia Realizada: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,10).add(
	    		new Paragraph("Condições Pré-Operatórias: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,8).add(
	    		new Paragraph("Exames Clínicos: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Risco ASA: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,8).add(
	    		new Paragraph("Premedicação: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);
	    cell = new Cell(1,2).add(
	    		new Paragraph("Hora: ")
	    		.setFont(base)
	    		.setFontSize(9)
	    		).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
	    tableDadosProcedimento.addCell(cell);

	    doc.add(tableHeader);
	    doc.add(ls);
	    doc.add(tableDadosPessoais);
	    doc.add(ls1);
	    doc.add(tableDadosProcedimento);
	    doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	    
	    doc.close();
	}
}