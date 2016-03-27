package com.github.maxopoly.WurstCivTools.misc;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class CoolDownHandler {
	private Map <UUID, Long> cds;
	private long cooldown; //in milliseconds
	
	public CoolDownHandler(long cooldown) {
		this.cooldown = cooldown;
		cds = new TreeMap<UUID, Long>();
	}
	
	public void putOnCoolDown(UUID uuid) {
		cds.put(uuid, System.currentTimeMillis());
	}
	
	public boolean onCoolDown(UUID uuid) {
		Long lastUsed = cds.get(uuid);
		if (lastUsed == null || (System.currentTimeMillis() - lastUsed) > cooldown) {
			return false;
		}
		return true;
	}
}
