package com.unicundi.parqueadero.Service;

import com.unicundi.parqueadero.Dto.*;
import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.FotoRegistro;
import com.unicundi.parqueadero.Entity.Registro;
import com.unicundi.parqueadero.Repository.FotoRegistroRepository;
import com.unicundi.parqueadero.Repository.RegistroRepository;
import com.unicundi.parqueadero.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminRegistroService {
    private final RegistroRepository registros;
    private final FotoRegistroRepository fotos;
    private final UsuarioRepository usuarios;

    private Registro buscarPorId(Long id) {
        return registros.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado"));
    }

    public List<RegistroAdminRes> activos() {
        return registros.findByActivoTrueOrderByEntradaDesc().stream().map(RegistroAdminRes::de).toList();
    }

    public RegistroAdminRes detalle(Long id) {
        return RegistroAdminRes.de(buscarPorId(id));
    }

    @Transactional
    public RegistroAdminRes guardarDetalle(Long id, DetalleReq req) {
        Registro r = buscarPorId(id);
        boolean sinPlaca = req.placa() == null || req.placa().isBlank();
        boolean sinDescripcion = req.descripcion() == null || req.descripcion().isBlank();
        if (r.getCupo().getCategoria() == Categoria.BICICLETA) {
            if (sinDescripcion) throw new IllegalArgumentException("Para bicicletas escribe una descripción (color, marca)");
        } else if (sinPlaca) {
            throw new IllegalArgumentException("Escribe la placa del vehículo");
        }
        r.setPlaca(sinPlaca ? null : req.placa().trim().toUpperCase());
        r.setDescripcion(sinDescripcion ? null : req.descripcion().trim());
        r.setNovedades(req.novedades() == null || req.novedades().isBlank() ? null : req.novedades().trim());
        return RegistroAdminRes.de(r);
    }

    @Transactional
    public void guardarFoto(Long id, MultipartFile archivo) throws IOException {
        Registro r = buscarPorId(id);
        String tipo = archivo.getContentType();
        if (tipo == null || !tipo.startsWith("image/"))
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        FotoRegistro f = new FotoRegistro();
        f.setRegistroId(id);
        f.setTipo(tipo);
        f.setContenido(archivo.getBytes());
        fotos.save(f);
        r.setTieneFoto(true);
    }

    public FotoRegistro foto(Long id) {
        return fotos.findById(id).orElseThrow(() -> new IllegalArgumentException("Este registro no tiene foto"));
    }

    public ResumenDia resumen(LocalDate fecha) {
        LocalDateTime ini = fecha.atStartOfDay();
        Map<Categoria, Long> m = new EnumMap<>(Categoria.class);
        for (Object[] fila : registros.contarPorCategoria(ini, ini.plusDays(1))) {
            m.put((Categoria) fila[0], (Long) fila[1]);
        }
        long b = m.getOrDefault(Categoria.BICICLETA, 0L);
        long mo = m.getOrDefault(Categoria.MOTO, 0L);
        long v = m.getOrDefault(Categoria.VEHICULO, 0L);
        return new ResumenDia(fecha, b, mo, v, b + mo + v);
    }

    public List<RegistroAdminRes> delDia(LocalDate fecha, Categoria categoria) {
        LocalDateTime ini = fecha.atStartOfDay();
        LocalDateTime fin = ini.plusDays(1);
        List<Registro> lista = categoria == null
                ? registros.delDia(ini, fin)
                : registros.delDiaPorCategoria(ini, fin, categoria);
        return lista.stream().map(RegistroAdminRes::de).toList();
    }

    public List<RegistroAdminRes> buscar(String q) {
        if (q == null || q.trim().length() < 2)
            throw new IllegalArgumentException("Escribe al menos 2 caracteres para buscar");
        String patron = "%" + q.trim().toLowerCase() + "%";
        return registros.buscar(patron, PageRequest.of(0, 50)).stream().map(RegistroAdminRes::de).toList();
    }

    public List<VisitanteRes> visitantes() {
        return usuarios.findByVisitanteTrueOrderByCreadoEnDesc().stream()
                .map(u -> new VisitanteRes(u.getId(), u.getNombre(), u.getCorreo(), u.getMotivo(), u.getCreadoEn()))
                .toList();
    }
}