package tk.hugo4715.golema.santabox.box;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.md_5.bungee.api.ChatColor;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.anim.BoxOpenAnimation;

public class Box implements ConfigurationSerializable {
	
	public static Set<UUID> opening = Sets.newHashSet();
	
	private Location boxLocation;
	
	//used to indicate if the box is used by a player
	private AtomicBoolean used = new AtomicBoolean(false);
	
	public Box(Location center) {
		boxLocation = center;
	}
	
	public Box(Map<?,?> s){
		Validate.isTrue(s.containsKey("center"));
		boxLocation = Location.deserialize((Map<String, Object>) s.get("center"));
	}
	
	public Location getBoxLocation() {
		return boxLocation;
	}
	
	public void open(Player p){
		
		try {
			if(BoxPlugin.get().getDatabaseManager().getPlayerKeys(p) < 1){
				p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Vous n'avez pas de clé pour ouvrir une SantaBox, revenez plus tard.");
				return;
			}
			
			if(opening.contains(p.getUniqueId())){
				p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Vous etes déja en train d'ouvrir une box.");
				return;
			}
			
			if(!isUsed().compareAndSet(false, true)){
				p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Cette box est en cours d'utilisation.");
				return;
			}
			
			opening.add(p.getUniqueId());
			
			p.sendMessage(BoxPlugin.PREFIX + "Vous ouvrez maintenant une box.");

			BoxOpenAnimation.openBox(p, this);
		} catch (SQLException e) {
			e.printStackTrace();
			p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Une erreur s'est produite. Merci de contacter un administrateur pour régler le probleme.");
		}
	}
	
	public Map<String, Object> serialize() {
		Map<String,Object> map = Maps.newHashMap();
		map.put("center", boxLocation.serialize());
		return map;
	}
	
	public AtomicBoolean isUsed() {
		return used;
	}
	
}
