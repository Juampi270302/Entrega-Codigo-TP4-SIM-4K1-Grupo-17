package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Eventos;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionPractica extends Simulacion {

    //Desplegar para ver informacion de los atributos de abajo
    /**
     * PARAMETROS DE LA SIMULACION
     * - tiempoSimulacion: Tiempo de simulacion en horas.
     * - probabilidadesTipoTrabajo: Probabilidades de los diferentes tipos de trabajo (A,B,C,D).
     * - tiemposMediaTrabajo: Tiempos medios de ejecucion de los diferentes tipos de trabajo (A,B,C,D).
     * - limite_inferiorUniforme: Limite inferior de la distribucion uniforme del tiempo de trabajo.
     * - limite_superiorUniforme: Limite superior de la distribucion uniforme del tiempo de trabajo.
     * - tiempoDesdeInicioEquipoC: Tiempo desde que inicia el trabajo C hasta que puede ser dejado solo.
     * - tiempoAntesFinEquipoC: Tiempo antes de terminar el trabajo C en el que hay que retomarlo.
     * - tiempoInicioResultado: Tiempo desde el que empieza a guardar filas del vector para devolver.
     * - cantidadItercaciones: Cantidad de iteraciones que devuelve a partir del tiempoInicioResultado.
     */
    private double tiempoSimulacion;
    private ArrayList<Double> probabilidadesTipoTrabajo;
    private ArrayList<Double> tiemposMediaTrabajo;
    private double limite_inferiorUniforme;
    private double limite_superiorUniforme;
    private double tiempoDesdeInicioEquipoC;
    private double tiempoAntesFinEquipoC;
    private double tiempoInicioResultado;
    private int cantidadItercaciones;

    //Desplegar para ver informacion de los atributos de abajo
    /**
     * - tipoTrabajos: Tipos de trabajos que se pueden realizar.
     * - vectorDeEstados: Vector de estados de la simulacion, almacena una fila por iteracion a partir
     *      del "tiempoInicioResultado" y hasta alcanzar un tamaño de "cantiadaIteraciones".
     * - equipos: Lista de equipos que ingresaron al sistema, son eliminados una vez que salen.
     * - colaComun: Cola de equipos que ingresaron al sistema y esperan ser atendidos por primera vez.
     * - colaTrabajosC: Cola de equipos a los que se le realiza un trabajo C, fueron atendidos, dejados en segundo plano,
     *      y al momento de retomar su atencion el servidor estaba ocupado, por lo tanto esperan en esta cola a que termine
     *      lo que este haciendo y pueda finalizar su atencion.
     * - proximosEventos: Lista de los proximos eventos a ocurrir en la simulacion, en cada iteracion se extrae el que
     *      ocurre y se añade el proximo si es que hay.
     * - filaActual: Es la fila que se crea al finalizar la ejecucion del evento correspondiente a la iteracion actual,
     *      se añade al vector de estados al finalizar el evento actual.
     * - filaAnterior: Al inicio de la iteracion, el elemento que está en "filaActual", corresponde a la fila generada
     *      en la iteracion anterior, por lo tanto pasa a esta variable y sirve para mantener en la proxima fila a generar
     *      los valores que deban mantenerse de la fila de la interacion anterior.
     * - contadorIteraciones: Contador de las iteraciones que se han realizado en la simulacion, sirve para poner
     *      finalizar la simulacion si se alcanzan las 100000 iteraciones.
     * - contadorIteracionesResultado: Contador de las iteraciones que sirve para determinar cuantas filas
     *      del vector de estados guardar en el objeto a devolver a partir del tiempoInicioResultado.
     * - reloj: Tiempo actual de la simulacion.
     * - proximoEvento: Proximo evento a ocurrir en la simulacion, se extrae en cada iteracion del array de
     *      proximosEventos y los datos del evento (Hora, Equipo involucrado, Tipo de evento).
     * - contadorEquipos: Contador de los equipos que han ingresado al sistema, sirve para asignar un id a cada equipo y
     *      llevar un conteo para las estadisticas.
     * */

    private ArrayList<Trabajo> tipoTrabajos = new ArrayList<>(Arrays.asList(Trabajo.values()));
    private ArrayList<FilaVector> vectorDeEstados = new ArrayList<>();
    private ArrayList<Equipo> equipos = new ArrayList<>();
    private ArrayList<Equipo> colaComun = new ArrayList<>();
    private ArrayList<Equipo> colaTrabajosC = new ArrayList<>();
    private ArrayList<Evento> proximosEventos = new ArrayList<>();
    private FilaVector filaActual = null;
    private FilaVector filaAnterior = null;
    private int contadorIteraciones = 0;
    private int contadorIteracionesResultado = 1;

    private double reloj = 0;
    private Evento proximoEvento = null;
    private int contadorEquipos = 0;


    private void buscarProximoEvento() {

        Evento proximoEvento = null;
        Optional<Evento> proxEvento = proximosEventos.stream()
                .min(Comparator.comparing(evento ->
                        evento.getHoraEvento() - this.reloj));
        if(proxEvento.isPresent()){
            proximoEvento = proxEvento.get();
        }
        this.proximosEventos.remove(proximoEvento);
        this.proximoEvento = proximoEvento;
    }

    public FilasPaginadas getFilasPaginadas(Integer page) {
        FilasPaginadas filasPaginadas = new FilasPaginadas();
        FilaVector ultimaFila = this.vectorDeEstados.getLast();
        if (this.vectorDeEstados.size() > 200) {
            int fromIndex = page * 200;
            int toIndex = Math.min((page + 1) * 200, this.vectorDeEstados.size());
            filasPaginadas.setFilas(this.vectorDeEstados.subList(fromIndex, toIndex));
        } else {
            filasPaginadas.setFilas(this.vectorDeEstados);
        }
        if (!filasPaginadas.getFilas().contains(ultimaFila)) {
            filasPaginadas.getFilas().add(ultimaFila);
        }
        if (filasPaginadas.getFilas().getFirst() == ultimaFila) {
            filasPaginadas.getFilas().remove(ultimaFila);
            filasPaginadas.getFilas().add(ultimaFila);
        }
        return filasPaginadas;
    }

    public ResultadosSimulacion cola(double tiempo_simulacion,
                                     ArrayList<Double> probabilidadesTipoTrabajo,
                                     ArrayList<Double> tiemposMediaTrabajo,
                                     double limite_inferiorUniforme,
                                     double limite_superiorUniforme,
                                     double tiempoDesdeInicioEquipoC,
                                     double tiempoAntesFinEquipoC,
                                     double tiempoInicioResultado,
                                     int cantidadItercaciones) {

        this.tiempoSimulacion = tiempo_simulacion;
        this.probabilidadesTipoTrabajo = probabilidadesTipoTrabajo;
        this.tiemposMediaTrabajo = tiemposMediaTrabajo;
        this.limite_inferiorUniforme = limite_inferiorUniforme / 60;
        this.limite_superiorUniforme = limite_superiorUniforme / 60;
        this.tiempoDesdeInicioEquipoC = tiempoDesdeInicioEquipoC / 60;
        this.tiempoAntesFinEquipoC = tiempoAntesFinEquipoC / 60;
        this.tiempoInicioResultado = tiempoInicioResultado;
        this.cantidadItercaciones = cantidadItercaciones;

        this.vectorDeEstados.clear();
        this.equipos.clear();
        this.colaComun.clear();
        this.colaTrabajosC.clear();
        this.proximosEventos.clear();

        this.filaActual = null;
        this.filaAnterior = null;
        this.contadorIteraciones = 0;
        this.contadorIteracionesResultado = 0;

        this.reloj = 0;
        this.proximoEvento = null;
        this.contadorEquipos = 0;
        this.vectorDeEstados.clear();

        double reloj = this.reloj;
        Llegada llegada_primera = new Llegada();
        llegada_primera.generarProximaLlegada(reloj);

        this.proximosEventos.add(new Evento(
                Eventos.Llegada,
                llegada_primera.getHoraProximaLlegada(),
                null
        ));

        ColaVector colaVectorInicio = new ColaVector(
                this.colaComun.size(),
                this.colaTrabajosC.size(),
                0,
                9);

        int contadorEquipos = this.contadorEquipos;

        FinTrabajo finTrabajo = new FinTrabajo();

        Servidor servidorInicio = new Servidor(EstadoServidor.Libre,
                0,
                0);


        this.filaActual = new FilaVector(
                Eventos.Inicio.toString(),
                this.reloj,
                llegada_primera,
                colaVectorInicio,
                contadorEquipos,
                0,
                0,
                finTrabajo,
                servidorInicio,
                clonarEquipos());

        if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones) {
            this.vectorDeEstados.add(this.filaActual);
            this.contadorIteracionesResultado++;
        }

        this.contadorIteraciones++;

        while (this.reloj < this.tiempoSimulacion && this.contadorIteraciones <= 100000) {
            this.buscarProximoEvento();
            this.filaAnterior = this.filaActual;
            this.reloj = this.proximoEvento.getHoraEvento();

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Llegada)) {
                this.eventoLlegada();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Cambio)) {
                this.eventoCambioTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Reanudacion)) {
                this.eventoReanudacionTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.FinTrabajo)) {
                this.eventoFinTrabajo();
            }

            if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones) {
                this.contadorIteracionesResultado++;
                this.vectorDeEstados.add(this.filaActual);
            }
            this.contadorIteraciones++;
        }

        if (this.vectorDeEstados.getLast() != this.filaActual) {
            this.vectorDeEstados.add(this.filaActual);
        }

        ResultadosSimulacion resultados = new ResultadosSimulacion();
        resultados.calcularPorcentajeOcupacion(this.reloj, this.filaActual.servidor.getTiempoOcupacionAcum());
        resultados.calcularPromedioPermanencia(this.contadorEquipos, this.filaActual.servidor.getTiempoPermanenciaEquipoAcum());
        resultados.setCantidadFilas(this.vectorDeEstados.size());

        if (this.vectorDeEstados.size() > 200) {
            resultados.setFilasPaginadas(this.vectorDeEstados.subList(0, 200));
            resultados.getFilasPaginadas().add(this.vectorDeEstados.getLast());
        } else {
            resultados.setFilasPaginadas(this.vectorDeEstados);
        }
        FilaVector ultimaFila = this.vectorDeEstados.getLast();
        if (!resultados.getFilasPaginadas().contains(ultimaFila)) {
            resultados.getFilasPaginadas().add(ultimaFila);
        }
        return resultados;
    }

    private void eventoFinTrabajo() {

        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        Equipo equipoFinalizacion = this.proximoEvento.getEquipo();
        FinTrabajo finTrabajo = new FinTrabajo();

        if (colasEstadoActual.getColaTrabajoC() > 0) {
            Equipo equipoEnColaCAAtender = this.colaTrabajosC.getFirst();
            this.colaTrabajosC.remove(equipoEnColaCAAtender);
            equipoEnColaCAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();

            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaCAAtender
                    )
            );
        } else if (colasEstadoActual.getColaComun() > 0) {
            colasEstadoActual.restarColaComun();
            Equipo equipoEnColaComun = this.colaComun.getFirst();
            this.colaComun.remove(equipoEnColaComun);
            equipoEnColaComun.setEquipo_estado(EstadoEquipo.Atendido);

            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComun.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );
            equipoEnColaComun.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComun
                    )
            );

            if (equipoEnColaComun.getTipo_trabajo().equals(Trabajo.C)) {
                Double horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                this.reloj + this.tiempoDesdeInicioEquipoC,
                                equipoEnColaComun
                        )
                );
                equipoEnColaComun.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }

        } else {
            servidorActual.setEstado(EstadoServidor.Libre);
        }

        equipoFinalizacion.setHora_salida(this.reloj);
        equipoFinalizacion.setEquipo_estado(EstadoEquipo.Finalizado);

        double tiempoPermanencia = equipoFinalizacion.getHora_salida() - equipoFinalizacion.getHora_llegada();
        servidorActual.acumTiempoPermanenciaEquipoAcum(tiempoPermanencia);

        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        double promedioPermanencia = servidorActual.getTiempoPermanenciaEquipoAcum() / this.contadorEquipos;
        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.FinTrabajo + " E" + equipoFinalizacion.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                promedioPermanencia,
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );
    }

    private void eventoReanudacionTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        FinTrabajo finTrabajo = new FinTrabajo();

        Equipo equipoReanudacion = this.proximoEvento.getEquipo();
        equipoReanudacion.setHoraReanudacionTrabajoC(null);

        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {

            equipoReanudacion.setEquipo_estado(EstadoEquipo.EncolaC);
            equipoReanudacion.setHoraFinAtencionEstimada(null);
            colasEstadoActual.sumarColaTrabajoC();
            this.colaTrabajosC.add(equipoReanudacion);
            this.anularFinTrabajoC(equipoReanudacion.getId_equipo());
            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());
        } else {

            equipoReanudacion.setEquipo_estado(EstadoEquipo.Atendido);
            servidorActual.setEstado(EstadoServidor.Ocupado);
            finTrabajo.setHoraFinTrabajo(equipoReanudacion.getHoraFinAtencionEstimada());
            colasEstadoActual.restarTrabajoCSegundoPlano();
        }

        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Reanudacion + " E" + equipoReanudacion.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );

    }

    private void anularFinTrabajoC(Integer idEquipo) {
        for (Evento evento : this.proximosEventos) {
            if (evento.getTipoEvento().equals(Eventos.FinTrabajo) && evento.getEquipo().getId_equipo() == idEquipo) {
                this.proximosEventos.remove(evento);
                break;
            }
        }
    }


    private void eventoCambioTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum
        );

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        Equipo equipoCambioTrabajo = this.proximoEvento.getEquipo();
        equipoCambioTrabajo.setEquipo_estado(EstadoEquipo.At2doplano);
        colasEstadoActual.sumarTrabajoCSegundoPlano();

        Double horaReanudacionTrabajoC =
                equipoCambioTrabajo.getHoraFinAtencionEstimada() - this.tiempoAntesFinEquipoC;
        equipoCambioTrabajo.setHoraCambioTrabajoC(null);
        equipoCambioTrabajo.setHoraReanudacionTrabajoC(horaReanudacionTrabajoC);

        this.proximosEventos.add(
                new Evento(
                        Eventos.Reanudacion,
                        horaReanudacionTrabajoC,
                        equipoCambioTrabajo
                )
        );

        FinTrabajo finTrabajo = new FinTrabajo();

        // Se verifica el estado de las colas
        if (colasEstadoActual.getColaTrabajoC() > 0) {

            Equipo equipoEnColaCAAtender = this.colaTrabajosC.getFirst();
            equipoEnColaCAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();

            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaCAAtender
                    )
            );

        } else if (colasEstadoActual.getColaComun() > 0) {

            Equipo equipoEnColaComunAAtender = this.colaComun.getFirst();
            equipoEnColaComunAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            this.colaComun.remove(equipoEnColaComunAAtender);
            colasEstadoActual.restarColaComun();

            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComunAAtender.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComunAAtender
                    )
            );

            // Se asigna la hora de fin de atencion estimada al equipo que se esta atendiendo.
            equipoEnColaComunAAtender.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            if (equipoEnColaComunAAtender.getTipo_trabajo().equals(Trabajo.C)) {
                double horaCambioTrabajoC = this.reloj + this.tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipoEnColaComunAAtender
                        )
                );
                equipoEnColaComunAAtender.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }
        } else {
            servidorActual.setEstado(EstadoServidor.Libre);
            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());
        }

        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Cambio + " E" + equipoCambioTrabajo.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );
    }

    private void eventoLlegada() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().getTiempoOcupacionAcum(),
                this.filaAnterior.getServidor().getTiempoPermanenciaEquipoAcum());

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        Llegada proximaLLegada = new Llegada();
        proximaLLegada.generarProximaLlegada(this.reloj);

        this.proximosEventos.add(
                new Evento(
                        Eventos.Llegada,
                        proximaLLegada.getHoraProximaLlegada(),
                        null)
        );

        FinTrabajo finTrabajo = new FinTrabajo();
        Equipo equipo = new Equipo();

        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {

            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());

            if (colasEstadoActual.getLugaresLibres() > 0) {

                proximaLLegada.calcularTipoTrabajo(tipoTrabajos, probabilidadesTipoTrabajo);
                this.contadorEquipos++;

                equipo.setId_equipo(this.contadorEquipos);
                equipo.setHora_llegada(reloj);
                equipo.setTipo_trabajo(proximaLLegada.getTrabajo());
                equipos.add(equipo);

                colasEstadoActual.sumarColaComun();
                this.colaComun.add(equipo);
                equipo.setEquipo_estado(EstadoEquipo.EnCola);
            }

        } else {
            this.contadorEquipos++;
            servidorActual.setEstado(EstadoServidor.Ocupado);

            proximaLLegada.calcularTipoTrabajo(tipoTrabajos, probabilidadesTipoTrabajo);

            finTrabajo.calcularHoraFinTrabajo(
                    proximaLLegada.getTrabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme);

            equipo.setId_equipo(this.contadorEquipos);
            equipo.setEquipo_estado(EstadoEquipo.Atendido);
            equipo.setTipo_trabajo(proximaLLegada.getTrabajo());
            equipo.setHora_llegada(reloj);
            equipo.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipo)
            );

            if (proximaLLegada.getTrabajo().equals(Trabajo.C)) {
                double horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipo)
                );
                equipo.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }
            equipos.add(equipo);
        }

        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Llegada + " E" + equipo.getId_equipo(),
                this.reloj,
                proximaLLegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos());
    }


    private ArrayList<Equipo> clonarEquipos() {
        ArrayList<Equipo> equipos = new ArrayList<>();
        for (Equipo equipo : this.equipos) {
            if (!equipo.isYaTermino()) {
                Equipo equipoClon = new Equipo();
                equipoClon.setId_equipo(equipo.getId_equipo());
                equipoClon.setEquipo_estado(equipo.getEquipo_estado());
                equipoClon.setTipo_trabajo(equipo.getTipo_trabajo());
                equipoClon.setHora_llegada(equipo.getHora_llegada());
                equipoClon.setHoraCambioTrabajoC(equipo.getHoraCambioTrabajoC());
                equipoClon.setHoraReanudacionTrabajoC(equipo.getHoraReanudacionTrabajoC());
                equipoClon.setHoraFinAtencionEstimada(equipo.getHoraFinAtencionEstimada());
                equipoClon.setHora_salida(equipo.getHora_salida());
                equipos.add(equipoClon);
            }
            if (equipo.getEquipo_estado() == EstadoEquipo.Finalizado) {
                equipo.setYaTermino(true);
            }
        }
        return equipos;
    }
}

