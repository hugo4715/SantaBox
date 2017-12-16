package tk.hugo4715.golema.santabox.holo;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.box.Box;
import tk.hugo4715.golema.santabox.util.HologramBuilder;

public class BoxHoloUpdater extends BukkitRunnable {

	private Map<Player,List<HologramBuilder>> holos = Maps.newHashMap();
	
	private Map<Player,Map<Box,List<HologramBuilder>>> holo = Maps.newHashMap();
	
	
	public void clear() {
		for(Map<Box,List<HologramBuilder>> a : holo.values()) {
			for(List<HologramBuilder> b : a.values()) {
				b.forEach(HologramBuilder::destroy);
			}
		}
	}
	
	
	@Override
	public void run() {
		for(Player p : Bukkit.getOnlinePlayers()){
			for(Box box : BoxPlugin.get().getBoxManager().getBoxes()){
				
				if(!holo.containsKey(p)) {
					holo.put(p, Maps.newHashMap());
				}
				
				Map<Box,List<HologramBuilder>> a = holo.get(p);
				
				if(a.containsKey(box)) {
					if(p.getLocation().distanceSquared(box.getBoxLocation()) > 16*16 || box.isUsed().get()){
						a.get(box).forEach(HologramBuilder::destroy);
						a.remove(box);
					}
				}else {
					if(p.getLocation().distanceSquared(box.getBoxLocation()) < 16*16 && !box.isUsed().get()){
						List<HologramBuilder> holograms = Lists.newArrayList();
						HologramBuilder b1 = new HologramBuilder();
						b1.editLocation(box.getBoxLocation().clone().add(0.5, -0.75, 0.5));
						b1.editMessage(ChatColor.GOLD + "SantaBox");
						b1.sendToPlayer(p);
						holograms.add(b1);
						
						System.out.println("created");
						a.put(box, holograms);
					}
				}
				
				
//				if(box.getBoxLocation().getWorld().equals(p.getWorld())){
//					
//					if(holos.containsKey(p)){
//						//player already have his hologram, check if we need to remove it
//						if(p.getLocation().distanceSquared(box.getBoxLocation()) > 16*16 || box.isUsed().get()){
//							holos.get(p).forEach(HologramBuilder::destroy);
//							holos.remove(p);
//							System.out.println("removed");
//						}
//					}else{
//						if(p.getLocation().distanceSquared(box.getBoxLocation()) < 16*16 && !box.isUsed().get()){
//							List<HologramBuilder> holograms = Lists.newArrayList();
//							HologramBuilder b1 = new HologramBuilder();
//							b1.editLocation(box.getBoxLocation().clone().add(0.5, -0.75, 0.5));
//							b1.editMessage(ChatColor.GOLD + "SantaBox");
//							b1.sendToPlayer(p);
//							holograms.add(b1);
//							
//							System.out.println("created");
//							holos.put(p, holograms);
//						}
//					}
//				}
			}
		}
	}
}