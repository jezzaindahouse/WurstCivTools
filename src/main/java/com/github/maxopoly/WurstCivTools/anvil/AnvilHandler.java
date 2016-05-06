package com.github.maxopoly.WurstCivTools.anvil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

public class AnvilHandler {

	private Map<ItemMap, Double> values;
	private Map<Enchantment, Double> enchantCosts;
	private double renamingCost;
	private static Set<Material> tools;
	private boolean scaleWithMissingDurability;

	public AnvilHandler(Map<ItemMap, Double> values, Map<Enchantment, Double> enchantCosts, double renamingCost, boolean scaleWithMissingDura) {
		this.values = values;
		this.enchantCosts = enchantCosts;
		this.renamingCost = renamingCost;
		this.scaleWithMissingDurability = scaleWithMissingDura;
		initToolsSet();
	}

	public boolean canTakeItem(AnvilInventory i) {
		if (isCombiningTools(i)) {
			return true;
		} else {
			double requiredAmount = getTotalAnvilActionCost(i);
			double availableAmount = getValue(i.getItem(1), true);
			return requiredAmount <= availableAmount;
		}
	}

	public double getTotalAnvilActionCost(AnvilInventory i) {
		double requiredAmount = calculateRepairCost(i.getItem(0));
		if (isBeingRenamed(i)) {
			requiredAmount += renamingCost;
		}
		return requiredAmount;
	}

	public double calculateRepairCost(ItemStack is) {
		if (!isTool(is.getType())) {
			return 0;
		}
		double duraMultiplier = 1.0;
		if (scaleWithMissingDurability) {
			duraMultiplier = is.getDurability()
					/ is.getType().getMaxDurability();
		}
		ItemMeta im = is.getItemMeta();
		double enchantMultiplier = 0.0;
		if (im.hasEnchants()) {
			for (Entry<Enchantment, Integer> enchant : im.getEnchants()
					.entrySet()) {
				Double multi = enchantCosts.get(enchant.getKey());
				if (multi != null) {
					enchantMultiplier += multi * enchant.getValue();
				}
			}
		}
		return enchantMultiplier * duraMultiplier;
	}

	public double getValue(ItemStack is, boolean respectAmount) {
		if (is == null || is.getType() == Material.AIR) {
			return 0;
		}
		Double matValue = null;
		for (Entry<ItemMap, Double> entry : values.entrySet()) {
			if (entry.getKey().getAmount(is) != 0) {
				matValue = entry.getValue();
			}
		}
		if (matValue != null) {
			if (respectAmount) {
				return matValue * is.getAmount();
			}
			return matValue;
		}
		return 0;
	}

	public static boolean isTool(Material m) {
		return tools.contains(m);
	}

	public boolean consumeRequiredMaterials(AnvilInventory i) {
		double requiredAmount = getTotalAnvilActionCost(i);
		double availableAmount = getValue(i.getItem(1), true);
		if (availableAmount != 0 && availableAmount >= requiredAmount) {
			int amountToConsume = (int) Math.ceil(requiredAmount
					/ getValue(i.getItem(1), false));
			i.getItem(1).setAmount(i.getItem(1).getAmount() - amountToConsume);
			return true;
		}
		return false;
	}

	public static ItemStack getAdjustedOutput(AnvilInventory i) {
		ItemStack firstItem = i.getItem(0);
		ItemStack secondItem = i.getItem(1);
		String newName = null;
		if (firstItem != null && firstItem.getItemMeta().hasDisplayName()) {
			newName = firstItem.getItemMeta().getDisplayName();
		}
		if (isCombiningTools(i)) {
			System.out.println("Combining tools");
			Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			List<String> lore = new LinkedList<String>();
			List<ItemStack> items = new LinkedList<ItemStack>();
			items.add(firstItem);
			items.add(secondItem);
			for (ItemStack is : items) {
				ItemMeta im = is.getItemMeta();
				if (im.hasEnchants()) {
					for (Entry<Enchantment, Integer> entry : im.getEnchants()
							.entrySet()) {
						Integer existingVal = enchants.get(entry.getKey());
						if (existingVal == null) {
							enchants.put(entry.getKey(), entry.getValue());
						} else {
							enchants.put(entry.getKey(),
									Math.max(entry.getValue(), existingVal));
						}
					}
				}
				if (im.hasLore()) {
					for (String exisLore : im.getLore()) {
						if (!lore.contains(exisLore)) {
							lore.add(exisLore);
						}
					}
				}
			}
			ItemStack result = new ItemStack(firstItem.getType(), 1,
					firstItem.getDurability());
			ItemMeta im = result.getItemMeta();
			if (lore.size() != 0) {
				im.setLore(lore);
			}
			for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
				im.addEnchant(entry.getKey(), entry.getValue(), true);
			}
			if (newName != null) {
				im.setDisplayName(newName);
			}
			result.setItemMeta(im);
			return result;
		}
		else { //put in a normal tool
			if (firstItem == null) {
				return null;
			}
			ItemStack repl = firstItem.clone();
			repl.setDurability((short) 0);
			return repl;
		}
	}
	
	

	private static boolean isBeingRenamed(AnvilInventory i) {
		ItemStack originalItem = i.getItem(0);
		ItemStack resultItem = i.getItem(2);
		if (originalItem == null || resultItem == null) {
			return false;
		}
		if (!resultItem.getItemMeta().hasDisplayName()) {
			return false;
		}
		if (!originalItem.getItemMeta().hasDisplayName()) {
			// original doesnt have name, but target does
			return true;
		}
		// both have a custom name, so we need to check whether they are equal
		return !originalItem.getItemMeta().getDisplayName()
				.equals(resultItem.getItemMeta().getDisplayName());
	}

	public static boolean isCombiningTools(AnvilInventory i) {
		return i.getItem(0) != null && i.getItem(1) != null
				&& isTool(i.getItem(0).getType())
				&& isTool(i.getItem(1).getType());
	}

	private void initToolsSet() {
		tools = new HashSet<Material>();
		tools.add(Material.WOOD_AXE);
		tools.add(Material.WOOD_HOE);
		tools.add(Material.WOOD_PICKAXE);
		tools.add(Material.WOOD_SPADE);
		tools.add(Material.WOOD_SWORD);
		tools.add(Material.IRON_AXE);
		tools.add(Material.IRON_HOE);
		tools.add(Material.IRON_PICKAXE);
		tools.add(Material.IRON_SPADE);
		tools.add(Material.IRON_SWORD);
		tools.add(Material.GOLD_AXE);
		tools.add(Material.GOLD_HOE);
		tools.add(Material.GOLD_PICKAXE);
		tools.add(Material.GOLD_SPADE);
		tools.add(Material.GOLD_SWORD);
		tools.add(Material.DIAMOND_AXE);
		tools.add(Material.DIAMOND_HOE);
		tools.add(Material.DIAMOND_PICKAXE);
		tools.add(Material.DIAMOND_SPADE);
		tools.add(Material.DIAMOND_SWORD);
	}

}
