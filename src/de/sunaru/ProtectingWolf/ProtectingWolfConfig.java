package de.sunaru.ProtectingWolf;

import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ProtectingWolfConfig {

	private HashMap settings[] = new HashMap[7];
	private String settingNames[] = { "kamikaze-dog", "till-death", "sit-is-sit", "msg-on-attack", "msg-on-peace", "msg-on-death", "attack-click" };

	public static int CONFIG_KAMIKAZEDOG = 0;
	public static int CONFIG_TILLDEATH = 1;
	public static int CONFIG_SITISSIT = 2;
	public static int CONFIG_MSGONATTACK = 3;
	public static int CONFIG_MSGONPEACE = 4;
	public static int CONFIG_MSGONDEATH = 5;
	public static int CONFIG_RIGHTCLICKATTACK = 6;

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
	}
}
