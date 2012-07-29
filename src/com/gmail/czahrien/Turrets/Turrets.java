/*
 * File: Turret.java
 * Author: czahrien <czahrien@gmail.com>
 * Description: A plugin which implements Turrets in Minecraft.
 */
package com.gmail.czahrien.Turrets;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

/**
 *
 * @author Czahrien
 */
public class Turrets extends JavaPlugin implements Listener, Runnable { 
    public Storage myStorage;
    
    
    @Override
    public void onEnable() {
        myStorage = Storage.getInstance(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this, 1, 1);
    }
    
    @Override
    public void onDisable() {
        myStorage.save();
    }
    
    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if(e.getPlayer().isOp() && e.getPlayer().getItemInHand().getType() == Material.WOOD_HOE) {
            e.setCancelled(true);
            if(e.getAction() == Action.LEFT_CLICK_BLOCK) { 
                Location l = e.getClickedBlock().getLocation();
                TurretInfo i;
                i = new TurretInfo(myStorage.info.get(e.getPlayer().getName()));
                if(i == null || i.type == TurretType.NONE) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Select a turret type using /turret type!");
                } else {
                    i.elem = new QueueElement();
                    i.loc = l;
                    i.elem.time = i.firerate + myStorage.ticksRunning;
                    i.elem.info = i;
                    TurretInfo old = myStorage.turrets.put(l,i);
                    if(old != null) {
                        old.elem.time = -1;
                    }
                    myStorage.fireQueue.add(i.elem);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Created/updated turret at the clicked block.");
                }
            } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location l = e.getClickedBlock().getLocation();
                TurretInfo removed = myStorage.turrets.remove(l);
                if(removed != null) {
                    removed.elem.time = -1;
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully removed a turret at clicked blocked.");
                }
            }
        }
    }
    
    @EventHandler
    public void login(PlayerLoginEvent e) {
        if(!myStorage.info.containsKey(e.getPlayer().getName())) {
            myStorage.info.put(e.getPlayer().getName(), new TurretInfo());
        }
    }
    
    @Override
    public void run() {
        myStorage.ticksRunning++;
        while(!myStorage.fireQueue.isEmpty() && myStorage.fireQueue.peek().time < myStorage.ticksRunning) {
            QueueElement e = myStorage.fireQueue.poll();
            if(e.time < 0) {
                continue;
            }
            
            TurretInfo i = e.info;
            Location t = i.loc;
            e.time += i.firerate;
            myStorage.fireQueue.add(e);
            
            Player closest = null;
            
            double dist = i.distance;    
            for(Player p : t.getWorld().getPlayers()) {
                Location l = p.getLocation();
                double d = l.distance(t);
                if(d < dist) {
                    if(i.los) {
                        boolean good = true;
                        // tracing from the player should be more efficienct
                        // because we'd expect a player to hide when under fire.
                        BlockIterator b = new BlockIterator(l.getWorld(),l.toVector(),t.toVector().add(l.toVector().multiply(-1)), 1.0,(int)d-2);
                        while(b.hasNext()) {
                            Block bl = b.next();
                            // TODO: All transparent blocks.
                            if(bl.getType() != Material.AIR) {
                                good = false;
                                break;
                            }
                        }
                        if(!good) {
                            continue;
                        }
                    }
                    closest = p;
                    dist = d;
                }  
            }
            if(closest != null) {
                i.type.fire(t,closest,i.speed);
            }
        
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && sender.isOp()) {
            TurretInfo i = myStorage.info.get(sender.getName());
            if(label.equalsIgnoreCase("turret") && args.length >= 1) {
                label = args[0];
                switch(label.toLowerCase()) {
                    case "speed":
                        if(args.length == 2) {
                            try {
                                double v = Double.parseDouble(args[1]);
                                i.speed = v;
                                sender.sendMessage(ChatColor.GREEN + "Speed set to " + ChatColor.GOLD + v + ChatColor.GREEN + ".");
                            } catch(NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid number given.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "USAGE: /turret speed [multiplier]");
                        }
                        break;
                    case "distance":
                        if(args.length == 2) {
                            try {
                                double v = Double.parseDouble(args[1]);
                                if( v > 0 ) {
                                    i.speed = v;
                                    sender.sendMessage(ChatColor.GREEN + "Distance set to " + ChatColor.GOLD + v + ChatColor.GREEN + ".");
                                }
                            } catch(NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid number given.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "USAGE: /turret distance [fire distance]");
                        }
                        break;
                    case "type":
                        if(args.length == 2) {
                            TurretType tt = TurretType.fromString(args[1]);
                            i.type = tt;
                            sender.sendMessage(ChatColor.GREEN + "Type set to " + ChatColor.GOLD + i.type + ChatColor.GREEN + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "USAGE: /turret type [ARROW|FIREBALL|SMALL_FIREBALL]");
                        }
                        break;
                    case "firerate":
                        if(args.length == 2) {
                            try {
                                int r = Integer.parseInt(args[1]);
                                if(r > 0) {
                                    sender.sendMessage(ChatColor.GOLD + "Rate set to " + r + " ticks.");
                                    i.firerate = r;
                                }
                            } catch(NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Invalid number given.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "USAGE: /turret firerate [# of ticks]");
                        }
                        break;
                    case "los":
                        if(args.length == 2) {
                            switch(args[1].toLowerCase()) {
                                case "yes":
                                case "on":
                                case "true":
                                    i.los = true;
                                    sender.sendMessage(ChatColor.GOLD + "Placed turrets will now use line of sight.");
                                    break;
                                case "no":
                                case "off":
                                case "false":
                                    i.los = false;
                                    sender.sendMessage(ChatColor.GOLD + "Placed turrets will now fire regardless of line of sight.");
                                    break;
                                default:
                                    sender.sendMessage(ChatColor.RED + "USAGE: /turret los [on|off]");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "USAGE: /turret los [on|off]");
                        }
                        break;
                    case "save":
                        myStorage.save();
                        break;
                    default:
                        return false;
                }
                return true;
            }

        }
        return false;
    }   
}