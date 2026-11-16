package com.unicundi.parqueadero.Service;

import com.unicundi.parqueadero.Dto.Disponibilidad;
import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Cupo;
import com.unicundi.parqueadero.Entity.Registro;
import com.unicundi.parqueadero.Repository.CupoRepository;
import com.unicundi.parqueadero.Repository.RegistroRepository;
import com.unicundi.parqueadero.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CupoService {
    private final CupoRepository cupos;
    private final RegistroRepository registros;
    private final UsuarioRepository usuarios;
    private final NotificacionService notis;

    public List<Disponibilidad> disponibilidad() {
        return Arrays.stream(Categoria.values()).map(c -> {
            long total = cupos.countByCategoria(c);
            long libres = cupos.countByCategoriaAndOcupadoFalse(c);
            return new Disponibilidad(c, total, libres, total - libres);
        }).toList();
    }

    @Transactional
    public void configurar(Categoria cat, int capacidad) {
        if (capacidad < 0) throw new IllegalArgumentException("La capacidad no puede ser negativa");
        List<Cupo> actuales = cupos.findByCategoriaOrderByNumero(cat);
        int n = actuales.size();
        if (capacidad > n) {
            for (int i = n + 1; i <= capacidad; i++) {
                Cupo c = new Cupo();
                c.setCategoria(cat);
                c.setNumero(i);
                cupos.save(c);
            }
        } else if (capacidad < n) {
            List<Cupo> sobran = actuales.subList(capacidad, n);
            if (sobran.stream().anyMatch(Cupo::isOcupado))
                throw new IllegalStateException("No se puede reducir: hay cupos ocupados por encima de " + capacidad);
            cupos.deleteAll(sobran);
        }
    }

    @Transactional
    public Registro entrar(Long uid, Categoria cat) {
        if (registros.findByUsuarioIdAndActivoTrue(uid).isPresent())
            throw new IllegalStateException("Ya tienes un ingreso activo, registra primero tu salida");

        Cupo cupo = cupos.libres(cat, PageRequest.of(0, 1)).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay cupos disponibles en " + cat));
        cupo.setOcupado(true);

        Registro r = new Registro();
        r.setUsuario(usuarios.getReferenceById(uid));
        r.setCupo(cupo);
        r.setEntrada(LocalDateTime.now());
        r.setActivo(true);
        registros.save(r);

        long disp = cupos.countByCategoriaAndOcupadoFalse(cat);
        if (disp == 10 || disp == 5)
            notis.publicar(cat, disp, "Quedan solo " + disp + " cupos de " + cat);
        else if (disp == 1)
            notis.publicar(cat, 1, "¡Último cupo disponible de " + cat + "!");
        else if (disp == 0)
            notis.publicar(cat, 0, "Parqueadero de " + cat + " lleno, sin cupos");
        return r;
    }

    @Transactional
    public Registro salir(Long uid) {
        Registro r = registros.findByUsuarioIdAndActivoTrue(uid)
                .orElseThrow(() -> new IllegalStateException("No tienes un ingreso activo"));
        r.setActivo(false);
        r.setSalida(LocalDateTime.now());
        r.getCupo().setOcupado(false);

        Categoria cat = r.getCupo().getCategoria();
        long disp = cupos.countByCategoriaAndOcupadoFalse(cat);
        if (disp == 10 || disp == 5)
            notis.publicar(cat, disp, "Se liberó un cupo: hay " + disp + " cupos de " + cat);
        else if (disp == 1)
            notis.publicar(cat, 1, "Hay 1 cupo disponible de " + cat);
        return r;
    }
}