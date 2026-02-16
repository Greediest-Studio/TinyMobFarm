package cn.davidma.tinymobfarm.common.compat;

import cn.davidma.tinymobfarm.common.event.TinyMobFarmOutputEvent;
import cn.davidma.tinymobfarm.common.event.TinyMobFarmWorkEvent;
import mods.tinymobfarm.events.EventManagerExpansion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CraftTweakerEventBridge {

	@SubscribeEvent
	public void onOutput(TinyMobFarmOutputEvent event) {
		EventManagerExpansion.publishOutput(event);
	}

	@SubscribeEvent
	public void onWork(TinyMobFarmWorkEvent event) {
		EventManagerExpansion.publishWork(event);
	}
}
