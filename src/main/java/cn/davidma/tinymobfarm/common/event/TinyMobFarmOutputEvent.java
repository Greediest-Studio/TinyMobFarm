package cn.davidma.tinymobfarm.common.event;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class TinyMobFarmOutputEvent extends Event {

	private final World world;
	private final BlockPos pos;
	private final ItemStack item;
	private final String entityID;
	@Nullable
	private ItemStack[] output;

	public TinyMobFarmOutputEvent(World world, BlockPos pos, ItemStack item, String entityID, @Nullable ItemStack[] output) {
		this.world = world;
		this.pos = pos;
		this.item = item;
		this.entityID = entityID;
		this.output = output;
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

	@Nullable
	public ItemStack[] getOutput() {
		return this.output;
	}

	public void setOutput(@Nullable ItemStack[] output) {
		this.output = output;
	}
}
