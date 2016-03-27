package com.github.maxopoly.WurstCivTools.tags;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.github.maxopoly.WurstCivTools.effect.WurstEffect;

public abstract class Tag {
	
	protected Material material;
	protected WurstEffect effect;
	
	public Tag(Material m) {
		this.material = m;
	}
	
	public abstract boolean appliedOn(ItemStack is);
	
	public Material getMaterial() {
		return material;
	}
	
	public WurstEffect getEffect() {
		return effect;
	}
	
	public void setEffect(WurstEffect effect) {
		this.effect = effect;
	}

}
