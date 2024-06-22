package com.demo.entities;

import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class FinTrabajo {
    public Double rndFinTrabajo;
    public Double mediaTiempoAtencion;
    public Double tiempoAtencion;
    public Double horaFinTrabajo;


    private Double generateRandom() {
        Random rnd = new Random();
        double rndCeroUno = rnd.nextDouble();
        return (Math.round(rndCeroUno * 100.0) / 100.0);
    }

    public void calcularHoraFinTrabajo(Trabajo tipoTrabajo,
                                       ArrayList<Double> tiemposMediosTrabajo,
                                       Double reloj,
                                       Double limite_inferiorUniforme,
                                       Double limite_superiorUniforme
                                           ){
        Double rnd = generateRandom();
        Double mediaTrabajo = calcularMediaTrabajo(tipoTrabajo, tiemposMediosTrabajo);
        Double tiempoAtencion = (mediaTrabajo - limite_inferiorUniforme) +
                rnd * ((mediaTrabajo + limite_superiorUniforme) - (mediaTrabajo - limite_inferiorUniforme));
        this.mediaTiempoAtencion = mediaTrabajo;
        this.rndFinTrabajo = rnd;
        this.tiempoAtencion = tiempoAtencion;
        this.horaFinTrabajo = reloj + tiempoAtencion;
    }

    public Double calcularMediaTrabajo(  Trabajo tipoTrabajo,
                                         ArrayList<Double> tiemposMediosTrabajo) {
        switch (tipoTrabajo) {
            case A:
                return tiemposMediosTrabajo.get(0);
            case B:
                return tiemposMediosTrabajo.get(1);
            case C:
                return tiemposMediosTrabajo.get(2);
            case D:
                return tiemposMediosTrabajo.get(3);
            default:
                return 0.0;
        }
    }
}
