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

package it.uniroma2.sel.simlab.simj.entities;

import it.uniroma2.sel.simlab.simarch.data.GeneralEntity;
import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.data.RemoteEntity;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simj.data.SimjFullName;
import it.uniroma2.sel.simlab.simj.data.SimjName;
import it.uniroma2.sel.simlab.simj.engines.DistributedProcessEngine;

/** Represents a proxy class for a remote {@code SimjEntity} (a {@code LocalEntity} running on a remote system)
 *
 *  @author     Daniele Gianni
 *  @version    2.1 06-01-06
 */
public class SimjRemoteEntity implements SimjEntity, RemoteEntity {

    // remote entity full name, which consists of the system name and of the entity name
    private SimjFullName name;

    // reference to the distributed engine, for scheduling the event for the remote entity
    private static DistributedProcessEngine engine;
    
    /** Creates a new instance of {@code RemoteEntity}
     *  
     *  @param e the entity to build from
     */  
    public SimjRemoteEntity(final RemoteEntity e) throws InvalidNameException {
        assert (!engine.getSystemName().equals(e.getSystemName())) : "Remote system coincides with the local system";
        
        setName(SimjFullName.buildFrom(e.getSystemName(), e.getEntityName()));
    }
    
    /** Creates a new instance of {@code RemoteEntity}
     *  
     *  @param e the entity to build from
     */
    public SimjRemoteEntity(final GeneralEntity e) throws InvalidNameException {
        assert (!engine.getSystemName().equals(e.getSystemName())) : "Remote system coincides with the local system";
        
        setName(SimjFullName.buildFrom(e.getSystemName(), e.getEntityName()));
    }
    
    /** Creates a new instance of {@code RemoteEntity} 
     *  
     *  @param  sn the system name
     *  @param  en the entity name
     */
    public SimjRemoteEntity(final Name sn, final Name en) throws InvalidNameException {
        assert (!engine.getSystemName().equals(sn)) : "Remote system coincides with the local system";
        
        setName(SimjFullName.buildFrom(sn, en));
    }    

    /**
     * Getter method for the engine property
     *
     * @return the reference to the distributed engine
     */
    public static DistributedProcessEngine getEngine() {
        return engine;
    }

    /**
     * Static factory method to create local stub of a remote SimJ entity from a SimArch compliant
     * definition
     *
     * @param e the SimArch compliant entity
     * @return the SimJ local stub
     * @throws InvalidNameException
     */
    public static SimjRemoteEntity buildFrom(final GeneralEntity e) throws InvalidNameException {        
        return new SimjRemoteEntity(SimjName.buildFrom(e.getSystemName()), SimjName.buildFrom(e.getEntityName()));
    }

    /**
     * Static factory method to create local stub of a remote SimJ entity from general
     * list of basic properties
     *
     * @param sn the system name
     * @param en the entity name
     * @return the SimJ local stub
     * @throws InvalidNameException
     */
    public static SimjRemoteEntity buildFrom(final Name sn, final Name en) throws InvalidNameException {        
        return new SimjRemoteEntity(SimjName.buildFrom(sn), SimjName.buildFrom(en));
    }

    /**
     * Setter method for the engine property
     *
     * @param e the reference to the distributed engine
     */
    public static void setEngine(final DistributedProcessEngine e) {
        engine = e;
    }

    /**
     * Getter method for the entity name
     *
     * @return the entity name
     */
    public Name getEntityName() {
        return name.getEntity();
    }

    /**
     * Getter method for the entity unique id
     *
     * @return -1 as this is a remote entity and therefore has no local reference
     */
    public Integer getEntityId() {
        return -1;        
    }

    /**
     * Getter method for entity name
     *
     * @return the entity name
     */
    public Name getFullName() {        
        return name;
        //systemName.getValue() + SimjName.TOKEN + entityName.getValue();
    }

    /**
     * Getter method for the system name
     *
     * @return the system name
     */
    public Name getSystemName() {
        return name.getSystem();
    }

    /**
     * Setter method for the entity name
     *
     * @param n the name
     */
    public void setName(final SimjFullName n) {
        name = n;
    }    
         
    /** {@inheritDoc}
     *  
     *  @return false
     */
    public boolean isLocal() {
        return false;
    }

    /**
     * {@inheritDoc }
     *
     * @return {@inheritDoc }
     */
    public RemoteEntity getAsRemoteEntity() {
        return this;
    }
}
