package com.github.maxopoly.WurstCivTools;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;

import com.github.maxopoly.WurstCivTools.effect.WurstEffect;
import com.github.maxopoly.WurstCivTools.tags.Tag;

public class WurstManager {
	private WurstCivTools plugin;
	private Map <Material, List <Tag>> tags;
	
	public WurstManager() {
		this.plugin = WurstCivTools.getPlugin();
		tags = new TreeMap<Material, List<Tag>> ();
	}
	
	public List<Tag> getTagsFor(Material m) {
		return tags.get(m);
	}
	
	public void addTag(Tag tag) {
		List <Tag> existing = tags.get(tag.getMaterial());
		if (existing == null) {
			existing = new LinkedList<Tag>();
			tags.put(tag.getMaterial(), existing);
		}
		existing.add(tag);
	}
	
	public Collection<List<Tag>> getAllTags(){
		return tags.values();
	}

	public Tag getEffectTag(WurstEffect effect) {
		for(List<Tag> taglist : tags.values()){
			for(Tag tag : taglist){
				if (tag.getEffect().equals(effect)){
					return tag;
				}
			}
		}
		return null;
	}
}
