package me.guitarxpress.gibcraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.guitarxpress.gibcraft.enums.Mode;
import me.guitarxpress.gibcraft.enums.Status;

public class Arena {

	private String name;
	private Status status;
	private Mode mode;
	private List<Player> players;
	private List<Player> spectators;
	private Location[] boundaries;

	private Map<Player, Integer> scores;

	private List<Player> inArena;

	private List<ArmorStand> powerups;

	public Arena(String name, Status status, Mode mode) {
		this.name = name;
		this.status = status;
		this.mode = mode;
		this.players = new ArrayList<>();
		this.spectators = new ArrayList<>();
		this.boundaries = new Location[2];
		this.scores = new HashMap<>();
		this.inArena = new ArrayList<>();
		this.powerups = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public void addPlayer(Player player) {
		if (players.size() >= mode.maxPlayers())
			return;
		players.add(player);
	}

	public void removePlayer(Player player) {
		if (players.contains(player))
			players.remove(player);
	}

	public List<Player> getSpectators() {
		return spectators;
	}

	public void setSpectators(List<Player> spectators) {
		this.spectators = spectators;
	}

	public void addSpectator(Player player) {
		spectators.add(player);
	}

	public void removeSpectator(Player player) {
		spectators.remove(player);
	}

	public void addToArena(Player player) {
		inArena.add(player);
	}

	public void removeFromArena(Player player) {
		inArena.remove(player);
	}

	public List<Player> getAllPlayers() {
		return inArena;
	}

	public Location[] getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(Location[] boundaries) {
		this.boundaries = boundaries;
	}

	public Map<Player, Integer> getScores() {
		return scores;
	}

	public void setScores(Map<Player, Integer> scores) {
		this.scores = scores;
	}

	public void addScore(Player player, int kills) {
		scores.put(player, kills);
	}

	public void removeScore(Player player) {
		scores.remove(player);
	}

	public void increaseScore(Player player) {
		int score = scores.get(player);
		scores.put(player, ++score);
	}

	public void decreaseScore(Player player) {
		int score = scores.get(player);
		scores.put(player, --score);
	}

	public void clearScores() {
		scores.clear();
	}

	public boolean isEmpty() {
		return players.size() == 0;
	}

	public boolean isFull() {
		return players.size() == mode.maxPlayers();
	}

	public int getPlayerCount() {
		return players.size();
	}

	public List<ArmorStand> getPowerups() {
		return powerups;
	}

	public void setPowerups(List<ArmorStand> powerups) {
		this.powerups = powerups;
	}

	public void addPowerup(ArmorStand as) {
		this.powerups.add(as);
	}

	public void removePowerup(ArmorStand as) {
		this.powerups.remove(as);
		as.remove();
	}

	public void removePowerup(int i) {
		this.powerups.get(i).remove();
		this.powerups.remove(i);
	}

	public void removeAllPowerups() {
		for (int i = powerups.size() - 1; i >= 0; i--) {
			powerups.get(i).remove();
			powerups.remove(i);
		}
	}

	/*
	 * @return random spawn if any was found. Null otherwise.
	 */
	public Location selectRandomSpawn() {
		World w = boundaries[0].getWorld();
		Location start = boundaries[0];
		Location end = boundaries[1];

		int topBlockX = (start.getBlockX() < end.getBlockX() ? end.getBlockX() : start.getBlockX());
		int bottomBlockX = (start.getBlockX() > end.getBlockX() ? end.getBlockX() : start.getBlockX());

		int topBlockY = (start.getBlockY() < end.getBlockY() ? end.getBlockY() : start.getBlockY());
		int bottomBlockY = (start.getBlockY() > end.getBlockY() ? end.getBlockY() : start.getBlockY());

		int topBlockZ = (start.getBlockZ() < end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());
		int bottomBlockZ = (start.getBlockZ() > end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());

		int blocks = (topBlockX - bottomBlockX) * (topBlockY - bottomBlockY) * (topBlockZ - bottomBlockZ);

		boolean found = false;
		Random r = new Random();
		int i = 0;
		while (!found) {
			Location loc = null;
			boolean safeSpawn = true;
			int x = r.nextInt(topBlockX - bottomBlockX) + bottomBlockX;
			int y = r.nextInt((topBlockY - 1) - bottomBlockY) + bottomBlockY;
			int z = r.nextInt(topBlockZ - bottomBlockZ) + bottomBlockZ;
			if (i < blocks) {
				if (w.getBlockAt(x, y, z).getType() != Material.AIR) {
					if (w.getBlockAt(x, y + 1, z).getType() == Material.AIR
							&& w.getBlockAt(x, y + 2, z).getType() == Material.AIR) {
						loc = new Location(w, x + .5, y + 1, z + .5);
						for (Entity e : w.getNearbyEntities(loc, 8, 8, 8)) {
							if (e instanceof Player) {
								safeSpawn = false;
								break;
							}
						}
						if (safeSpawn) {
							return loc;
						}
					}
				}
				i++;
			} else {
				return loc;
			}
		}
		return null;
	}
}