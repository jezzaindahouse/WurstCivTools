package com.github.maxopoly.WurstCivTools.effect;

import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.igotyou.FactoryMod.FactoryMod;
import com.github.igotyou.FactoryMod.eggs.FurnCraftChestEgg;
import com.github.igotyou.FactoryMod.factories.Factory;
import com.github.igotyou.FactoryMod.factories.FurnCraftChestFactory;
import com.github.igotyou.FactoryMod.recipes.IRecipe;
import com.github.igotyou.FactoryMod.recipes.PylonRecipe;
import com.github.igotyou.FactoryMod.recipes.Upgraderecipe;
import com.github.maxopoly.WurstCivTools.misc.CoolDownHandler;

public class PylonFinder extends WurstEffect {

	private boolean showNonRunning;
	private boolean showUpgrading;
	private CoolDownHandler cdHandler;

	public PylonFinder(boolean showNonRunning, boolean showUpgrading, long updateCooldown) {
		super();
		this.showNonRunning = showNonRunning;
		this.showUpgrading = showUpgrading;
		this.cdHandler = new CoolDownHandler(updateCooldown);
	}

	public void setCompassLocation(Player p) {
		if (cdHandler.onCoolDown(p.getUniqueId())) {
			return;
		}
		cdHandler.putOnCoolDown(p.getUniqueId());
		HashSet<FurnCraftChestFactory> pylons = FurnCraftChestFactory
				.getPylonFactories();
		FurnCraftChestFactory closest = null;
		double distance = 0;
		if (!showUpgrading) {
			for (FurnCraftChestFactory pylon : pylons) {
				if (!showNonRunning) {
					if (!pylon.isActive()
							|| !(pylon.getCurrentRecipe() instanceof PylonRecipe)) {
						continue;
					}
				}
				if (closest == null) {
					closest = pylon;
					distance = pylon.getMultiBlockStructure().getCenter()
							.distance(p.getLocation());
					continue;
				}
				double compDistance = pylon.getMultiBlockStructure()
						.getCenter().distance(p.getLocation());
				if (compDistance < distance) {
					distance = compDistance;
					closest = pylon;
				}
			}
		} else {
			//show factories that are currently upgrading to be a pylon
			for (Factory f : FactoryMod.getManager().getAllFactories()) {
				if (!f.isActive() && !pylons.contains(f)) {
					continue;
				}
				if (!(f instanceof FurnCraftChestFactory)) {
					continue;
				}
				FurnCraftChestFactory fac = (FurnCraftChestFactory) f;
				if (!pylons.contains(fac)) {
					if (!(fac.getCurrentRecipe() instanceof Upgraderecipe)) {
						continue;
					} else {
						// checks whether factory is upgrading to a pylon
						List<IRecipe> upgradedRecipes = ((FurnCraftChestEgg) ((Upgraderecipe) fac
								.getCurrentRecipe()).getEgg()).getRecipes();
						boolean found = false;
						for (IRecipe rec : upgradedRecipes) {
							if (rec instanceof PylonRecipe) {
								found = true;
								break;
							}
						}
						if (!found) {
							continue;
						}
					}
				}
				if (closest == null) {
					closest = fac;
					distance = fac.getMultiBlockStructure().getCenter()
							.distance(p.getLocation());
					continue;
				}
				double compDistance = fac.getMultiBlockStructure()
						.getCenter().distance(p.getLocation());
				if (compDistance < distance) {
					distance = compDistance;
					closest = fac;
				}
			}

		}
		if (closest != null) {
			p.setCompassTarget(closest.getMultiBlockStructure().getCenter());
		}

	}

	@Override
	public void handleInteract(Player p, PlayerInteractEvent e) {
		setCompassLocation(p);
	}

}
