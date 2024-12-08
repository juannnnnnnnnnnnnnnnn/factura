package com.facturacion.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.facturacion.entity.CabFactura;
import com.facturacion.entity.DetFactura;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(CabFactura factura) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Factura #" + factura.getNumeroFactura(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Add invoice details
            document.add(new Paragraph("RUC Cliente: " + factura.getRucCliente()));
            document.add(new Paragraph("Subtotal: $" + factura.getSubtotal()));
            document.add(new Paragraph("IGV: $" + factura.getIgv()));
            document.add(new Paragraph("Total: $" + factura.getTotal()));
            document.add(new Paragraph("\n"));

            // Add products table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add table headers
            addTableHeader(table);

            // Add products
            for (DetFactura detalle : factura.getDetFactura()) {
                table.addCell(detalle.getCodigoProducto().toString());
                table.addCell(detalle.getCantidad().toString());
                table.addCell(detalle.getPkCabFactura().toString());
            }

            document.add(table);

        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);

        cell.setPhrase(new Phrase("Código Producto"));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Cantidad"));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Precio"));
        table.addCell(cell);
    }
} 