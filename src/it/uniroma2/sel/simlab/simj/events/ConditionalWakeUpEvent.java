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
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;

/** Defines the service (i.e. associated to the implementation of SimJ services)
 * event that realizes the hold service (i.e. conditional wake up of an entity
 * if an event is received before the specified time)
 *
 * @author Daniele Gianni
 */
public class ConditionalWakeUpEvent extends WakeUpEvent {

    // internal identifier of the entity invoking the hold service
    private int ordinal;
    //private static int c = 0;
    //private static int d = 0;
    
    /** Creates a new instance of ConditionalWakeUpEvent */
    public ConditionalWakeUpEvent(final LocalEntity e, final SimjTime t, final int ordinal) {
        super(e, t);
        
        setOrdinal(ordinal);
    }
    
    protected void setOrdinal(final int i) {
        ordinal = i;
    }
        
    public int compareTo(Object o) {
                
        int comparisonResult = super.compareTo(o);
        
        if (comparisonResult == 0) {
            return 1;
        } else return comparisonResult;
    }
    
    /** {@inheritDoc}
     *  <p>
     *  For this particular event, the action is to wake up the {@code} LocalEntity which scheduled it by
     *  {@link LocalEntity#hold()}.
     *  </p>
     */
    public void process() {           
        getEngine().setClock(getTime());        
        
        //assert (!getSender().getState().equals(States.HOLDING)) : "Entity " + getSender().getFullName() + " was not holding";        
        
        //System.out.println("Processo un conditional waiting " + getSender().getOrdinal() + " " + ordinal + " " + getSender().isHoldingWhileWaitingState() + " " + getTime().getValue());
        
        if ((getSender().getOrdinal() == ordinal) && getSender().isHoldingWhileWaitingState()) {
            //c++;
            //System.out.println("Risveglio entity! Conditional Event Successful! " + c + " SU " + ordinal);
            getSender().setRunnable();
        } else {
            //d++;
            //System.out.println("Altro evento prima!!! " + d);
            //System.out.flush();
        }
    }
}
