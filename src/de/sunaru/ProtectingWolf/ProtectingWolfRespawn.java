package de.sunaru.ProtectingWolf;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProtectingWolfRespawn {
	private static int PLAYERS = 0;
	private static int TIME = 1;
	private static int WORLD = 2;

	private HashMap upcomingRespawns[] = new HashMap[3];

	private static ProtectingWolfRespawn instance = null;

	private boolean respawnLock = false;

	public static ProtectingWolfRespawn getInstance() {
		if (instance == null) {
			instance = new ProtectingWolfRespawn();
		}
		return instance;
	}

	public ProtectingWolfRespawn() {
		this.upcomingRespawns[PLAYERS] = new HashMap<Integer, Player>();
		this.upcomingRespawns[TIME] = new HashMap<Integer, Integer>();
		this.upcomingRespawns[WORLD] = new HashMap<Integer, World>();
	}

	public void addEntry(Entity entity, Player player, int time) {
		this.upcomingRespawns[PLAYERS].put(entity.getEntityId(), player);
		this.upcomingRespawns[TIME].put(entity.getEntityId(), time);
		this.upcomingRespawns[WORLD].put(entity.getEntityId(), player.getWorld());
	}

	public void removeEntry(int monsterId) {
		this.upcomingRespawns[PLAYERS].remove(monsterId);
		this.upcomingRespawns[TIME].remove(monsterId);
		this.upcomingRespawns[WORLD].remove(monsterId);
	}

	public void respawnWolves() {
		try {
			if (!respawnLock) {
				respawnLock = true;
				Iterator it = this.upcomingRespawns[PLAYERS].keySet().iterator();
				while (it.hasNext()) {
					Integer key = (Integer)it.next();
					int timeRemaining = (Integer)this.upcomingRespawns[TIME].get(key);
					World world = (World)this.upcomingRespawns[WORLD].get(key);
					Player player = (Player)this.upcomingRespawns[PLAYERS].get(key);
					timeRemaining--;
					if (timeRemaining <= 0) {
						ProtectingWolfLibrary.spawnDog(world, true, false, player);
						this.removeEntry(key);
						it = this.upcomingRespawns[PLAYERS].keySet().iterator();
					}
					else {
						this.upcomingRespawns[TIME].put(key, timeRemaining);
					}
				}
				respawnLock = false;
			}
		}
		catch (Exception e) {
			respawnLock = false;
		}
	}
}
