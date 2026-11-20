package com.unicundi.parqueadero.Controller;

import com.unicundi.parqueadero.Dto.*;
import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.FotoRegistro;
import com.unicundi.parqueadero.Service.AdminRegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminRegistroService service;

    @GetMapping("/registros/activos")
    public List<RegistroAdminRes> activos() {
        return service.activos();
    }

    @GetMapping("/registros/buscar")
    public List<RegistroAdminRes> buscar(@RequestParam String q) {
        return service.buscar(q);
    }

    @GetMapping("/registros/{id}")
    public RegistroAdminRes detalle(@PathVariable Long id) {
        return service.detalle(id);
    }

    @PutMapping("/registros/{id}/detalle")
    public RegistroAdminRes guardarDetalle(@PathVariable Long id, @Valid @RequestBody DetalleReq req) {
        return service.guardarDetalle(id, req);
    }

    @PostMapping(value = "/registros/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> subirFoto(@PathVariable Long id,
                                         @RequestPart("archivo") MultipartFile archivo) throws IOException {
        service.guardarFoto(id, archivo);
        return Map.of("mensaje", "Foto guardada");
    }

    @GetMapping("/registros/{id}/foto")
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        FotoRegistro f = service.foto(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(f.getTipo())).body(f.getContenido());
    }

    @GetMapping("/historial/resumen")
    public ResumenDia resumen(@RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.resumen(fecha == null ? LocalDate.now() : fecha);
    }

    @GetMapping("/historial")
    public List<RegistroAdminRes> historial(@RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                            @RequestParam(required = false) Categoria categoria) {
        return service.delDia(fecha == null ? LocalDate.now() : fecha, categoria);
    }

    @GetMapping("/visitantes")
    public List<VisitanteRes> visitantes() {
        return service.visitantes();
    }
}