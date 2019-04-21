package org.crossfit.app.service;
 
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.service.util.PdfUtils;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.xmp.XMPException;
 
 
/**
 * Reads bill data from a test database and creates ZUGFeRD bills
 * (Basic profile).
 * @author Bruno Lowagie
 */
public class PdfBill {

	private static final String FONTS_CFN_TTF = "fonts/cfn.ttf";
	private static final String I18N_MESSAGES_BILLS = "i18n/messages-bills";
	private static final BaseColor TAB_HEADER_COLOR = new BaseColor(87,113,138);

    protected Font fontCFN;
    protected Font fontCFN30;
    protected Font font10;
    protected Font font10b;
    protected Font font12;
    protected Font font12b;
    protected Font font14;

	private ResourceBundle i18n;
    
 
    private PdfBill() throws DocumentException, IOException {
    	i18n = ResourceBundle.getBundle(I18N_MESSAGES_BILLS);
        BaseFont bf = BaseFont.createFont();
        BaseFont bfCFN = BaseFont.createFont(FONTS_CFN_TTF, BaseFont.WINANSI, BaseFont.EMBEDDED);
        BaseFont bfb = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
        fontCFN = new Font(bfCFN, 18);
        fontCFN30 = new Font(bfCFN, 30);
        font10 = new Font(bf, 10);
        font10b = new Font(bfb, 10);
        font12 = new Font(bf, 12);
        font12b = new Font(bfb, 12);
        font14 = new Font(bf, 14);
    }
    
    public static PdfBill getBuilder() throws DocumentException, IOException {
    	return new PdfBill();
    }
 
    public String getI18n(String key) throws UnsupportedEncodingException {
//    	return new String(bundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
    	return i18n.getString(key);
    }
    public void createPdf(Bill bill, OutputStream os) throws ParserConfigurationException, SAXException, TransformerException, IOException, DocumentException, XMPException, ParseException {

        CrossFitBox box = bill.getBox();
        
        // step 1
        Document document = new Document(PageSize.A4, 20, 20, 50, 50);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, os);
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        // step 3
        document.open();

 
        // header
        PdfPCell cFactNumberl = new PdfPCell(new Paragraph(getI18n("bill.pdf.label.number") + bill.getNumber(), font14));
        cFactNumberl.setPaddingTop(13);
        cFactNumberl.setBorder(PdfPCell.NO_BORDER);
        cFactNumberl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell seller = new PdfPCell();
        seller.setBorder(PdfPCell.NO_BORDER);
        if (StringUtils.isNotEmpty(box.getBillName())) {
    		seller.addElement(new Paragraph(box.getBillName(), fontCFN));
        }
        if (StringUtils.isNotEmpty(box.getBillAddress())) {
            seller.addElement(new Paragraph(box.getBillAddress(), font12));
        }

        
        // Address seller / buyer
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(seller);
        table.addCell(cFactNumberl);
        
        document.add(table);

        
        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        PdfPCell cDest = new PdfPCell();
        cDest.setBorder(PdfPCell.NO_BORDER);
        cDest.addElement(new Phrase("\n"));
        cDest.addElement(new Paragraph(bill.getDisplayName(), font12b));
        cDest.addElement(new Paragraph(bill.getDisplayAddress(), font12));
        table.addCell(cDest);
        

        PdfPTable tableInfo = new PdfPTable(2);
        
        addLineInfo(tableInfo, getI18n("bill.pdf.label.effectiveDate"), formatDate(bill.getEffectiveDate(), i18n));
        addLineInfo(tableInfo, getI18n("bill.pdf.label.ref"), bill.getNumber());
        addLineInfo(tableInfo, getI18n("bill.pdf.label.memberId"), bill.getMember().getId()+"");
        addLineInfo(tableInfo, getI18n("bill.pdf.label.paymentMethod"), getI18n("bill.pdf.label.paymentMethod."+bill.getPaymentMethod()));
        addLineInfo(tableInfo, getI18n("bill.pdf.label.payAtDate"), formatDate(bill.getPayAtDate(), i18n));

        table.addCell(tableInfo);
        
        document.add(table);

        
        if (StringUtils.isNotBlank(bill.getComments())) {
	        document.add(new Paragraph(getI18n("bill.pdf.label.comments"), font12b));
	        document.add(new Paragraph(bill.getComments(), font12));
        }

        // line items
        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new int[]{55, 5, 10, 10, 12, 12});
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.label"), Element.ALIGN_LEFT, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.quantity"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.priceTaxExcl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.taxPerCent"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.totalTaxExcl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.line.totalTaxIncl"), Element.ALIGN_CENTER, font12b, TAB_HEADER_COLOR));
        for (BillLine line : bill.getLines()) {
        	String label = line.getLabel();
        	if (line.getSubscription() != null && line.getPeriodStart() != null) {
        		label += " ("+ formatDate(line.getPeriodStart(), i18n) + " au " + formatDate(line.getPeriodEnd(), i18n) + ")";
        	}
            table.addCell(PdfUtils.getCell(label, Element.ALIGN_LEFT, font12));
            table.addCell(PdfUtils.getCell(String.valueOf(line.getQuantity()), Element.ALIGN_RIGHT, font12));
            table.addCell(PdfUtils.getCell(formatPrice(line.getPriceTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(PdfUtils.getCell(formatPerCent(line.getTaxPerCent()), Element.ALIGN_RIGHT, font12));
            table.addCell(PdfUtils.getCell(formatPrice(line.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));
            table.addCell(PdfUtils.getCell(formatPrice(line.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        }

        table.addCell(PdfUtils.getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.totalTaxExcl"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(formatPrice(bill.getTotalTaxExcl()), Element.ALIGN_RIGHT, font12));

        table.addCell(PdfUtils.getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.totalTax"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(formatPrice(bill.getTotalTax()), Element.ALIGN_RIGHT, font12));        

        table.addCell(PdfUtils.getCell("", Element.ALIGN_RIGHT, font12b, 4, PdfPCell.NO_BORDER));
        table.addCell(PdfUtils.getCell(getI18n("bill.pdf.label.totalTaxIncl"), Element.ALIGN_RIGHT, font12b, TAB_HEADER_COLOR));
        table.addCell(PdfUtils.getCell(formatPrice(bill.getTotalTaxIncl()), Element.ALIGN_RIGHT, font12));
        document.add(table);

        if (StringUtils.isNotBlank(box.getBillLogoUrl())){
            Image img = Image.getInstance(new URL(box.getBillLogoUrl()));
            img.setAlignment(Element.ALIGN_CENTER);
            document.add(img);
        }

        if (StringUtils.isNotEmpty(box.getBillFooter())) {
            document.add(new Paragraph(box.getBillFooter(), font12));
        }
                
        // step 5
        document.close();
    }

	private String formatDate(LocalDate date, ResourceBundle i18n) throws UnsupportedEncodingException {
		return date == null ? "" : date.toString(getI18n("bill.pdf.label.date.format"));
	}

	private void addLineInfo(PdfPTable tableInfo, String label, String value) {
		PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.addElement(new Paragraph(label, font12));        
		cell.setPadding(5);
        tableInfo.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);        
		cell.setPadding(5);
		cell.addElement(new Paragraph(value, font12));        
        tableInfo.addCell(cell);
	}
 
	private String formatPrice(double value) {
		return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(value);
	}
    private String formatPerCent(double value) {
    	return value + "%";
	}
    public String convertDate(Date d, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(newFormat);
        return sdf.format(d);
    }
}