package com.hotel.reservas.controller;

import com.hotel.reservas.dto.FacturaDTO;
import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.UsuarioRepository;
import com.hotel.reservas.model.Factura;
import com.hotel.reservas.service.impl.FacturaPDFGenerator;
import com.hotel.reservas.service.FacturaService;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final UsuarioRepository usuarioRepository;
    private final FacturaPDFGenerator facturaPDFGenerator;

    @PostMapping("/generar")
    public FacturaDTO generarFactura(@RequestParam Long reservaId,
                                     @RequestParam int cantidadPersonas) {
        return facturaService.generarFactura(reservaId, cantidadPersonas);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ROL_ADMIN', 'ROL_CLIENTE')")
    public ResponseEntity<InputStreamResource> descargarFacturaPDF(@PathVariable Long id) {
        String emailUsuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = facturaService.obtenerFacturaPorId(id);

        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombre().name().equals("ROL_ADMIN"));

        boolean esSuFactura = factura.getReserva().getCliente().getUsuario().getId().equals(usuario.getId());

        if (!esAdmin && !esSuFactura) {
            return ResponseEntity.status(403).build();
        }

        ByteArrayInputStream pdfStream = facturaPDFGenerator.generarPDF(factura);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}
