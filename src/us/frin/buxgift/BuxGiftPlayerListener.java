package us.frin.buxgift;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import us.frin.buxgift.commands.GiftCommands;

public class BuxGiftPlayerListener implements Listener {
	BuxGift plugin;
	
	public BuxGiftPlayerListener(BuxGift plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onPlayerJoin(PlayerJoinEvent evt) {
//		Player player = evt.getPlayer(); // The player who joined
//		GiftCommands cmds = new GiftCommands(this.plugin);
	
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onPlayerQuit(PlayerQuitEvent evt) {
//		Player player = evt.getPlayer(); // The player who quit
//		GiftCommands cmds = new GiftCommands(this.plugin);
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission("buxgift.use")) {
			if (event.getClickedBlock() != null && plugin.isSign(event.getClickedBlock())) {
				Sign theSign = (Sign)event.getClickedBlock().getState();
				if (theSign.getLine(0).equals(ChatColor.GREEN + "[GIFT]")) {
					String samount = theSign.getLine(1);
					int amount = 0;
					try {
						amount = Integer.parseInt(samount.trim());
					}
					catch (NumberFormatException e) {
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid amount.");
						return;
					}
					String speriod = theSign.getLine(2);
					if (speriod.length() >= 7 && speriod.substring(0, 6).equalsIgnoreCase("every ")) {
						speriod = speriod.substring(6);
					}
					int period = 0;
					String periodType = "";
					if (speriod.indexOf(' ') == -1) {
						// We don't have spacing
						if (!speriod.toLowerCase().matches("[0-9]+[mhd][a-z]*")) {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid repeat period.");
							return;
						}
						String[] tmp = speriod.toLowerCase().split("(?=[mhd])");
						if (tmp.length != 2 || tmp[1].length() < 1) {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid repeat period.");
							return;
						}
						try {
							period = Integer.parseInt(tmp[0].trim());
						}
						catch (NumberFormatException e) {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid repeat period.");
							return;
						}
						periodType = tmp[1].substring(0, 1).toLowerCase();
					}
					else {
						String[] tmp = speriod.split(" ");
						if (tmp.length != 2 || tmp[1].length() < 1) {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid repeat period.");
							return;
						}
						try {
							period = Integer.parseInt(tmp[0].trim());
						}
						catch (NumberFormatException e) {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid repeat period.");
							return;
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
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Sign has invalid period format.");
						return;
					}
					GiftCommands cmds = new GiftCommands(plugin);
					int already = cmds.getAmountAlreadyGiven(player, event.getClickedBlock(), period, interval);
					
					if (already == -1) {
						Block block = event.getClickedBlock();
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Error while retrieving data from database.");
						plugin.getLogger().warning("Player "+player.getName()+" tried to use BuxGift sign in "+block.getWorld().getName()+" at "+block.getX()+", "+block.getY()+", "+block.getZ()+", something went wrong while fetching data from MySQL database.");
						return;
					}
					if (already >= amount) {
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "You already received your quota for this gift sign, please try again later.");
						return;
					}
					
					String more = "";
					if (amount - already - 1 > 0) {
						more = " You can receive "+(amount - already - 1)+" more.";
					}
					
					org.bukkit.material.Sign sign = (org.bukkit.material.Sign) theSign.getData();
					Block block = event.getClickedBlock().getRelative(sign.getAttachedFace());
			        int sum = 0;
					try {
						if (block.getType() == Material.DROPPER) {
							Dropper dropper = (Dropper)block.getState();
							for (ItemStack stack : ((InventoryHolder) dropper).getInventory()) {
								if (stack == null) continue;
								sum += stack.getAmount();
							}
						}
						else {
							player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Cannot find a dropper with this sign.");
							return;
						}
					}
					catch (Exception e) {
						System.out.println("[BuxGift] Error: "+e.toString());
					}

					if (sum < 1) {
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "This gift sign doesn't contain enough gifts.");
						return;
					}
					
					boolean result = cmds.saveGive(player, event.getClickedBlock());
					if (!result) {
						Block blockx = event.getClickedBlock();
						player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.RED + "Error while storing data to database.");
						plugin.getLogger().warning("Player "+player.getName()+" tried to use BuxGift sign in "+blockx.getWorld().getName()+" at "+blockx.getX()+", "+blockx.getY()+", "+blockx.getZ()+", something went wrong while storing data to MySQL database (sign usage).");
						return;
					}
					
					try {
						if (block.getType() == Material.DROPPER) {
							Dropper dropper = (Dropper)block.getState();
							dropper.drop();
						}
					}
					catch (Exception e) {
						System.out.println("[BuxGift] Error: "+e.toString());
					}
					
					player.sendMessage(ChatColor.DARK_PURPLE + "[BuxGift] " + ChatColor.GREEN + "You received your gift."+more);
				}
			}
		}
	}
	
}
