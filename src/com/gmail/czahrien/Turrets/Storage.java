/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.czahrien.Turrets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Czahrien
 */
public class Storage {
    public int ticksRunning;
    public Map<String, TurretInfo> info;
    public PriorityQueue<QueueElement> fireQueue;
    public Map<Location, TurretInfo> turrets;
    private Plugin myPlugin;
    private static Storage myInstance = null;
    
    public static Storage getInstance(Plugin p) {
        if(myInstance == null) {
            myInstance = new Storage(p);
        }
        return myInstance;
    }
    
    
    private Storage(Plugin p) {
        myPlugin = p;
        load();
    }
    
    @SuppressWarnings("unchecked")
    private void load() {
        try {
            ticksRunning = 0;
            info = new HashMap<>();
            fireQueue = new PriorityQueue();
            turrets = new HashMap<>();
            
            ConfigurationSection sect = myPlugin.getConfig().getConfigurationSection("turrets");
            if(sect != null) {
                Set<String> keys = sect.getKeys(false);
                for(String s : keys) {
                    System.out.println(s);
                    ConfigurationSection turret = sect.getConfigurationSection(s);
                    TurretInfo inf = new TurretInfo();

                    inf.type = TurretType.values()[turret.getInt("type")];
                    inf.distance = turret.getDouble("distance");
                    inf.speed = turret.getDouble("speed");
                    inf.los = turret.getBoolean("los");
                    inf.firerate = turret.getInt("firerate");
                    inf.loc = new Location(myPlugin.getServer().getWorld(turret.getString("world")),
                            turret.getInt("xloc"),turret.getInt("yloc"),turret.getInt("zloc"));

                    QueueElement e = new QueueElement();
                    inf.elem = e;
                    e.info = inf;
                    e.time = inf.firerate;

                    turrets.put(inf.loc, inf);
                    fireQueue.add(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
    
    public void save() {
        try {
            int num = 0;
            ConfigurationSection sect = myPlugin.getConfig().createSection("turrets");
            for(TurretInfo i : turrets.values()) {
                Map<String,Object> vals = new HashMap<>();
                vals.put("type", i.type.ordinal());
                vals.put("distance",i.distance);
                vals.put("speed",i.speed);
                vals.put("los", i.los);
                vals.put("firerate", i.firerate);
                vals.put("world", i.loc.getWorld().getName());
                vals.put("xloc", i.loc.getBlockX());
                vals.put("yloc", i.loc.getBlockY());
                vals.put("zloc", i.loc.getBlockZ());
                sect.createSection(""+num, vals);
                ++num;
            }
            myPlugin.saveConfig();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
