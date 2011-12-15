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

import it.uniroma2.sel.simlab.simarch.data.Time;

/** Provides a layer 2 internal representation for Time interface
 *
 * @author Daniele Gianni
 */
public class SimjTime implements Time {
    
    private double value;
    
    /** 
     * Creates a new instance of Time 
     *
     * @param d the time value 
     */
    public SimjTime(final double d) { //throws IllegalTimeValue() {
        setValue(d);
    }
        
    /** 
     * Creates a new instance of Time 
     *
     * @param n the time value 
     */
    public SimjTime(final Number n) {
        setValue(n);
    }
        
    public SimjTime(final Time t) {
        setValue(t.getValue());
    }
    
    public static SimjTime buildFrom(final Time t) {
        return new SimjTime(t);
    }
       
    public void decreaseBy(final Time t) {
        value -= t.getValue();
    }
    
    public SimjTime decreasedBy(final Time t) {
        return new SimjTime(value - t.getValue());
    }
    
    public void increaseBy(final Time t) {
        value += t.getValue();
    }
        
    public SimjTime increasedBy(final Time t) {
        return new SimjTime(value + t.getValue());
    }    
    
    public void setValue(final Number n) { //throws IllegalTimeValue() {        
        setValue(n.doubleValue());
    }
    

    public void setValue(final double d) {
        //if (d() < 0) throws IllegalTimeValue(n.doubleValue + " < 0 ");
        value = d;
    }
    
    public double getValue() {
        return value;
    }    
        
    public int compareTo(final Time t) {        
        
        if (value < t.getValue()) return -1;
        if (value > t.getValue()) return 1;
        
        return 0;
    } 
    
    public boolean isGreaterThan(final Time t) {
        return (compareTo(t) > 0);
    }
    
    public boolean isGreaterOrEqualThan(final Time t) {
        return (compareTo(t) >= 0);
    }
    
    public boolean isLesserThan(final Time t) {
        return (compareTo(t) < 0);
    }
    
    public boolean isLesserOrEqualThan(final Time t) {
        return (compareTo(t) <= 0);
    }    
}
