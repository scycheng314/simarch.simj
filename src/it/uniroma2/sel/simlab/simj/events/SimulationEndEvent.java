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
import it.uniroma2.sel.simlab.simj.exceptions.SimjException;

/**
 * Represents the Simulation End Event for LocalProcessEngine
 *
 * @author  Daniele Gianni
 * @version 1.1 06-01-06
 * @see     LocalProcessEngine
 * @see     SimjLocalEvent
 */
public final class SimulationEndEvent extends SimjEvent {
        
    /** Creates a new instance of SimulationEndEvent
     *
     * @param t time
     */
    public SimulationEndEvent(final SimjTime t) {
        super();
        
        setTime(t);
    }
            
    /** {@inheritDoc}
     *  <p>
     *  For this event the action to be performed is to stop the simulation engine    
     *  </p>
     */
    public void process() throws SimjException {   
        assert (getTime().isGreaterOrEqualThan(getEngine().getClock())) : "Simulation end event scheduled in past time";
        
        getEngine().setClock(getTime());
        
        getEngine().stop();
        
        //System.out.println("Numero eventi ancora in lista == " + getEngine().getNumberOfEvents());                                
    }

    public int compareTo(Object o) {
                
        SimjEvent e = (SimjEvent) o;
        
        int comparisonResult = getTime().compareTo(e.getTime());
        
        if (comparisonResult == 0) {
            return 1;
        } else return comparisonResult;
    }
}
