package puj.sd.biblioteca;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Actividad implements Serializable {
    private static final AtomicInteger _ID = new AtomicInteger(0);

    private Integer id;
    private TipoActividad tipoActividad;
    private LocalDate fechaInicioPrestamo;
    private LocalDate fechaFinPrestamo;
    private Long usuarioCedula;
    private String usuarioNombre;
    private Boolean usuarioPenalizado;
    private String codigoLibro;
    private EstadoPrestamo estado;


    public Actividad(TipoActividad tipoActividad, LocalDate fechaInicioPrestamo, LocalDate fechaFinPrestamo, Long usuarioCedula,
            String usuarioNombre, String codigoLibro, EstadoPrestamo estado) {
        this.id = _ID.incrementAndGet(); 
        this.tipoActividad = tipoActividad;
        this.fechaInicioPrestamo = fechaInicioPrestamo;
        this.fechaFinPrestamo = fechaFinPrestamo;
        this.usuarioCedula = usuarioCedula;
        this.usuarioNombre = usuarioNombre;
        this.usuarioPenalizado = false;
        this.codigoLibro = codigoLibro;
        this.estado = estado;
    }


    public Actividad(TipoActividad tipoActividad, LocalDate fechaDevolucion, Long usuarioCedula, String usuarioNombre, String codigoLibro, Boolean usuarioPenalizado) {
        this.tipoActividad = tipoActividad;
        this.fechaFinPrestamo = fechaDevolucion;
        this.usuarioCedula = usuarioCedula;
        this.usuarioNombre = usuarioNombre;
        this.codigoLibro = codigoLibro;
        this.usuarioPenalizado = usuarioPenalizado;
    }
    
    public Actividad(Long usuarioCedula) {
        this.id = _ID.incrementAndGet(); 
        this.usuarioCedula = usuarioCedula;
    }
}
