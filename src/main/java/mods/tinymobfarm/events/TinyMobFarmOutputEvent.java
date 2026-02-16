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
@ZenClass("mods.tinymobfarm.events.TinyMobFarmOutputEvent")
public class TinyMobFarmOutputEvent implements IEventCancelable {

	private final cn.davidma.tinymobfarm.common.event.TinyMobFarmOutputEvent internal;

	public TinyMobFarmOutputEvent(cn.davidma.tinymobfarm.common.event.TinyMobFarmOutputEvent internal) {
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
	public IItemStack[] getOutput() {
		if (this.internal.getOutput() == null) return new IItemStack[0];
		return CraftTweakerMC.getIItemStacks(this.internal.getOutput());
	}

	@ZenMethod
	public void setOutput(IItemStack[] output) {
		if (output == null) {
			this.internal.setOutput(null);
			return;
		}
		this.internal.setOutput(CraftTweakerMC.getItemStacks(output));
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
