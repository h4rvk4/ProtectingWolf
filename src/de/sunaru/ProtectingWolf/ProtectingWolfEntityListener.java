package de.sunaru.ProtectingWolf;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class ProtectingWolfEntityListener extends EntityListener {

	public static ProtectingWolf plugin;
	private HashMap upcomingVictims[] = new HashMap[2];

	public ProtectingWolfEntityListener(ProtectingWolf instance) {
		plugin = instance;

		upcomingVictims[0] = new HashMap<Integer, Player>();
		upcomingVictims[1] = new HashMap<Integer, Entity>();
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
	
		if (event.getTarget() instanceof CraftPlayer && event.getEntity() instanceof CraftMonster) {
			Player player = (Player) event.getTarget();
			List<Wolf> wolves = ProtectingWolfLibrary.getWolves(player);

			if (event.getEntity() instanceof CraftCreeper) {
				if (config.getValue(player, ProtectingWolfConfig.CONFIG_KAMIKAZEDOG) == 0) {
					return;
				}
			}

			if (wolves.size() > 0) {
				for (Wolf wolf : wolves) {
					if (!event.getEntity().isDead()) {
						ProtectingWolfLibrary.actionWolfAttack(player, wolf, event.getEntity());
					}
				}
			}
			
			if (!this.upcomingVictims[0].containsValue(player)) {
				if (config.getValue(player, ProtectingWolfConfig.CONFIG_MSGONATTACK) == 1) {
					player.sendMessage(ChatColor.RED + " Beware, your dogs spotted enemies.");
				}
			}

			this.upcomingVictims[0].put(event.getEntity().getEntityId(), player);
			this.upcomingVictims[1].put(event.getEntity().getEntityId(), event.getEntity());
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
	
		if (this.upcomingVictims[0].containsKey(event.getEntity().getEntityId())) {
			Player player = (Player)this.upcomingVictims[0].get(event.getEntity().getEntityId());
			this.upcomingVictims[0].remove(event.getEntity().getEntityId());
			this.upcomingVictims[1].remove(event.getEntity().getEntityId());
			
			if (this.upcomingVictims[0].containsValue(player)) {
				Integer key = ProtectingWolfLibrary.getHashKeyByValue(this.upcomingVictims[0], player);
				Entity newVictim = (Entity)this.upcomingVictims[1].get(key);
				
				if (newVictim != null) {
					List<Wolf> wolves = ProtectingWolfLibrary.getWolves(player);
					if (wolves.size() > 0) {
						for (Wolf wolf : wolves) {
							ProtectingWolfLibrary.actionWolfAttack(player, wolf, newVictim, event.getEntity());
						}
					}
				}
			}
			else {
				if (config.getValue(player, ProtectingWolfConfig.CONFIG_MSGONPEACE) == 1) {
					player.sendMessage(ChatColor.RED + " Your dogs don't see more enemies.");
				}
			}
		}
		else if (event.getEntity() instanceof Wolf) {
			Wolf deadWolf = (Wolf)event.getEntity();
			if (deadWolf.isTamed()) {
				List<Player> players = deadWolf.getWorld().getPlayers();
				if (players.size() > 0) {
					for (Player player : players) {
						if (player.isOnline() && player.getName().equalsIgnoreCase(ProtectingWolfLibrary.getWolfOwnerName(deadWolf))) {
							if (config.getValue(player, ProtectingWolfConfig.CONFIG_MSGONDEATH) == 1) {
								player.sendMessage(ChatColor.RED + " One of your dogs died.");
							}
							break;
						}
					}
				}
			}
		}
		else if (event.getEntity() instanceof Player) {
			while (this.upcomingVictims[0].containsValue(event.getEntity())) {
				Integer key = ProtectingWolfLibrary.getHashKeyByValue(this.upcomingVictims[0], event.getEntity());
				this.upcomingVictims[0].remove(key);
				this.upcomingVictims[1].remove(key);
			}
		}
	}
}
