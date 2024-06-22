package com.demo.entities;

import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipo {
    private Integer id_equipo;
    private EstadoEquipo equipo_estado;
    private Trabajo tipo_trabajo;
    private Double hora_llegada;
    private Double horaCambioTrabajoC;
    private Double horaReanudacionTrabajoC;
    private Double horaFinAtencionEstimada;
    private Double hora_salida;
    private boolean yaTermino;
}

