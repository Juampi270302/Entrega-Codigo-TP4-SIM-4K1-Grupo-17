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

public class Llegada {

    public Double rndLlegada;
    public Double tiempoEntreLlegada;
    public Double horaProximaLlegada;

    public Double rndTipoTrabajo;
    public Trabajo trabajo;


    private double rndLlegada() {
        Random random = new Random();
        double numero_random1 = random.nextDouble();
        double numero_random = Math.round(numero_random1 * 100.0) / 100.0;
        return numero_random;
    }

    public void generarProximaLlegada(double reloj) {
        double ran = rndLlegada();
        ran = Math.round(ran * 100.0) / 100.0;
        double tiempo_entre_llegada = 0.5 + ran * (1.5 - 0.5);
        tiempo_entre_llegada = Math.round(tiempo_entre_llegada * 100.0) / 100.0;

        this.rndLlegada = ran;
        this.tiempoEntreLlegada = tiempo_entre_llegada;
        this.horaProximaLlegada = tiempo_entre_llegada + reloj;
    }

    private Double generateRandom() {
        Random rnd = new Random();
        double rndCeroUno = rnd.nextDouble();
        return (Math.round(rndCeroUno * 100.0) / 100.0);
    }


    public void calcularTipoTrabajo(ArrayList<Trabajo> tiposTrabajo,
                                    ArrayList<Double> probabilidadesTipoTrabajo) {

        Double rnd = generateRandom();
        ArrayList<Double> liProbabilidadesTipoTrabajo =
                calcularLimitesInferiores(probabilidadesTipoTrabajo);

        this.rndTipoTrabajo = rnd;

        for (int i = 0; i <= liProbabilidadesTipoTrabajo.size() - 1; i++) {
            if (i == liProbabilidadesTipoTrabajo.size() - 1) {
                if (rnd >= liProbabilidadesTipoTrabajo.get(i)) {
                    this.trabajo = tiposTrabajo.get(i);
                }
            } else {
                if (rnd >= liProbabilidadesTipoTrabajo.get(i) &&
                        rnd < liProbabilidadesTipoTrabajo.get(i + 1)
                ) {
                    this.trabajo = tiposTrabajo.get(i);
                    break;
                }
            }
        }
    }

    private ArrayList<Double> calcularLimitesInferiores(ArrayList<Double> arrayProbabilidades) {
        ArrayList<Double> limitesInferiores = new ArrayList<>();
        for (int i = 0; i <= arrayProbabilidades.size() - 1; i++) {
            if (i == 0) {
                limitesInferiores.add(0.00);
            } else {
                Double limiteInferiorAnterior = limitesInferiores.get(i - 1);
                Double probabilidadAnterior = arrayProbabilidades.get(i - 1);
                Double limiteInferiorActual = limiteInferiorAnterior + probabilidadAnterior;
                limitesInferiores.add(limiteInferiorActual);
            }
        }
        return limitesInferiores;
    }

    public double[][] intervalos(double[] valores_probabilidad) {
        int n = valores_probabilidad.length;
        double[][] intervalo_proba = new double[n][2];
        double primero = 0.0;

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                intervalo_proba[i][0] = primero;
                intervalo_proba[i][1] = 1.0;
                return intervalo_proba;
            }

            double ultimo = primero + valores_probabilidad[i];
            intervalo_proba[i][0] = primero;
            intervalo_proba[i][1] = ultimo;
            primero = ultimo;
        }
        return intervalo_proba;
    }
}
