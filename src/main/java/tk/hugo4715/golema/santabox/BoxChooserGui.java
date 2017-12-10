package tk.hugo4715.golema.santabox;

import org.bukkit.entity.Player;

import tk.hugo4715.golema.santabox.box.Box;
import tk.hugo4715.golema.santabox.util.AbstractGui;

public class BoxChooserGui extends AbstractGui {

	private Box box;
	
	public BoxChooserGui(Player player,Box box) {
		super(BoxPlugin.get(), player, "SantaBox", 3*9, 10);
		this.box = box;
	}
	
	
	@Override
	public void update() {
		super.update();
		
		buttons.clear();
		
		buttons.put(10, slot -> {
			//lots
		});
		
		buttons.put(13, slot -> {
			//ouvrir
		});
		
		buttons.put(16, slot -> {
			
		});
		
		
	}
	

}
