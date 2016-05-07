package com.github.maxopoly.WurstCivTools.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.github.maxopoly.WurstCivTools.misc.CoolDownHandler;

public class AnvilListener implements Listener {

	private AnvilHandler handler;
	private CoolDownHandler cdHandler;

	public AnvilListener(AnvilHandler handler) {
		this.handler = handler;
		cdHandler = new CoolDownHandler(100); // 50 ms cooldown
	}

	@EventHandler(ignoreCancelled = true)
	public void anvilResultSlotClick(InventoryClickEvent e) {
		if (e.getInventory() != null
				&& e.getInventory().getType() == InventoryType.ANVIL
				&& e.getRawSlot() == 2) {
			e.setCancelled(true);
			if (e.getInventory().getItem(2) == null) {
				return;
			}
			// result slot in anvil was clicked
			if (handler.canTakeItem((AnvilInventory) e.getInventory())) {
				ItemStack result = e.getInventory().getItem(2).clone();
				if (handler.consumeRequiredMaterials((AnvilInventory) e
						.getInventory())) {
					e.setCursor(result);
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							WurstCivTools.getPlugin(), new Runnable() {

								@Override
								public void run() {
									for (HumanEntity h : e.getViewers()) {
										((Player) h).updateInventory();
									}
								}
							});
				}
			}
		}
	}

	@EventHandler
	public void anvilPrepare(PrepareAnvilEvent e) {
		ItemStack res = handler.getAdjustedOutput(e.getInventory(),
				e.getResult());
		if (res != null) {
			e.setResult(res);
		}
		int cost = (int) Math.ceil(handler.getTotalAnvilActionCost(e
				.getInventory()));
		if (cost != 0) {
			for (HumanEntity h : e.getInventory().getViewers()) {
				if (!cdHandler.onCoolDown(h.getUniqueId())) {
					h.sendMessage(ChatColor.GOLD
							+ "Fully repairing this item will cost " + cost + " XP");
					cdHandler.putOnCoolDown(h.getUniqueId());
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				WurstCivTools.getPlugin(), new Runnable() {

					@Override
					public void run() {
						for (HumanEntity h : e.getViewers()) {
							((Player) h).updateInventory();
						}

					}
				});
	}

}
