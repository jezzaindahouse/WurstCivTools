package com.github.maxopoly.WurstCivTools.misc;

import java.util.UUID;

public class HookData {
	private final UUID source;
	private UUID target;
	
	
	public HookData(UUID source, UUID target) {
		this.source = source;
		this.target = target;
	}
	public UUID getSource() {
		return source;
	}
	public UUID getTarget() {
		return target;
	}
	public void setTarget(UUID target) {
		this.target = target;
	}
	
	

}
