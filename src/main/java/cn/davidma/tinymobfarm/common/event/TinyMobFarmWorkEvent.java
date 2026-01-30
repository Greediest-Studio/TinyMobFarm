package cn.davidma.tinymobfarm.common.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class TinyMobFarmWorkEvent extends Event {

	private final World world;
	private final BlockPos pos;
	private final ItemStack item;
	private final String entityID;
	private boolean forcePass;

	public TinyMobFarmWorkEvent(World world, BlockPos pos, ItemStack item, String entityID) {
		this.world = world;
		this.pos = pos;
		this.item = item;
		this.entityID = entityID;
		this.forcePass = false;
	}

	public World getWorld() {
		return this.world;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public String getEntityID() {
		return this.entityID;
	}

	public boolean isForcePass() {
		return this.forcePass;
	}

	public void setForcePass(boolean forcePass) {
		this.forcePass = forcePass;
	}
}
