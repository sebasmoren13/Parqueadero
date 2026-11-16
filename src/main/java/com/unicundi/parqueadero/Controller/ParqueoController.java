package com.unicundi.parqueadero.Controller;

import com.unicundi.parqueadero.Dto.EntradaReq;
import com.unicundi.parqueadero.Dto.RegistroRes;
import com.unicundi.parqueadero.Dto.SalidaReq;
import com.unicundi.parqueadero.Entity.TipoQr;
import com.unicundi.parqueadero.Repository.RegistroRepository;
import com.unicundi.parqueadero.Security.JwtService;
import com.unicundi.parqueadero.Service.CupoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parqueo")
@RequiredArgsConstructor
public class ParqueoController {
    private final CupoService service;
    private final JwtService jwt;
    private final RegistroRepository registros;

    @PostMapping("/entrada")
    public RegistroRes entrada(@AuthenticationPrincipal Long uid, @Valid @RequestBody EntradaReq req) {
        jwt.validarQr(req.qr(), TipoQr.ENTRADA);
        return RegistroRes.de(service.entrar(uid, req.categoria()));
    }

    @PostMapping("/salida")
    public RegistroRes salida(@AuthenticationPrincipal Long uid, @Valid @RequestBody SalidaReq req) {
        jwt.validarQr(req.qr(), TipoQr.SALIDA);
        return RegistroRes.de(service.salir(uid));
    }

    @GetMapping("/activo")
    public RegistroRes activo(@AuthenticationPrincipal Long uid) {
        return registros.findByUsuarioIdAndActivoTrue(uid).map(RegistroRes::de).orElse(null);
    }

    @GetMapping("/historial")
    public List<RegistroRes> historial(@AuthenticationPrincipal Long uid) {
        return registros.findByUsuarioIdOrderByEntradaDesc(uid).stream().map(RegistroRes::de).toList();
    }
}