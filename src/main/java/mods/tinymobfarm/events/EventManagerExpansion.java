package mods.tinymobfarm.events;

import cn.davidma.tinymobfarm.common.event.TinyMobFarmOutputEvent;
import cn.davidma.tinymobfarm.common.event.TinyMobFarmWorkEvent;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenExpansion("crafttweaker.api.event.IEventManager")
public class EventManagerExpansion {

	private static final EventList<mods.tinymobfarm.events.TinyMobFarmOutputEvent> OUTPUT_EVENTS = new EventList<mods.tinymobfarm.events.TinyMobFarmOutputEvent>();
	private static final EventList<mods.tinymobfarm.events.TinyMobFarmWorkEvent> WORK_EVENTS = new EventList<mods.tinymobfarm.events.TinyMobFarmWorkEvent>();

	private EventManagerExpansion() {
	}

	@ZenMethod
	public static IEventHandle onTinyMobFarmOutput(IEventManager manager, IEventHandler<mods.tinymobfarm.events.TinyMobFarmOutputEvent> handler) {
		return OUTPUT_EVENTS.add(handler);
	}

	@ZenMethod
	public static IEventHandle onTinyMobFarmWork(IEventManager manager, IEventHandler<mods.tinymobfarm.events.TinyMobFarmWorkEvent> handler) {
		return WORK_EVENTS.add(handler);
	}

	public static void publishOutput(TinyMobFarmOutputEvent event) {
		if (!OUTPUT_EVENTS.hasHandlers()) return;
		OUTPUT_EVENTS.publish(new mods.tinymobfarm.events.TinyMobFarmOutputEvent(event));
	}

	public static void publishWork(TinyMobFarmWorkEvent event) {
		if (!WORK_EVENTS.hasHandlers()) return;
		WORK_EVENTS.publish(new mods.tinymobfarm.events.TinyMobFarmWorkEvent(event));
	}
}
