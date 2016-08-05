package com.github.maxopoly.WurstCivTools.nms.v1_10_R1;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.ItemStack;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.github.maxopoly.WurstCivTools.misc.ReflectionHelper;
import com.github.maxopoly.WurstCivTools.nms.INmsManager;

public class NmsManager implements INmsManager {
	public boolean damageItem(org.bukkit.inventory.ItemStack item, int damage, Player player) {
		EntityPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		ItemStack nmsItem = (ItemStack)ReflectionHelper.getFieldValue((CraftItemStack)item, "handle");
		
		nmsItem.damage(damage, nmsPlayer);
		
		return nmsItem.count == 0;
	}
}
