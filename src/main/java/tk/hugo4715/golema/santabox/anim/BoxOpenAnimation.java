package tk.hugo4715.golema.santabox.anim;

import java.sql.SQLException;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.golema.database.GolemaBukkitDatabase;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.redis.spigot.RedisPubSubSpigot;
import net.golema.database.support.builder.items.heads.CustomSkull;
import net.golema.database.support.particle.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntityEnderChest;
import net.minecraft.server.v1_8_R3.World;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.box.Box;
import tk.hugo4715.golema.santabox.prize.DatabaseManager.Prize;
import tk.hugo4715.golema.santabox.util.EntityRegistry;
import tk.hugo4715.golema.santabox.util.HologramBuilder;

public class BoxOpenAnimation {
	
    public static void openBox(Player p, Box box) throws SQLException {
    	Prize reward = null;
    	GolemaPlayer gp = GolemaPlayer.getGolemaPlayer(p);
    	
    	try {
			BoxPlugin.get().getDatabaseManager().addPlayerKeys(p.getUniqueId(), -1);
			reward = BoxPlugin.get().getDatabaseManager().choosePrize();
			BoxPlugin.get().getDatabaseManager().usePrize(reward);
			BoxPlugin.get().getDatabaseManager().winPrize(p, reward);
		} catch (SQLException e) {
			e.printStackTrace();
			p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Une erreur s'est produite. Merci de contacter un administrateur pour r�gler le probleme.");
		}
    	
    	reward.give(gp);
    	
    	new BukkitRunnable() {
			int ticks = 0;
			Prize reward;
			HologramBuilder holo;
			//orb:
			//http://textures.minecraft.net/texture/e6799bfaa3a2c63ad85dd378e66d57d9a97a3f86d0d9f683c498632f4f5c
			//present:
			//http://textures.minecraft.net/texture/b5651a18f54714b0b8f7f011c018373b33fd1541ca6f1cfe7a6c97b65241f5
			ItemStack head = CustomSkull.getPlayerSkull("http://textures.minecraft.net/texture/b5651a18f54714b0b8f7f011c018373b33fd1541ca6f1cfe7a6c97b65241f5");
			ArmorStand stand;
			
			
			float pitch = 0;
			@Override
			public void run() {
				ticks++;
				
				if(!p.isOnline()){
					playChestAction(box.getBoxLocation(), false);
					box.opening.remove(p.getUniqueId());
					box.isUsed().set(false);
					reward.give(gp);
					cancel();
					return;
				}
				
				if(stand != null){
					float speed = (float) Math.sqrt(ticks) * 0.1f;
					stand.setHeadPose(stand.getHeadPose().add(0, speed, 0));;
					ParticleEffect.FIREWORKS_SPARK.display(0, 0,0, 0.1f, 1, stand.getEyeLocation(), box.getBoxLocation().getWorld().getPlayers());
				}
				
				if(ticks < 60 && ticks % 5 == 0){
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, pitch);
						pitch += 0.1;
				}
				
				
				
				
				//one time events
				if(ticks == 1){
					//spawn all armorstands
					stand = box.getBoxLocation().getWorld().spawn(box.getBoxLocation().clone().add(0.5, 0, 0.5), ArmorStand.class);
					stand.setGravity(false);
					stand.setVisible(false);
					stand.setHelmet(head);
					stand.setMetadata("stop-taking-my-items", new FixedMetadataValue(BoxPlugin.get(), true));
					EntityRegistry.add(stand);
				
				}else if(ticks == 60){
					//show reward here
					
					holo = new HologramBuilder();
					holo.editLocation(box.getBoxLocation().clone().add(0.5, -1, 0.5));
					holo.editMessage(reward.getRarity().getColor() + reward.getRarity().getFrench());
					holo.sendToPlayers(box.getBoxLocation().getWorld().getPlayers());
					
					reward.give(gp);
					Rank rank = getRealRank(gp);
					
					switch(reward.getRarity()){
					case COMMON:
						p.sendMessage(BoxPlugin.PREFIX + "Vous venez d'obtenir " + reward.getRarity().getTag() + WordUtils.capitalizeFully(reward.getName()) + ChatColor.GREEN + ".");
						break;
					case EPIC:
						Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1));
						GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastMessage(BoxPlugin.PREFIX + rank.getPrefix() + " " + gp.getPlayerRealName() +  ChatColor.YELLOW + " viens d'obtenir " + reward.getRarity().getTag() + " " + reward.getName());
						break;
					case LEGENDARY:
						Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1));
						GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastMessage(" ");
						GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastMessage(BoxPlugin.PREFIX + rank.getPrefix() + " " + gp.getPlayerRealName() +  ChatColor.YELLOW + " viens d'obtenir " + reward.getRarity().getTag() + " " + reward.getName());
						GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastMessage(" ");
						break;
					}
					
					if(!reward.isAutomatic()){
						p.sendMessage(BoxPlugin.PREFIX + "Vous pouvez des a present nous contacter en message privé sur twitter " + ChatColor.AQUA + " @GolemaMC" + ChatColor.YELLOW + " pour reclamer votre prix!");
					}
					
					//open chest
					playChestAction(box.getBoxLocation(), true);
					
					stand.remove();
				}else if(ticks > 100){
					holo.destroyAllPlayers();
					
					playChestAction(box.getBoxLocation(), false);

					box.opening.remove(p.getUniqueId());
					box.isUsed().set(false);
					
					cancel();
				}
			}
		}.runTaskTimer(BoxPlugin.get(), 1, 1);	
	}

	private static void playChestAction(Location location, boolean open) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
        world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
    }
	
	private static Rank getRealRank(GolemaPlayer gp){
		for(Rank r : Rank.values()){
			if(r.getPower() == gp.getRankPower())return r;
		}
		return Rank.PLAYER;
	}
}
