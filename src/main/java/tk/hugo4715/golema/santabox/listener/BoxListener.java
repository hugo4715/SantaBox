package tk.hugo4715.golema.santabox.listener;

import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
		Optional<Box> clickedBox = BoxPlugin.get().getBoxManager().getclickedBox(e);
		
		if(clickedBox.isPresent()){
			e.setCancelled(true);
			
			//antispam
			if(antiSpam.containsKey(e.getPlayer()) && antiSpam.get(e.getPlayer()) + minDelay > System.currentTimeMillis())return;
			antiSpam.put(e.getPlayer(), System.currentTimeMillis());
			
			new BoxChooserGui(e.getPlayer(),clickedBox.get());
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e){
		if(e.getRightClicked().hasMetadata("stop-taking-my-items"))e.setCancelled(true);
	}
	
}