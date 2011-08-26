package de.sunaru.ProtectingWolf;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProtectingWolfCommands implements CommandExecutor {

	public ProtectingWolfCommands(ProtectingWolf instance) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = ((Player)sender);
			if (label.equalsIgnoreCase("pwolf")) {
				if (args.length == 0) {
					this.showHelp(player);
				}
				else if (args[0].equalsIgnoreCase("info")) {
					this.showInformation(player);
				}
				else if (args[0].equalsIgnoreCase("help")) {
					this.showHelp(player);
				}
				else if (args[0].equalsIgnoreCase("enable")) {
					this.changeSetting(player, args[1], 1);
				}
				else if (args[0].equalsIgnoreCase("disable")) {
					this.changeSetting(player, args[1], 0);
				}
				else {
					this.showHelp(player);
				}
			}
		}
		return true;
	}
	
	private void showHelp(Player sender) {
		sender.sendMessage("ProtectingWolf usage:");
		sender.sendMessage("* /pwolf help");
		sender.sendMessage("* /pwolf info");
		sender.sendMessage("* /pwolf enable/disable <setting>");
		
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings = ProtectingWolfLibrary.stringJoin(config.getSettingNames(), ", ");
		sender.sendMessage("Possible settings:");
		sender.sendMessage(settings);
	}
	
	private void showInformation(Player sender) {
		sender.sendMessage("Your current ProtectingWolf settings:");
		
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings[] = config.getSettingNames();
		for (int i = 0; i < settings.length; i++) {
			sender.sendMessage("* "+settings[i]+": "+getStateString(config.getValue(sender, i)));
		}
	}
	
	private String getStateString(int value) {
		if (value == 1) {
			return "enabled";
		}
		return "disabled";
	}
	
	private void changeSetting(Player sender, String key, int value) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings[] = config.getSettingNames();
		if (ProtectingWolfLibrary.inArray(settings, key)) {
			config.setValue(sender, key, value);
			config.saveConfig(sender);
			if (value == 0) {
				sender.sendMessage("Setting "+key+" was disabled.");
			}
			else {
				sender.sendMessage("Setting "+key+" was enabled.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "Setting "+key+" is unknown.");
			sender.sendMessage(ChatColor.RED + "Use /pwolf help to see all settings.");
		}
	}
	
}
