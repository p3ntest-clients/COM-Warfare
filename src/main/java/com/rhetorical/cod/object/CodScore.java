package com.rhetorical.cod.object;

import org.bukkit.entity.Player;

import com.rhetorical.cod.StatHandler;

public class CodScore {

	final Player owner;

	private int deaths;
	private int kills;
	private int killStreak;
	private double score;

	CodScore(Player p) {
		this.owner = p;
		this.deaths = 0;
		this.kills = 0;
		this.killStreak = 0;
		this.score = 0D;
	}

	public Player getOwner() {
		return this.owner;
	}
	
	int getDeaths() {
		return this.deaths;
	}

	void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	int getKills() {
		return this.kills;
	}

	void addKill() {
		this.kills++;
		StatHandler.addKill(owner);
	}
	
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	int getKillstreak() {
		return this.killStreak;
	}

	void resetKillstreak() {
		this.killStreak = 0;
	}

	void addKillstreak() {
		this.killStreak++;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	void addScore(double toAdd) {
		this.score += toAdd;
	}
	
	public void removeScore(double toRemove) {
		this.score -= toRemove;
	}
}