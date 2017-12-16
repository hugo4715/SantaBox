package tk.hugo4715.golema.santabox;

import java.sql.SQLException;
import java.util.Set;

import javax.swing.BoxLayout;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Sets;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import tk.hugo4715.golema.santabox.box.Box;
import tk.hugo4715.golema.santabox.box.BoxManager;
import tk.hugo4715.golema.santabox.cmd.SantaCMD;
import tk.hugo4715.golema.santabox.database.DatabaseManager;
import tk.hugo4715.golema.santabox.holo.BoxHoloUpdater;
import tk.hugo4715.golema.santabox.listener.BoxListener;
import tk.hugo4715.golema.santabox.util.EntityRegistry;

public class BoxPlugin extends JavaPlugin {
	public static final String PREFIX = ChatColor.AQUA + "" + ChatColor.BOLD + "SantaBox" + ChatColor.WHITE + " │ " + ChatColor.YELLOW;
	public static boolean DEV;
	
	@Getter
	private BoxManager boxManager;
	@Getter
	private DatabaseManager databaseManager;
	private BoxHoloUpdater holo;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		DEV = getConfig().getBoolean("dev", false);
		
		boxManager = new BoxManager();
		try {
			databaseManager = new DatabaseManager();
		} catch (SQLException e) {
			e.printStackTrace();
			getLogger().severe("Could not create tables! Disabling plugin...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}	
		
		Bukkit.getPluginManager().registerEvents(new BoxListener(), this);
		holo = new BoxHoloUpdater();
		holo.runTaskTimer(this, 20, 5);
		
		
		getCommand("santabox").setExecutor(new SantaCMD());
	}
	
	@Override
	public void onDisable() {
		EntityRegistry.kill();
		holo.cancel();
		holo.clear();
	}

	public static BoxPlugin get() {
		return getPlugin(BoxPlugin.class);
	}
}
