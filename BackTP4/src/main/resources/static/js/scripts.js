document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById('simulate-button');

    button.addEventListener('click', function () {
        const requestData = {
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

        fetch('http://localhost:8080/api/simular', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(data => {
                const tableBody = document.querySelector("#simulacion-table tbody");
                tableBody.innerHTML = ''; // Limpiar la tabla antes de agregar nuevos datos

                data.forEach(item => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${item.evento}</td>
                    <td>${item.reloj}</td>
                    <td>${item.llegada.rndLlegada}</td>
                    <td>${item.llegada.tiempoEntreLlegada}</td>
                    <td>${item.llegada.tiempoHoraProximaLlegada}</td>
                    <td>${item.llegada.rndTipoTrabajo}</td>
                    <td>${item.llegada.trabajo}</td>
                    <td>${item.colaVector.colaComun}</td>
                    <td>${item.colaVector.colaTrabajoC}</td>
                    <td>${item.colaVector.trabajoCSegundoPlano}</td>
                    <td>${item.colaVector.lugaresLibres}</td>
                    <td>${item.contadorEquipo}</td>
                    <td>${item.horaCambioTrabajoC}</td>
                    <td>${item.horaReanudacionTrabajoC}</td>
                    <td>${item.finTrabajo.rndFinTrabajo}</td>
                    <td>${item.finTrabajo.mediaTiempoAtencion}</td>
                    <td>${item.finTrabajo.tiempoAtencion}</td>
                    <td>${item.finTrabajo.horafinTrabajo}</td>
                    <td>${item.servidor.estado}</td>
                    <td>${item.servidor.horaInicioOcupacion}</td>
                    <td>${item.servidor.horaFinOcupacion}</td>
                    <td>${item.servidor.tiempoOcupacionAcum}</td>
                    <td>${item.servidor.tiempoPermanenciaEquipoAcum}</td>
                `;
                    tableBody.appendChild(row);
                });
            })
            .catch(error => console.error('Error fetching data:', error));
    });
});