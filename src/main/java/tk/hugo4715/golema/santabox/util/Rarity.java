package tk.hugo4715.golema.santabox.util;

import org.bukkit.ChatColor;

public enum Rarity {
	COMMON("Commun",ChatColor.GREEN,1000),
	EPIC("Epic",ChatColor.LIGHT_PURPLE,250),
	LEGENDARY("Legendary",ChatColor.GOLD,100),
	;
	
	private String french;
	private String english;
	private ChatColor color;
	private double weight;
	
	private Rarity(String french, ChatColor color, double weight) {
		this.french = french;
		this.color = color;
		this.weight = weight;
	}
	
	public String getFrench() {
		return french;
	}
	
	public ChatColor getColor() {
		return color;
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
