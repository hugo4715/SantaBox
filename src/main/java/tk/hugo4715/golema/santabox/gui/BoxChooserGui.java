package tk.hugo4715.golema.santabox.gui;

import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.golema.database.support.builder.items.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.box.Box;
import tk.hugo4715.golema.santabox.util.AbstractGui;

public class BoxChooserGui extends AbstractGui {

	private Box box;
	
	private int available = -1;
	
	public BoxChooserGui(Player player,Box box) {
		super(BoxPlugin.get(), player, "SantaBox", 3*9, 10);
		this.box = box;
		try {
			available = BoxPlugin.get().getDatabaseManager().getPlayerKeys(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Une erreur est survenue.");
			stop();
		}
	}
	
	
	@Override
	public void update() {
		super.update();

		
		buttons.clear();
		
		inv.setItem(12, new ItemBuilder().type(Material.BOOK).name(ChatColor.GREEN + "Liste des lots").build());
		buttons.put(12, slot -> {
			//lots
			
		});
		
		String msg = ChatColor.GOLD +""+ available + " disponible" + (available > 1 ? "s" : ""); 
	    msg = available > -1 ? msg : "";
		inv.setItem(14, new ItemBuilder().type(Material.CHEST).name(ChatColor.GREEN + "Ouvrir une SantaBox").lore(msg).build());
		buttons.put(14, slot -> {
			//ouvrir
			stop();
		});
		
	}
	

}
