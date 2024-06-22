package com.demo.services;



import com.demo.entities.FilaVector;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class Simulacion {
    private ArrayList<Double> probabilidadesOcurrencia = new ArrayList<>(Arrays.asList(0.3, 0.25, 0.25, 0.2));
    private ArrayList<Double> tiempo_Demora = new ArrayList<>(Arrays.asList(2.0,1.0,3.0,1.0));

    private ArrayList<FilaVector> filaVectors = new ArrayList<>();

    private FilaVector filaVectorAnterior;
    private FilaVector filaVectorActual;
}
