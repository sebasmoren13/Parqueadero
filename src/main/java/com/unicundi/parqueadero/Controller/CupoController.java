package com.unicundi.parqueadero.Controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.unicundi.parqueadero.Dto.Disponibilidad;
import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.TipoQr;
import com.unicundi.parqueadero.Security.JwtService;
import com.unicundi.parqueadero.Service.CupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CupoController {
    private final CupoService service;
    private final JwtService jwt;

    @GetMapping("/cupos/disponibilidad")
    public List<Disponibilidad> disponibilidad() {
        return service.disponibilidad();
    }

    @PutMapping("/admin/cupos/{categoria}")
    public List<Disponibilidad> configurar(@PathVariable Categoria categoria, @RequestParam int capacidad) {
        service.configurar(categoria, capacidad);
        return service.disponibilidad();
    }

    @GetMapping("/admin/qr/{tipo}")
    public Map<String, Object> qr(@PathVariable TipoQr tipo) {
        return Map.of("token", jwt.qr(tipo), "venceEnSeg", jwt.qrSegundos());
    }

    @GetMapping(value = "/admin/qr/{tipo}/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] qrImagen(@PathVariable TipoQr tipo) throws Exception {
        BitMatrix m = new MultiFormatWriter().encode(jwt.qr(tipo), BarcodeFormat.QR_CODE, 400, 400);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(m, "PNG", out);
        return out.toByteArray();
    }
}