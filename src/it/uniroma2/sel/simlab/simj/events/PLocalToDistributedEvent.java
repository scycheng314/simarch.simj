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

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.data.RemoteEntity;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;

import it.uniroma2.sel.simlab.simj.data.SimjTime;

import it.uniroma2.sel.simlab.simj.engines.DistributedProcessEngine;

import it.uniroma2.sel.simlab.simj.entities.SimjEntity;
import it.uniroma2.sel.simlab.simj.entities.SimjRemoteEntity;
import it.uniroma2.sel.simlab.simj.exceptions.EventScheduledInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;
import it.uniroma2.sel.simlab.simj.exceptions.SimjInternalException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownEventRecipientException;

/** Specializes the Process Interaction event for events to be sent from the local system to a remote system.
 *  It is just a commodity to present the overall architecture in a conform way.
 *  
 *  @author     Daniele Gianni
 *  @version    1.0 06-01-06
 */
public class PLocalToDistributedEvent extends PEvent implements Event {

    // local stub for the remote recipient
    private RemoteEntity dest;
    
    /** 
     * Creates a new instance of PLocalToDistributedEvent
     *
     * @param s sender entity
     * @param r recipient entity
     * @param t simulation time
     * @param e event tag
     * @param o data attached to the event
     */
    public PLocalToDistributedEvent(final SimjEntity s, RemoteEntity r, final SimjTime t, final Enum e, final Object o) {
        super(s, null, t, e, o);
        
        dest = r;
    } 
    
    /** 
     * Creates a new instance of PLocalToDistributedEvent
     *
     * @param s sender entity
     * @param r recipient entity
     * @param t simulation time
     * @param e event tag
     * @param o data attached to the event
     */
    public PLocalToDistributedEvent(final SimjEntity s, RemoteEntity r, final Time t, final Enum e, final Object o) {
        super(s, null, SimjTime.buildFrom(t), e, o);
        
        dest = r;
    } 

    /**
     * Getter method for the engine property
     *
     * @return reference to the engine
     */
    public static DistributedProcessEngine getEngine() {
        return (DistributedProcessEngine )PEvent.getEngine();
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public SimjRemoteEntity getRecipient() {
        try {
            return new SimjRemoteEntity(dest);   
        } catch (InvalidNameException ex) {
            ex.printStackTrace();           
        }   
        return null;
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Name getRecipientName() {
        //System.out.println("Remote Recipient Full Name : " + dest.getFullName().getValue());
        return dest.getFullName();
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public Name getSenderName() {
        return getSender().getFullName();
    }
    
    /** Is supposed to process the event. However, being an event to a remote system,
     * it can not be processed locally.
     *
     * @throws EventScheduledInPastTimeException
     * @throws UnknownEventRecipientException
     * @throws SimjException
     */
    public void process() throws EventScheduledInPastTimeException, UnknownEventRecipientException, SimjException {
        throw new SimjInternalException("Unexpected execution of method process in PLocalToDistributedEvent");
    }
}
