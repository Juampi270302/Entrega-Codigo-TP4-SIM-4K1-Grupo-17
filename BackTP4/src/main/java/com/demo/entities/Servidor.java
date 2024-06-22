package com.demo.entities;

import com.demo.entities.Estados.EstadoServidor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Servidor {
    public EstadoServidor estado;
    public double tiempoOcupacionAcum;
    public double tiempoPermanenciaEquipoAcum;

    public void acumTiempoPermanenciaEquipoAcum(double tiempoPermanenciaEquipoAcum) {
        this.tiempoPermanenciaEquipoAcum = this.tiempoPermanenciaEquipoAcum + tiempoPermanenciaEquipoAcum;
    }

    public void acumularIteracionAIteracion(Double tiempoOcupacion){
        this.tiempoOcupacionAcum = this.tiempoOcupacionAcum + tiempoOcupacion;
    }
}
