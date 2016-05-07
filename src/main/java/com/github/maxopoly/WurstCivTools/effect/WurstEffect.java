package com.github.maxopoly.WurstCivTools.effect;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class WurstEffect {
	
	public void handleDamageEntity(Player p, EntityDamageByEntityEvent e) {
		
	}
	
	public void handleBreak(Player p, BlockBreakEvent e) {
		
	}
	
	public void handleInteract(Player p, PlayerInteractEvent e) {
		
	}
}
