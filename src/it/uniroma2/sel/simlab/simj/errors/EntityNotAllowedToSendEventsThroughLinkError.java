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

package it.uniroma2.sel.simlab.simj.errors;

/** Identifies a consistency issue due to an entity sending event on a link not
 * connected to any of the entity's ports
 *
 * @author  Daniele Gianni
 * @version 1.1 06-01-06
 */
public class EntityNotAllowedToSendEventsThroughLinkError extends ConfigurationError {
    
    /** Creates a new instance of EntityNotAllowedToSendEventsThroughThisPort */
    public EntityNotAllowedToSendEventsThroughLinkError() {
    }

    /**
     * {@inheritDoc }
     *
     * @param s {@inheritDoc }
     */
    public EntityNotAllowedToSendEventsThroughLinkError(final String s) {
        super(s);
    }    
}
