package tk.hugo4715.golema.santabox.util;

import org.bukkit.ChatColor;

public enum Rarity {
	COMMON("Commun",ChatColor.GREEN),
	EPIC("Epic",ChatColor.LIGHT_PURPLE),
	LEGENDARY("Legendaire",ChatColor.GOLD),
	;
	
	private String french;
	private ChatColor color;
	
	private Rarity(String french, ChatColor color) {
		this.french = french;
		this.color = color;
	}
	
	public String getFrench() {
		return french;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getTag(){
		return getColor() + "[" + getFrench() + "] ";
	}
	
	public static Rarity fromName(String name) {
		for(Rarity r : values()) {
			if(r.getFrench().equalsIgnoreCase(name)) {
				return r;
			}
		}
		return null;
	}
}
