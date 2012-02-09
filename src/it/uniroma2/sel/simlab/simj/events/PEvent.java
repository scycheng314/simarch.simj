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
import it.uniroma2.sel.simlab.simj.data.SimjTime;
import it.uniroma2.sel.simlab.simj.entities.SimjEntity;

import it.uniroma2.sel.simlab.simj.engines.ProcessEngine;

/** Specializes the general SimjEvent for Process Interaction simulations
 *
 *  @author     Daniele Gianni
 *  @version    1.1 06-01-06
 */
public abstract class PEvent extends SimjEvent implements Event {

    private SimjEntity sender;
    private SimjEntity recipient;
            
    /**
     * Creates a new instance of PEvent
     */
    public PEvent() {
        super();
    }
              
    /**
     * Creates a new instance of PEvent 
     * 
     * 
     * @param s the sender entity
     * @param r the recipient entity
     * @param t the time
     * @param i the tag
     * @param o the data
     */
    public PEvent(final SimjEntity s, final SimjEntity r, final SimjTime t, final Enum e, final Object o) {
        super(t, e, o);
        
        setSender(s);
        setRecipient(r);
    }   
    
    /** Gets the {@link ProcessEngine} currently used*/
    public static ProcessEngine getEngine() {
        return (ProcessEngine) SimjEvent.getEngine();        
    }
    
    public int compareTo(final Object o) {
        
        int compareResult = super.compareTo(o);
        
        if (compareResult == 0) {      
            PEvent e = (PEvent) o;                        
            
            String tagString;
            String tagString1;
            
            String senderString;
            String senderString1;
            
            String recipientString;
            String recipientString1;
            
            if (getTag() == null) { 
                tagString = getClass().getName();
            } else {
                tagString = getTag().toString();
            }
            
            if (e.getTag() == null) { 
                tagString1 = e.getClass().getName();
            } else {
                tagString1 = e.getTag().toString();
            }            
            
            if (getSender() == null) {
                senderString = getClass().getName();
            } else {
                senderString = getSender().getFullName().getValue();
            }
                        
            if (e.getSender() == null) {
                senderString1 = e.getClass().getName();
            } else {
                senderString1 = e.getSender().getFullName().getValue();
            }
            
            if (getRecipient() == null) {
                recipientString = getClass().getName();
            } else {
                recipientString = getRecipient().getFullName().getValue();
            }
            
            if (e.getRecipient() == null) {
                recipientString1 = e.getClass().getName();
            } else {
                recipientString1 = e.getRecipient().getFullName().getValue();
            }
            
            //System.out.println("TagString : " + tagString + "    " + " TagString1 : " + tagString1);
            
            /*
            if (getSender() == null) System.out.println("Sender == NULL");
            if (getRecipient() == null) System.out.println("Recipient == NULL");
            */
            
            
            String event = senderString + "." + recipientString + "." + getTime() + "." + tagString; 
            String event1 = senderString1 + "." + recipientString1 + "." + e.getTime() + "." + tagString1;
            
            compareResult = event.compareTo(event1);  
            
            System.out.flush();
        }        
        
        return compareResult;
    }
         
    // accessor methods

    /**
     * Getter method for the recipient property
     *
     * @return the reference to the recipient property
     */
    public SimjEntity getRecipient() {
        return recipient;
    }

    /**
     * Getter method for the sender entity property
     *
     * @return the reference to the sender entity property
     */
    public SimjEntity getSender() {
        return sender;
    }

    /**
     * Setter method for the recipient entity property
     *
     * @param e the recipient entity
     */
    public void setRecipient(final SimjEntity e) {
        recipient = e;
    }

    /**
     * Setter method for the sender entity property
     *
     * @param e the sender entity
     */
    public void setSender(final SimjEntity e) {
        sender = e;
    }       
    
    public String toString() {
        //return getSender().getEntityName().getValue() + "." + getRecipient().getEntityName().getValue() + "." + super.toString();
        return getSender().getFullName().getValue() + "." + getRecipient().getFullName().getValue() + "." + super.toString();
    }
    
}
