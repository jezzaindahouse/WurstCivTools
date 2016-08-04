/**
 * @author Aleksey Terzi
 *
 */

package com.github.maxopoly.WurstCivTools.effect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.citadel.Citadel;
import vg.civcraft.mc.citadel.PlayerState;
import vg.civcraft.mc.citadel.reinforcement.PlayerReinforcement;
import vg.civcraft.mc.citadel.reinforcement.Reinforcement;

import com.github.maxopoly.WurstCivTools.WurstCivTools;

public class LeafShears extends WurstEffect {
	private int clearCubeSize;

	public LeafShears(int clearCubeSize) {
		super();
		
		this.clearCubeSize = clearCubeSize;
	}

	@Override
	public void handleBreak(Player p, BlockBreakEvent e) {
		if(e.isCancelled()) {
			return;
		}
		
		final Player player = p;
		
		if(player == null) {
			return;
		}
		
		if(!isLeaf(e.getBlock())) {
			return;
		}
		
		e.setCancelled(true);
		e.getBlock().setType(Material.AIR);
		
		//Following manipulations are needed to synchronize tool durability with client after cancel block break
		final ItemStack handItem = player.getInventory().getItemInMainHand();

		handItem.setDurability((short)(handItem.getDurability() + 1));
		
		final Location center = e.getBlock().getLocation();
		
		Bukkit.getScheduler().runTask(WurstCivTools.getPlugin(), new Runnable() {
            public void run() {
            	handItem.setDurability((short)(handItem.getDurability() - 1));
            	
           		clearCube(player, center);
            }
        });
	}
	
	private void clearCube(Player player, Location center) {
		if(this.clearCubeSize <= 1) {
			return;
		}
		
		World world = center.getWorld();
		int radius = (this.clearCubeSize - 1) / 2;
		int startX = center.getBlockX() - radius;
		int startY = center.getBlockY() - radius;
		int startZ = center.getBlockZ() - radius;
		PlayerState state = PlayerState.get(player);
		boolean isBypassMode = state.isBypassMode();
		
		for(int x = startX; x < startX + this.clearCubeSize; x++) {
			for(int y = startY; y < startY + this.clearCubeSize; y++) {
				for(int z = startY; z < startZ + this.clearCubeSize; z++) {
					if(x == center.getBlockX() && y == center.getBlockY() && z == center.getBlockZ()) {
						continue;
					}
					
					Block block = world.getBlockAt(x, y, z);
					
					if(!isLeaf(block) || !canBypass(player, isBypassMode, block.getLocation())) {
						continue;
					}
					
					block.setType(Material.AIR);
				}
			}
		}
	}
	
	private static boolean canBypass(Player player, boolean isBypassMode, Location loc) {
		Reinforcement rein = Citadel.getReinforcementManager().getReinforcement(loc);
		
		if(rein == null || !(rein instanceof PlayerReinforcement)) return true;
		
		if(!isBypassMode) return false;
		
		PlayerReinforcement playerRein = (PlayerReinforcement)rein;
		
		return playerRein.canBypass(player)
			|| player.hasPermission("citadel.admin.bypassmode");
	}
	
	private static boolean isLeaf(Block block) {
		Material blockMaterial = block.getType(); 
		
		return blockMaterial.equals(Material.LEAVES) || blockMaterial.equals(Material.LEAVES_2);
	}
}
