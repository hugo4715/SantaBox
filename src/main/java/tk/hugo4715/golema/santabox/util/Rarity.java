package tk.hugo4715.golema.santabox.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Rarity {
	COMMON("Commun",ChatColor.GREEN,Material.CLAY_BRICK),
	EPIC("Epic",ChatColor.LIGHT_PURPLE,Material.GOLD_INGOT),
	LEGENDARY("Legendaire",ChatColor.GOLD,Material.DIAMOND),
	;
	
	private String french;
	private ChatColor color;
	private Material mat;
	
	private Rarity(String french, ChatColor color,Material mat) {
		this.french = french;
		this.color = color;
		this.mat = mat;
	}
	
	public String getFrench() {
		return french;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public Material getMaterial() {
		return mat;
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
