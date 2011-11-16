package de.sunaru.ProtectingWolf;

import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ProtectingWolfConfig {

	private HashMap settings[] = new HashMap[13];
	private String settingNames[] = {
		"kamikaze-dog", "till-death", "sit-is-sit", "msg-on-attack", "msg-on-peace", "msg-on-death", "attack-click", "invincible",
		"respawn", "respawn-time", "max-wolves", "unlimited-wolves", "msg-on-respawn"
	};
	private boolean settingBoolean[] = {
		true, true, true, true, true, true, true, true, true, false, false, true, true
	};

	public static int CONFIG_KAMIKAZEDOG = 0;
	public static int CONFIG_TILLDEATH = 1;
	public static int CONFIG_SITISSIT = 2;
	public static int CONFIG_MSGONATTACK = 3;
	public static int CONFIG_MSGONPEACE = 4;
	public static int CONFIG_MSGONDEATH = 5;
	public static int CONFIG_RIGHTCLICKATTACK = 6;
	public static int CONFIG_INVINCIBLE = 7;
	public static int CONFIG_RESPAWN = 8;
	public static int CONFIG_RESPAWNTIME = 9;
	public static int CONFIG_MAXWOLVES = 10;
	public static int CONFIG_UNLIMITEDWOLVES = 11;
	public static int CONFIG_MSGONRESPAWN = 12;

	private static ProtectingWolfConfig instance = null;

	public static ProtectingWolfConfig getInstance() {
		if (instance == null) {
			instance = new ProtectingWolfConfig();
		}
		return instance;
	}

	public ProtectingWolfConfig() {
		this.initDefaultSettings();
	}

	public void loadConfig() {
		if (!new File("plugins/ProtectingWolf").exists()) {
			new File("plugins/ProtectingWolf").mkdir();
		}

		if (new File("plugins/ProtectingWolf/default.yml").exists()) {
			try {
				YamlConfiguration config = new YamlConfiguration();
				config.load(new File("plugins/ProtectingWolf", "default.yml"));
				for (int i = 0; i < settings.length; i++) {
					settings[i].put(null, config.getInt(settingNames[i], (Integer)settings[i].get(null)));
				}
			}
			catch (Exception e) {}
		}
		this.saveDefaultConfig();

		String[] entries = new File("plugins/ProtectingWolf").list();
		if (entries.length > 0) {
			for (String file : entries) {
				if (!file.equalsIgnoreCase("default.yml")) {
					try {
						String playerName = file.replace(".yml", "");
						YamlConfiguration config = new YamlConfiguration();
						config.load(new File("plugins/ProtectingWolf", file));
						for (int i = 0; i < settings.length; i++) {
							settings[i].put(playerName, config.getInt(settingNames[i], (Integer)settings[i].get(null)));
						}
					}
					catch (Exception e) {}
				}
			}
		}
	}

	public boolean isSettingBoolean(String key) {
		int i = ProtectingWolfLibrary.indexOf(settingNames, key);
		return settingBoolean[i];
	}

	public int getValue(Player player, int setting) {
		if (player != null) {
			if (settings[setting].containsKey(player.getName())) {
				return ((Integer)settings[setting].get(player.getName())).intValue();
			}
		}
		return ((Integer)settings[setting].get(null)).intValue();
	}

	public void setValue(Player player, String key, int value) {
		int i = ProtectingWolfLibrary.indexOf(settingNames, key);
		settings[i].put(player.getName(), value);
	}

	public String[] getSettingNames() {
		return this.settingNames;
	}

	public void saveConfig(Player player) {
		try {
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < settings.length; i++) {
				config.set(settingNames[i], getValue(player, i));
			}
			config.save(new File("plugins/ProtectingWolf", player.getName()+".yml"));
		}
		catch (Exception e) {}
	}

	private void saveDefaultConfig() {
		try {
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < settings.length; i++) {
				config.set(settingNames[i], getValue(null, i));
			}
			config.save(new File("plugins/ProtectingWolf", "default.yml"));
		}
		catch (Exception e) {}
	}

	private void initDefaultSettings() {
		for (int i = 0; i < settings.length; i++) {
			settings[i] = new HashMap<Player, Integer>();
		}

		settings[CONFIG_KAMIKAZEDOG].put(null, 1);
		settings[CONFIG_TILLDEATH].put(null, 0);
		settings[CONFIG_SITISSIT].put(null, 0);
		settings[CONFIG_MSGONATTACK].put(null, 1);
		settings[CONFIG_MSGONPEACE].put(null, 1);
		settings[CONFIG_MSGONDEATH].put(null, 1);
		settings[CONFIG_RIGHTCLICKATTACK].put(null, 0);
		settings[CONFIG_INVINCIBLE].put(null, 0);
		settings[CONFIG_RESPAWN].put(null, 0);
		settings[CONFIG_RESPAWNTIME].put(null, 10);
		settings[CONFIG_MAXWOLVES].put(null, 10);
		settings[CONFIG_UNLIMITEDWOLVES].put(null, 1);
		settings[CONFIG_MSGONRESPAWN].put(null, 1);
	}
}
