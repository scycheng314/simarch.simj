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

import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simj.data.SimjTime;

import it.uniroma2.sel.simlab.simj.engines.LocalProcessEngine;
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;
import it.uniroma2.sel.simlab.simj.entities.SimjEntity;
import it.uniroma2.sel.simlab.simj.exceptions.EventScheduledInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;
import it.uniroma2.sel.simlab.simj.exceptions.UnknownEventRecipientException;

/** Specializes Process Interaction events for local environments
 *
 *  @author     Daniele Gianni
 *  @version    2.0 06-01-06
 */
public class PLocalEvent extends PEvent {
    
    /**
     * Creates a new instance of PLocalEvent
     */
    public PLocalEvent() {
        super();
    }

    /**
     * Creates a new instance of PLocalEvent
     *
     * @param s source entity
     * @param r recipient entity
     * @param t simulation time
     * @param e event tag
     * @param o data attached to the event
     */
    public PLocalEvent(final LocalEntity s, final LocalEntity r, final SimjTime t, final Enum e, final Object o) {
        super(s, r, t, e, o);
    }

    /**
     * Creates a new instance of PLocalEvent
     *
     * @param s source entity
     * @param r recipient entity
     * @param t simulation time
     * @param e event tag
     * @param o data attached to the event
     */
    public PLocalEvent(final SimjEntity s, final LocalEntity r, final SimjTime t, final Enum e, final Object o) {
        super(s, r, t, e, o);
    }

    /**
     * Getter method for the recipient entity property
     *
     * @return the event recipient
     */
    public LocalEntity getRecipient() {
        return (LocalEntity) super.getRecipient();
    }

    /**
     * Getter method for the recipient entity name
     *
     * @return the recipient name
     */
    public Name getRecipientName() {
        return getRecipient().getEntityName();
    }

    /**
     * Getter method for the sender entity
     *
     * @return the sender entity
     */
    public LocalEntity getSender() {
        return (LocalEntity) super.getSender();
    }

    /**
     * Getter method for the sender entity name
     *
     * @return the sender name
     */
    public Name getSenderName() {
        return getSender().getEntityName();
    }
    
    /** Perform the action correspoding to Process Interaction Local2Local events. Basically it delivers the event 
     *  to the reciepient letting it go on with its execution
     */
    public void process() throws EventScheduledInPastTimeException, UnknownEventRecipientException, SimjException {                        
        LocalProcessEngine lpe = getEngine();
                
        assert (getTime().isGreaterOrEqualThan(lpe.getClock())) : "Event sent to past time";
        
        LocalEntity recipient = getRecipient();
        if (recipient == null) throw new UnknownEventRecipientException("LocalEvent.process(): null recipient");
        else
            if (recipient.equals(null)) throw new UnknownEventRecipientException("LocalEvent.process(): null recipient");
            else {
                if ((recipient.isWaitingState()) || recipient.isHoldingWhileWaitingState()) {
                    recipient.setReceivedEvent(this);
                    recipient.setRunnable();
                } else {                                        
                    assert false : "The event recipient " + recipient.getFullName() + " was not expecting any events";
                    
                    System.err.println("The event recipient was not expecting any events");                    
                }
            }
        getEngine().setClock(getTime());
    }

    /**
     * Setter method for the recipient entity property
     *
     * @param e the recipient
     */
    public void setRecipient(final LocalEntity e) {
        super.setRecipient(e);
    }

    /**
     * Setter method for the sender entity property
     *
     * @param e the sender
     */
    public void setSender(final LocalEntity e) {
        super.setSender(e);
    }

    /**
     * Getter method for the engine property
     *
     * @return the local engine
     */
    public static LocalProcessEngine getEngine() {
        return (LocalProcessEngine) PEvent.getEngine();        
    }
    
    public String toString() {
        return getSenderName() + " " + getRecipientName() + " " + getTag() + " " + super.toString();
    }
}
