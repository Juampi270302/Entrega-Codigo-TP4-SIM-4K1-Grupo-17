package com.demo.services;

import java.util.ArrayList;
import java.util.Random;

public class Soporte {

    public double numeroRandom() {
        Random random = new Random();
        double numero_random1 = random.nextDouble();
        double numero_random = Math.round(numero_random1 * 100.0) / 100.0;
        return numero_random;
    }

}