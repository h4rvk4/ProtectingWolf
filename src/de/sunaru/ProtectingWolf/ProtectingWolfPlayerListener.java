package de.sunaru.ProtectingWolf;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProtectingWolfPlayerListener extends PlayerListener {

public static ProtectingWolf plugin;

	public ProtectingWolfPlayerListener(ProtectingWolf instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.clearPlayerMonsterList(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		this.clearPlayerMonsterList(event.getPlayer());
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		this.clearPlayerMonsterList(event.getPlayer());
	}

	@Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof LivingEntity) {
			ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
			LivingEntity clickedEntity = (LivingEntity)event.getRightClicked();
			Player player = (Player)event.getPlayer();

			if (clickedEntity instanceof CraftCreeper) {
				if (config.getValue(player, ProtectingWolfConfig.CONFIG_KAMIKAZEDOG) == 0) {
					return;
				}
			}
			else if (clickedEntity instanceof CraftPlayer) {
				if (player.getWorld().getPVP() == false) {
					return;
				}
			}
			else if (clickedEntity instanceof CraftWolf) {
				if (((CraftWolf)clickedEntity).isTamed()) {
					String ownerName = ProtectingWolfLibrary.getWolfOwnerName((Wolf)clickedEntity);
					if (!ownerName.equalsIgnoreCase(player.getName())) {
						player.sendMessage(ChatColor.GREEN + "This dog is owned by " + ownerName);
					}

					if (player.getWorld().getPVP() == false || ownerName.equalsIgnoreCase(player.getName())) {
						return;
					}
				}
				if (player.getItemInHand().getType() == Material.BONE) {
					if (!((CraftWolf)clickedEntity).isTamed() && ProtectingWolfLibrary.hasTooManyWolves(player)) {
						int maxWolves = config.getValue(player, ProtectingWolfConfig.CONFIG_MAXWOLVES);
                		player.sendMessage(ChatColor.RED + "You can't own more than " + maxWolves + " dogs!");
                		event.setCancelled(true);
           			}
           			return;
				}
			}

			if (config.getValue(player, ProtectingWolfConfig.CONFIG_RIGHTCLICKATTACK) == 0) {
				return;
			}

			List<Wolf> wolves = ProtectingWolfLibrary.getNearByWolves(player);
			if (wolves.size() > 0) {
				for (Wolf wolf : wolves) {
					if (!clickedEntity.isDead()) {
						ProtectingWolfLibrary.actionWolfAttack(player, wolf, clickedEntity);
					}
				}
			}

			ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();
			victims.addDisputants(clickedEntity, player);
		}
	}

	private void clearPlayerMonsterList(Player player) {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();

		while (victims.isPlayerUnderAttack(player)) {
			Entity newVictim = victims.getNextPlayerDisputant(player);
			victims.removeDisputants(newVictim.getEntityId());
		}
	}
}
