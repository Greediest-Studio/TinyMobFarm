package mods.tinymobfarm.events;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.tinymobfarm.events.TinyMobFarmWorkEvent")
public class TinyMobFarmWorkEvent implements IEventCancelable {

	private final cn.davidma.tinymobfarm.common.event.TinyMobFarmWorkEvent internal;

	public TinyMobFarmWorkEvent(cn.davidma.tinymobfarm.common.event.TinyMobFarmWorkEvent internal) {
		this.internal = internal;
	}

	@ZenMethod
	public IWorld getWorld() {
		return CraftTweakerMC.getIWorld(this.internal.getWorld());
	}

	@ZenMethod
	public IBlockPos getPos() {
		return CraftTweakerMC.getIBlockPos(this.internal.getPos());
	}

	@ZenMethod
	public IItemStack getItem() {
		return CraftTweakerMC.getIItemStack(this.internal.getItem());
	}

	@ZenMethod
	public String getEntityID() {
		return this.internal.getEntityID();
	}

	@ZenMethod
	public boolean isForcePass() {
		return this.internal.isForcePass();
	}

	@ZenMethod
	public void setForcePass(boolean forcePass) {
		this.internal.setForcePass(forcePass);
	}

	@Override
	public boolean isCanceled() {
		return this.internal.isCanceled();
	}

	@Override
	public void setCanceled(boolean canceled) {
		this.internal.setCanceled(canceled);
	}
}
