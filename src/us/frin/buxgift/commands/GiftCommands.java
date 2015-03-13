package us.frin.buxgift.commands;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import us.frin.buxgift.BuxGift;
import us.frin.buxgift.GiftRecord;

public class GiftCommands {
	BuxGift plugin;
	
	public GiftCommands(BuxGift plugin) {
		this.plugin = plugin;
	}
	
	public void createSign(Player player, Block block, GiftRecord record, String owner) {
		PreparedStatement stmt = null;
		ResultSet res = null;

		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("SELECT * FROM `buxgifts` bg WHERE bg.world = ? AND bg.x = ? AND bg.y = ? AND bg.z = ? LIMIT 0,1");
			stmt.setString(1, block.getWorld().getName());
			stmt.setInt(2, block.getX());
			stmt.setInt(3, block.getY());
			stmt.setInt(4, block.getZ());
			res = stmt.executeQuery();
			
			if (res.first()) {
				int buxgiftid = res.getInt("buxgiftid");
				if (stmt != null) stmt.close();
				
				stmt = this.plugin.con.prepareStatement("DELETE FROM `buxgiftreceipts` WHERE buxgiftid = ?");
				stmt.setInt(1, buxgiftid);
				stmt.executeUpdate();
				if (stmt != null) stmt.close();
				
				stmt = this.plugin.con.prepareStatement("DELETE FROM `buxgifts` WHERE buxgiftid = ?");
				stmt.setInt(1, buxgiftid);
				stmt.executeUpdate();
				if (stmt != null) stmt.close();
			}
			else {
				if (stmt != null) stmt.close();
			}
			stmt = this.plugin.con.prepareStatement("INSERT INTO `buxgifts` (`owner_uuid`, `creator_uuid`, `uses`, `world`, `x`, `y`, `z`, `amount`, `duration`, `duration_type`, `created_at`, `updated_at`) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())");
			
			String ow = player.getUniqueId().toString();
			
			if (owner != null && owner.length() > 0) {
				// Attempt to get player
				@SuppressWarnings("deprecation")
				Player ownerp = Bukkit.getServer().getPlayerExact(owner);
				if (ownerp != null) {
					ow = ownerp.getUniqueId().toString();
				}
			}
			
			stmt.setString(1, ow);
			stmt.setString(2, player.getUniqueId().toString());
			stmt.setString(3, block.getWorld().getName());
			stmt.setInt(4, block.getX());
			stmt.setInt(5, block.getY());
			stmt.setInt(6, block.getZ());
			stmt.setInt(7, record.amount);
			stmt.setInt(8, record.duration);
			stmt.setString(9, record.duration_type);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
	}
	
	public void removeSign(Block block) {
		PreparedStatement stmt = null;
		ResultSet res = null;

		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("SELECT * FROM `buxgifts` bg WHERE bg.world = ? AND bg.x = ? AND bg.y = ? AND bg.z = ? LIMIT 0,1");
			stmt.setString(1, block.getWorld().getName());
			stmt.setInt(2, block.getX());
			stmt.setInt(3, block.getY());
			stmt.setInt(4, block.getZ());
			res = stmt.executeQuery();
			
			if (res.first()) {
				int buxgiftid = res.getInt("buxgiftid");
				if (stmt != null) stmt.close();
				
				stmt = this.plugin.con.prepareStatement("DELETE FROM `buxgiftreceipts` WHERE buxgiftid = ?");
				stmt.setInt(1, buxgiftid);
				stmt.executeUpdate();
				if (stmt != null) stmt.close();
				
				stmt = this.plugin.con.prepareStatement("DELETE FROM `buxgifts` WHERE buxgiftid = ?");
				stmt.setInt(1, buxgiftid);
				stmt.executeUpdate();
				if (stmt != null) stmt.close();
			}
			else {
				if (stmt != null) stmt.close();
			}
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
	}
	
	public int getAmountAlreadyGiven(Player player, Block block, int period, String interval) {
		PreparedStatement stmt = null;
		ResultSet res = null;

		int sum = -1;
		
		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("SELECT SUM(bgr.amount) AS sumamount FROM `buxgifts` bg LEFT JOIN `buxgiftreceipts` bgr USING(`buxgiftid`) WHERE bgr.uuid = ? AND bg.world = ? AND bg.x = ? AND bg.y = ? AND bg.z = ? AND DATE_SUB(NOW(), INTERVAL ? "+interval+") <= bgr.created_at LIMIT 0,1");
			stmt.setString(1, player.getUniqueId().toString());
			stmt.setString(2, block.getWorld().getName());
			stmt.setInt(3, block.getX());
			stmt.setInt(4, block.getY());
			stmt.setInt(5, block.getZ());
			stmt.setInt(6, period);
			res = stmt.executeQuery();
			
			if (res.first()) {
				sum = res.getInt("sumamount");
			}
			if (stmt != null) stmt.close();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
		return sum;
	}
	
	public boolean ownedBy(Player player, Block block) {
		PreparedStatement stmt = null;
		ResultSet res = null;

		boolean result = false;
		
		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("SELECT * FROM `buxgifts` bg WHERE bg.world = ? AND bg.x = ? AND bg.y = ? AND bg.z = ? LIMIT 0,1");
			stmt.setString(1, block.getWorld().getName());
			stmt.setInt(2, block.getX());
			stmt.setInt(3, block.getY());
			stmt.setInt(4, block.getZ());
			res = stmt.executeQuery();
			
			if (res.first()) {
				if (res.getString("owner_uuid").equalsIgnoreCase(player.getUniqueId().toString())
						|| res.getString("creator_uuid").equalsIgnoreCase(player.getUniqueId().toString())) {
					result = true;
				}
			}
			if (stmt != null) stmt.close();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
		return result;
	}
	
	public void purgeOldLogs() {
		PreparedStatement stmt = null;
		ResultSet res = null;

		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("DELETE FROM `buxgiftreceipts` WHERE created_at <= DATE_SUB(NOW(), INTERVAL 1 MONTH)");
			stmt.executeUpdate();
			if (stmt != null) stmt.close();
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
	}
	
	public boolean saveGive(Player player, Block block) {
		PreparedStatement stmt = null;
		ResultSet res = null;

		boolean result = false;
		
		try {
			if (this.plugin.con.isClosed()) {
				this.plugin.getLogger().warning("MySQL Connection was closed, attempting reconnect");
				this.plugin.connect();
			}
			stmt = this.plugin.con.prepareStatement("SELECT bg.* FROM `buxgifts` bg WHERE bg.world = ? AND bg.x = ? AND bg.y = ? AND bg.z = ? LIMIT 0,1");
			stmt.setString(1, block.getWorld().getName());
			stmt.setInt(2, block.getX());
			stmt.setInt(3, block.getY());
			stmt.setInt(4, block.getZ());
			res = stmt.executeQuery();
			
			if (res.first()) {
				int buxgiftid = res.getInt("buxgiftid");
				if (stmt != null) stmt.close();
				
				stmt = this.plugin.con.prepareStatement("INSERT INTO `buxgiftreceipts` (`buxgiftid`, `uuid`, `amount`, `created_at`) VALUES (?, ?, 1, NOW())");
				stmt.setInt(1, buxgiftid);
				stmt.setString(2, player.getUniqueId().toString());
				stmt.executeUpdate();
				result = true;
				if (stmt != null) stmt.close();

				stmt = this.plugin.con.prepareStatement("UPDATE `buxgifts` SET uses = uses + 1, updated_at = NOW() WHERE buxgiftid = ?");
				stmt.setInt(1, buxgiftid);
				stmt.executeUpdate();
				if (stmt != null) stmt.close();
			}
			else {
				if (stmt != null) stmt.close();
			}
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		}
		finally {
			if (res != null) {
				try {
					res.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				res = null;
			}
			
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					// Nothing
				}
				stmt = null;
			}
		}
		return result;
	}
}
