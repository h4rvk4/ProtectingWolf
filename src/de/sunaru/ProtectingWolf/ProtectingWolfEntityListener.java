package de.sunaru.ProtectingWolf;

import java.util.List;
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

	public ProtectingWolfEntityListener(ProtectingWolf instance) {
		plugin = instance;
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
	
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
			
			
			if (!victims.isPlayerUnderAttack(player)) {
				if (config.getValue(player, ProtectingWolfConfig.CONFIG_MSGONATTACK) == 1) {
					player.sendMessage(ChatColor.RED + " Beware, your dogs spotted enemies.");
				}
			}

			victims.addDisputants(event.getEntity(), player);
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
		
		if (victims.isMonsterUnderAttack(event.getEntity().getEntityId())) {
			this.handleDeadMonster(event);
		}
		else if (event.getEntity() instanceof Wolf) {
			this.handleDeadWolf(event);
		}
		else if (event.getEntity() instanceof Player) {
			this.handleDeadPlayer(event);
		}
	}
	
	private void handleDeadMonster(EntityDeathEvent event) {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();

		Player player = victims.getPlayer(event.getEntity().getEntityId());
		victims.removeDisputants(event.getEntity().getEntityId());

		if (victims.isPlayerUnderAttack(player)) {
			Entity newVictim = victims.getNextPlayerDisputant(player);

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
	
	private void handleDeadWolf(EntityDeathEvent event) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();

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
	
	private void handleDeadPlayer(EntityDeathEvent event) {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();

		while (victims.isPlayerUnderAttack((Player)event.getEntity())) {
			Entity newVictim = victims.getNextPlayerDisputant((Player)event.getEntity());
			victims.removeDisputants(newVictim.getEntityId());
		}
	}
}
