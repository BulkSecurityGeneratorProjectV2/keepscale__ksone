package org.crossfit.app.service.pdf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public abstract class AbstractPdf  {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected static final BaseColor HEADER_COLOR = new BaseColor(87,113,138);
    protected Font font6;
    protected Font font10;
    protected Font font10White;
    protected Font font10b;
    protected Font font12;
    protected Font font12White;
    protected Font font12b;
    protected Font font12bWhite;
    protected Font font14;


    protected final DocumentContext jsonData;

    protected AbstractPdf(String json) throws IOException, DocumentException {
        BaseFont bf = BaseFont.createFont();
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        font6 = new Font(bf, 6);
        font10 = new Font(bf, 10);
        font10b = new Font(bfb, 10);
        font12 = new Font(bf, 12);
        font12b = new Font(bfb, 12);
        font14 = new Font(bf, 14);

        font10White = new Font(font10);
        font10White.setColor(BaseColor.WHITE);
        font12bWhite = new Font(font12b);
        font12bWhite.setColor(BaseColor.WHITE);
        font12White = new Font(font12);
        font12White.setColor(BaseColor.WHITE);

        this.jsonData=JsonPath.parse(json);;
    }


    protected Element createImgFromURL(String url, int fitWidth, int fitHeight) {
        try{
            Image img = Image.getInstance(new URL(url));
            img.scaleToFit(fitWidth, fitHeight);
            return img;
        } catch(Exception e){
            return new Chunk();
        }
    }
    protected Element createImgFromB64(String signatureDataEncoded, int fitWidth, int fitHeight){
        try{
            String base64Image = signatureDataEncoded.split(",")[1];
            Image img = Image.getInstance(javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image));
            img.scaleToFit(fitWidth, fitHeight);
            return img;
        } catch(Exception e){
            return new Chunk();
        }
    }

    protected List<String> getStrings(String pointer){
        try{
            return jsonData.read("$."+pointer);
        }catch (PathNotFoundException e){
            log.info("Path {} not found: {}", pointer, e.getMessage());
            return new ArrayList<String>();
        }
        //return ((JSONArray)jsonData.query(pointer)).toList().stream().map(Object::toString).collect(Collectors.toList());
    }
    protected String getString(String pointer){
        try{
            String path = "$."+pointer;
            log.debug("Reading path {}", path);
			return jsonData.read(path);
        }catch (PathNotFoundException e){
            log.info("Path {} not found: {}", pointer, e.getMessage());
            return null;
        }
        //return jsonData.query(pointer).toString();
    }



    protected Paragraph createParagraph(String text) {
        return createParagraph(text, font10);
    }
    protected Paragraph createParagraph(String text, Font font){
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        return p;
    }
    protected void createLine(PdfPTable table, String label, String value) throws UnsupportedEncodingException {
        this.createLine(table, label, value, null, null);
    }
    protected void createLine(PdfPTable table, String label, String value, String label2, String value2) throws UnsupportedEncodingException {
        Phrase p1 = new Phrase(getString(label), font10b);
        p1.add(new Chunk(value == null ? "" : value, font10));

        createLine(table, p1);


        Phrase p2 = new Phrase();

        if (label2 != null){
            p2.add(new Phrase(getString(label2), font10b));
            p2.add(new Chunk(value2 == null ? "" : value2, font10));
        }
        createLine(table, p2);
    }
    protected void createLine(PdfPTable table, Element e) throws UnsupportedEncodingException {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPaddingLeft(0);
        cell.addElement(e);
        table.addCell(cell);
    }

    protected void createBlock(Document document, String header, List<Element> elements) throws UnsupportedEncodingException, DocumentException {

        if (elements.isEmpty())
            return;

        PdfPTable t = new PdfPTable(1);
        t.setSpacingBefore(10);
        t.setWidthPercentage(100);
        //t.setKeepTogether(true);

        if (header != null) {
            PdfPCell cell = new PdfPCell();

            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setPadding(5);
            cell.setPaddingLeft(20);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.addElement(new Paragraph(header, font12bWhite));
            t.addCell(cell);
        }

        for (Element e : elements) {
            PdfPCell content = new PdfPCell();
            content.setBorder(PdfPCell.NO_BORDER);
            content.setUseAscender(true);
            content.setUseDescender(true);
            content.setPadding(10);
            content.setPaddingLeft(30);
            content.setPaddingRight(30);
            content.addElement(e);
            t.addCell(content);
        }

        document.add(t);

    }

}
