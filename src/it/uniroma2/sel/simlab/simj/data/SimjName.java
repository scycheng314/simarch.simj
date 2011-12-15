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

/** Provides a layer 2 internal representation for Name interface
 *
 *  @author  Daniele Gianni
 *  @version 1.0 06-01-06
 */
public class SimjName extends Name {
    
    public static final String TOKEN = ".";           
    
    /** Creates a new instance of Name */
    public SimjName(final Name n) throws InvalidNameException {
        super(n.getValue());
    }
    
    public SimjName(final String s) throws InvalidNameException {
        super(s);
    }
        
    public static SimjName buildFrom(final Name n) throws InvalidNameException {
        return new SimjName(n);
    }
    
    public static SimjName buildFrom(final String s) throws InvalidNameException {
        return new SimjName(s);
    }
    
    /** Sets the value*/
    public void setValue(final String s) throws InvalidNameException {        
        if (s.indexOf(TOKEN) >= 0) throw new InvalidNameError("String '" + TOKEN + "' is not allowed in Simj names");
        
        value = new String(s);
    }           
}
