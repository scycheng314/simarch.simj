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

package it.uniroma2.sel.simlab.simj.entities;

import it.uniroma2.sel.simlab.simarch.data.RemoteEntity;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.errors.Layer2Error;
import it.uniroma2.sel.simlab.simj.data.SimjFullName;
import it.uniroma2.sel.simlab.simj.data.SimjName;
import it.uniroma2.sel.simlab.simj.engines.LocalProcessEngine;
import it.uniroma2.sel.simlab.simj.engines.ProcessEngine;
import it.uniroma2.sel.simlab.simj.errors.SimjError;

import it.uniroma2.sel.simlab.simj.exceptions.SimjException;

import it.uniroma2.sel.simlab.generalLibrary.concurrentProgramming.Semaphore;
import it.uniroma2.sel.simlab.simarch.data.ComponentLevelEntity;
import it.uniroma2.sel.simlab.simarch.data.GeneralEntity;

import it.uniroma2.sel.simlab.simarch.data.OutputPort;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.Layer2InternalException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnableToRegisterEntityException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnableToStartEngineException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnknownRecipientException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer3.Layer3Exception;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2DeveloperInterface;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2UserInterface;
import it.uniroma2.sel.simlab.simj.data.SimjTime;
import it.uniroma2.sel.simlab.simj.events.PEvent;
import it.uniroma2.sel.simlab.simj.exceptions.AttemptingToScheduleAnEventInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.PortNotProperlyConfiguredException;
import it.uniroma2.sel.simlab.simj.exceptions.UnableToAddEntityException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownLocalEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Defines SimJ local simulation entities
 *
 */
public class LocalEntity extends Thread implements Layer3ToLayer2, SimjEntity {//Cloneable,  {

    // reference to SimJ local engine
    private static LocalProcessEngine engine;

    // the basic entity that implements the Layer2ToLayer3 interface
    protected ComponentLevelEntity entity;
        
    private States state;       // The entity's current state
    private Semaphore restart;   // Used by the LocalProcessEngine to schedule the entity   
    
    private int ordinal = 0;
    
//    private PLocalEvent receivedEvent;
    private boolean eventReceived;    
                   
    /**
     * Creates a new {@code LocalEntity}.
     *
     * @param name the name of this entity
     */
    public LocalEntity(final ComponentLevelEntity entity) throws UnableToAddEntityException {       
        
        setEntity(entity);        
        setEntityState(States.RUNNABLE);        
        setRestart(new Semaphore(0));        
 
        //???????????????????
        getEngine().add(this);        
    }        
     
    //////////////////////////////////////////
    //
    //  Layer3ToLayer2 service implementation
    //
    //////////////////////////////////////////
    public void waitNextEvent() { //final ComponentLevelEntity e)        
        if (!engine.isRunning()) {
            throw new SimjError("LocalEntity " + getFullName() + " waiting for next event while engine not running");
        }
        
        setEntityState(States.WAITING);
        unSetEventReceived();
        
        do {
            pause();
            if (!engine.isRunning()) {
                throw new SimjError("LocalEntity " + getFullName() + " waiting for next event while engine not running");
            }
        } while (!isEventReceived());                                
    }
    
    public Time getClock() {
        return getEngine().getClock();
    }
    
    public void hold(final Time t) {
        setEntityState(States.HOLDING);
        getEngine().hold(this, t);
        pause();        
    }
    
    public boolean holdUnlessIncomingEvent(final Time t) {        
        ordinal++;
        
        Time targetTime = t.increasedBy(getClock());
        
        setEntityState(States.HOLDING_WHILE_WAITING);
                
        getEngine().holdWhileWait(this, t, ordinal);         
        pause();
        
        //System.out.println(" Time targetTime == " + targetTime.getValue() + "  Current Time : " + getEngine().getClock().getValue());
        return (getEngine().getClock().compareTo(targetTime) < 0);
    }
    
        //Send events functions
    
    public void send(final GeneralEntity dest, final Time delay, final Enum tag, final Object data) throws UnknownRecipientException, TimeAlreadyPassedException, Layer2InternalException {                                
        if (!getEngine().isRunning()) {
            throw new Layer2InternalException("Simulation engine is not running");            
        }
        try {                        
            SimjEntity simjDest;
            simjDest = engine.getEntity(dest);
          
            getEngine().schedule(this, simjDest, SimjTime.buildFrom(delay), tag, data);
        } catch (UnknownLocalEntityException ex) {
            throw new UnknownRecipientException(ex);
        } catch (AttemptingToScheduleAnEventInPastTimeException exT) {
            throw new TimeAlreadyPassedException(exT);
        } catch (SimjException exS) {
            throw new Layer2InternalException(exS);
        } catch (InvalidNameException ex) {
            throw new UnknownRecipientException(ex);
        }
    }
               
    public void send(final OutputPort o, final Time delay, final Enum tag, final Object data) throws TimeAlreadyPassedException, UnlinkedPortException, Layer2InternalException {
        try {
            getEngine().schedule(this, o, delay, tag, data);
        } catch (AttemptingToScheduleAnEventInPastTimeException exT) {
            throw new TimeAlreadyPassedException(exT);
        } catch (PortNotProperlyConfiguredException exP) {
            throw new UnlinkedPortException(exP);
        } catch (SimjException ex) {
            throw new Layer2InternalException(ex);                
        }
    }
    
    public void send(final Name dest, final Time delay, final Enum tag, final Object data) throws UnknownRecipientException, TimeAlreadyPassedException, Layer2InternalException, InvalidNameException {                
        try {
            getEngine().schedule(getEntityName(), SimjName.buildFrom(dest), delay, tag, data);
        } catch (UnknownLocalEntityException ex) {
            throw new UnknownRecipientException(ex);
        } catch (AttemptingToScheduleAnEventInPastTimeException exT) {
            throw new TimeAlreadyPassedException(exT);
        } catch (SimjException exS) {
            throw new Layer2InternalException(exS);
        }
    }
    
    /**
     * Includes entity {@code e} into the simulation system
     * 
     * 
     * @param e the entity to be included
     * @see GeneralEntity
     */
    
    public void registerEntity() throws Layer2InternalException, UnableToRegisterEntityException {
        try {
            getEngine().add(this);
        } catch (UnableToAddEntityException ex) {
            throw new UnableToRegisterEntityException(ex);
        }
    }
        
    public void startEngine() throws UnableToStartEngineException {
        try {
            engine.start();
        } catch (SimjException ex) {
            System.err.println("Ex msg : " + ex.getMessage());
            throw new UnableToStartEngineException();
        }
    }        
    
    public boolean isLocal() {
        return true;
    }

    ////
    
    public Layer3ToLayer2UserInterface getUserInterface() {
        return this;
    }
    
    public Layer3ToLayer2DeveloperInterface getDeveloperInterface() {
        return this;
    }
    
    
    //////////////////////////////////
    //
    // LocalEntity proper methods
    //
    //////////////////////////////////

    /**
     * Runs the entity's thread
     */
    public final void run() {
        try {
            pause();        
            entity.body();
            setEntityState(States.FINISHED);
        
            getEngine().recEntityPauses();            
        } catch(Layer3Exception e) {
            throw new SimjError(e);
        }
    }                 
    
    /** Pauses the entity and notifies it to the engine */
    protected void pause() {
        engine.recEntityPauses();
        restart.get();
    }    
    
    ///////////////////////////////
    //
    //  Engine interaction methods
    //
    ///////////////////////////////
     
    public void printStatistics() {
        entity.printStatistics();
    }
    
    /** Reactivates the entity upon a delivered event
     */
    public void restart() {
        restart.release();
    }
    
    ///////////////////////////////
    //
    //  Getter and setter methods
    //
    ///////////////////////////////   
    
    // static
    
    /** Getter for property engine.
     * @return Value of property engine.
     *
     */
    public static LocalProcessEngine getEngine() {
        return engine;
    }
    
    /** Setter for property engine.
     * @param engine New value of property engine.
     *
     */
    public static void setEngine(final LocalProcessEngine e) {
        engine = e;
    }
    
    // non-static
    
    /** Getter for property id.
     * @return Value of property id.
     *
     */
    public Integer getEntityId() {
        return entity.getId();
    }   
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public Name getEntityName() {
        return entity.getEntityName();
    }            
    
    /** Getter for property state.
     * @return Value of property state.
     *
     */
    public States getEntityState() {
        return state;
    }
        
    public Name getFullName() {
        try {                                      
            return SimjFullName.buildFrom(engine.getSystemName(), getEntityName());
        } catch (InvalidNameException ex) {
            throw new Layer2Error("Inconsistent system name");
        }
    }
        
    public int getOrdinal() {
        return ordinal;        
    }
    
    /** Getter for property restart.
     * @return Value of property restart.
     *
     */
    public Semaphore getRestart() {
        return restart;
    }
    /** */
    public Name getSystemName() {
        return ((ProcessEngine) getEngine()).getSystemName();
    }    
    
    public boolean isHoldingWhileWaitingState() {
        return state.equals(States.HOLDING_WHILE_WAITING);
    }
            
    public boolean isRunnable() {
        return state.equals(States.RUNNABLE);
    }    
    
    public boolean isWaitingState() {
        return state.equals(States.WAITING);
    }
    
    protected void setEntity(final ComponentLevelEntity e) {
        entity = e;
    }
    
    protected void setEntityName(final SimjName n) {
        assert false;
       // entityName = n;
    }  
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    protected void setEntityName(final String s) {
        assert false;
      //  entityName = new Name(s);
    }            
    
    protected void setEventReceived(final boolean b) {
        if (b) entity.setEventReceived();
        //else entity.
    }                    
    
    /** Setter for property state.
     * @param state New value of property state.
     *
     */
    protected void setEntityState(final States s) {
        state = s;
    }
    
    /** Setter for property restart.
     * @param restart New value of property restart.
     *
     */
    public void setRestart(final Semaphore restart) {
        this.restart = restart;
    }                            
    
    /** Setter for property receivedEvent.
     * @param receivedEvent New value of property receivedEvent.
     *
     */
    public void setReceivedEvent(final PEvent receivedEvent) {
        /*
         this.receivedEvent = receivedEvent;
        setEventReceived(true);*/
        
        setEventReceived();                
        entity.setReceivedEvent(receivedEvent);
        
        assert !(state.equals(States.WAITING)): "Entity " + getFullName() + " was not waiting for an event";
    }         
    
    public boolean isEventReceived() {
        return eventReceived;
    }    
    
    public void setEventReceived() {
        eventReceived = true;       
        entity.setEventReceived();
    }
    
    public void unSetEventReceived() {        
        eventReceived = false;
        /////////// entity.unSetEventReceived(); ?????????????????
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     */
    public void setEntityId(final Integer id) {
       entity.setId(id);
        
        assert (id >= 0) : "Entity Id not valid";
    }           
    
    public void setRunnable() {
        assert !(state.equals(States.RUNNABLE)): "Entity " + getFullName() + " already running";
        
        setEntityState(States.RUNNABLE);
    }                 
    
    // Object overriden/implemented methods
    
    public String toString() {
        return entity.getFullName().toString();
    }  
  
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not yet supported");
        /*
        Entity copy = (Entity)super.clone();
        copy.setName(new String(getName()));
        copy.setReceivedEvent(null);
        return copy;*/
        //return null;
    }

    public RemoteEntity getAsRemoteEntity() {
        try {
            return new SimjRemoteEntity(getSystemName(), getEntityName());
        } catch (InvalidNameException ex) {
            Logger.getLogger(LocalEntity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
