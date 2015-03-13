package us.frin.buxgift;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import us.frin.buxgift.commands.GiftCommands;

public class BuxGift extends JavaPlugin {
	
	public Connection con;
	public Permission permission = null;
	
	public void initDatabase() {
		PreparedStatement stmt = null;
		ResultSet res = null;

		try {
			stmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS `buxgifts` ("
					+"`buxgiftid` int(10) unsigned NOT NULL AUTO_INCREMENT,"
					+"`owner_uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,"
					+"`creator_uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,"
					+"`uses` bigint(20) unsigned NOT NULL DEFAULT '0',"
					+"`world` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'world',"
					+"`x` int(10) NOT NULL,"
					+"`y` int(10) NOT NULL,"
					+"`z` int(10) NOT NULL,"
					+"`amount` int(10) unsigned NOT NULL DEFAULT '1',"
					+"`duration` int(10) unsigned NOT NULL DEFAULT '1',"
					+"`duration_type` enum('minute','hour','day') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'day',"
					+"`created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
					+"`updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
					+"PRIMARY KEY (`buxgiftid`)"
					+") ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1");
			stmt.executeUpdate();
			if (stmt != null) stmt.close();
			stmt = this.con.prepareStatement("CREATE TABLE IF NOT EXISTS `buxgiftreceipts` ("
					+"`buxgiftreceipts` bigint(20) unsigned NOT NULL AUTO_INCREMENT,"
					+"`buxgiftid` int(10) unsigned NOT NULL,"
					+"`uuid` varchar(36) COLLATE utf8_unicode_ci NOT NULL,"
					+"`amount` int(10) unsigned NOT NULL DEFAULT '1',"
					+"`created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
					+"PRIMARY KEY (`buxgiftreceipts`)"
					+") ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1");
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
		return;
	}
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			String user = getConfig().getString("user");
			String pass = getConfig().getString("pass");
			String name = getConfig().getString("name");
			String port = getConfig().getString("port");
			String address = getConfig().getString("address");
			con = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + name, user, pass);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		// Attempt MySQL connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		connect();
		this.initDatabase();
		
		GiftCommands cmds = new GiftCommands(this);
		cmds.purgeOldLogs();
		
		// Setup connections to other plugins
		if (!setupPermissions()) {
			getLogger().severe("Disabling BuxGift - No Permissions Plugin Found");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		// Initialize listener
		getServer().getPluginManager().registerEvents(new BuxGiftPlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new BuxGiftBlockListener(this), this);
		
		// Initialize command handler
		BuxGiftCommandExecutor executor = new BuxGiftCommandExecutor(this);
//		this.getCommand("friends").setExecutor(new BuxFriendsCommandExecutor(this));
		this.getCommand("gift").setExecutor(executor);

		getLogger().info("Plugin BuxGift loaded successfully");
	}
	
	// Vault code
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
    }
	
	@Override
	public void onDisable() {
		// Clean up MySQL connection
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSign(Block theBlock) {
		if (theBlock.getType() == Material.SIGN || theBlock.getType() == Material.SIGN_POST || theBlock.getType() == Material.WALL_SIGN) {
			return true;
        }
		else {
			return false;
		}
    }
}
