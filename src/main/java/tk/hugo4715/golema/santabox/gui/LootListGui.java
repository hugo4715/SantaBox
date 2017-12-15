package tk.hugo4715.golema.santabox.gui;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.golema.database.support.builder.items.ItemBuilder;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.database.DatabaseManager.Prize;
import tk.hugo4715.golema.santabox.util.AbstractGui;

public class LootListGui extends AbstractGui {
	
	
	private List<ItemStack> prizes; 
	
	
	public LootListGui(Player player) {
		super(BoxPlugin.get(), player, "Liste des lots", 3*9, 10);
		
		try {
			List<Prize> p = BoxPlugin.get().getDatabaseManager().getPrizes();
			
			for(Prize prize : p){
				ItemStack is = new ItemBuilder()
						.type(prize.getRarity().getMaterial())
						.name(prize.getRarity().getColor() + prize.getRarity().getFrench())
						.lore(ChatColor.GRAY + "Marque: " + ChatColor.GOLD + prize.getMarque())
						.build();
				prizes.add(is);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Une erreur est survenue.");
			stop();
			return;
		}
	}
	
	
	@Override
	public void update() {
		super.update();
		
		for (int i = 0; i < prizes.size(); i++) {
			inv.setItem(i, prizes.get(i));
		}
	}
	
}
