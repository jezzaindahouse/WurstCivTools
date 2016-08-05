package com.github.maxopoly.WurstCivTools.effect;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public abstract class WurstEffect {
	
	public Listener getListener(){
		return null;
	}
	
	public void handleDamageEntity(Player p, EntityDamageByEntityEvent e) {
		
	}
	
	public void handleBreak(Player p, BlockBreakEvent e) {
		
	}
	
	public void handleInteract(Player p, PlayerInteractEvent e) {
		
	}
	
	public void handleItemSelect(Player p, PlayerItemHeldEvent e){
		
	}
	
	public void handleItemDeselect(Player p, PlayerItemHeldEvent e){
		
	}
	
	public void handleSwapToMainHand(Player p, PlayerSwapHandItemsEvent e){
		
	}
	
	public void handleSwapToOffHand(Player p, PlayerSwapHandItemsEvent e){
		
	}
	
}
