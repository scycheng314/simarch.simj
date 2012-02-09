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

/* Defines the SimJ local simulation entity
 *
 * @author  Daniele Gianni
 * @version 1.1 06-01-06
 */
public class LocalEntity extends Thread implements Layer3ToLayer2, SimjEntity {//Cloneable,  {

    // reference to SimJ local engine
    private static LocalProcessEngine engine;

    // the basic entity that implements the Layer2ToLayer3 interface
    protected ComponentLevelEntity entity;

    // The entity's current state
    private States state;

    // Used by the LocalProcessEngine to schedule the entity
    private Semaphore restart;   

    // internal attribute to synchronize the conditional hold
    private int ordinal = 0;

    // flag indicating whether an even has been received
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
 
        getEngine().add(this);        
    }        
     
    //////////////////////////////////////////
    //
    //  Layer3ToLayer2 service implementation
    //
    //////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
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
        } while (!isEventReceived()); // "wait" until an event is received
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Time getClock() {
        return getEngine().getClock();
    }

    /**
     * {@inheritDoc }
     *
     * @param t {@inheritDoc }
     */
    public void hold(final Time t) {
        setEntityState(States.HOLDING);
        getEngine().hold(this, t);
        pause();        
    }

    /**
     * {@inheritDoc }
     *
     * @param t {@inheritDoc }
     * @return {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     * @param dest {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws UnknownRecipientException {@inheritDoc }
     * @throws TimeAlreadyPassedException {@inheritDoc }
     * @throws Layer2InternalException {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     *
     * @param o {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws TimeAlreadyPassedException {@inheritDoc }
     * @throws UnlinkedPortException {@inheritDoc }
     * @throws Layer2InternalException {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     *
     * @param dest {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws UnknownRecipientException {@inheritDoc }
     * @throws TimeAlreadyPassedException {@inheritDoc }
     * @throws Layer2InternalException {@inheritDoc }
     * @throws InvalidNameException {@inheritDoc }
     */
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
     * Insert this entity reference into the simulation system
     *
     * @throws Layer2InternalException {@inheritDoc }
     * @throws UnableToRegisterEntityException {@inheritDoc }
     */
    public void registerEntity() throws Layer2InternalException, UnableToRegisterEntityException {
        try {
            getEngine().add(this);
        } catch (UnableToAddEntityException ex) {
            throw new UnableToRegisterEntityException(ex);
        }
    }

    /**
     * Starts the engine execution
     *
     * @throws UnableToStartEngineException
     */
    public void startEngine() throws UnableToStartEngineException {
        try {
            engine.start();
        } catch (SimjException ex) {
            System.err.println("Ex msg : " + ex.getMessage());
            throw new UnableToStartEngineException();
        }
    }        

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public boolean isLocal() {
        return true;
    }

    ////

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Layer3ToLayer2UserInterface getUserInterface() {
        return this;
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Layer3ToLayer2DeveloperInterface getDeveloperInterface() {
        return this;
    }
    
    
    //////////////////////////////////
    //
    // LocalEntity "own" methods
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
    
    /** 
     * Pauses the entity and notifies it to the engine
     */
    protected void pause() {
        engine.recEntityPauses();
        restart.get();
    }    
    
    ///////////////////////////////
    //
    //  Engine interaction methods
    //
    ///////////////////////////////

    /**
     * Prints on std.out the statistics collected by the entity specialized class
     */
    public void printStatistics() {
        entity.printStatistics();
    }
    
    /** 
     * Reactivates the entity upon the delivery of an event
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
     * 
     * @return referenc to the engine
     */
    public static LocalProcessEngine getEngine() {
        return engine;
    }
    
    /** Setter for property engine.
     *
     * @param e reference to the engine
     */
    public static void setEngine(final LocalProcessEngine e) {
        engine = e;
    }
    
    // non-static
    
    /** Getter for property id.
     * 
     * @return the entity unique id
     */
    public Integer getEntityId() {
        return entity.getId();
    }   
    
    /** Getter for property name.
     * 
     * @return the entity name
     */
    public Name getEntityName() {
        return entity.getEntityName();
    }            
    
    /** Getter for property state.
     * 
     * @return the entity state
     */
    public States getEntityState() {
        return state;
    }

    /**
     * Getter method for the property full name, which consists of system name + {@code TOKEN} + entity name
     *
     * @return the entity full name, including the system name
     */
    public Name getFullName() {
        try {                                      
            return SimjFullName.buildFrom(engine.getSystemName(), getEntityName());
        } catch (InvalidNameException ex) {
            throw new Layer2Error("Inconsistent system name");
        }
    }

    /**
     * Getter method for the property ordinal, used to implement the conditional hold service
     *
     * @return the ordinal value
     */
    public int getOrdinal() {
        return ordinal;        
    }
    
    /** Getter for property restart.
     *
     * @return Value of property restart.
     */
    public Semaphore getRestart() {
        return restart;
    }

    /** Getter method for the system name property
     *
     * @return the system name
     */
    public Name getSystemName() {
        return ((ProcessEngine) getEngine()).getSystemName();
    }    

    /**
     * Getter flag method for the respective entity state
     *
     * @return  {@code true} if the entity is in {@link States.HOLDING_WHILE_WAITING}, {@code false} otherwise
     */
    public boolean isHoldingWhileWaitingState() {
        return state.equals(States.HOLDING_WHILE_WAITING);
    }

    /**
     * Getter flag method for the respective entity state
     *
     * @return  {@code true} if the entity is in {@link States.RUNNABLE}, {@code false} otherwise
     */
    public boolean isRunnable() {
        return state.equals(States.RUNNABLE);
    }    

    /**
     * Getter flag method for the respective entity state
     *
     * @return  {@code true} if the entity is in {@link States.WAITING}, {@code false} otherwise
     */
    public boolean isWaitingState() {
        return state.equals(States.WAITING);
    }

    /**
     * Setter method for the implementation of the Layer2toLayer3 service interface
     *
     * @param e the entity implementing the service interface
     */
    protected void setEntity(final ComponentLevelEntity e) {
        entity = e;
    }

    /**
     * This method is deprecated and should generate a false assertion if/when invoked
     * To be removed in next versions.
     *
     */
    protected void setEntityName(final SimjName n) {
        assert false;
       // entityName = n;
    }  
    
    /**
     * This method is deprecated and should generate a false assertion if/when invoked
     * To be removed in next versions.
     *
     */
    protected void setEntityName(final String s) {
        assert false;
      //  entityName = new Name(s);
    }            

    /**
     * Set the {@code eventReceived} flag
     *
     * @param b the value to set
     */
    protected void setEventReceived(final boolean b) {
        if (b) entity.setEventReceived();
        //else entity.
    }                    
    
    /** Setter for property state.
     * 
     * @param state New value of property state - to be choosen from {@link States}
     */
    protected void setEntityState(final States s) {
        state = s;
    }
    
    /** Setter for property restart.
     *
     * @param restart New value of property restart.
     */
    public void setRestart(final Semaphore restart) {
        this.restart = restart;
    }                            
    
    /** Setter for property receivedEvent.
     *
     * @param receivedEvent New value of property receivedEvent.
     */
    public void setReceivedEvent(final PEvent receivedEvent) {
        /*
         this.receivedEvent = receivedEvent;
        setEventReceived(true);*/
        
        setEventReceived();                
        entity.setReceivedEvent(receivedEvent);
        
        assert !(state.equals(States.WAITING)): "Entity " + getFullName() + " was not waiting for an event";
    }         

    /**
     * Getter method for the {@code eventReceived} flag
     *
     * @return the flag value
     */
    public boolean isEventReceived() {
        return eventReceived;
    }    

    /**
     * Setter method for the {@code eventReceived} flag
     */
    public void setEventReceived() {
        eventReceived = true;       
        entity.setEventReceived();
    }

    /**
     * Setter method for the {@code eventReceived} flag, to false
     */
    public void unSetEventReceived() {        
        eventReceived = false;       
    }
    
    /** Setter for property id
     *
     * @param id New value of property id.
     */
    public void setEntityId(final Integer id) {
       entity.setId(id);
        
        assert (id >= 0) : "Entity Id not valid";
    }           

    /**
     * Sets the entity state to {@link States.RUNNABLE}
     */
    public void setRunnable() {
        assert !(state.equals(States.RUNNABLE)): "Entity " + getFullName() + " already running";
        
        setEntityState(States.RUNNABLE);
    }                 
    
    // Object overriden/implemented methods
    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public String toString() {
        return entity.getFullName().toString();
    }  

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not yet supported");
        /*
        Entity copy = (Entity)super.clone();
        copy.setName(new String(getName()));
        copy.setReceivedEvent(null);
        return copy;*/
        //return null;
    }

    /**
     * Produces a local stub for this entity, as it was a remote one
     *
     * @return the entity stub
     */
    public RemoteEntity getAsRemoteEntity() {
        try {
            return new SimjRemoteEntity(getSystemName(), getEntityName());
        } catch (InvalidNameException ex) {
            Logger.getLogger(LocalEntity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
