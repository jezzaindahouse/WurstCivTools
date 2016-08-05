package com.github.maxopoly.WurstCivTools.tags;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreTag extends Tag{
	private String lore;
	
	public LoreTag(Material mat, String lore) {
		super(mat);
		this.lore = lore;
	}
	
	public boolean appliedOn(ItemStack is) {
		if (is == null) {
			return false;
		}
		
		ItemMeta im = is.getItemMeta();
		List <String> appliedLore = im.getLore();
		if (appliedLore == null)
			return false;
		for(String s : appliedLore) {
			if (s.equals(lore)) {
				return true;
			}
		}
		
		return false;
	}
}
