package com.facturacion.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacion.entity.CabFactura;
import com.facturacion.service.CabFacturaService;
import com.facturacion.service.PdfGeneratorService;
import com.facturacion.service.EmailService;
import com.facturacion.util.ResponseMessage;
import com.itextpdf.text.DocumentException;

@RestController
@RequestMapping("/cab-factura")
public class CabFacturaController {

    private final CabFacturaService cabFacturaService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    public CabFacturaController(CabFacturaService cabFacturaService, PdfGeneratorService pdfGeneratorService, EmailService emailService) {
        this.cabFacturaService = cabFacturaService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<CabFactura>> obtenerTodasCabeceras() {
        List<CabFactura> cabeceras = cabFacturaService.obtenerTodas();
        return new ResponseEntity<>(cabeceras, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CabFactura> obtenerFacturaPorId(@PathVariable("id") Integer id) {
        return cabFacturaService.obtenerPorId(id)
                .map(factura -> new ResponseEntity<>(factura, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CabFactura> guardarFactura(@RequestBody CabFactura cabFactura) {
        CabFactura facturaGuardada = cabFacturaService.guardarCabFactura(cabFactura);
        return new ResponseEntity<>(facturaGuardada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFacturaPorId(@PathVariable("id") Integer id) {
        cabFacturaService.eliminarPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/genera-factura")
    public ResponseEntity<ResponseMessage> generaFactura() {
        return ResponseEntity.ok(new ResponseMessage(200, this.cabFacturaService.generaFactura()));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable("id") Integer id) {
        try {
            Optional<CabFactura> factura = cabFacturaService.obtenerPorId(id);
            if (factura.isPresent()) {
                byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(factura.get());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", "factura-" + id + ".pdf");
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DocumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/email")
    public ResponseEntity<Void> sendInvoiceByEmail(@PathVariable("id") Integer id, @RequestBody String email) {
        try {
            Optional<CabFactura> factura = cabFacturaService.obtenerPorId(id);
            if (factura.isPresent()) {
                byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(factura.get());
                emailService.sendInvoiceEmail(
                    email,
                    "Factura #" + factura.get().getNumeroFactura(),
                    "Adjunto encontrará su factura.",
                    pdfBytes,
                    "factura-" + id + ".pdf"
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
