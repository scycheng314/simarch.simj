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
        
    public static DistributedProcessEngine getEngine() {
        return engine;
    }
    
    public static SimjRemoteEntity buildFrom(final GeneralEntity e) throws InvalidNameException {        
        return new SimjRemoteEntity(SimjName.buildFrom(e.getSystemName()), SimjName.buildFrom(e.getEntityName()));
    }
    
    public static SimjRemoteEntity buildFrom(final Name sn, final Name en) throws InvalidNameException {        
        return new SimjRemoteEntity(SimjName.buildFrom(sn), SimjName.buildFrom(en));
    }
    
    public static void setEngine(final DistributedProcessEngine e) {
        engine = e;
    }
    
    public Name getEntityName() {
        return name.getEntity();
    }
    
    public Integer getEntityId() {
        return -1;        
    }
    
    public Name getFullName() {        
        return name;
                //systemName.getValue() + SimjName.TOKEN + entityName.getValue();
    }
    
    public Name getSystemName() {
        return name.getSystem();
    }
    
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

    public RemoteEntity getAsRemoteEntity() {
        return this;
    }
}
