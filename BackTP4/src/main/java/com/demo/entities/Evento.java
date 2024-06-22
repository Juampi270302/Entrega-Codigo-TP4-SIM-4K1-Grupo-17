package com.demo.entities;

import com.demo.entities.Estados.Eventos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Evento {
    private Eventos tipoEvento;
    private double horaEvento;
    private Equipo equipo;
}
