package de.sunaru.ProtectingWolf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class ProtectingWolfLibrary {

	public static String getWolfOwnerName(Wolf w) {
		CraftWolf wolf = (CraftWolf) w;
		return wolf.getHandle().getOwnerName();
	}

	public static List<Wolf> getAllWolves(Player player) {
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

	public static List<Wolf> getNearByWolves(Player player) {
		List<Wolf> wolves = new ArrayList<Wolf>();

		for (LivingEntity entity : player.getWorld().getLivingEntities()) {
			if (entity instanceof Wolf) {
				Wolf wolf = (Wolf) entity;

				if (wolf.isTamed() && player.getName().equals(getWolfOwnerName(wolf))) {
					boolean found = false;
					List<Entity> nearBy = wolf.getNearbyEntities(40, 10, 20);
					if (nearBy.size() > 0) {
						for (Entity nearByEntity : nearBy) {
							if (nearByEntity.getEntityId() == player.getEntityId()) {
								found = true;
								break;
							}
						}
						if (found) {
							wolves.add(wolf);
						}
					}
				}
			}
		}

		return wolves;
	}

	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity entity) {
		return actionWolfAttack(player, wolf, entity, null, false);
	}

	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity entity, boolean forceAttack) {
		return actionWolfAttack(player, wolf, entity, null, forceAttack);
	}

	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity entity, Entity oldVictim) {
		return actionWolfAttack(player, wolf, entity, oldVictim, false);
	}

	public static boolean actionWolfAttack(Player player, Wolf wolf, Entity newVictim, Entity oldVictim, boolean forceAttack) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();

		if (wolf.getTarget() != null) {
			if (oldVictim != null) {
				if (wolf.getTarget().getEntityId() != oldVictim.getEntityId()) {
					return false;
				}
			}
			else if (forceAttack == false) {
				if (!wolf.getTarget().isDead()) {
					return false;
				}
			}
		}

		if (wolf.getHealth() <= 5) {
			if (config.getValue(player, ProtectingWolfConfig.CONFIG_TILLDEATH) == 0) {
				return false;
			}
		}

		if (wolf.isSitting()) {
			if (config.getValue(player, ProtectingWolfConfig.CONFIG_SITISSIT) == 1) {
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

	public static void callWolves(Player player, int count) {
		List<Wolf> wolves = getAllWolves(player);
		if (wolves.size() > 0) {
			int n = 0;
			while (n < wolves.size() && n < count) {
				Wolf wolf = (Wolf)wolves.get(n);
				wolf.setSitting(false);
				wolf.teleport(player);
				n++;
			}
		}
	}

	public static boolean hasTooManyWolves(Player player) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();

		if (config.getValue(player, ProtectingWolfConfig.CONFIG_MAXWOLVES) == -1) {
			return false;
		}

        if (config.getValue(player, ProtectingWolfConfig.CONFIG_UNLIMITEDWOLVES) == 1) {
            return false;
        }

        return (ProtectingWolfLibrary.getAllWolves(player).size() >= config.getValue(player, ProtectingWolfConfig.CONFIG_MAXWOLVES));
	}

	public static void spawnDog(World world, boolean isTamed, boolean isAngry, Player player) {
		Location location = player.isOnline() ? player.getLocation() : player.getWorld().getSpawnLocation();
		Wolf wolf = (Wolf)world.spawnCreature(location, CreatureType.WOLF);
		wolf.setAngry(isAngry);
		wolf.setTamed(isTamed);
		wolf.setHealth(20);
		if (isTamed) {
			wolf.setOwner(player);
		}

		if (player.isOnline()) {
			ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
			if (config.getValue(player, ProtectingWolfConfig.CONFIG_MSGONRESPAWN) == 1) {
				player.sendMessage("Dog respawned");
			}
		}
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
