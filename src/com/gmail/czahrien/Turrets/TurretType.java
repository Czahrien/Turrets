/*
 * File: TurretType.java
 * Author: czahrien <czahrien@gmail.com>
 * Description: An enumeration over all types of turrets.
 */
package com.gmail.czahrien.Turrets;

import java.lang.reflect.Field;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntitySmallFireball;
import net.minecraft.server.MathHelper;
import net.minecraft.server.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * An enumeration containing all types of Turrets and methods for identifying
 * them and controlling what happens when they fire.
 * 
 * @author Czahrien
 */
    public enum TurretType {
        ARROW,          // fires an arrow
        SMALL_FIREBALL, // fires a blaze's fireball
        FIREBALL,       // fires a ghast's fireball
        NONE;
        
        /** 
         * Fires a projectile from the turret at the source to a player at the
         * destination with a speed multiplier.
         * 
         * @param source The source location.
         * @param desination The player to fire a projectile at.
         * @param speed The speed multiplier.
         */
        public void fire(Location source, Player destination, double speed) {
            switch(this) {
                    case ARROW:
                        spawnArrow(source, ((CraftPlayer)destination).getHandle(),speed);
                        break;
                    case SMALL_FIREBALL: {
                        Location l = destination.getLocation();
                        Location fbloc = source.clone();
                        Vector v = new Vector(l.getX()-source.getX(),l.getY()-source.getY(),l.getZ()-source.getZ());
                        v = v.normalize();
                        fbloc.add(v.multiply(new Vector(2,2,2)));
                        double velocity = speed;
                        spawnSmallFireball(fbloc, v, velocity);
                        break;
                    }
                    case FIREBALL: {
                        Location l = destination.getLocation();
                        Location fbloc = source.clone();
                        Vector v = new Vector(l.getX()-source.getX(),l.getY()-source.getY(),l.getZ()-source.getZ());
                        v = v.normalize();
                        fbloc.add(v.multiply(new Vector(2,2,2)));
                        double velocity = speed;
                        spawnFireball(fbloc, v, velocity);
                        break;
                    }
            }
        }
        
        /**
         * Obtains a TurretType from a string.
         * @param s The string to convert into a TurretType.
         * @return A TurretType corresponding to the string given. 
         */
        public static TurretType fromString(String s) {
            s = s.toLowerCase();
            switch(s) {
                case "arrow":
                    return ARROW;
                case "small_fireball":
                case "small fireball":
                case "smallfireball":
                    return SMALL_FIREBALL;
                case "fireball":
                    return FIREBALL;
                default:
                    return NONE;
            }
        }
        
    /**
     * Fires an arrow from the given location towards the victim at the given
     * multiplier of the speed to a normal arrow with pinpoint accuracy.
     * @param l         The location.
     * @param victim    The victim.
     * @param speed     Speed multiplier.
     */    
    public void spawnArrow(Location l, EntityLiving victim, double speed) {
        try{
            EntityArrow arrow = new EntityArrow(victim.world,l.getX(),l.getY(),l.getZ());
            double d0 = victim.locX - l.getX();
            double d1 = victim.locY - l.getY();
            double d2 = victim.locZ - l.getZ();
            double d3 = Math.sqrt(d0*d0 + d2*d2);
            if(d3 >= 1.0E-7D) {
                float f2 = (float) (Math.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
                float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / 3.1415927410125732D));
                double d4 = d0 / d3;
                double d5 = d2 / d3;

                arrow.setPositionRotation(l.getX() + d4, l.getY(), l.getZ() + d5, f2, f3);
                arrow.height = 0.0F;
                arrow.shoot(d0, d1+d3*0.2F, d2, (float)(speed*2.0), 0.0F);
                victim.world.addEntity(arrow);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Spawns a fireball at location l using v as a velocity vector with the
     * given speed multiplier
     * 
     * @param l The location to create the fireball at.
     * @param v The direction vector.
     * @param speed The speed multiplier.
     */
    public void spawnFireball(Location l, Vector v, double speed) {
        try {
            Field world = CraftWorld.class.getDeclaredField("world");
            world.setAccessible(true);
            WorldServer ws = (WorldServer)world.get(l.getWorld());
            EntityFireball fb = new EntityFireball(ws);
            fb.setPositionRotation(l.getX(), l.getY(), l.getZ(), 0f, 0f);
            double div = (double) MathHelper.sqrt(v.getX()*v.getX() + v.getY()*v.getY() + v.getZ() * v.getZ());
            fb.dirX = v.getX()/div*0.1D * speed;
            fb.dirY = v.getY()/div*0.1D * speed;
            fb.dirZ = v.getZ()/div*0.1D * speed;
            ws.addEntity(fb);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spawns a small fireball at location l using v as a velocity vector with the
     * given speed multiplier
     * 
     * @param l The location to create the fireball at.
     * @param v The direction vector.
     * @param speed The speed multiplier.
     */
    public void spawnSmallFireball(Location l, Vector v, double speed) {
        try {
            Field world = CraftWorld.class.getDeclaredField("world");
            world.setAccessible(true);
            WorldServer ws = (WorldServer)world.get(l.getWorld());
            EntitySmallFireball fb = new EntitySmallFireball(ws);
            fb.setPositionRotation(l.getX(), l.getY(), l.getZ(), 0f, 0f);
            double div = (double) MathHelper.sqrt(v.getX()*v.getX() + v.getY()*v.getY() + v.getZ() * v.getZ());
            fb.dirX = v.getX()/div*0.1D * speed;
            fb.dirY = v.getY()/div*0.1D * speed;
            fb.dirZ = v.getZ()/div*0.1D * speed;
            ws.addEntity(fb);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    }