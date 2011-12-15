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
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;

/** Provides a layer 2 internal representation for Name interface
 *
 *  @author  Daniele Gianni
 *  @version 1.0 06-01-06
 */
public class SimjFullName extends Name {
    
    public static final String TOKEN = ".";    
    
    private Name entity;
    private Name system;
    
    /** Creates a new instance of Name */
    public SimjFullName(final Name system, final Name entity) throws InvalidNameException {
        setSystem(system);
        setEntity(entity);
    }    
        
    public static SimjFullName buildFrom(final Name system, final Name entity) throws InvalidNameException {
        return new SimjFullName(system, entity);
    }
    
    public Name getEntity() {
        return entity;        
    }
    
    public Name getSystem() {
        return system;
    }
    
    public String getValue() {
        return system.getValue() + "." + entity.getValue();
    }
    
    protected void setEntity(final Name n) {
        entity = n;        
    }
    
    protected void setSystem(final Name n) {
        system = n;
    }
    
    /** Sets the value*/
    public void setValue(final String s) throws InvalidNameException {        
        String subnames[] = s.split(TOKEN);
        
        if (subnames.length > 2) {       
            throw new InvalidNameException();            
        } else {
            system = new SimjName(subnames[0]);
            entity = new SimjName(subnames[1]);
        }               
    }         
}
