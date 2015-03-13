package us.frin.buxgift;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuxGiftCommandExecutor implements CommandExecutor {
	BuxGift plugin;
	
	public BuxGiftCommandExecutor(BuxGift plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gift")) {
			if (args.length == 0) {
				//////////////////////////////////
				// Gift command
				//////////////////////////////////
				if (!(sender instanceof Player)) {
					sender.sendMessage("This command can only be run by a player.");
				}
				else {
					Player player = (Player) sender;
					
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"The Buxville Gift Plugin");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"/gift: this help page");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"Place a dropper then place a sign on the dropper while crouching (so that you don't keep opening dropper contents).");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"First line write [GIFT]");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"Second line write number of items (for example 1), this will be the amount limit");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"Third line write time span (for example: every 1 day), can be 'every 1 day', '1h', '1 minute'. You can use words day, hour, minute; or shorter d, h, m");
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] "+ChatColor.WHITE+"Fourth line can be used to write new owner's name, this user has to be online");
				}
				
				return true;
			}
			return true;
		}
		return false;
	}
	
}
