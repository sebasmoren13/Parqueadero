package com.unicundi.parqueadero.Service;

import com.unicundi.parqueadero.Entity.Categoria;
import com.unicundi.parqueadero.Entity.Notificacion;
import com.unicundi.parqueadero.Repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepository repo;
    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();

    public SseEmitter suscribir() {
        SseEmitter e = new SseEmitter(0L);
        emisores.add(e);
        e.onCompletion(() -> emisores.remove(e));
        e.onTimeout(() -> emisores.remove(e));
        e.onError(x -> emisores.remove(e));
        return e;
    }

    public void publicar(Categoria cat, long disponibles, String mensaje) {
        Notificacion n = new Notificacion();
        n.setCategoria(cat);
        n.setDisponibles(disponibles);
        n.setMensaje(mensaje);
        n.setFecha(LocalDateTime.now());
        repo.save(n);
        for (SseEmitter e : emisores) {
            try {
                e.send(SseEmitter.event().name("cupos").data(n));
            } catch (Exception ex) {
                emisores.remove(e);
            }
        }
    }

    public List<Notificacion> ultimas() {
        return repo.findTop20ByOrderByFechaDesc();
    }
}