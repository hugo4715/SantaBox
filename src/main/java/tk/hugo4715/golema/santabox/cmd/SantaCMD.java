package tk.hugo4715.golema.santabox.cmd;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.golema.database.GolemaBukkitDatabase;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.UUIDFetcher;
import net.golema.database.golemaplayer.rank.Rank;
import net.md_5.bungee.api.ChatColor;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.box.Box;

public class SantaCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		boolean isPlayer = sender instanceof Player;


		if(args.length >= 3 && args[0].equalsIgnoreCase("give") && (!isPlayer || (BoxPlugin.DEV || GolemaPlayer.getGolemaPlayer((Player)sender).getRankPower() >= Rank.ADMINISTRATOR.getPower()))){
			// /santabox give hugo4715 5

			UUID target = UUIDFetcher.getUUID(args[1]);
			int amount = Integer.parseInt(args[2]);

			if(target == null){
				sender.sendMessage((isPlayer ? ChatColor.GREEN + "" : "" ) + "Erreur Santabox: Le joueur " + args[1] + " n'existe pas! (Achat de " + amount +" clé)");
				GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastStaff(ChatColor.RED + "Erreur Santabox: Le joueur " + args[1] + " n'existe pas! (Achat de " + amount +" clé)");
				return true;
			}

			try {
				BoxPlugin.get().getDatabaseManager().addPlayerKeys(target, amount);
				sender.sendMessage((isPlayer ? ChatColor.GREEN + "" : "" ) + "Le joueur " + args[1] + " viens de recevoir " + amount + " clés");
			} catch (SQLException e) {
				e.printStackTrace();
				BoxPlugin.get().getLogger().severe("Le joueur " + args[1] + " n'existe pas! (Achat de " + amount +" clé)");
				GolemaBukkitDatabase.INSTANCE.redisPubSubSpigot.broadcastStaff(ChatColor.RED + "Erreur Santabox: Le joueur " + args[1] + " n'existe pas! (Achat de " + amount +" clé)");
			}
		}else if(isPlayer && args.length >= 1 && args[0].equals("create")){
			Player p = (Player)sender;
			
			if(!BoxPlugin.DEV && GolemaPlayer.getGolemaPlayer(p).getRankPower() < Rank.DEVELOPER.getPower()){
				GolemaPlayer.getGolemaPlayer(p).sendMessageNoPermission();
				return true;
			}
			
			List<Block> blocks = p.getLineOfSight((Set)null, 16);
			blocks = blocks.stream().filter(block -> block.getType().equals(Material.ENDER_CHEST)).collect(Collectors.toList());

			if(blocks.isEmpty()){
				p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Merci de pointer un enderchest");
				return true;
			}

			Block chest = blocks.get(0);

			//security against strange setup
			for(Box box : BoxPlugin.get().getBoxManager().getBoxes()){
				if(box.getBoxLocation().equals(chest.getLocation())){
					p.sendMessage(BoxPlugin.PREFIX + ChatColor.RED + "Une box existe déja sur ce block.");
					return true;
				}
			}


			BoxPlugin.get().getBoxManager().addBox(new Box(chest.getLocation()));
			BoxPlugin.get().getBoxManager().saveBoxes();
			p.sendMessage(BoxPlugin.PREFIX + "Une SantaBox été créée.");
			return true;
		}
		return false;
	}
}
