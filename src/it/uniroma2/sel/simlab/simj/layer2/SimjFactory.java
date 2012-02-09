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

import it.uniroma2.sel.simlab.simarch.data.ComponentLevelEntity;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer1ToLayer2;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer2ToLayer1;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;
import it.uniroma2.sel.simlab.simj.engines.DistributedProcessEngine;
import it.uniroma2.sel.simlab.simj.engines.LocalProcessEngine;
import it.uniroma2.sel.simlab.simj.engines.ProcessEngine;
import it.uniroma2.sel.simlab.simj.entities.LocalEntity;
import it.uniroma2.sel.simlab.simj.errors.SimjError;
import it.uniroma2.sel.simlab.simj.exceptions.UnableToAddEntityException;

/** Provides a SimJ implementation of the SimArch's Layer3 factory
 *
 * @author Daniele Gianni
 * @version 1.1 06-01-06
 */
public class SimjFactory implements Layer3ToLayer2Factory {

    // the engine coordinating the simulation execution
    private ProcessEngine engine;
    
    /** Creates a new instance of SimjFactory */
    public SimjFactory(final Time simulationEnd) {
        engine = new LocalProcessEngine(simulationEnd);
    }

    /** Creates a new instance of SimjFactory, linking it to a distributed simulation
     * environment
     *
     * @param layer2ToLayer1 reference to the underlying layer implementation
     */
    public SimjFactory(final Layer2ToLayer1 layer2ToLayer1) {
        
        engine = new DistributedProcessEngine(layer2ToLayer1);
    }

    /** Allocated an implementation of Layer3toLayer2 services for the provided entity
     *
     * @param e entity
     * @return layer implementation
     * @throws InvalidNameException
     */
    public Layer3ToLayer2 create(final ComponentLevelEntity e) throws InvalidNameException {
        try {
            return new LocalEntity(e);
        } catch (UnableToAddEntityException ex) {
            ex.printStackTrace();
            throw new SimjError(ex);
        }
    }

    /**
     * Getter method for the Layer1ToLayer2 service implementation
     *
     * @return the interface implementation
     */
    public Layer1ToLayer2 getLayer1ToLayer2() {
        return (DistributedProcessEngine) engine;
    }
}
