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

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.GeneralEntity;
import it.uniroma2.sel.simlab.simarch.data.InputPort;
import it.uniroma2.sel.simlab.simarch.data.Link;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.data.RemoteEntity;
import it.uniroma2.sel.simlab.simarch.data.Time;

import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer1.DistributedSimulationInfrastructureInternalException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer1.GlobalSimulationTimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer1.UnknownDistributedSystemRecipientException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;

import it.uniroma2.sel.simlab.simarch.interfaces.Layer1ToLayer2;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer2ToLayer1;

import it.uniroma2.sel.simlab.simj.data.SimjTime;
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;

import it.uniroma2.sel.simlab.simj.entities.SimjEntity;

import it.uniroma2.sel.simlab.simj.events.PDistributedToLocalEvent;
import it.uniroma2.sel.simlab.simj.events.PEvent;
import it.uniroma2.sel.simlab.simj.events.PLocalToDistributedEvent;
import it.uniroma2.sel.simlab.simj.events.SimulationEndEvent;
import it.uniroma2.sel.simlab.simj.exceptions.AttemptingToScheduleAnEventInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.PortNotProperlyConfiguredException;

import it.uniroma2.sel.simlab.simj.exceptions.SimjException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownEntityException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownLocalEntityException;

import java.util.Date;
import java.util.List;

/** Defines the distributed simulation engine for the process interaction formalism
 *
 * @author  Daniele Dianni
 * @version 1.0 06-01-06
 */
public class DistributedProcessEngine extends LocalProcessEngine implements Layer1ToLayer2 {    
    
    // the distributed time
    private Time disClock;
    
    // the reference to the underlying layer
    private Layer2ToLayer1 ddes;
    
    // a flag stating whether the simulation end has been reached
    private boolean simulationEndsReceived;   
    
    // the flag stating whether the data in the receivedEvent buffer is new
    private boolean eventReceived;

    // the buffer for the incoming distributed events
    private PEvent receivedEvent;

    // HLA Performance statistics
    // the attributes are currently unused, but are temporarely left here
    // to support the implementation of sampling capabilities for the
    // simulation performance
    private double totalSendingTime = 0.0;
    private double nextMessageRequestAvailableTotalTime = 0.0;
    private int numberOfSendings = 0;
    private int numberOfNextMessageRequest = 0;
    private Date startStartUpPhase;
    private double eventsProcessingTime = 0;
    private double waitingTime = 0;
    
    private double totalInternalSchedulingTime = 0.0;
    private int numberOfInternalScheduling = 0;
      
    /** Builds an instance of the DistributedProcessEngine, providing it with a
     * reference to the underlying Layer 1 services
     *
     * @param ddesInterface the object implementing the Layer 1 service
     */
    public DistributedProcessEngine(final Layer2ToLayer1 ddesInterface) {           
        super();

        unSetSimulationEndsReceived();
        unSetEventReceived();

        // initialize also the engine references within the LocalEntities. This is needed
        // as each local entity (Layer 3) will use the Layer 2 services provided by this engine
        LocalEntity.setEngine(this);                

        setDdes(ddesInterface);                
    }    

    /**
     * Returns the entity instance from instances of the SimArch's GeneralEntity interface.
     * If the instance refers to a remote entity, a null value is returned
     *
     * @param e the general (local or distributed) entity
     *
     */
    public SimjEntity getEntity(final GeneralEntity e) throws UnknownLocalEntityException, InvalidNameException { 
        if (e.isLocal()) {
            return super.getEntity(e);
        } else {
            System.err.println("Unknown Entity");
            return null; //return new RemoteEntity(e);
        }
    }

    /**
     * Return the system name as the name of the underlying federate
     *
     * @return {@inheritDoc }
     */
    public Name getSystemName() {
        return ddes.getSystemName();
    }
    
    /**
     * {@inheritDoc }
     *
     * @param src {@inheritDoc }
     * @param link {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws SimjException {@inheritDoc }
     * @throws AttemptingToScheduleAnEventInPastTimeException {@inheritDoc }
     * @throws UnknownEntityException {@inheritDoc }
     * @throws PortNotProperlyConfiguredException {@inheritDoc }
     */
    public void schedule(final LocalEntity src, final Link link, final Time delay, final Enum tag, final Object data) throws SimjException, AttemptingToScheduleAnEventInPastTimeException, UnknownEntityException, PortNotProperlyConfiguredException {                
        List<? extends InputPort> inputPorts = link.getInputPorts();

        for (InputPort p : inputPorts) {
            if (p.getOwner().isLocal()) {
                schedule(src, getEntityFromId(p.getOwner().getEntityId()), delay, tag, data);
            } else {
                schedule(src, (RemoteEntity) p.getOwner(), delay, tag, data);
            }
        }
    }

    /**
     * {@inheritDoc }
     *
     * @param src {@inheritDoc }
     * @param dest {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws SimjException {@inheritDoc }
     */
    public void schedule(final SimjEntity src, final SimjEntity dest, final Time delay, final Enum tag, final Object data) throws SimjException {       
        //Date d = new Date();       
        
        if (dest.isLocal()) {
            super.schedule(src, dest, delay, tag, data);
        } else {
            System.err.println("Unexpected type of entity");
        }        
        /*
        Date d1 = new Date();
        
        double partialInternalSchedulingTime = d1.getTime() - d.getTime();
        totalInternalSchedulingTime += partialInternalSchedulingTime;
        numberOfInternalScheduling++;
        System.out.println("Internal Scheduling Number " + numberOfInternalScheduling + "  Time " + totalInternalSchedulingTime + "   Partial " + partialInternalSchedulingTime);
         */
    }   

    /**
     * {@inheritDoc }
     *
     * @param src {@inheritDoc }
     * @param dest {@inheritDoc }
     * @param delay {@inheritDoc }
     * @param tag {@inheritDoc }
     * @param data {@inheritDoc }
     * @throws SimjException {@inheritDoc }
     */
    public void schedule(final SimjEntity src, final RemoteEntity dest, final Time delay, final Enum tag, final Object data) throws SimjException {        
        try {
            //System.out.println("Sending out distributed event: DELAY : " + delay.getValue() + "  TIME " + getClock().increasedBy(delay).getValue() + "  CURRENT CLOCK " + getClock().getValue());
            ddes.sendEvent(new PLocalToDistributedEvent(src, dest, getClock().increasedBy(delay), tag, data));
        } catch (GlobalSimulationTimeAlreadyPassedException ex) {
            throw new AttemptingToScheduleAnEventInPastTimeException(ex.getMessage());
        } catch (UnknownDistributedSystemRecipientException exR) {
            System.err.println("Unknown local recipient");
        } catch (DistributedSimulationInfrastructureInternalException exD) {
            System.err.println("DIS internal exception");
        }
    }

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     * @throws TimeAlreadyPassedException {@inheritDoc }
     */
    public void scheduleEvent(final Event e) throws TimeAlreadyPassedException {
        try {
            setEventReceived();        
                        
            receivedEvent = PDistributedToLocalEvent.buildFrom(e);                  
            assert receivedEvent.getSender().getSystemName().equals(this.getSystemName()) : "Distributed Infrastructure Routing Error: Event delivered to wrong recipient";
            
            schedule(receivedEvent);
        } catch (SimjException ex) {
            throw new TimeAlreadyPassedException(ex.getMessage());
        } catch (InvalidNameException exN) {
            System.err.println(exN.getMessage());
        }
    }

    /**
     * {@inheritDoc }
     * @param t {@inheritDoc }
     */
    public void scheduleSimulationEnd(final Time t) {
        try {
            schedule(new SimulationEndEvent(SimjTime.buildFrom(t)));
        } catch (SimjException e) {
            System.err.println("Cannot schedule simulation end at Time " + t + " Error msg: " + e.getMessage());
        }
    }    
        
    // Simj Process Engine methods
    /**
     * Determines whether the entity names with s, is a local or remote entiy
     *
     * @param s the name of the entity
     * @return {@code true} if the entity is remote, {@code false} if it is local
     */
    public static boolean isRemoteEntity(final String s) {
        return (s.indexOf(".") > 0);
    }

    /** {@inherit}
     */    
    public void start() throws SimjException {        
        try {
            startStartUpPhase = new Date();
            
            System.out.println("DDES Infrastructure is starting up");
            ddes.initDistributedSimulationInfrastructure();            
            
            System.out.println("DDES Infrastructure has been set up");
            runSim();
            
            System.out.println("Simulation ended");
            ddes.postProcessDistributedSimulationInfrastructure();
            
            System.out.println("DDES Infrastructure post processing");
        } catch (DistributedSimulationInfrastructureInternalException ex) {
            throw new SimjException(ex);
        } catch (GlobalSimulationTimeAlreadyPassedException exT) {
            throw new SimjException(exT);
        }
    }

    /**
     * Executes the locally defined entities
     */
    protected void runSim() throws DistributedSimulationInfrastructureInternalException, GlobalSimulationTimeAlreadyPassedException, SimjException {        
        setRunning(true);

        System.out.println("Simj DistributedProcessEngine: Starting entities");
        LocalEntity e;

        // activates in order all entities
        startAllEntities();
        
        System.out.println("Simj DistributedProcessEngine: Ready to simulate");
                
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            
        }                       

        System.out.println("Simj DistributedProcessEngine: Simulation processing cycle");
        
        //Date endStartUpPhase = new Date();
        //double startUpPhase = endStartUpPhase.getTime() - startStartUpPhase.getTime();
        //System.out.println("Start up phase:  " + startUpPhase);
        
        // regulates that only one entity at time will be executing
        while (isRunning()) {
            int runningEntities = 0;
            
            for (int i = 0; i < getNumberOfEntities(); i++) {
                e = getEntity(i);
                if (e.isRunnable()) {
                    e.restart();
                    getOneEntityIn().get();
                }
            }
            
            // Please note: Engine dependent statements!!!
            if (getEventsList().size() > 0) {
                Time nextEventTime = seeNextEvent().getTime();                                
                ddes.waitNextDistributedEventBeforeTime(nextEventTime);                                
                //Date startProcessingCycle = new Date();
                getNextEvent().process();
                // Date endProcessingCycle = new Date();
                // eventsProcessingTime = eventsProcessingTime + endProcessingCycle.getTime() - startProcessingCycle.getTime();
            } else {
                ddes.waitNextDistributedEvent();
                // Date startProcessingCycle = new Date();
                getNextEvent().process();                
                // Date endProcessingCycle = new Date();
                // eventsProcessingTime = eventsProcessingTime + endProcessingCycle.getTime() - startProcessingCycle.getTime();
            }
        }
        
        stopAllEntities();        
        printStatistics();
        
        System.out.println("Simj HLAProcessEngine : Simulation completed");
    }        
    
    // Getter and setter methods

    /**
     * Setter method for the Layer 1 service interface
     *
     * @param i service interface implementation
     */
    protected void setDdes(final Layer2ToLayer1 i) {
        ddes = i;
    }

    /**
     * Setter method for the distributed simulation clock, within the SimJ distributed engine
     *
     * @param t the time
     */
    public void setDisClock(final SimjTime t) {
        setClock(t);
        disClock = t;
    }

    /**
     * Getter method for the distributed simulation clock stored in SimJ
     *
     * @return the time
     */
    public Time getDisClock() {
        return disClock;
    }           
    
    /**
     * Getter method for checking whether the simulation end event has been received or not
     *
     * @return {@code true} if received, {@code false} otherwise
     */       
    public boolean isSimulationEndsReceived() {
        return simulationEndsReceived;
    }

    /**
     * Setter method for accessing the {@link #eventReceived} flag
     */
    protected void setEventReceived() {
        eventReceived = true;
    }   

    /**
     * Setter method for accessing the {@link #eventReceived} flag
     */
    protected void unSetEventReceived() {
        eventReceived = false;
    }    
    
    /** Setter method for the {@link #simulationEndsReceived}
     * 
     */    
    public void setSimulationEndsReceived() {
        simulationEndsReceived = true;
    }

    /** Setter method for the {@link #simulationEndsReceived}
     *
     */
    public void unSetSimulationEndsReceived() {
        simulationEndsReceived = false;
    }   
}
