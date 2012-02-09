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

package it.uniroma2.sel.simlab.simj.events;

import it.uniroma2.sel.simlab.simarch.data.DistributedEvents;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Name;

import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simj.data.SimjTime;

import it.uniroma2.sel.simlab.simj.engines.DistributedProcessEngine;
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;
import it.uniroma2.sel.simlab.simj.entities.SimjRemoteEntity;

import it.uniroma2.sel.simlab.simj.exceptions.SimjException;

/** Specializes the Process Event for distributed simulations. This event is for distributed events with 
 *  a local recipient.
 *
 *  @author Daniele Gianni
 *  @version 1.0 06-01-06
 */
public class PDistributedToLocalEvent extends PEvent {
    
    //private RemoteEntity sender;

    /**
     * Creates a new instance of PDistributedToLocalEvent
     */
    private PDistributedToLocalEvent() {
        super();
    }
    
    /**
     * Creates a new instance of PDistributedToLocalEvent
     */
    public PDistributedToLocalEvent(SimjRemoteEntity s, LocalEntity r, final SimjTime t, final Enum e, final Object o) {
        super(s, r, t, e, o);        
    }
    
    /**
     * Builds {@code PDistributedToLocalEvent} from {@code e}. It is assumed that {@code e} is sent by a 
     *  remote entity and that {@code e}'s recipient is a local entity
     *
     * @param e event
     */
    public static PDistributedToLocalEvent buildFrom(final Event e) throws SimjException, InvalidNameException  {
        // assumed that event is sent by a remote entity
        assert (!e.getSender().getSystemName().equals(getEngine().getSystemName())) : "Inconsistent state in Distributed event. D Event has local sender";                
        
        PDistributedToLocalEvent simjEvent = new PDistributedToLocalEvent();        
        simjEvent.setSender(SimjRemoteEntity.buildFrom(e.getSender().getSystemName(), e.getSender().getEntityName()));       
        
        // and has a local recipient
        assert (e.getRecipient().getSystemName().equals(getEngine().getSystemName())) : "Inconsistent state in Distributed event. D Event delivered has no local recipients";
        
        /*
         System.out.println("Recipient === NULL? " + (e.getRecipient() == null));
            System.out.println("PDisToLocalEvent recipient name : " + e.getRecipient().getEntityName());
         */
        
        simjEvent.setRecipient(getEngine().getEntity(e.getRecipient().getEntityName()));
        
        simjEvent.setData(e.getData());
        simjEvent.setTag(e.getTag());
        simjEvent.setTime(SimjTime.buildFrom(e.getTime()));  
        
        return simjEvent;
    }

    /**
     * Getter method for the engine property
     *
     * @return the reference to the engine
     */
    public static DistributedProcessEngine getEngine() {
        return (DistributedProcessEngine) PEvent.getEngine();    
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public SimjRemoteEntity getSender() {
        return (SimjRemoteEntity) super.getSender();
    }    

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Name getSenderName() {
        return getSender().getEntityName();
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public LocalEntity getRecipient() {
        return (LocalEntity) super.getRecipient();
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Name getRecipientName() {
        return getRecipient().getEntityName();        
    }
    
    /** {@inheritDoc}
     * <p>
     * The action associated to this type of event is the same as the local events have
     * </p>
     */
    public void process() throws SimjException {        
                
        DistributedProcessEngine dpe = getEngine();
                
        if (getTime().isLesserThan(dpe.getClock())) {
            System.out.println("Event sent to past time!!!! ################################");
            //System.out.println("Ev time == " + getTime() + " Clock == " + lpe.getClock() + "  Sender == " + getSender().getEName() + " dest == " + getRecipient());
            //throw new EventInPastTimeException(getSender() + " " + getRecipient() + " " + getTime());
        } else {
            LocalEntity recipient = getRecipient();
            if (recipient == null) { //throw new UnknownEventRecipientException("LocalEvent.process(): null recipient");
                
            } else if (recipient.equals(null)) {
                //throw new UnknownEventRecipientException("LocalEvent.process(): null recipient");
            } else {
                if (recipient.isWaitingState()) {
                    recipient.setReceivedEvent(this);
                    recipient.setRunnable();
                } else {
                    assert false : "The event recipient " + recipient.getFullName() + " was not expecting any events";
                }
            }
        }
        getEngine().setClock(getTime());
                
        //System.out.println("TAG ::> " + getTag().toString() + "  " + DistributedEvents.SIM_END_TAG.toString());
        if (getTag().name().equals(DistributedEvents.SIM_END_TAG.name())) {
            getEngine().stop();
        }
    }

    /**
     * Setter method for the recipient entity property
     *
     * @param e the reference to the local entity
     */
    public void setRecipient(LocalEntity e) {
        super.setRecipient(e);
    }

    /**
     * Setter method for the sender entity propertu
     *
     * @param e the reference to the sender entity
     */
    public void setSender(SimjRemoteEntity e) {
        super.setSender(e);
    }
}
