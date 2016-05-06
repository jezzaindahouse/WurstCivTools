package com.github.maxopoly.WurstCivTools.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import com.github.maxopoly.WurstCivTools.WurstCivTools;
import com.github.maxopoly.WurstCivTools.anvil.AnvilHandler;

public class AnvilListener implements Listener {
	
	private AnvilHandler handler;
	
	public AnvilListener(AnvilHandler handler) {
		this.handler = handler;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void anvilResultSlotClick(InventoryClickEvent e) {
		if  (e.getInventory() != null && e.getInventory().getType() == InventoryType.ANVIL && e.getRawSlot() == 2) {
			e.setCancelled(true);
			//result slot in anvil was clicked
			System.out.println("Checking whether allowed to take");
			if (handler.canTakeItem((AnvilInventory) e.getInventory())) {
				e.setCursor(e.getCurrentItem());
				e.getInventory().setItem(0, null);
				e.getInventory().setItem(0, null);
				Bukkit.getScheduler().scheduleSyncDelayedTask(WurstCivTools.getPlugin(), new Runnable() {
					
					@Override
					public void run() {
						for(HumanEntity h : e.getViewers()) {
							((Player) h).updateInventory();
						}
					}
				});
			}
		}
	}
	
	@EventHandler
	public void anvilPrepare(PrepareAnvilEvent e) {
		ItemStack res = AnvilHandler.getAdjustedOutput(e.getInventory());
		if (res != null) {
			System.out.println("Setting result to " + res);
			e.setResult(res);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(WurstCivTools.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				for(HumanEntity h : e.getViewers()) {
					((Player) h).updateInventory();
				}
			}
		});
	}
	
}
