package com.github.maxopoly.WurstCivTools.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface INmsManager {
	boolean damageItem(ItemStack item, int damage, Player player);
}
