package cn.davidma.tinymobfarm.common.compat;

import net.minecraftforge.common.MinecraftForge;

public final class CraftTweakerCompat {

	private CraftTweakerCompat() {
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new CraftTweakerEventBridge());
	}
}
