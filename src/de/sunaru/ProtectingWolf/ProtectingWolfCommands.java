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
					if (args.length < 2) {
						sender.sendMessage("Missing argument");
						return true;
					}
					this.changeSetting(player, args[1], 1);
				}
				else if (args[0].equalsIgnoreCase("disable")) {
					if (args.length < 2) {
						sender.sendMessage("Missing argument");
						return true;
					}
					this.changeSetting(player, args[1], 0);
				}
				else if (args[0].equalsIgnoreCase("set")) {
					if (args.length < 3) {
						sender.sendMessage("Missing argument");
						return true;
					}
					int value = this.getValidIntegerValue(args[2]);
					if (value < 0) {
						sender.sendMessage("Value is invalid");
						return true;
					}
					this.changeSetting(player, args[1], value);
				}
				else if (args[0].equalsIgnoreCase("call")) {
					this.checkCallDogs(sender, args, 2);
				}
				else {
					this.showHelp(player);
				}
			}
			if (label.equalsIgnoreCase("pwcall")) {
				this.checkCallDogs(sender, args, 1);
			}
		}
		return true;
	}

	private void showHelp(Player sender) {
		sender.sendMessage("ProtectingWolf usage:");
		sender.sendMessage("* /pwolf help");
		sender.sendMessage("* /pwolf info");
		sender.sendMessage("* /pwolf enable/disable/set <setting>");
		sender.sendMessage("* /pwcall <amount|all>");

		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings = ProtectingWolfLibrary.stringJoin(config.getSettingNames(), ", ");
		sender.sendMessage("Possible settings:");
		sender.sendMessage(settings);
	}

	private void showHelpCall(Player sender) {
		sender.sendMessage("ProtectingWolf /pwcall usage:");
		sender.sendMessage("* /pwcall <amount|all>");
		sender.sendMessage("* Demo: /pwcall 4 or /pwcall all");
	}

	private void showInformation(Player sender) {
		sender.sendMessage("Your current ProtectingWolf settings:");

		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings[] = config.getSettingNames();
		for (int i = 0; i < settings.length; i++) {
			sender.sendMessage("* "+settings[i]+": "+getStateString(config.getValue(sender, i)));
		}
	}

	private void checkCallDogs(CommandSender sender, String[] args, int ArgsLength) {
		Player player = (Player) sender;

		if (!player.hasPermission("protectingwolf.call-wolves")) {
			sender.sendMessage("Access denied");
			return;
		}

		if (args.length != ArgsLength) {
			this.showHelpCall(player);
			return;
		}

		int count = 1000;
		if (!args[(ArgsLength-1)].equalsIgnoreCase("all")) {
			try {
				count = Integer.parseInt(args[(ArgsLength-1)]);
			}
			catch (Exception e) {}
		}
		ProtectingWolfLibrary.callWolves((Player)sender, count);
	}

	private void changeSetting(Player sender, String key, int value) {
		ProtectingWolfConfig config = ProtectingWolfConfig.getInstance();
		String settings[] = config.getSettingNames();
		if (ProtectingWolfLibrary.inArray(settings, key)) {
			if (!sender.hasPermission("protectingwolf.option."+key)) {
				sender.sendMessage("Access denied");
				return;
			}
			config.setValue(sender, key, value);
			config.saveConfig(sender);
			if (config.isSettingBoolean(key)) {
				if (value == 0) {
					sender.sendMessage("Setting "+key+" is now disabled.");
				}
				else {
					sender.sendMessage("Setting "+key+" is now enabled.");
				}
			}
			else {
				sender.sendMessage("Setting "+key+" is now '"+value+"'.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "Setting "+key+" is unknown.");
			sender.sendMessage(ChatColor.RED + "Use /pwolf help to see all settings.");
		}
	}

	private String getStateString(int value) {
		if (value == 1) {
			return "enabled";
		}
		else if (value <= 0) {
			return "disabled";
		}
		else {
			return ""+value;
		}
	}

	private int getValidIntegerValue(String value) {
		try {
			return (int)Integer.parseInt(value);
		}
		catch (Exception e) {
			return -1;
		}
	}

}
