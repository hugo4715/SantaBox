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
import tk.hugo4715.golema.santabox.listener.BoxListener;
import tk.hugo4715.golema.santabox.prize.DatabaseManager;
import tk.hugo4715.golema.santabox.util.EntityRegistry;

public class BoxPlugin extends JavaPlugin {
	public static final String PREFIX = ChatColor.WHITE + "[" + ChatColor.GOLD + "SantaBox" + ChatColor.WHITE + "] " + ChatColor.GREEN;
	
	@Getter
	private BoxManager boxManager;
	
	@Getter
	private DatabaseManager databaseManager;

	@Getter
	private Set<Box> boxes = Sets.newHashSet();

	@Override
	public void onEnable() {
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
	}
	
	@Override
	public void onDisable() {
		EntityRegistry.kill();
	}

	public static BoxPlugin get() {
		return getPlugin(BoxPlugin.class);
	}
}
