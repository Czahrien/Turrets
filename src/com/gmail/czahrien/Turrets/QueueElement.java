/*
 * File: QueueElement.java
 * Author: czahrien <czahrien@gmail.com>
 * Description: A class for use with a priority queue.
 */
package com.gmail.czahrien.Turrets;

import java.io.Serializable;
import org.bukkit.Location;

/**
 * A class containing needed information for determining whether or not a turret
 * should fire meant for a binary min heap based priority queue.
 * 
 * @author Czahrien
 */
public class QueueElement implements Comparable, Serializable {
        /**
         * The total number of server ticks passed before the turret fires again
         */
        public int time;
        /**
         * The turret information associated with this QueueElement.
         */
        public TurretInfo info;
        
        @Override
        /**
         * An unstable comparison between two QueueElements. Since all that
         * matters in our application is the time that is all that we compare
         * with.
         *
         * @param o The other QueueElement.
         * @return < 0 if the supplied QE has more time, 0 if they are equal, > 0 if the supplied QE has less time.
         * 
         */
        public int compareTo(Object o) {
            QueueElement other = (QueueElement)o;
            return time - other.time;
        }
    }