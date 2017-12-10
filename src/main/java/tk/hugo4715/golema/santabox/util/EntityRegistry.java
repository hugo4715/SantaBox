package tk.hugo4715.golema.santabox.util;

import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.entity.Entity;

import com.google.common.collect.Sets;

public class EntityRegistry {
	private static Set<Entity> registry = Sets.newSetFromMap(new WeakHashMap<>());
	
	public static void add(Entity e){
		registry.add(e);
	}
	
	public static void kill(){
		registry.forEach(e -> e.remove());
		registry.clear();
	}
}
