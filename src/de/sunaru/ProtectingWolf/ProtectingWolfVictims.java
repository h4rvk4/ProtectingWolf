package de.sunaru.ProtectingWolf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
		return this.upcomingVictims[PLAYERS].containsValue(player);
	}
	
	public boolean isMonsterUnderAttack(int monsterId) {
		return this.upcomingVictims[MONSTERS].containsKey(monsterId);
	}

	public Player getPlayer(int monsterId) {
		return (Player)this.upcomingVictims[PLAYERS].get(monsterId);
	}
	
	public Entity getMonster(int monsterId) {
		return (Entity)this.upcomingVictims[MONSTERS].get(monsterId);
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
		if (!cleanUpLock) {
			cleanUpLock = true;
			Set keys = this.upcomingVictims[PLAYERS].keySet();
			Iterator it = keys.iterator();
			while (it.hasNext()) {
				Object key = it.next();
				Entity entity = (Entity)this.upcomingVictims[PLAYERS].get(key);
				List<Entity> nearBy = entity.getNearbyEntities(40, 10, 20);
				if (nearBy.size() > 0) {
					if (!nearBy.contains((Entity)this.upcomingVictims[MONSTERS].get(key))) {
						this.removeDisputants((Integer)key);
					}
				}
			}
			cleanUpLock = false;
		}
	}
}
