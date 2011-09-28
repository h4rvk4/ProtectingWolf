package de.sunaru.ProtectingWolf;

import java.util.List;
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

			if (config.getValue(player, ProtectingWolfConfig.CONFIG_RIGHTCLICKATTACK) == 0) {
				return;
			}
			
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
				if (((CraftWolf)clickedEntity).isTamed() && player.getWorld().getPVP() == false) {
					return;
				}
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
