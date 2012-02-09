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

import it.uniroma2.sel.simlab.generalLibrary.dataStructures.SortedList;
import it.uniroma2.sel.simlab.generalLibrary.dataStructures.UnableToInsertException;

import it.uniroma2.sel.simlab.simj.data.SimjTime;
import it.uniroma2.sel.simlab.simj.errors.SimulationReproducibilityViolatedError;

import it.uniroma2.sel.simlab.simj.events.SimjEvent;
import it.uniroma2.sel.simlab.simj.exceptions.AttemptingToScheduleAnEventInPastTimeException;

import it.uniroma2.sel.simlab.simj.exceptions.SimjException;


/** Defines an abstract engine for discrete event simulations (DES)
 *
 * @author  Daniele Gianni
 * @version 1.0 07-01-05
 */
public abstract class Engine {
    
    private final static int EVENT_LIST_INITIAL_SIZE = 100;
    private final static double INITIAL_TIME = 0.0;

    // internal engine clock
    private SimjTime clock;

    //list of the events
    private SortedList eventsList;    
           
    /** Creates a new instance of Engine */
    public Engine() {
        setEventsList(new SortedList(EVENT_LIST_INITIAL_SIZE));        
        setClock(new SimjTime(INITIAL_TIME));
    }        

    /**
     * Retrieves and removes the next event in list
     *
     * @return the next event to be processed
     */
    protected SimjEvent getNextEvent() {
        assert (eventsList.size() > 0): "Trying to get an event from an empty event list";
        return (SimjEvent) eventsList.removeFirst();
    }    

    /**
     * Gets the current number of events
     *
     * @return the size of the event list
     */
    public int getNumberOfEvents() {
        return this.getEventsList().size();
    }

    /**
     * Inserts a {@link it.uniroma2.sel.simlab.simj.events.SimjEvent} event in the event list
     * according to the event time
     *
     * @param e the event
     * @throws SimjException thrown if {@code e}'s time is lesser than {@link #clock}
     */
    public void schedule(SimjEvent e) throws SimjException { //throws AttemptingToSendEventToPastTimeException {        
        assert (e != null) : "Scheduling null event";                       

        try {
            
            if (e.getTime().compareTo(getClock()) < 0) {
                throw new AttemptingToScheduleAnEventInPastTimeException("Event time error: Attempting to schedule an event in the past. Event Time : " + e.getTime().getValue() + "  Current time : " + getClock().getValue());             
            }            
            eventsList.add(e);

        } catch (UnableToInsertException ex) {            
            throw new SimulationReproducibilityViolatedError(ex.toString());
        }
    }
    
    /** Starts the simulation run */
    public abstract void start() throws SimjException;         

    /** Stops the simulation run */
    public abstract void stop() throws SimjException;
    
    // Properties access methods     
    
    /** Gets the clock.
     *
     * @return the clock.
     */
    public SimjTime getClock() {
        return clock;
    }

    /**
     * Retrieves the first event in the event list, without removing it
     *
     * @return the event
     */
    protected SimjEvent seeNextEvent() {
        return (SimjEvent) eventsList.seeFirst();        
    }

    /** Sets the engine clock
     *
     * @param t the new time for the clock
     */
    public void setClock(final SimjTime t) {
        clock = t;
    }   
    
    /** Gets the eventsList
     *
     * @return the eventsList.
     */
    protected SortedList getEventsList() {
        return eventsList;
    }
    
    /** Sets the eventsList.
     *
     * @param l the eventList
     */
    private void setEventsList(final SortedList l) {
        eventsList = l;
    }    
}


