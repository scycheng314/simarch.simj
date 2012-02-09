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

package it.uniroma2.sel.simlab.simj.data;

import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.errors.InvalidNameError;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;

/** Provides a SimJ internal representation for Layer 2 Name interface
 *
 *  @author  Daniele Gianni
 *  @version 1.0 06-01-06
 */
public class SimjName extends Name {
    
    public static final String TOKEN = ".";           
    
    /** Creates a new instance of SimJName from another implementation of the {@code Name} interface
     *
     * @param n the {@code Name} object
     * @throws InvalidNameException
     */
    public SimjName(final Name n) throws InvalidNameException {
        super(n.getValue());
    }

    /** Creates a new instance of SimJName from a {@code String} object
     *
     * @param s the {@code String} object
     * @throws InvalidNameException
     */
    public SimjName(final String s) throws InvalidNameException {
        super(s);
    }

    /**
     * Is a static constructor of {@code SimJName} objects from other {@code Name} implementations
     *
     * @param n the object to be converted in {@code SimJName}
     * @return the {@code SimJName} object
     * @throws InvalidNameException
     */
    public static SimjName buildFrom(final Name n) throws InvalidNameException {
        return new SimjName(n);
    }

    /**
     * Is a static constructor of {@code SimJName} objects from {@code String}
     *
     * @param s the String containing the names, and to be converted in {@code SimJName}
     * @return the {@code SimJName} object
     * @throws InvalidNameException
     */
    public static SimjName buildFrom(final String s) throws InvalidNameException {
        return new SimjName(s);
    }
    
    /** Sets the name value to the specified {@code String} object
     *
     * @param s the {@code String} object containing the name. The string must not contain the {@link #TOKEN} character
     * @throws InvalidNameException thrown if {@code s} contains the {@link #TOKEN} character
     */
    public void setValue(final String s) throws InvalidNameException {        
        if (s.indexOf(TOKEN) >= 0) throw new InvalidNameError("String '" + TOKEN + "' is not allowed in Simj names");
        
        value = new String(s);
    }           
}
