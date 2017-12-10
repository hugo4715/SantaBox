package tk.hugo4715.golema.santabox.listener;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.Maps;

import tk.hugo4715.golema.santabox.BoxChooserGui;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.box.Box;

public class BoxListener implements Listener {
	private Map<Player,Long> antiSpam = Maps.newHashMap();
	private long minDelay = 1000;
	
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		
		
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.ENDER_CHEST)){
			for(Box box : BoxPlugin.get().getBoxes()){
				if(box.getBoxLocation().equals(e.getClickedBlock().getLocation())){
					e.setCancelled(true);
					
					//antispam
					if(antiSpam.containsKey(e.getPlayer()) && antiSpam.get(e.getPlayer()) + minDelay > System.currentTimeMillis())return;
					antiSpam.put(e.getPlayer(), System.currentTimeMillis());
					
					new BoxChooserGui(e.getPlayer(),box);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		if(e.getRightClicked().hasMetadata("stop-taking-my-items"))e.setCancelled(true);
	}
	
}