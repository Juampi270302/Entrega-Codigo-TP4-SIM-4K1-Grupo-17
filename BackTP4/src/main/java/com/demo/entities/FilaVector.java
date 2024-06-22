package com.demo.entities;

import com.demo.entities.Estados.Eventos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FilaVector {
    public String evento;
    public double reloj;

    public Llegada llegada;
    public ColaVector colaVector;
    public int contadorEquipo;
    public double PromedioPermanencia;
    public double promedioOcupacion;
    public FinTrabajo finTrabajo;
    public Servidor servidor;
    public ArrayList<Equipo> equipos = new ArrayList<>();
}
