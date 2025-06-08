package com.hotel.reservas.service.impl;

import com.hotel.reservas.model.Factura;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

@Component
public class FacturaPDFGenerator {

    public ByteArrayInputStream generarPDF(Factura factura) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("Factura de Reserva", font);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Factura ID: " + factura.getId()));
            document.add(new Paragraph("Cliente: " + factura.getReserva().getCliente().getNombre()));
            document.add(new Paragraph("Email: " + factura.getReserva().getCliente().getEmail()));
            document.add(new Paragraph("Habitación: " + factura.getReserva().getHabitacion().getNumero()));
            document.add(new Paragraph("Tipo: " + factura.getReserva().getHabitacion().getTipo()));
            document.add(new Paragraph("Fecha entrada: " + factura.getReserva().getFechaInicio()));
            document.add(new Paragraph("Fecha salida: " + factura.getReserva().getFechaFin()));
            document.add(new Paragraph("Precio total: " + factura.getMontoTotal()));
            document.add(new Paragraph("Cantidad de personas: " + factura.getCantidadPersonas()));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
