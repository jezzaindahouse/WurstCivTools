package com.github.maxopoly.WurstCivTools;

import vg.civcraft.mc.civmodcore.ACivMod;

public class WurstCivTools extends ACivMod {
	
	private static WurstCivTools instance;
	
	public String getPluginName() {
		return "WurstCivTools";
	}
	
	public void onEnable() {
		instance = this;
	}
	
	public void onDisable() {
		
	}
	
	public static WurstCivTools getPlugin() {
		return instance;
	}

}
