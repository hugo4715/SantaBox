package tk.hugo4715.golema.santabox.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import javax.swing.text.html.StyleSheet.BoxPainter;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.golema.database.GolemaBukkitDatabase;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.UUIDFetcher;
import net.golema.database.golemaplayer.currency.Currency;
import net.golema.database.golemaplayer.game.luckbox.ILuckBox;
import net.golema.database.golemaplayer.game.luckbox.LuckBoxType;
import net.golema.database.golemaplayer.rank.Rank;
import tk.hugo4715.golema.santabox.BoxPlugin;
import tk.hugo4715.golema.santabox.util.RandomCollection;
import tk.hugo4715.golema.santabox.util.Rarity;

public class DatabaseManager {

	private static final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS `santabox_keys` (`id` INTEGER AUTO_INCREMENT, `uuid` VARCHAR(40) NOT NULL UNIQUE, `amount` SMALLINT NOT NULL, PRIMARY KEY (`id`));";

	private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS `santabox_win` (`id` INTEGER AUTO_INCREMENT, `uuid` VARCHAR(40) NOT NULL, `prize` MEDIUMTEXT NOT NULL, `redeemed` BOOL DEFAULT 0, PRIMARY KEY (`id`));";

	private static final String CREATE_TABLE3 = "CREATE TABLE IF NOT EXISTS `santabox_prizes` ( `id` INTEGER AUTO_INCREMENT, `prize` MEDIUMTEXT NOT NULL, `left` SMALLINT NOT NULL, `proba` DECIMAL NOT NULL,`rarity` MEDIUMTEXT NOT NULL, `marque` MEDIUMTEXT NOT NULL, PRIMARY KEY (`id`));";

	private static final String GET_PRIZES = "SELECT * FROM `santabox_prizes`;";
	private static final String GET_KEYS_PLAYER = "SELECT * FROM `santabox_keys` WHERE uuid=?;";
	private static final String ADD_KEYS_PLAYER = "INSERT INTO `santabox_keys` (`uuid`, `amount`) VALUES (?,?) ON DUPLICATE KEY UPDATE amount=amount+?;";
	private static final String PLAYER_WIN_PRIZE = "INSERT INTO `santabox_win` (`uuid`,`prize` ) VALUES (?,?);";
	private static final String UPDATE_PRIZES = "UPDATE santabox_prizes SET `left`=`left` - 1 WHERE `id`=? AND `left` > 0;";

	public DatabaseManager() throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(Statement s = c.createStatement()){
				s.execute(CREATE_TABLE1);
			}

			try(Statement s = c.createStatement()){
				s.execute(CREATE_TABLE2);
			}

			try(Statement s = c.createStatement()){
				s.execute(CREATE_TABLE3);
			}		
		} 
	}

	public List<Prize> getPrizes() throws SQLException{
		List<Prize> prizes = Lists.newArrayList();

		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(ResultSet rs = c.createStatement().executeQuery(GET_PRIZES)){
				while(rs.next()) {
					Prize p = new Prize(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getFloat(4),Rarity.fromName(rs.getString(5)),rs.getString(6));
					prizes.add(p);
				}
			}
		}

		return prizes;
	}

	public void usePrize(Prize p) throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(PreparedStatement ps = c.prepareStatement(UPDATE_PRIZES)){
				ps.setInt(1, p.getId());
				ps.executeUpdate();
			}
		}
	}

	public int getPlayerKeys(Player p) throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(PreparedStatement ps = c.prepareStatement(GET_KEYS_PLAYER)){
				ps.setString(1, UUIDFetcher.getUUID(GolemaPlayer.getGolemaPlayer(p).getPlayerRealName()).toString());

				try(ResultSet rs = ps.executeQuery()){
					if(rs.next()) {
						System.out.println("returned " + rs.getInt("amount"));
						return rs.getInt("amount");
					}
					System.out.println("nothing returned");
					return 0;
				}
			}
		}
	}

	public void addPlayerKeys(UUID target, int amount) throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(PreparedStatement ps = c.prepareStatement(ADD_KEYS_PLAYER)){
				ps.setString(1, target.toString());
				ps.setInt(2, amount);
				ps.setInt(3, amount);
				ps.executeUpdate();
			}
		}
	}
	
	public Prize choosePrize() throws SQLException {
		List<Prize> prizes = getPrizes();
		RandomCollection<Prize> c = new RandomCollection<>();
		prizes.stream().filter(p -> p.getLeft() != 0).forEach(p -> c.add(p.getProba(), p));
		
		return c.next();
	}
	
	public void winPrize(Player p, Prize prize) throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(PreparedStatement ps = c.prepareStatement(PLAYER_WIN_PRIZE)){
				ps.setString(1, p.getUniqueId().toString());
				ps.setString(2, prize.getName());
				ps.executeUpdate();
			}
		}
	}
	
	@Getter
	@AllArgsConstructor
	public class Prize{
		private int id;
		private String name;
		private int left;
		private float proba;
		private Rarity rarity;
		private String marque;
		
		public void give(GolemaPlayer golemaPlayer) {
			if(name.toLowerCase().contains("coins")){
				int amount = Integer.valueOf(name.split(" ")[0]);
				golemaPlayer.addCoins(Currency.GCOINS, amount);
			}else if(name.toLowerCase().contains("credit")){
				int amount = Integer.valueOf(name.split(" ")[0]);
				golemaPlayer.addCoins(Currency.GOLEMACREDITS, amount);
			}else if(name.toLowerCase().contains("santabox")){
				int amount = Integer.valueOf(name.split(" ")[0]);
				try {
					BoxPlugin.get().getDatabaseManager().addPlayerKeys(UUIDFetcher.getUUID(golemaPlayer.getPlayerRealName()), amount);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else if(name.toLowerCase().contains("luckbox")){
				final int amount = Integer.valueOf(name.split(" ")[0]);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						for(int i = 0; i < amount; i++){
							ILuckBox.addPlayerLuckBox(UUIDFetcher.getUUID(golemaPlayer.getPlayerRealName()), LuckBoxType.LUCKBOX_SKYWARS);
						}
					}
				}.runTaskAsynchronously(BoxPlugin.get());
			}else if(name.toLowerCase().contains("vip+")){
				if(golemaPlayer.getRankPower() < Rank.VIPPLUS.getPower()){
					golemaPlayer.setRank(Rank.VIPPLUS);
				}
			}else if(name.toLowerCase().contains("vip")){
				if(golemaPlayer.getRankPower() < Rank.VIP.getPower()){
					golemaPlayer.setRank(Rank.VIP);
				}
			}
		}
		
		public boolean isAutomatic(){
			if(name.toLowerCase().contains("coins")
					|| name.toLowerCase().contains("santabox")
					|| name.toLowerCase().contains("luckbox")
					|| name.toLowerCase().contains("vip")
					|| name.toLowerCase().contains("credit")
					)return true;
			
			return false;
		}
	}
}
