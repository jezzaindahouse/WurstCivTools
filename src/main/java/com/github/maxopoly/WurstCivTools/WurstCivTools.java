package com.github.maxopoly.WurstCivTools;

import org.bukkit.Bukkit;

import com.github.maxopoly.WurstCivTools.listener.ToolListener;

import vg.civcraft.mc.civmodcore.ACivMod;

public class WurstCivTools extends ACivMod {
	
	private static WurstCivTools instance;
	private static WurstManager manager;
	
	public String getPluginName() {
		return "WurstCivTools";
	}
	
	public void onEnable() {
		instance = this;
		ConfigParser cp = new ConfigParser();
		manager = cp.parse();
		registerListeners();
	}
	
	public void onDisable() {
		
	}
	
	public static WurstCivTools getPlugin() {
		return instance;
	}
	
	public static WurstManager getManager() {
		return manager;
	}
	
	public void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new ToolListener(), this);
	}
}
