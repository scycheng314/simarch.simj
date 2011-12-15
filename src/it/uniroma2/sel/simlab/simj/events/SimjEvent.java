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

import it.uniroma2.sel.simlab.simj.data.SimjTime;

import it.uniroma2.sel.simlab.simj.engines.Engine;
import it.uniroma2.sel.simlab.simj.exceptions.EventScheduledInPastTimeException;
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;


/** Represents the abstract (base) Simj events
 * 
 *  @author     Daniele Gianni
 *  @version    07-31-05
 */
public abstract class SimjEvent implements Cloneable, Comparable {

    // reference to the engine within which the event is scheduled
    private static Engine engine;

    // element characterizing the type of event
    private Enum tag;

    // data conveyed by the event
    private Object data;

    // event time
    private SimjTime time; 
                
    /** Creates a new instance of this class */
    public SimjEvent() {
        setTime(null);
        setTag(null);
        setData(null);
    }
    
    /** Creates a new instance of this class 
     *  
     *  @param  time the time the event occurs
     *  @param  tag the tag associated to the event
     *  @param  edata the data attached to the event 
     */
    public SimjEvent(final SimjTime time, final Enum tag, final Object edata) {       
        setTime(time);
        setTag(tag);
        setData(edata);
    }

    /** Sets the engine
     *
     *  @param  e the engine   
     */
    public static void setEngine(final Engine e) {
        engine = e;
    }
    
    /** Gets the engine
     *
     *  @return the current engine
     */
    protected static Engine getEngine() {
        return engine;
    }
    
    
    public int compareTo(final Object o) {
        SimjEvent e = (SimjEvent) o;
        
        // Exception should be thrown if compareTo is 0 (simulation reproducibility is violated)
        int compareResult = getTime().compareTo(e.getTime());                            
                
        return compareResult;
    }   
        
    public void copy(final SimjEvent e) {
        setTime(e.getTime());
        setData(e.getData());
        setTag(e.getTag());
    }        

    /** Performs the action related to this event 
     *  
     *  @throws SimjException
     */
    public abstract void process() throws EventScheduledInPastTimeException, SimjException;                

    // accessor methods
    /** Gets the event data*/
    public Object getData() {
        return data;
    }

    /** Gets the event tag*/
    public Enum getTag() {
        return tag;
    }

    /** Gets the event time*/
    public SimjTime getTime() {
        return time;
    }
    
    /** Sets the event data*/
    public void setData(final Object data) {
        this.data = data;
    }    
    
    /** Sets the event data*/
    public void setTag(final Enum tag) {
        this.tag = tag;
    }
       
    /** Sets the event data*/
    public void setTime(final SimjTime time) {
        this.time = time;
    }            
    
    public String toString() {
        return Double.toString(getTime().getValue()); // + getTag().toString();
    }
}
