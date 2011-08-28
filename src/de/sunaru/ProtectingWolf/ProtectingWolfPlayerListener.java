package de.sunaru.ProtectingWolf;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
	
	private void clearPlayerMonsterList(Player player) {
		ProtectingWolfVictims victims = ProtectingWolfVictims.getInstance();

		while (victims.isPlayerUnderAttack(player)) {
			Entity newVictim = victims.getNextPlayerDisputant(player);
			victims.removeDisputants(newVictim.getEntityId());
		}
	}
}
