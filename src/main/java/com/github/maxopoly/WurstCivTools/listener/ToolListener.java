package com.github.maxopoly.WurstCivTools.listener;

import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.github.maxopoly.WurstCivTools.WurstCivTools;
import com.github.maxopoly.WurstCivTools.WurstManager;
import com.github.maxopoly.WurstCivTools.tags.Tag;

public class ToolListener implements Listener {
	private WurstManager manager;
	
	public ToolListener() {
		this.manager = WurstCivTools.getManager();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent e) {
		ItemStack is = e.getPlayer().getItemInHand();
		for (Tag tag : getTags(is)) {
			tag.getEffect().handleBreak(e.getPlayer(), e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void damageEntity(EntityDamageByEntityEvent e) {
		Player p;
		if (e.getDamager().getType() != EntityType.PLAYER) {
			// not a player, but it could be a projectile shot by a player
			if (e.getDamager() instanceof Projectile) {
				ProjectileSource ps = ((Projectile) e.getDamager())
						.getShooter();
				if (ps instanceof Player) {
					p = (Player) ps;
				} else {
					// not shot by a player
					return;
				}
			} else {
				// not a projectile
				return;
			}
		} else {
			p = (Player) e.getDamager();
		}
		ItemStack is = p.getItemInHand();
		for (Tag tag : getTags(is)) {
			tag.getEffect().handleDamageEntity(p, e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void interact(PlayerInteractEvent e) {
		for (Tag tag : getTags(e.getItem())) {
			tag.getEffect().handleInteract(e.getPlayer(), e);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void itemSelect(PlayerItemHeldEvent e){
		Player p = e.getPlayer();
		ItemStack olditem = p.getInventory().getItem(e.getPreviousSlot());
		for (Tag tag : getTags(olditem)){
			tag.getEffect().handleItemDeselect(p, e);
		}
		
		ItemStack newitem = p.getInventory().getItem(e.getNewSlot());
		for (Tag tag : getTags(newitem)){
			tag.getEffect().handleItemSelect(p, e);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void switchItemOffhand(PlayerSwapHandItemsEvent e){
		for (Tag tag : getTags(e.getMainHandItem())){
			tag.getEffect().handleSwapToMainHand(e.getPlayer(), e);
		}
		
		for (Tag tag : getTags(e.getOffHandItem())){
			tag.getEffect().handleSwapToOffHand(e.getPlayer(), e);
		}
	}

	private HashSet<Tag> getTags(ItemStack is) {
		HashSet<Tag> tags = new HashSet<Tag>();
		if (is == null) {
			return tags;
		}
		List<Tag> typetags = manager.getTagsFor(is.getType());
		if (typetags == null) {
			return tags;
		}
		for (Tag tag : typetags) {
			if (tag.appliedOn(is)) {
				tags.add(tag);
			}
		}
		return tags;
	}

}
