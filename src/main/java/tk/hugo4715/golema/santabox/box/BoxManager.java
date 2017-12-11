package tk.hugo4715.golema.santabox.box;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Sets;

import tk.hugo4715.golema.santabox.BoxPlugin;


public class BoxManager {
	private Set<Box> boxes = Sets.newHashSet();
	
	public BoxManager() {
		if(BoxPlugin.get().getConfig().contains("boxes")){
			BoxPlugin.get().getConfig().getMapList("boxes").forEach(map -> boxes.add(new Box(map)));
		}
		BoxPlugin.get().getLogger().info("Loaded " + boxes.size() + " boxes");
	}
	
	public void saveBoxes(){
		BoxPlugin.get().getConfig().set("boxes", boxes.stream().map(Box::serialize).collect(Collectors.toList()));
		BoxPlugin.get().saveConfig();
		BoxPlugin.get().getLogger().info("Saved boxes");
	}
	
	public void addBox(Box box){
		boxes.add(box);
		saveBoxes();
	}
	
	public void removeBox(Box box){
		boxes.remove(box);
		saveBoxes();
	}
	
	public Set<Box> getBoxes(){
		return Collections.unmodifiableSet(boxes);
	}
	
	public Optional<Box> getclickedBox(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.ENDER_CHEST)){
			for(Box box : getBoxes()){
				if(box.getBoxLocation().equals(e.getClickedBlock().getLocation())){
					return Optional.of(box);
				}
			}
		}
		return Optional.empty();
	}
	
}
