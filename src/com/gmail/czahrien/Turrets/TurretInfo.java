/*
 * File: TurretInfo.java
 * Author: czahrien <czahrien@gmail.com>
 * Description: A class that stores information about a Turret.
 */
package com.gmail.czahrien.Turrets;

import java.io.Serializable;
import org.bukkit.Location;

/**
 * Stores all information associated with a turret.
 * 
 * @author Czahrien
 */
    public class TurretInfo implements Serializable {
        /**
         * Creates a turret with default parameters and no type.
         */
        public TurretInfo() {
            type = TurretType.NONE;
            speed = 1.0;
            firerate = 20;
            distance = 160;
            los = false;
            loc = null;
            elem = null;
        }
        
        /**
         * Copies all parts of the provided turret except for its QueueElement.
         * 
         * @param toCopy The turret to copy.
         */
        public TurretInfo(TurretInfo toCopy) {
            type = toCopy.type;
            speed = toCopy.speed;
            firerate = toCopy.firerate;
            distance = toCopy.distance;
            los = toCopy.los;
            loc = null;
            elem = null;
        }
        
        /**
         * What does the turret fire?
         */
        public TurretType type;
        /**
         * At what rate does the turret fire? (measured in ticks)
         */
        public int firerate;
        /**
         * What is the maximum distance of the turret?
         */
        public double distance;
        /**
         * Should the turret only fire if it has line of sight to the victim?
         */
        public boolean los;
        /**
         * What speed should the turret's projectile have?
         */
        public double speed;
        /**
         * The location of the turret.
         */
        public Location loc;
        /**
         * A reference to this turret's queue element.
         */
        public QueueElement elem;
    }
