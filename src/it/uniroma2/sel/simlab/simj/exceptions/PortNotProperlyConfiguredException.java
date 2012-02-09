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

package it.uniroma2.sel.simlab.simj.exceptions;

/** Detects that a port is not configured - and it can be floating, ie not
 * attached to any link
 *
 * @author Daniele Gianni
 * @version 1.1 06-01-06
 */
public class PortNotProperlyConfiguredException extends SimjException {
    
    /** Creates a new instance of PortNotProperlyConfiguredException */
    public PortNotProperlyConfiguredException() {
    }

    /** Creates a new instance of PortNotProperlyConfiguredException
     *
     * @param e encapsulated exception for further details
     */
    public PortNotProperlyConfiguredException(final Exception e) {
        super(e);
    }

    /** Creates a new instance of PortNotProperlyConfiguredException
     *
     * @param s message associated to the exception
     */
    public PortNotProperlyConfiguredException(final String s) {
        super(s);
    }
}
