# wot-project-BESpringboot-SkorupskiNocco

## contenuti
1. [Descrizione del progetto](#descrizione-progetto)
2. [Architettura del sistema](#architettura)
3. [Link alle repository](#links)
4. [Descrizione del componente](#descrizione-componente)


### Descrizione del progetto <a name="descrizione-progetto"></a>

Il progetto ha come obiettivo quello di guidare un individo all'interno di un ospedale(adattabile a strutture differenti) al fine di raggiungere un determinato reparto/stanza mediante l'uso di beacon. 
L'utente, installando l'applicazione sul proprio dispositivo android avrà la possibilità di selezionare un reparto di destinazione ed essere guidato attraverso a delle indicazioni visualizzate sullo schermo.
Le applicazioni sono due, una per l'utente ed una per l'amministratore. 
L'amministratore avrà il compito di aggiungere e mappare inizialmente i beacon attraverso l'applicazione fornita, assegnando un reparto ed un insieme di stanze al singolo beacon, inoltre dovrà inizialmente posizionarsi all'ingresso dell'edificio con il dispositivo puntato verso l'interno per calibrare le coordinate attraverso un determinato pulsante via app, in quanto l'applicazione usa un magnetometro per capire il verso di percorrenza rispetto al singolo beacon.
Una volta completato questo processo basterà semplicemente installare l'applicazione Hospital Maps per poter usufluire del navigatore. 
L'applicazione offre la possibilità di selezionare un percorso adeguato per persone diversamente abili.


### Architettura del sistema <a name="architettura"></a>

I beacon comunicano con il FrontEnd inviando costantemenet i loro segnali BLE.
Entrambe le applicaioni comunicano con i beacon, ricevendo i loro segnali con una determinata potenza RSSI.
Le applicazioni amministratore e utente utilizzano un BackEnd in cloud deployato su AWS che mette a disposizione diverse API.
Infine il BE utilizza un database MongoDb, anch'esso deployato su AWS, entrambi in un unica istanza di EC2.


### Link delle repository <a name="links"></a>

- FrontEnd Utente: [Link](https://github.com/UniSalento-IDALab-IoTCourse-2022-2023/wot-project-2022-2023-AppAndroidFE-NoccoSkorupski)
- FrontEnd Amministratore: [Link](https://github.com/UniSalento-IDALab-IoTCourse-2022-2023/wot-project-AdminAppAndroidFE-NoccoSkorupski)
- BackEnd: [Link](https://github.com/UniSalento-IDALab-IoTCourse-2022-2023/wot-project-BESpringboot-SkorupskiNocco)


### Descrizione del Componente <a name="descrizione-componente"></a>
L'applicazione Admin Hospital Maps deve essere scaricata dall'amministratore, colui che effettua il montaggio dei beacon della struttura e sa come funziona il sistema, pertanto sa dove posizionarli esattamente e come regolare la loro potenza attraverso l'app terza del produttore degli stessi. 
Quest'app permette all'admin di:
1. Registrare un nuovo beacon: l'amministratore dovrà isolarsi dagli altri beacons mantenendosi lontano almeno 7 metri e posizionare vicino al dispositivo su cui sta utilizzando l'app solo il beacon che vuole registrare. Compilerà il form inserendo il piano, il reparto e la stanza a cui il beacon farà riferimento una volta posizioato. Se rappresenterà uno snodo ossia un incrocio e non delle specifiche stanze allora compilerà i campi reparto e stanza con 'none'.
2. Mappare un nuovo beacon: l'amministratore riceverà la lista di tutti i beacon mappabili e ne selezionerà uno, quindi di questo specificherà il suo vicino di destra, di sinistra, di fronte e retrostante, se non dovesse averne selezionerà 'none', ma se si dovrà specificarne la distanza.
3. Calibrare la bussola del sistema: l'amministratore si posizionerà in divrezione e verso d'entrata alla struttura e invierà la posizione iniziale, ossia l'azimut in gradi che rappresentano l'orientamento dello smartphone.

