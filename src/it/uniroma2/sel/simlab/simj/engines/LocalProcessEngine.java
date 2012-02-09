/*
 * 	Copyright (C) 2005-2011 Department of Enteprise Engineering, University of Rome "Tor Vergata"
 *                              ( http://www.dii.uniroma2.it )
 *
 *      This file is part of SimArch and was developed at the Software Engineering Laboratory
 *      ( http://www.sel.uniroma2.it )
 *
 *      SimArch is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      SimArch is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with SimArch.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.uniroma2.sel.simlab.simj.engines;

import it.uniroma2.sel.simlab.simarch.data.GeneralEntity;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simj.data.SimjName;
import it.uniroma2.sel.simlab.simj.data.SimjTime;

import it.uniroma2.sel.simlab.simj.entities.LocalEntity;
import it.uniroma2.sel.simlab.simj.entities.SimjEntity;
import it.uniroma2.sel.simlab.simj.errors.SimjError;
import it.uniroma2.sel.simlab.simj.events.PLocalEvent;
import it.uniroma2.sel.simlab.simj.events.SimjEvent;
import it.uniroma2.sel.simlab.simj.events.SimulationEndEvent;
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownLocalEntityException;

/** Specializes the Process Engine for local environments
 *
 * @author  Daniele Gianni
 * @version 1.0 07-01-05
 */
public class LocalProcessEngine extends ProcessEngine {

    // the conventional name used to refer to local SimJ engines
    private /*final*/ SimjName LOCAL_SYSTEM_NAME;

    // stats variable to collect performance data -- currently not used
    private double totalInternalSchedulingTime = 0.0;
    private int numberOfInternalScheduling = 0;

    /** Creates a new instance of LocalProcessEngine */
    protected LocalProcessEngine() {
        try {
            LOCAL_SYSTEM_NAME = SimjName.buildFrom("local");

            PLocalEvent.setEngine(this);
            LocalEntity.setEngine(this);
        } catch (InvalidNameException ex) {
            ex.printStackTrace();
            throw new SimjError(ex);
        }
    }

    /**
     * Created a new instance of the LocalProcessEngine, setting the simulation end
     * at the time value specified by {@code simulationEnd}
     *
     * @param simulationEnd the end time for the local simulation execution
     */
    public LocalProcessEngine(final Time simulationEnd) {
        try {
            LOCAL_SYSTEM_NAME = SimjName.buildFrom("local");

            PLocalEvent.setEngine(this);
            LocalEntity.setEngine(this);

            schedule(new SimulationEndEvent(SimjTime.buildFrom(simulationEnd)));
        } catch (InvalidNameException ex) {
            ex.printStackTrace();
            throw new SimjError(ex);
        } catch (SimjException ex) {
            ex.printStackTrace();
            throw new SimjError(ex);
        }
    }

    /**
     * Gets the local engine name
     *
     * @return {@link #LOCAL_SYSTEM_NAME}
     */
    public Name getSystemName() {
        return LOCAL_SYSTEM_NAME;
    }

    /**
     * Gets the memory reference to a {@code LocalEntity} from an abstract {@code GeneralEntity}
     * description
     *
     * @param e the {@code GeneralEntity}
     * @return the entity reference to the SimJ entity list
     * @throws UnknownLocalEntityException thrown if the {@code GeneralEntity} cannot be found in the engine
     * @throws InvalidNameException thrown if the {@code GeneralEntity}'s name does not conform to the naming convention
     */
    public SimjEntity getEntity(final GeneralEntity e) throws UnknownLocalEntityException, InvalidNameException {
        if (e.isLocal()) {
            return getEntityFromId(e.getEntityId());
        } else {
            throw new UnknownLocalEntityException(e.getFullName().toString());
        }
    }

    /**
     * Schedules a local event from {@code src} to {@code dest}, at time {@code clock}
     * + {@code delay}, associating to the event the {@code tag} and the {@code data}
     *
     * @param src the sending entity
     * @param dest the recipient entity
     * @param delay the time delay with respect to the engine's clock
     * @param tag the event descriptor
     * @param data the attached data to be associated to the event
     * @throws SimjException
     */
    public void schedule(final SimjEntity src, final SimjEntity dest, final Time delay, final Enum tag, final Object data) throws SimjException {
        //Date d = new Date();       
        schedule(new PLocalEvent((LocalEntity) src, (LocalEntity) dest, getClock().increasedBy(delay), tag, data));
        /*
        Date d1 = new Date();
        
        double partialInternalSchedulingTime = d1.getTime() - d.getTime();
        totalInternalSchedulingTime += partialInternalSchedulingTime;
        numberOfInternalScheduling++;
        System.out.println("Internal Scheduling Number " + numberOfInternalScheduling + "  Time " + totalInternalSchedulingTime + "   Partial " + partialInternalSchedulingTime);
         */
    }

    /**
     * Schedules a local event from entity named {@code src} to entity named {@code dest}, at time {@code clock}
     * + {@code delay}, associating to the event the {@code tag} and the {@code data}
     *
     * @param src the name of the sender entity
     * @param dest the name of the recipient entity
     * @param delay the delay with respect to the engine's clock
     * @param tag the event descriptor
     * @param data the data to be attached to the event
     * @throws SimjException
     */
    public void schedule(final Name src, final Name dest, final Time delay, final Enum tag, final Object data) throws SimjException {
        schedule(new PLocalEvent(getEntity(src), getEntity(dest), getClock().increasedBy(delay), tag, data));

        //Date d = new Date();
        /*
        Date d1 = new Date();
        
        double partialInternalSchedulingTime = d1.getTime() - d.getTime();
        totalInternalSchedulingTime += partialInternalSchedulingTime;
        numberOfInternalScheduling++;
        System.out.println("Internal Scheduling Number " + numberOfInternalScheduling + "  Time " + totalInternalSchedulingTime + "   Partial " + partialInternalSchedulingTime);
         */
    }

    /**
     * Activates the engine, by starting the execution all the registered entities
     *
     * @throws SimjException
     */
    public void start() throws SimjException {
        setRunning(true);

        System.out.println("Simj LocalProcessEngine: Starting entities");
        LocalEntity e;

        startAllEntities();

        System.out.println("Simj LocalProcessEngine: Ready to simulate");
        System.out.println("Simj LocalProcessEngine: Simulation processing cycle");

        int timePrinter = 0;

        while (isRunning()) {

            timePrinter++;

            // activate all the runnable entities
            for (int i = 0; i < getNumberOfEntities(); i++) {
                e = getEntity(i);

                if (e.isRunnable()) {                    
                    e.restart();
                    getOneEntityIn().get();
                }
            }

            // Please note: Engine dependent statements!!!
            if (getEventsList().size() > 0) {
                // event list not empty --> retrieve and process next event
                SimjEvent se = getNextEvent();
                //if ((timePrinter % 100) == 0) System.out.println("Time " + se.getTime().getValue());
                se.process();
            } else {
                //empty event list --> no more future events

                // Please note that this branch must not be done in a DIS environment!!!
                // The following statements are valid only for Local Simulators!!!
                setRunning(false);
                System.out.println("Simj LocalProcessEngine: No more future events");
                System.out.println("Simulation time " + getClock().getValue());
            }
        }
        stop();
        printStatistics();
        System.out.println("Simj LocalProcessEngine : Simulation completed");
    }

    /**
     * Activates the entity in position {@code i} in the engine's entity list
     *
     * @param i the entity ordinal
     */
    protected void startEntity(int i) {
        assert ((i >= 0) && (i < getNumberOfEntities())) : "Attempting to start an unexisting entity";

        ((Thread) getEntity(i)).start();

        // locks the semaphore regulating the execution of one entity per time
        getOneEntityIn().get();
    }

    /**
     * Interrupts the execution of all the entities
     */
    public void stop() {
        setRunning(false);
        stopAllEntities();
    }

    /**
     * Interrupts the execution of the entity in position {@code i} in the engine's entity list
     *
     * @param i the entity ordinal
     */
    protected void stopEntity(int i) {
        assert ((i >= 0) && (i < getNumberOfEntities())) : "Attempting to stop an unexisting entity";

        ((Thread) getEntity(i)).stop();
    }
}
