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

import it.uniroma2.sel.simlab.generalLibrary.concurrentProgramming.Semaphore;

import it.uniroma2.sel.simlab.simarch.data.InputPort;
import it.uniroma2.sel.simlab.simarch.data.Link;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.data.OutputPort;
import it.uniroma2.sel.simlab.simarch.data.Time;

import it.uniroma2.sel.simlab.simj.data.SimjName;
import it.uniroma2.sel.simlab.simj.data.SimjTime;
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;
import it.uniroma2.sel.simlab.simj.entities.SimjEntity;

import it.uniroma2.sel.simlab.simj.events.ConditionalWakeUpEvent;
import it.uniroma2.sel.simlab.simj.events.SimulationEndEvent;
import it.uniroma2.sel.simlab.simj.events.WakeUpEvent;
import it.uniroma2.sel.simlab.simj.exceptions.AttemptingToScheduleAnEventInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.PortNotProperlyConfiguredException;
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;
import it.uniroma2.sel.simlab.simj.exceptions.UnableToAddEntityException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownEntityException;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/** Specializes the abstract <code>Engine</code> for <i>Process Interaction</i> (PI) paradigm
 *
 * @author  Daniele Gianni
 * @version 1.1 06-01-06
 */
public abstract class ProcessEngine extends Engine {

    // entities running locally, organized in hashtable access
    private Hashtable entitiesH;

    // entities running locally, organized in vectorial access
    private Vector entitiesV;

    // the semaphore indicating that one entity is under execution
    private Semaphore oneEntityIn;

    // flag stating that the engine is running
    private boolean running;
    
    /** Creates a new instance of SimEngine */
    public ProcessEngine() {
        super();
        
        setOneEntityIn(new Semaphore(0));
        setRunning(false);
        
        setEntitiesH(new Hashtable());
        setEntitiesV(new Vector());
    }

    /**
     * Adds a {@code LocalEntity} into the engine execution context
     *
     * @param e the entity
     * @throws UnableToAddEntityException
     */
    public void add(final LocalEntity e) throws UnableToAddEntityException {
        assert (e != null) : "Trying to add a null entity";
        
        if (isRunning()) {
            throw new UnableToAddEntityException("Unable to add entity " + e.getFullName() + ". System is running");
        } else {
            e.setEntityId(new Integer(entitiesV.size()));            
            entitiesH.put(e.getEntityName().getValue(), e);
            entitiesV.add(e);
        }
    }

    /**
     * Retrieves the {@code LocalEntity} object reference from the entity id
     *
     * @param id the entity id
     * @return the {@code LocalEntity} identified by {@code id}
     */
    public LocalEntity getEntityFromId(final Integer id) {
        assert (id > 0) : "Invalid entity id: it must be non negative";
        return (LocalEntity) entitiesV.get(id);
    }

    /**
     * Retrieves the {@code LocalEntity} object reference from the entity name
     *
     * @param n the entity name
     * @return the {@code LocalEntity} identified by {@code n}
     */
    public LocalEntity getEntityFromName(final SimjName n) {
        return (LocalEntity)entitiesH.get(n);
    }

    /**
     * Gets the system name
     *
     * @return system name
     */
    public abstract Name getSystemName();

    /**
     * Suspends the execution of entity {@code e}, for a time {@code t}
     *
     * @param e entity to suspend
     * @param t suspension time
     */
    public void hold(final LocalEntity e, final Time t) {
        hold(e.getEntityId(), t);
    }

    /**
     * Suspends the execution of entity ({@code id}), for a time {@code t}
     *
     * @param id id of the entity to suspend
     * @param t suspension time
     */
    public void hold(final int i, final Time t) {
        try {                        
            WakeUpEvent e = new WakeUpEvent(getEntity(i), getClock().increasedBy(t));            
            schedule(e);            
        } catch (SimjException e) {
            assert false : "Unexpected exception caught in hold(int, Time)";
        }
    }

    /**
     * Suspen
     * @param e
     * @param t
     * @param ordinal
     */
    public void holdWhileWait(final LocalEntity e, final Time t, final int ordinal) {
        holdWhileWait(e.getEntityId(), t, ordinal);
    }
    
    public void holdWhileWait(final int i, final Time t, final int ordinal) {
        try {
            ConditionalWakeUpEvent e = new ConditionalWakeUpEvent(getEntity(i), getClock().increasedBy(t), ordinal);
            schedule(e);
        } catch (SimjException e) {
            assert false : "Unexpected exception caught in hold(int, Time)";
        }
    }
         
    public void recEntityPauses() {
        oneEntityIn.release();
    }
    
    public abstract void schedule(final SimjEntity src, final SimjEntity dest, final Time delay, final Enum tag, final Object data) throws SimjException, AttemptingToScheduleAnEventInPastTimeException, UnknownEntityException;
    
    public void schedule(final LocalEntity src, final OutputPort out, final Time delay, final Enum tag, final Object data) throws SimjException, AttemptingToScheduleAnEventInPastTimeException, UnknownEntityException, PortNotProperlyConfiguredException {
        schedule(src, out.getLink(), delay, tag, data);
    }
    
    public void schedule(final LocalEntity src, final Link link, final Time delay, final Enum tag, final Object data) throws SimjException, AttemptingToScheduleAnEventInPastTimeException, UnknownEntityException, PortNotProperlyConfiguredException {                
        List<? extends InputPort> inputPorts = link.getInputPorts();
        
        for (InputPort p : inputPorts) {           
            schedule(src, getEntityFromId(p.getOwner().getEntityId()), delay, tag, data);
        }
    }
    
    public void setSimulationEnd(final Time d) throws SimjException {
        schedule(new SimulationEndEvent(SimjTime.buildFrom(d)));
    }
    
    public abstract void start() throws SimjException;
    
    protected void startAllEntities() throws SimjException {
        for (int i = 0; i < entitiesV.size(); i++) {
            startEntity(i);
        }
    }
    
    protected abstract void startEntity(final int i) throws SimjException;    
    
    protected void stopAllEntities() {
        for (int i = 0; i < entitiesV.size(); i++) {
            stopEntity(i);
        }
    }
    
    protected abstract void stopEntity(final int i);
    
    /*
     * Properties getter and setter methods
     */
    public boolean getRunning() {
        return isRunning();
    }
    
    public boolean isRunning() {
        return running;
    }
    
    protected void setRunning(final boolean b) {
        running = b;
    }
    
    protected Semaphore getOneEntityIn() {
        return oneEntityIn;
    }
    
    public LocalEntity getEntity(final Name n) {
        return (LocalEntity) entitiesH.get(n.getValue());
    }
    
    public LocalEntity getEntity(final int i) {
        return (LocalEntity) entitiesV.elementAt(i);
    }
    
    public LocalEntity getEntity(final Integer i) {
        return (LocalEntity) entitiesV.elementAt(i.intValue());
    }
    
    public int getNumberOfEntities() {
        return entitiesV.size();
    }
    
    protected void incClock(Time t) {
        getClock().increaseBy(t);
    }
    
    protected void printStatistics() {
        for (int i = 0; i < entitiesV.size(); i++) {
            System.out.println("\n");
            ((LocalEntity) getEntity(i)).printStatistics();
            System.out.println("\n");
        }
    }
    
    protected void setEntitiesH(Hashtable h) {
        entitiesH = h;
    }
    
    protected void setEntitiesV(Vector v) {
        entitiesV = v;
    }
    
    protected void setOneEntityIn(Semaphore s) {
        oneEntityIn = s;
    }
}
