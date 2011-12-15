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
import it.uniroma2.sel.simlab.simj.entities.States;

/** 
 * Represents the wake up event for {@code LocalProcessEngine} and for {@code LocalEntity} in 
 * {@code DistributedProcessEngine}
 *
 * @author  Daniele Gianni
 * @version July 2005
 * @see     LocalProcessEngine
 * @see     LocalEntity
 */
public class WakeUpEvent extends PLocalEvent {
          
    /** Creates a new instance of WakeUpEvent */
    public WakeUpEvent(final LocalEntity e, final SimjTime t) {
        super(e, e, t, null, null);
        
        setSender(e);
        setRecipient(e);        
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
        //????
        getEngine().setClock(getTime());        
        
        assert (!getSender().getState().equals(States.HOLDING)) : "Entity " + getSender().getFullName() + " was not holding";
        
        getSender().setRunnable();
    }
}
