import {createContext, useReducer, useState} from "react";

export const ContextoSimulacion = createContext(null)

const initialState = {
    probTA: 0.3,
    probTB: 0.25,
    probTC: 0.25,
    probTD: 0.2,
    timeTA: 2,
    timeTB: 1,
    timeTC: 3,
    timeTD: 1,
    timeMin: 5,
    timeMax: 5,
    timeInitTC: 15,
    timeEndTC: 15,
    cantTimeSim: 100,
    initTimeView: 50,
    cantSimIterations: 50
};


function reducer(params, action) {
    switch (action.type) {
        case 'setProbTA':
            return {...params, probTA: action.value};
        case 'setProbTB':
            return {...params, probTB: action.value};
        case 'setProbTC':
            return {...params, probTC: action.value};
        case 'setProbTD':
            return {...params, probTD: action.value};
        case 'setTimeTA':
            return {...params, timeTA: action.value};
        case 'setTimeTB':
            return {...params, timeTB: action.value};
        case 'setTimeTC':
            return {...params, timeTC: action.value};
        case 'setTimeTD':
            return {...params, timeTD: action.value};
        case 'setTimeMin':
            return {...params, timeMin: action.value};
        case 'setTimeMax':
            return {...params, timeMax: action.value};
        case 'setTimeInitTC':
            return {...params, timeInitTC: action.value};
        case 'setTimeEndTC':
            return {...params, timeEndTC: action.value};
        case 'setCantTimeSim':
            return {...params, cantTimeSim: action.value};
        case 'setInitTimeView':
            return {...params, initTimeView: action.value};
        case 'setCantSimIterations':
            return {...params, cantSimIterations: action.value};
        default:
            throw new Error();
    }
}

export const ContextoSimulacionHandler = ({children}) => {
    const [params, updateParams] = useReducer(reducer, initialState);
    return (
        <ContextoSimulacion.Provider value={{params, updateParams}}>
            {children}
        </ContextoSimulacion.Provider>
    )
}