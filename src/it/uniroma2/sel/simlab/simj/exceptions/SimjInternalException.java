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

/** Identifies exceptions that are raised by the internal SimJ code. It distinguishes
 * these exceptions from those that can be raised by SimArch services
 *
 * @author Daniele Gianni
 * @version 1.1 06-01-06
 */
public class SimjInternalException extends SimjException {
    
    /** Creates a new instance of SimjInternalException */
    public SimjInternalException() {
    }

    /**
     * Creates a new instance of SimjInternalException
     *
     * @param e encapsulated exception
     */
    public SimjInternalException(final Exception e) {
        super(e);
    }

    /**
     * Creates a new instance of SimjInternalException
     *
     * @param s the message
     */
    public SimjInternalException(final String s) {
        super(s);
    }
}
