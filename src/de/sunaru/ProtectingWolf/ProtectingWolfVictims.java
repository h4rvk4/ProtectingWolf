package de.sunaru.ProtectingWolf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProtectingWolfVictims {
	private static int PLAYERS = 0;
	private static int MONSTERS = 1;

	private boolean cleanUpLock = false;

	private HashMap upcomingVictims[] = new HashMap[2];

	private static ProtectingWolfVictims instance = null;

	public static ProtectingWolfVictims getInstance() {
		if (instance == null) {
			instance = new ProtectingWolfVictims();
		}
		return instance;
	}

	public ProtectingWolfVictims() {
		upcomingVictims[PLAYERS] = new HashMap<Integer, Player>();
		upcomingVictims[MONSTERS] = new HashMap<Integer, Entity>();
	}

	public boolean isPlayerUnderAttack(Player player) {
		HashMap upcomingClone = (HashMap)this.upcomingVictims[PLAYERS].clone();
		return upcomingClone.containsValue(player);
	}

	public boolean isMonsterUnderAttack(int monsterId) {
		HashMap upcomingClone = (HashMap)this.upcomingVictims[MONSTERS].clone();
		return upcomingClone.containsKey(monsterId);
	}

	public Player getPlayer(int monsterId) {
		HashMap upcomingClone = (HashMap)this.upcomingVictims[PLAYERS].clone();
		return (Player)upcomingClone.get(monsterId);
	}

	public Entity getMonster(int monsterId) {
		HashMap upcomingClone = (HashMap)this.upcomingVictims[MONSTERS].clone();
		return (Entity)upcomingClone.get(monsterId);
	}

	public void addDisputants(Entity entity, Player player) {
		this.upcomingVictims[PLAYERS].put(entity.getEntityId(), player);
		this.upcomingVictims[MONSTERS].put(entity.getEntityId(), entity);
	}

	public void removeDisputants(int monsterId) {
		this.upcomingVictims[PLAYERS].remove(monsterId);
		this.upcomingVictims[MONSTERS].remove(monsterId);
	}

	public Entity getNextPlayerDisputant(Player player) {
		int monsterId = ProtectingWolfLibrary.getHashKeyByValue(this.upcomingVictims[PLAYERS], player);
		return this.getMonster(monsterId);
	}

	public void cleanUpDisputants() {
		try {
			if (!cleanUpLock) {
				cleanUpLock = true;
				Iterator it = this.upcomingVictims[PLAYERS].keySet().iterator();
				while (it.hasNext()) {
					Object key = it.next();
					Entity entity = (Entity)this.upcomingVictims[PLAYERS].get(key);
					List<Entity> nearBy = entity.getNearbyEntities(40, 10, 20);
					if (nearBy.size() > 0) {
						if (!nearBy.contains((Entity)this.upcomingVictims[MONSTERS].get(key))) {
							this.removeDisputants((Integer)key);
							it = this.upcomingVictims[PLAYERS].keySet().iterator();
						}
					}
				}
				cleanUpLock = false;
			}
		}
		catch (Exception e) {
			cleanUpLock = false;
		}
	}
}
