package de.sunaru.ProtectingWolf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class ProtectingWolfLibrary {

	public static String getWolfOwnerName(Wolf w) {
		CraftWolf wolf = (CraftWolf) w;
		return wolf.getHandle().getOwnerName();
	}

	public static List<Wolf> getWolves(Player player) {
		List<Wolf> wolves = new ArrayList<Wolf>();

		for (LivingEntity entity : player.getWorld().getLivingEntities()) {
			if (entity instanceof Wolf) {
				Wolf wolf = (Wolf) entity;

				if (wolf.isTamed() && player.getName().equals(getWolfOwnerName(wolf))) {
					wolves.add(wolf);
				}
			}
		}

		return wolves;
	}
	
	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity entity) {
		return actionWolfAttack(player, wolf, entity, null);
	}

	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity newVictim, Entity oldVictim) {
		if (wolf.getTarget() != null) {
			if (oldVictim != null) {
				if (wolf.getTarget().getEntityId() != oldVictim.getEntityId()) {
					return false;
				}
			}
			else {
				if (!wolf.getTarget().isDead()) {
					return false;
				}
			}
		}

		if (wolf.getHealth() <= 5) {
			if (ProtectingWolfConfig.getInstance().getValue(player, ProtectingWolfConfig.CONFIG_TILLDEATH) == 0) {
				return false;
			}
		}

		if (wolf.isSitting()) {
			if (ProtectingWolfConfig.getInstance().getValue(player, ProtectingWolfConfig.CONFIG_SITISSIT) == 1) {
				return false;
			}
		}

		boolean found = false;
		List<Entity> nearBy = wolf.getNearbyEntities(40, 10, 20);
		if (nearBy.size() > 0) {
			for (Entity nearByEntity : nearBy) {
				if (nearByEntity.getEntityId() == newVictim.getEntityId()) {
					found = true;
					break;
				}
			}
		}

		if (found) {
			wolf.setSitting(false);
			wolf.setTarget((LivingEntity)newVictim);
			return true;
		}
		return false;
	}

	public static Integer getHashKeyByValue(HashMap map, Object key) {
		Iterator<Map.Entry<Integer, Player>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Player> entry = iter.next();
			if (entry.getValue().equals(key)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static int indexOf(String array[], String value) {
		int result = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public static boolean inArray(String hackstack[], String needle) {
		return ((ProtectingWolfLibrary.indexOf(hackstack, needle) == -1) ? false : true);
	}
	
	public static String stringJoin(String array[], String clue) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			result+= array[i];
			if ((i + 1) < array.length) {
				result+= clue;
			}
		}
		return result;
	}
}
