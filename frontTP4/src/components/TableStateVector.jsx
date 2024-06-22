import React, {useContext, useEffect, useMemo, useState} from 'react';
import {ContextoSimulacion} from "../contexts/ContextoSimulacion.jsx";
import {getDatosPaginados} from "../scripts/HttpRequests.js";

const TableStateVector = ({calculosSimulacion}) => {
    const [filasPagina, setFilasPagina] = useState([]);

    const pages = Math.ceil(calculosSimulacion.cantidadFilas / 200);
    let pagesArray = ["Seleccione fila"];
    for (let i = 1; i <= pages; i++) {
        pagesArray.push(i);
    }

    useEffect(() => {
        setFilasPagina(calculosSimulacion.filasPaginadas)
    }, [calculosSimulacion]);


    const getDatos = async (page) => {
        try {
            console.log(page);
            if (page !== "Seleccione fila") {
                const response = await getDatosPaginados(page - 1);
                setFilasPagina(response.filas);
                console.log(response.filas);
            }
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <>
            <div className="row d-flex">
                <div className="col-4 d-flex justify-content-center">
                    <select className="form-select" onChange={(e) => {
                        getDatos(e.target.value)
                    }}>
                        {pagesArray && pagesArray.map((p) => {
                            return (<option value={p} key={p}>{p}</option>)
                        })}
                    </select>
                </div>
                <div className="col-4 d-flex justify-content-center flex-column text-center">
                    <label className="textoTitulo">Promedio permanencia</label>
                    <label className="textoSubTitulo">{calculosSimulacion.promedioPermanencia.toFixed(2)}</label>
                </div>
                <div className="col-4 d-flex justify-content-center flex-column text-center">
                    <label className="textoTitulo">Porcentaje ocupacion</label>
                    <label
                        className="textoSubTitulo">{calculosSimulacion.porcentajeOcupacionServidor.toFixed(2)}</label>
                </div>
            </div>
            <div className="row mt-2">
                <div className="col-12 container-fluid justify-content-center table-responsive table-container">

                    {filasPagina &&
                        <table className="table table-hover">
                            <thead>
                            <tr>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"></th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"></th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo">
                                    Llegadas
                                </th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo">
                                    Colas
                                </th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo">Fin trabajo</th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo"> Servidor</th>
                                <th className="bordeIzquierdo bordeArriba bordeAbajo">Acumuladores</th>
                                <th className="bordeDerecho bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                                <th className="bordeArriba bordeAbajo"></th>
                            </tr>
                            <tr>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Evento</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Reloj</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">RND llegada</th>
                                <th scope="col" className="bordeAbajo">Tiempo entre llegadas</th>
                                <th scope="col" className="bordeAbajo">Hora proxima llegada</th>
                                <th scope="col" className="bordeAbajo">RND tipo trabajo</th>
                                <th scope="col" className="bordeAbajo">Tipo trabajo</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Cola comun</th>
                                <th scope="col" className="bordeAbajo">Cola Trabajos C</th>
                                <th scope="col" className="bordeAbajo">Equipos en 2do plano</th>
                                <th scope="col" className="bordeAbajo">Lugares disponibles en cola</th>
                                <th scope="col" className="bordeAbajo">Contador de equipos</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">RND fin trabajo</th>
                                <th scope="col" className="bordeAbajo">Tiempo medio trabajo</th>
                                <th scope="col" className="bordeAbajo">Tiempo de trabajo</th>
                                <th scope="col" className="bordeAbajo"> Hora fin trabajo</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Estado tecnico</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Tiempo ocupacion</th>
                                <th scope="col" className="bordeDerecho bordeAbajo">Tiempo permanencia equipos</th>
                                <th scope="col" className="bordeIzquierdo bordeAbajo">Promedio permanencia</th>
                                <th scope="col" className="bordeAbajo">Porcentaje ocupacion</th>
                            </tr>
                            </thead>
                            <tbody>
                            {filasPagina && filasPagina.map((fila, indice) => (
                                <tr key={indice}>
                                    <td className="bordeIzquierdo">{fila.evento}</td>
                                    <td className="bordeIzquierdo">{fila.reloj.toFixed(2)}</td>
                                    <td className="bordeIzquierdo">{
                                        fila.llegada.rndLlegada !== null
                                            ? fila.llegada.rndLlegada.toFixed(2)
                                            : null}
                                    </td>
                                    <td>{fila.llegada.tiempoEntreLlegada !== null
                                        ? fila.llegada.tiempoEntreLlegada.toFixed(2)
                                        : null}
                                    </td>
                                    <td>{fila.llegada.horaProximaLlegada.toFixed(2)}</td>
                                    <td>{fila.llegada.rndTipoTrabajo !== null ?
                                        fila.llegada.rndTipoTrabajo.toFixed(2)
                                        : null}</td>
                                    <td>{fila.llegada.trabajo}</td>
                                    <td className="bordeIzquierdo">{fila.colaVector.colaComun}</td>
                                    <td>{fila.colaVector.colaTrabajoC}</td>
                                    <td>{fila.colaVector.trabajoCSegundoPlano}</td>
                                    <td>{fila.colaVector.lugaresLibres}</td>
                                    <td>{fila.contadorEquipo}</td>
                                    <td className="bordeIzquierdo">{fila.finTrabajo.rndFinTrabajo !== null
                                        ? fila.finTrabajo.rndFinTrabajo.toFixed(2)
                                        : null}
                                    </td>
                                    <td>{fila.finTrabajo.mediaTiempoAtencion !== null
                                        ? fila.finTrabajo.mediaTiempoAtencion
                                        : null}
                                    </td>
                                    <td>{fila.finTrabajo.tiempoAtencion !== null
                                        ? fila.finTrabajo.tiempoAtencion.toFixed(2)
                                        : null
                                    }
                                    </td>
                                    <td>{fila.finTrabajo.horaFinTrabajo !== null
                                        ? fila.finTrabajo.horaFinTrabajo.toFixed(2)
                                        : null
                                    }</td>
                                    <td className="bordeIzquierdo">{fila.servidor.estado}</td>
                                    <td className="bordeIzquierdo">{fila.servidor.tiempoOcupacionAcum.toFixed(2)}</td>
                                    <td className="bordeDerecho">{fila.servidor.tiempoPermanenciaEquipoAcum.toFixed(2)}</td>
                                    <td className="bordeIzquierdo">{fila.promedioPermanencia.toFixed(2)}</td>
                                    <td>{fila.promedioOcupacion.toFixed(2)}</td>
                                    {fila.equipos.map((equipo, index) => {
                                        if (filasPagina[indice + 1] !== undefined &&
                                            filasPagina[indice + 1].equipos[index] !== undefined
                                            && filasPagina[indice + 1].equipos[index] !== null
                                            && filasPagina[indice + 1].equipos[index].id_equipo !== null
                                            && equipo !== null

                                        ) {
                                            if (filasPagina[indice + 1].equipos[index].id_equipo !== equipo.id_equipo) {
                                                filasPagina[indice + 1].equipos.splice(index, 0, null)
                                            }
                                        }
                                        if (filasPagina[indice + 1] !== undefined &&
                                            equipo === null && filasPagina[indice + 1].equipos[index] !== null) {
                                            filasPagina[indice + 1].equipos.splice(index, 0, null)
                                        }
                                        if (equipo !== null) {
                                            return (<>
                                                <td className="bordeIzquierdo">
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Id equipo</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Id equipo</tr>
                                                            : null
                                                    }
                                                    {equipo.id_equipo}</td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Estado</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Estado</tr>
                                                            : null
                                                    }
                                                    {equipo.equipo_estado}</td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Tipo trabajo</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Tipo trabajo</tr>
                                                            : null
                                                    }
                                                    {equipo.tipo_trabajo}</td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Hora llegada</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Hora llegada</tr>
                                                            : null
                                                    }
                                                    {equipo.hora_llegada.toFixed(2)}</td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Hora cambio TC</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Hora cambio TC</tr>
                                                            : null
                                                    }
                                                    {equipo.horaCambioTrabajoC !== null
                                                        ? equipo.horaCambioTrabajoC.toFixed(2)
                                                        : null}
                                                </td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Hora reanudacion TC</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Hora reanudacion TC</tr>
                                                            : null
                                                    }
                                                    {equipo.horaReanudacionTrabajoC !== null
                                                        ? equipo.horaReanudacionTrabajoC.toFixed(2)
                                                        : null}
                                                </td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Fin at estimada</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Fin at estimada</tr>
                                                            : null
                                                    }
                                                    {equipo.horaFinAtencionEstimada !== null
                                                        ? equipo.horaFinAtencionEstimada.toFixed(2)
                                                        : null}
                                                </td>
                                                <td>
                                                    {filasPagina[indice - 1] === undefined
                                                        ? <tr className="textoSubTitulo">Hora salida</tr>
                                                        : filasPagina[indice - 1].equipos[index] === undefined
                                                            ? <tr className="textoSubTitulo">Hora salida</tr>
                                                            : null
                                                    }
                                                    {equipo.hora_salida !== null
                                                        ? equipo.hora_salida.toFixed(2)
                                                        : null}
                                                </td>
                                            </>)
                                        } else {
                                            return (
                                                <>
                                                    <td className="bordeIzquierdo"></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <th></th>
                                                </>

                                            )
                                        }
                                    })}
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    }
                </div>
            </div>
        </>


    );
};

export default TableStateVector;