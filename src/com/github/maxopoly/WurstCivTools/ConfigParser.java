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
			if (tag == null) {
				plugin.severe("Could not parse tag for effect at "
						+ config.getCurrentPath());
				continue;
			}
			WurstEffect effect;
			switch (type) {
			case "PYLONFINDER":
				if (!Bukkit.getPluginManager().isPluginEnabled("FactoryMod")) {
					plugin.severe("Attempted to load Pylonfinder tool, but FactoryMod is not installed on this server");
					continue;
				}
				boolean showNonRunning = current.getBoolean("show_non_running",
						true);
				boolean showUpgrading = current.getBoolean("show_upgrading",
						true);
				long cd = parseTime(current.getString("update_cooldown", "5s")) * 50L;
				effect = new PylonFinder(showNonRunning, showUpgrading, cd);
				plugin.info("Parsed Pylonfinder tool, show_non_running:"
						+ showNonRunning + ", showUpgrading:" + showUpgrading
						+ ", cooldown:" + cd);
				break;
			default:
				plugin.severe("Could not identify effect type " + type + " at "
						+ config.getCurrentPath());
				effect = null;

			}
			if (effect == null) {
				plugin.severe("Could not load effect from config at "
						+ config.getCurrentPath());
				continue;
			}
			tag.setEffect(effect);
			manager.addTag(tag);
		}
	}

	public Tag parseTag(ConfigurationSection config) {
		if (config == null) {
			return null;
		}
		Tag result;
		String type = config.getString("type");
		if (type == null) {
			plugin.severe("No type specified for tag at"
					+ config.getCurrentPath());
			return null;
		}
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
