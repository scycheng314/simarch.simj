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

/** Provides a SimJ internal representation for Layer 2 Name interface
 *
 *  @author  Daniele Gianni
 *  @version 1.0 06-01-06
 */
public class SimjFullName extends Name {

    /*
     * the separator character between local and distribute parts of a SimArch name
     */
    public static final String TOKEN = ".";    


    private Name entity;
    private Name system;
    
    /** Creates a new instance of Name
     *
     * @param system the name of the systems hosting the entity
     * @param entity the entity name
     * @throws InvalidNameException
     */
    public SimjFullName(final Name system, final Name entity) throws InvalidNameException {
        setSystem(system);
        setEntity(entity);
    }    

    /**
     * Builds a SimJ internal full name object from the specified set of parameters
     *
     * @param system the name of the system hosting the entity
     * @param entity the entity name
     * @return the {@code Name} object
     * @throws InvalidNameException
     */
    public static SimjFullName buildFrom(final Name system, final Name entity) throws InvalidNameException {
        return new SimjFullName(system, entity);
    }

    /**
     * Getter method for internal attribute
     *
     * @return the entity name
     */
    public Name getEntity() {
        return entity;        
    }

    /**
     * Getter method for internal attribute
     *
     * @return the system name
     */
    public Name getSystem() {
        return system;
    }

    /**
     * {@inheritDoc }
     * @return {@inheritDoc }
     */
    public String getValue() {
        return system.getValue() + "." + entity.getValue();
    }

    /**
     * Setter method for internal attribute
     *
     * @param n the entity name
     */
    protected void setEntity(final Name n) {
        entity = n;        
    }

    /**
     * Setter method for internal attribute
     *
     * @param n system name
     */
    protected void setSystem(final Name n) {
        system = n;
    }
    
    /** Sets the object internal attributes from the specified {@code String} object.
     *
     * @param s the {@code String} object containing the entity and system name in the following form systemName.entityName
     * @throws InvalidNameException
     */
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
