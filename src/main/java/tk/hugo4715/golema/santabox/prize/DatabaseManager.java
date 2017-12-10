package tk.hugo4715.golema.santabox.prize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.golema.database.GolemaBukkitDatabase;
import tk.hugo4715.golema.santabox.util.RandomCollection;
import tk.hugo4715.golema.santabox.util.Rarity;

public class DatabaseManager {

	private static final String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS `santabox_keys` (\r\n" + 
			"  `id` INTEGER AUTO_INCREMENT,\r\n" + 
			"  `uuid` VARCHAR(40) NOT NULL UNIQUE,\r\n" + 
			"  `amount` SMALLINT NOT NULL,\r\n" + 
			"  PRIMARY KEY (`id`)\r\n" + 
			");";

	private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS `santabox_win` (\r\n" + 
			"  `id` INTEGER AUTO_INCREMENT,\r\n" + 
			"  `uuid` VARCHAR(40) NOT NULL,\r\n" + 
			"  `code` VARCHAR(40) NOT NULL,\r\n" + 
			"  `prize` MEDIUMTEXT NOT NULL,\r\n" + 
			"  `prize` BOOL DEFAULT 0,\r\n" + 
			"  PRIMARY KEY (`id`)\r\n" + 
			");";

	private static final String CREATE_TABLE3 = "CREATE TABLE IF NOT EXISTS `santabox_prizes` (\r\n" + 
			"  `id` INTEGER AUTO_INCREMENT,\r\n" + 
			"  `prize` MEDIUMTEXT NOT NULL UNIQUE,\r\n" + 
			"  `left` SMALLINT NOT NULL,\r\n" + 
			"  `proba` FLOAT NOT NULL,\r\n" + 
			"  PRIMARY KEY (`id`)\r\n" + 
			");";

	private static final String GET_PRIZES = "SELECT * FROM `santabox_prizes`;";
	private static final String GET_KEYS_PLAYER = "SELECT * FROM `santabox_keys` WHERE `uuid` = ?;";
	private static final String ADD_KEYS_PLAYER = "INSERT INTO `santabox_keys` (`uuid`, `amount`) VALUES (?,?) ON DUPLICATE KEY UPDATE amount=amount+?;";
	private static final String PLAYER_WIN_PRIZE = "INSERT INTO `santabox_win` (`uuid`,`prize` ) VALUES (?,?);";
	private static final String UPDATE_PRIZES = "UPDATE `santabox_prizes` SET left = GREATEST(0, left- 1) WHERE id = ?;";

	public DatabaseManager() throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(Statement s = c.createStatement()){
				s.executeUpdate(CREATE_TABLE1);
			}

			try(Statement s = c.createStatement()){
				s.executeUpdate(CREATE_TABLE2);
			}

			try(Statement s = c.createStatement()){
				s.executeUpdate(CREATE_TABLE3);
			}		
		} 
	}

	public List<Prize> getPrizes() throws SQLException{
		List<Prize> prizes = Lists.newArrayList();

		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(ResultSet rs = c.createStatement().executeQuery(GET_PRIZES)){
				while(rs.next()) {
					Prize p = new Prize(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getFloat(4),Rarity.fromName(rs.getString(5)));
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
				ps.setString(1, p.getUniqueId().toString());

				try(ResultSet rs = ps.executeQuery()){
					if(rs.next()) {
						return rs.getInt("amount");
					}
					return 0;
				}
			}
		}
	}

	public void addPlayerKeys(Player p, int amount) throws SQLException {
		try(Connection c = GolemaBukkitDatabase.INSTANCE.sqlManager.getRessource()){
			try(PreparedStatement ps = c.prepareStatement(ADD_KEYS_PLAYER)){
				ps.setString(1, p.getUniqueId().toString());
				ps.setInt(2, amount);
				ps.setInt(3, amount);
				ps.executeUpdate();
			}
		}
	}
	
	public Prize choosePrize() throws SQLException {
		List<Prize> prizes = getPrizes();
		RandomCollection<Prize> c = new RandomCollection<>();
		prizes.stream().filter(p -> p.getLeft() > 0).forEach(p -> c.add(p.getProba(), p));
		
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
	}
}
