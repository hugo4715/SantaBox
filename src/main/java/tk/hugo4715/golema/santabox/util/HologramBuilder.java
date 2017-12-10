package tk.hugo4715.golema.santabox.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import tk.hugo4715.golema.santabox.BoxPlugin;

public class HologramBuilder {

	private String message;

	private int stay;

	private Location location;

	private EntityArmorStand stand;

	private List<Player> playersList;

	private Player player_;

	public String getMessage() {

		return message;

	}

	public int getStay() {

		return stay;

	}

	public Location getLocation() {

		return location;

	}

	/**
	 * 
	 * Editer le message
	 * 
	 */

	public HologramBuilder editMessage(String message) {

		this.message = message;

		return this;

	}

	/**
	 * 
	 * Editer la location
	 * 
	 */

	public HologramBuilder editLocation(Location location) {

		this.location = location;

		return this;

	}

	/**
	 * 
	 * Editer le stay
	 * 
	 */

	public HologramBuilder editStay(int stay) {

		this.stay = stay;

		return this;

	}

	/**
	 * CONSTRUCTEUR POUR UN HOLOGRAMME AVEC PLAYERS DE COLLECTIONS
	 * 
	 */

	public HologramBuilder() {
	}

	/**
	 * Mettez le message ? afficher sur l'hologramme
	 * 
	 * @param message
	 *            > String
	 * 
	 * @return HologramBuilder
	 * 
	 */

	public HologramBuilder withMessage(String message) {

		this.message = message;

		return this;

	}

	/**
	 * Mettez le @param ? 0 si vous voulez que l'hologramme reste ind?finiment
	 * 
	 * @param stay
	 *            > Integer (Le temps que va rester l'hologramme)
	 * 
	 * @return HologramBuilder
	 * 
	 */

	public HologramBuilder withStay(int stay) {

		this.stay = stay;

		return this;

	}

	/**
	 * Permet de set la location ? laquelle va apparaitre l'hologramme
	 * 
	 * @param location
	 *            > Location
	 * 
	 * @return HologramBuilder
	 * 
	 */

	public HologramBuilder withLocation(Location location) {

		this.location = location;

		return this;

	}

	/**
	 * Permet d'envoyer l'hologramme ? un joueur
	 * 
	 * @param player
	 *            > Player (Le joueur en question)
	 * 
	 */

	public void sendToPlayer(Player player) {

		stand = getArmorStand();

		this.player_ = player;

		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

		/* SI LE TEMPS EST PAS INDETERMINE */

		if (stay != 0) {

			Bukkit.getServer().getScheduler().runTaskLater(BoxPlugin.get(), () -> {

				PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());

				((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);

			}, stay);

		}

	}

	/**
	 * Permet d'envoyer l'hologramme ? une collection de joueurs
	 * 
	 * @param players
	 *            > Collection type Player (Ex: Bukkit.getOnlinePlayers())
	 * 
	 */

	public void sendToPlayers(Collection<? extends Player> players) {

		stand = getArmorStand();

		this.playersList = new ArrayList<Player>();

		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);

		for (Player playerCollection : players) {

			this.playersList.add(playerCollection);

			((CraftPlayer) playerCollection).getHandle().playerConnection.sendPacket(packet);

		}

		/* SI LE TEMPS EST PAS INDETERMINE */

		if (stay != 0) {

			Bukkit.getServer().getScheduler().runTaskLater(BoxPlugin.get(), () -> {

				PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());

				for (Player playerCollection : players)

					((CraftPlayer) playerCollection).getHandle().playerConnection.sendPacket(destroy);

			}, stay);

		}

	}

	private EntityArmorStand getArmorStand() {

		EntityArmorStand stand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

		stand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);

		stand.setCustomName(message);

		stand.setCustomNameVisible(true);

		stand.setGravity(false);

		stand.setInvisible(true);
		
		EntityRegistry.add(stand.getBukkitEntity());
		return stand;

	}

	/**
	 * 
	 * Delete le stand.
	 * 
	 */

	public void destroy() {

		if (this.player_ != null) {

			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());

			((CraftPlayer) player_).getHandle().playerConnection.sendPacket(destroy);

		}

	}

	public void destroyAllPlayers() {

		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getId());

		for (Player playerCollection : playersList) {

			if (playerCollection != null) {

				((CraftPlayer) playerCollection).getHandle().playerConnection.sendPacket(destroy);

			}

		}

	}

}