package com.github.maxopoly.WurstCivTools.tags;

import java.util.List;
import java.util.Objects;

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
		List<String> appliedLore = im.getLore();
		
		if(appliedLore != null) {
			for(String s : appliedLore) {
				if (Objects.equals(s, lore)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
