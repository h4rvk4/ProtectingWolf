package de.sunaru.ProtectingWolf;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectingWolf extends JavaPlugin {

	private final ProtectingWolfEntityListener entityListener = new ProtectingWolfEntityListener(this);
	private final ProtectingWolfPlayerListener playerListener = new ProtectingWolfPlayerListener(this);
	static final Logger log = Logger.getLogger("Minecraft");
	
	static String pluginPrefix = "";
	static String pluginVersion = "";

	@Override
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Event.Priority.Normal, this);
		
		ProtectingWolfConfig.getInstance().loadConfig();
		
		this.getCommand("pwolf").setExecutor(new ProtectingWolfCommands(this));
		
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new ProtectingWolfScheduler(), 0L, 600L);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginVersion = pdfFile.getVersion();
		pluginPrefix= "[" + pdfFile.getName() + "]";
		log.log(Level.INFO, pluginPrefix + " Plugin v" + pluginVersion + " has been enabled!");
	}

	@Override
	public void onDisable() {
		log.log(Level.INFO, pluginPrefix + " Plugin v" + pluginVersion + " has been disabled.");
	}
}