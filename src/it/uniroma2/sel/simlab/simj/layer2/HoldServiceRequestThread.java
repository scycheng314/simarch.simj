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

package it.uniroma2.sel.simlab.simj.layer2;

import it.uniroma2.sel.simlab.simarch.data.GeneralEntity;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simj.engines.ProcessEngine;

/** ##### This class is currently not used ####
 *
 * @author Daniele Gianni
 */
public final class HoldServiceRequestThread extends ServiceRequestThread {
    
    private Time time;
    /** Creates a new instance of HoldServiceRequestThread */
    public HoldServiceRequestThread(final GeneralEntity en, final Time t, ProcessEngine e) {
        super(e, en);
        
        setTime(t);
    }
    
    private void setTime(final Time t) {
        time = t;
        //Thread.
    }
    
    public void run() {
        //engine.
    }
}
