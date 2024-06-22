import React, {useContext, useState, useReducer, useEffect, useMemo} from 'react';
import TableTimeProb from "./TableTimeProb.jsx";
import {ContextoSimulacion} from "../contexts/ContextoSimulacion.jsx";
import "../styles/Estilos.css";
import {getSimulacion} from "../scripts/HttpRequests.js"

const SimulationParameters = ({setCalculosSimulacion}) => {

    const {updateParams, params} = useContext(ContextoSimulacion);
    useEffect(() => {
        validateProb();
    }, [params.probTA, params.probTB, params.probTC, params.probTD]);
    useEffect(() => {
        validateHoraInicio();
    }, [params.initTimeView, params.cantTimeSim]);
    useEffect(() => {
        validateTimeFinTC();
    }, [params.timeEndTC, params.timeTC, params.timeMin, params.timeInitTC])

    const initialState = {
        probMasUno: false,
        minInitFinTC: false,
        horaInicioMax: false,
        probActual: 0
    };

    function reducer(errors, action) {
        switch (action.type) {
            case 'probMasUno':
                return {...errors, probMasUno: action.value};
            case 'minInitFinTC':
                return {...errors, minInitFinTC: action.value};
            case 'horaInicioMax':
                return {...errors, horaInicioMax: action.value};
            case "probActual":
                return {...errors, probActual: action.value};
            default:
                throw new Error();
        }
    }

    const [errors, setErrors] = useReducer(reducer, initialState);

    const validateProb = () => {
        let sum = params.probTA + params.probTB + params.probTC + params.probTD;
        if (sum !== 1) {
            setErrors({type: 'probMasUno', value: true});
            setErrors({type: 'probActual', value: sum});
        } else {
            setErrors({type: 'probMasUno', value: false});
        }
    }

    const validateHoraInicio = () => {
        if (params.initTimeView >= params.cantTimeSim) {
            setErrors({type: 'horaInicioMax', value: true});
        } else {
            setErrors({type: 'horaInicioMax', value: false});
        }
    }

    const validateTimeFinTC = () =>{
        if(params.timeInitTC + params.timeEndTC >= (params.timeTC * 60)- params.timeMin){
            setErrors({type: 'minInitFinTC', value: true});
        } else {
            setErrors({type: 'minInitFinTC', value: false});
        }
    }
    const sendData = async () => {
        try {
            const response = await getSimulacion(params);
            console.log(response);
            setCalculosSimulacion(response);
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="container-fluid">
            <div className="row d-flex justify-content-center mt-4">
                <div className="col-12 ">
                    <div className="card">
                        <div className="card-body">
                            <div className="row d-flex justify-content-center textoTitulo">
                                Probabilidades trabajo C
                            </div>
                            <div className="row d-flex justify-content-center">
                                <TableTimeProb></TableTimeProb>
                                {errors.probMasUno && <span className="errores text-center mt-0">
                                    Error,la suma de las probabilidades debe ser de 1 (Valor actual: {errors.probActual})
                                </span>}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="row d-flex justify-content-center mt-2">
                <div className="col-6 d-flex flex-column">
                    <div className="card">
                        <div className="card-body">
                            <div className="row d-flex justify-content-center textoTitulo">
                                Limite superior e inferior de tiempo medio
                            </div>
                            <div className="row d-flex justify-content-center">
                                <div className="col-6 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Tiempo medio en menos</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={5}
                                           min={0}
                                           step={0.01}
                                           value={params.timeMin}
                                           onChange={() => {
                                               if (event.target.value >= 0) {
                                                   updateParams({
                                                       type: 'setTimeMin',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({type: 'setTimeMin', value: parseFloat(0)})
                                               }
                                           }}
                                    />
                                </div>
                                <div className="col-6 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Tiempo medio en mas</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={5}
                                           min={0}
                                           step={0.01}
                                           value={params.timeMax}
                                           onChange={() => {
                                               if (event.target.value >= 0) {
                                                   updateParams({
                                                       type: 'setTimeMax',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({type: 'setTimeMax', value: parseFloat(0)})
                                               }
                                           }}/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-6 d-flex flex-column">
                    <div className="card">
                        <div className="card-body">
                            <div className="row d-flex justify-content-center textoTitulo">
                                Parametros trabajos C
                            </div>
                            <div className="row d-flex justify-content-center">
                                <div className="col-6 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Minutos despues de inicio</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={15}
                                           min={0}
                                           step={0.01}
                                           value={params.timeInitTC}
                                           onChange={() => {
                                               if (event.target.value > 0) {
                                                   console.log(errors.minInitTC)
                                                   updateParams({
                                                       type: 'setTimeInitTC',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({
                                                       type: 'setTimeInitTC',
                                                       value: parseFloat(0)
                                                   })
                                               }
                                           }}/>
                                </div>
                                <div className="col-6 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Minutos antes del final</label>
                                    <input type="number" className="form-control textoBasico" defaultValue={15} min={1}
                                           onChange={() => {
                                               updateParams({
                                                   type: 'setTimeEndTC',
                                                   value: parseFloat(event.target.value)
                                               })
                                           }}/>
                                </div>
                            </div>
                            {errors.minInitFinTC &&
                            <span className="errores">Error, la suma de los minutos despues de inicio y antes de final debe ser menor a el minimo de tiempo C</span>}
                        </div>
                    </div>
                </div>
            </div>
            <div className="row d-flex justify-content-center mt-2">
                <div className="col-12 d-flex justify-content-center">
                    <div className="card">
                        <div className="card-body">
                            <div className="row d-flex justify-content-center">
                                <div className="col-12 text-center textoTitulo">
                                    Parametros tecnicos de la simulacion
                                </div>
                            </div>
                            <div className="row d-flex justify-content-center">
                                <div className="col-4 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Cantidad de tiempo</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={100}
                                           min={1}
                                           step={0.01}
                                           value={params.cantTimeSim}
                                           onChange={() => {
                                               if (event.target.value >= 1) {
                                                   updateParams({
                                                       type: 'setCantTimeSim',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({
                                                       type: 'setCantTimeSim',
                                                       value: parseFloat(1)
                                                   })
                                               }
                                           }}/>
                                </div>
                                <div className="col-4 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Hora inicio</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={50}
                                           min={0}
                                           step={0.01}
                                           value={params.initTimeView}
                                           onChange={() => {
                                               if (event.target.value > 0) {
                                                   updateParams({
                                                       type: 'setInitTimeView',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({
                                                       type: 'setInitTimeView',
                                                       value: parseFloat(0)
                                                   })
                                               }
                                           }}/>
                                </div>
                                <div className="col-4 d-flex flex-column text-center">
                                    <label className="textoSubTitulo">Cantidad iteraciones</label>
                                    <input type="number"
                                           className="form-control textoBasico"
                                           defaultValue={50}
                                           min={0}
                                           step={1}
                                           value={params.cantSimIterations}
                                           onChange={() => {
                                               if (event.target.value >= 1) {
                                                   updateParams({
                                                       type: 'setCantSimIterations',
                                                       value: parseFloat(event.target.value)
                                                   })
                                               } else {
                                                   updateParams({
                                                       type: 'setCantSimIterations',
                                                       value: parseFloat(0)
                                                   })
                                               }
                                           }}/>
                                </div>
                            </div>
                            {errors.horaInicioMax &&
                                <span className="errores">
                                    Error, la hora de inicio no puede ser mayor o igual a la cantidad de tiempo
                                </span>}
                        </div>
                    </div>
                </div>
            </div>

            <div className="row d-flex justify-content-center mt-2">
                <div className="col-2">
                    <button type="button" className="btn btn-success" onClick={sendData}>Simular</button>
                </div>
            </div>
        </div>
    );
};

export default SimulationParameters;