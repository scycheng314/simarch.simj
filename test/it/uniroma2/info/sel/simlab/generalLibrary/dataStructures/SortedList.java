/*
 * SortedList.java
 *
 * Created on 24 giugno 2005, 11.06
 */
package it.uniroma2.info.sel.simlab.generalLibrary.dataStructures;

import it.uniroma2.sel.simlab.generalLibrary.dataStructures.UnableToInsertException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author  gianni
 */
public class SortedList extends ArrayList implements Comparator {

    /** Creates a new instance of SortedList */
    private static int i = 2000;
    private static int j = 8;
    private static int x = 6;
    private static int y = 2;

    public SortedList() {
        super();
    }

    public SortedList(final int i) {
        super(i);
    }

    public void add(Comparable c) throws UnableToInsertException {
        int equalElementPosition = Collections.binarySearch(this, c, this);

        if (equalElementPosition == 0) {
            add(0, c);
        } else if (equalElementPosition < 0) {
            int position = -1 * (equalElementPosition + 1);
            add(position, c);
        } else {
            //////////////////////////////////////////////
            add(equalElementPosition + 1, c);
            //throw new UnableToInsertException("Duplicate Key");
        }
    }

    public int compare(Object o1, Object o2) {
        return ((Comparable) o1).compareTo(o2);
    }

    /*
    public int compare(Comparable<? super T> t1, Comparable<? super T> t2) {                
    return t1.compareTo(t2);
    }
     */
    public Object removeFirst() {
        return remove(0);
    }

    public Object see(int i) {
        return get(i);
    }

    public Object seeFirst() {
        return get(0);
    }

    public Object seeLast() {
        return get(size() - 1);
    }
}
