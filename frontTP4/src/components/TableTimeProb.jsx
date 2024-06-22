import React, {useContext} from 'react';
import {ContextoSimulacion} from "../contexts/ContextoSimulacion.jsx";
import "../styles/Estilos.css"

const TableTimeProb = () => {
    const {updateParams} = useContext(ContextoSimulacion);
    return (
        <table className="table table-bordered ">
            <thead className="textoSubTitulo text-center ">
            <tr >
                <th scope="col">Trabajo</th>
                <th scope="col">A</th>
                <th scope="col">B</th>
                <th scope="col">C</th>
                <th scope="col">D</th>
            </tr>
            </thead>
            <tbody className="textoBasico text-center align-text-bottom ">
            <tr className="tr">
                <td>Probabilidad</td>
                <td >
                    <input type="number" className="form-control textoBasico " defaultValue={0.3} step={0.01} max={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setProbTA', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td >
                    <input type="number" className="form-control textoBasico" defaultValue={0.25} step={0.01} max={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setProbTB', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td>
                    <input type="number" className="form-control textoBasico" defaultValue={0.25} step={0.01} max={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setProbTC', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td>
                    <input type="number" className="form-control textoBasico" defaultValue={0.2} step={0.01} max={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setProbTD', value: parseFloat(event.target.value)});
                           }}/>
                </td>
            </tr>
            <tr>
                <td>Tiempo</td>
                <td>
                    <input type="number" className="form-control textoBasico" defaultValue={2} step={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setTimeTA', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td>
                    <input type="number" className="form-control textoBasico" defaultValue={1} step={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setTimeTB', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td>
                    <input type="number" className="form-control textoBasico " defaultValue={3} step={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setTimeTC', value: parseFloat(event.target.value)});
                           }}/>
                </td>
                <td>
                    <input type="number" className="form-control textoBasico" defaultValue={1} step={1} min={0}
                           onChange={() => {
                               updateParams({type: 'setTimeTD', value: parseFloat(event.target.value)});
                           }}/>
                </td>
            </tr>
            </tbody>
        </table>
    );
};

export default TableTimeProb;