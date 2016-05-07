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
	private static Set<Material> tools;
	private boolean scaleWithMissingDurability;
	private List <String> blacklistedLore;

	public AnvilHandler(Map<ItemMap, Double> values,
			Map<Enchantment, Double> enchantCosts,
			boolean scaleWithMissingDura, List <String> blacklistedLore) {
		this.values = values;
		this.enchantCosts = enchantCosts;
		this.scaleWithMissingDurability = scaleWithMissingDura;
		this.blacklistedLore = blacklistedLore;
		initRepairableSet();
	}

	public boolean canTakeItem(AnvilInventory i) {
		if (isCombiningRepairables(i)) {
			return true;
		} else {
			double requiredAmount = getTotalAnvilActionCost(i);
			System.out.println(requiredAmount);
			double availableAmount = getValue(i.getItem(1), true);
			System.out.println(availableAmount);
			return requiredAmount <= availableAmount;
		}
	}

	public double getTotalAnvilActionCost(AnvilInventory i) {
		if (i.getItem(0) == null) {
			return 0.0;
		}
		double requiredAmount = calculateRepairCost(i.getItem(0));
		return requiredAmount;
	}

	public double calculateRepairCost(ItemStack is) {
		if (!isTool(is.getType())) {
			return 0;
		}
		double duraMultiplier = 1.0;
		if (scaleWithMissingDurability) {
			duraMultiplier = ((double) is.getDurability())
					/ ((double) is.getType().getMaxDurability());
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
		} else {
			enchantMultiplier = 1.0;
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
		if (isCombiningRepairables(i)) {
			i.setItem(0, null);
			i.setItem(1, null);
			return true;
		}
		double requiredAmount = getTotalAnvilActionCost(i);
		double availableAmount = getValue(i.getItem(1), true);
		if (i.getItem(1) == null) {
			i.setItem(0, null);
			return true;
		}
		if (availableAmount >= requiredAmount) {
			int amountToConsume = (int) Math.ceil(requiredAmount
					/ getValue(i.getItem(1), false));
			i.setItem(0, null);
			i.getItem(1).setAmount(i.getItem(1).getAmount() - amountToConsume);
			return true;
		}
		return false;
	}

	public ItemStack getAdjustedOutput(AnvilInventory i, ItemStack vanillaResult) {
		ItemStack firstItem = i.getItem(0);
		ItemStack secondItem = i.getItem(1);
		String newName = null;
		if (vanillaResult != null && vanillaResult.getItemMeta() != null
				&& vanillaResult.getItemMeta().hasDisplayName()) {
			newName = vanillaResult.getItemMeta().getDisplayName();
		}
		if (isCombiningRepairables(i)) {
			if (firstItem.getType() != secondItem.getType()) {
				return null;
			}
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
						if (!lore.contains(exisLore) && !blacklistedLore.contains(exisLore)) {
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
		} else {
			if (firstItem != null && secondItem == null) {
				// assume renaming and do nothing
				return null;
			} else { // put in a normal tool
				if (firstItem == null) {
					return null;
				}
				ItemStack repl = firstItem.clone();
				repl.setDurability((short) 0);
				return repl;
			}
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

	public static boolean isCombiningRepairables(AnvilInventory i) {
		return i.getItem(0) != null && i.getItem(1) != null
				&& isTool(i.getItem(0).getType())
				&& isTool(i.getItem(1).getType());
	}

	private void initRepairableSet() {
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
		tools.add(Material.LEATHER_BOOTS);
		tools.add(Material.LEATHER_CHESTPLATE);
		tools.add(Material.LEATHER_HELMET);
		tools.add(Material.LEATHER_LEGGINGS);
		tools.add(Material.IRON_BOOTS);
		tools.add(Material.IRON_CHESTPLATE);
		tools.add(Material.IRON_HELMET);
		tools.add(Material.IRON_LEGGINGS);
		tools.add(Material.GOLD_BOOTS);
		tools.add(Material.GOLD_CHESTPLATE);
		tools.add(Material.GOLD_HELMET);
		tools.add(Material.GOLD_LEGGINGS);
		tools.add(Material.DIAMOND_BOOTS);
		tools.add(Material.DIAMOND_CHESTPLATE);
		tools.add(Material.DIAMOND_HELMET);
		tools.add(Material.DIAMOND_LEGGINGS);
		tools.add(Material.CHAINMAIL_BOOTS);
		tools.add(Material.CHAINMAIL_CHESTPLATE);
		tools.add(Material.CHAINMAIL_HELMET);
		tools.add(Material.CHAINMAIL_LEGGINGS);
		tools.add(Material.SHEARS);
		tools.add(Material.SHIELD);
		tools.add(Material.BOW);
	}
}
