package us.frin.buxgift;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;

import us.frin.buxgift.commands.GiftCommands;

public class BuxGiftBlockListener implements Listener {
	BuxGift plugin;
	
	public BuxGiftBlockListener(BuxGift plugin) {
		this.plugin = plugin;
	}
	
	public GiftRecord checkValidity(SignChangeEvent event) {
		GiftRecord record = new GiftRecord(0, 0, "");
		
		String samount = event.getLine(1);
		int amount = 0;
		try {
			amount = Integer.parseInt(samount.trim());
		}
		catch (NumberFormatException e) {
			return record;
		}
		String speriod = event.getLine(2);
		if (speriod.length() >= 7 && speriod.substring(0, 6).equalsIgnoreCase("every ")) {
			speriod = speriod.substring(6);
		}
		int period = 0;
		String periodType = "";
		if (speriod.indexOf(' ') == -1) {
			// We don't have spacing
			if (!speriod.toLowerCase().matches("[0-9]+[mhd][a-z]*")) {
				return record;
			}
			String[] tmp = speriod.toLowerCase().split("(?=[mhd])");
			if (tmp.length != 2 || tmp[1].length() < 1) {
				return record;
			}
			try {
				period = Integer.parseInt(tmp[0].trim());
			}
			catch (NumberFormatException e) {
				return record;
			}
			periodType = tmp[1].substring(0, 1).toLowerCase();
		}
		else {
			String[] tmp = speriod.split(" ");
			if (tmp.length != 2 || tmp[1].length() < 1) {
				return record;
			}
			try {
				period = Integer.parseInt(tmp[0].trim());
			}
			catch (NumberFormatException e) {
				return record;
			}
			periodType = tmp[1].substring(0, 1).toLowerCase();
		}
		String interval = "DAY";
		if (periodType.equals("d")) {
			interval = "DAY";
		}
		else if (periodType.equals("h")) {
			interval = "HOUR";
		}
		else if (periodType.equals("m")) {
			interval = "MINUTE";
		}
		else {
			return record;
		}
		if (period < 1) return record;
		if (amount < 1) return record;
		record.amount = amount;
		record.duration = period;
		record.duration_type = interval.toLowerCase();
		record.isValid = true;
		return record;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.plugin.isSign(event.getBlock()) && ((Sign)event.getBlock().getState()).getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[GIFT]")) {
			GiftCommands cmds = new GiftCommands(plugin);
			if (cmds.ownedBy(event.getPlayer(), event.getBlock()) || event.getPlayer().hasPermission("buxgift.admin")) {
				// Delete
				cmds.removeSign(event.getBlock());
				event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.GREEN + "Gift sign has been removed.");
			}
			else {
				event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "You don't own this gift sign.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void signDetachCheck(BlockPhysicsEvent event) {
		Block b = event.getBlock();
		if (plugin.isSign(b) && ((Sign)event.getBlock().getState()).getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[GIFT]")) {
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
			Block attachedBlock = b.getRelative(sign.getAttachedFace());
			if (attachedBlock.getType() == Material.AIR) {  // or maybe any non-solid material, but AIR is the normal case
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[GIFT]") && event.getPlayer().hasPermission("buxgift.create")) {
			org.bukkit.material.Sign sign = (org.bukkit.material.Sign) event.getBlock().getState().getData();
			Block block = event.getBlock().getRelative(sign.getAttachedFace());
			if (block.getType() == Material.DROPPER) {
				GiftRecord record = this.checkValidity(event);
				if (record.isValid) {
					GiftCommands cmds = new GiftCommands(plugin);
					cmds.createSign(event.getPlayer(), event.getBlock(), record, event.getLine(3));
					event.setLine(0, ChatColor.GREEN + "[GIFT]");
					event.setLine(1, ""+record.amount);
					event.setLine(2, record.duration+" "+record.duration_type+(record.duration>1?"s":""));
					event.setLine(3, ChatColor.GREEN + "Right click");
				}
				else {
					event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Values on signs are not valid, see /gift for help.");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
				}
			}
			else {
				event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "You need to place sign on the dropper (crouch to place a sign).");
				event.setCancelled(true);
				event.getBlock().breakNaturally();
			}
		}
		else if(event.getLine(0).equals(ChatColor.GREEN + "[GIFT]")) {
			event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "To make a gift sign, don't use colors.");
			event.setCancelled(true);
			event.getBlock().breakNaturally();
		}
	}
}
