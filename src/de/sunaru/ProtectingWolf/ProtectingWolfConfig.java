package de.sunaru.ProtectingWolf;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class ProtectingWolfConfig {
	
	private HashMap settings[] = new HashMap[6];
	private String settingNames[] = { "kamikaze-dog", "till-death", "sit-is-sit", "msg-on-attack", "msg-on-peace", "msg-on-death" };
	
	public static int CONFIG_KAMIKAZEDOG = 0;
	public static int CONFIG_TILLDEATH = 1;
	public static int CONFIG_SITISSIT = 2;
	public static int CONFIG_MSGONATTACK = 3;
	public static int CONFIG_MSGONPEACE = 4;
	public static int CONFIG_MSGONDEATH = 5;

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
			Configuration config = new Configuration(new File("plugins/ProtectingWolf", "default.yml"));
			config.load();
			for (int i = 0; i < settings.length; i++) {
				settings[i].put(null, config.getInt(settingNames[i], (Integer)settings[i].get(null)));
			}
		}
		this.saveDefaultConfig();
		
		String[] entries = new File("plugins/ProtectingWolf").list();
		if (entries.length > 0) {
			for (String file : entries) {
				if (!file.equalsIgnoreCase("default.yml")) {
					String playerName = file.replace(".yml", "");
					Configuration config = new Configuration(new File("plugins/ProtectingWolf", file));
					config.load();
					for (int i = 0; i < settings.length; i++) {
						settings[i].put(playerName, config.getInt(settingNames[i], (Integer)settings[i].get(null)));
					}
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
		Configuration config = new Configuration(new File("plugins/ProtectingWolf", player.getName()+".yml"));
		for (int i = 0; i < settings.length; i++) {
			config.setProperty(settingNames[i], getValue(player, i));
		}
		config.save();
	}
	
	private void saveDefaultConfig() {
		Configuration config = new Configuration(new File("plugins/ProtectingWolf", "default.yml"));
		for (int i = 0; i < settings.length; i++) {
			config.setProperty(settingNames[i], getValue(null, i));
		}
		config.save();
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
	}
}
