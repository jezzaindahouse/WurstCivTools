package com.github.maxopoly.WurstCivTools;

import static vg.civcraft.mc.civmodcore.util.ConfigParsing.parseItemMap;
import static vg.civcraft.mc.civmodcore.util.ConfigParsing.parseTime;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.maxopoly.WurstCivTools.effect.PylonFinder;
import com.github.maxopoly.WurstCivTools.effect.WurstEffect;
import com.github.maxopoly.WurstCivTools.tags.LoreTag;
import com.github.maxopoly.WurstCivTools.tags.Tag;

public class ConfigParser {
	private WurstCivTools plugin;
	private WurstManager manager;

	public ConfigParser() {
		plugin = WurstCivTools.getPlugin();
	}

	public WurstManager parse() {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		FileConfiguration config = plugin.getConfig();
		manager = new WurstManager();
		parseTools(config.getConfigurationSection("tech"));
		plugin.info("Parsed complete config");
		return manager;
	}

	public void parseTools(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {
			ConfigurationSection current = config.getConfigurationSection(key);
			String type = current.getString("type");
			Tag tag = parseTag(current.getConfigurationSection("tag"));
			WurstEffect effect;
			switch (type) {
			case "PYLONFINDER":
				if (!Bukkit.getPluginManager().isPluginEnabled("FactoryMod")) {
					plugin.severe("Attempted to load Pylonfinder tool, but FactoryMod is not installed on this server");
					continue;
				}
				boolean showNonRunning = current.getBoolean("show_non_running");
				boolean showUpgrading = current.getBoolean("show_upgrading");
				long cd = parseTime(current.getString("update_cooldown", "5s")) * 50L;
				effect = new PylonFinder(showNonRunning, showUpgrading, cd);
				break;
			default:
				plugin.severe("Could not identify effect type " + type + " at "
						+ config.getCurrentPath());
				effect = null;

			}
			if (effect == null) {
				plugin.severe("Could not load effect from config at " + config.getCurrentPath());
				continue;
			}
			tag.setEffect(effect);
		}
	}

	public Tag parseTag(ConfigurationSection config) {
		if (config == null) {
			return null;
		}
		Tag result;
		String type = config.getString("type", "lore");
		String matString = config.getString("material");
		if (matString == null) {
			plugin.severe("No material specified for tag at"
					+ config.getCurrentPath());
			return null;
		}
		Material mat = Material.matchMaterial(matString);
		switch (type.toLowerCase()) {
		case "lore":
			String lore = config.getString("lore");
			result = new LoreTag(mat, lore);
			break;
		default:
			plugin.severe("Could not parse tag type " + type);
			result = null;
			break;
		}
		return result;
	}

}
