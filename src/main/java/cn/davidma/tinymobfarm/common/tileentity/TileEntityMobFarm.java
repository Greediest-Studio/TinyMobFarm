package cn.davidma.tinymobfarm.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import cn.davidma.tinymobfarm.common.block.BlockMobFarm;
import cn.davidma.tinymobfarm.common.event.TinyMobFarmOutputEvent;
import cn.davidma.tinymobfarm.common.event.TinyMobFarmWorkEvent;
import cn.davidma.tinymobfarm.core.ConfigTinyMobFarm;
import cn.davidma.tinymobfarm.core.EnumMobFarm;
import cn.davidma.tinymobfarm.core.Reference;
import cn.davidma.tinymobfarm.core.util.EntityHelper;
import cn.davidma.tinymobfarm.core.util.FakePlayerHelper;
import cn.davidma.tinymobfarm.core.util.MobFarmOutputRegistry;
import cn.davidma.tinymobfarm.core.util.NBTHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityMobFarm extends TileEntity implements ITickable {
	
	private ItemStackHandler inventory = new ItemStackHandler(1);
	private EnumMobFarm mobFarmData;
	private EntityLiving model;
	private EnumFacing modelFacing;
	private int currProgress;
	private boolean powered;
	private boolean shouldUpdate;

	@Override
	public void update() {
		if (this.shouldUpdate) {
			this.updateModel();
			this.updateRedstone();
			this.shouldUpdate = false;
		}
		if (this.isWorking()) {
			this.currProgress++;
			if (!this.world.isRemote && this.mobFarmData != null) {
				if (this.currProgress >= this.mobFarmData.getMaxProgress()) {
					this.currProgress = 0;
					
					this.generateDrops();
					
					FakePlayer daniel = FakePlayerHelper.getPlayer((WorldServer) world);
					this.getLasso().damageItem(this.mobFarmData.getRandomDamage(this.world.rand), daniel);
					
					this.saveAndSync();
				}
			}
		} else {
			this.currProgress = 0;
		}
	}
	
	private void generateDrops() {
		ItemStack lasso = this.getLasso();
		String lootTableLocation = NBTHelper.getBaseTag(lasso).getString(NBTHelper.MOB_LOOTTABLE_LOCATION);
		List<ItemStack> drops = new ArrayList<>();
		if (!lootTableLocation.isEmpty()) {
			drops.addAll(EntityHelper.generateLoot(new ResourceLocation(lootTableLocation), this.world));
		}
		String entityId = NBTHelper.getBaseTag(lasso).getCompoundTag(NBTHelper.MOB_DATA).getString("id");
		this.applyCustomDrops(drops, entityId);
		TinyMobFarmOutputEvent event = new TinyMobFarmOutputEvent(this.world, this.pos, lasso, entityId, drops.toArray(new ItemStack[0]));
		if (MinecraftForge.EVENT_BUS.post(event)) return;
		ItemStack[] output = event.getOutput();
		drops.clear();
		if (output != null) {
			for (ItemStack stack: output) {
				if (stack != null && !stack.isEmpty()) drops.add(stack);
			}
		}
		if (drops.isEmpty()) return;
		for (EnumFacing facing: EnumFacing.values()) {
			TileEntity tileEntity = this.world.getTileEntity(this.pos.offset(facing));
			if (tileEntity != null) {
				
				if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
					IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
					for (int i = 0; i < drops.size(); i++) {
						ItemStack remain = ItemHandlerHelper.insertItemStacked(itemHandler, drops.get(i), false);
						if (remain.isEmpty()) {
							drops.remove(i);
							i--;
						}
					}
				}
				
				if (drops.isEmpty()) return;
			}
		}
		
		for (ItemStack stack: drops) {
			EntityItem entityItem = new EntityItem(this.world, this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5, stack);
			this.world.spawnEntity(entityItem);
		}
		
	}

	private void applyCustomDrops(List<ItemStack> drops, String entityId) {
		if (entityId == null || entityId.isEmpty()) return;
		if (ConfigTinyMobFarm.MOB_EXTRA_DROPS != null) {
			for (String rule: ConfigTinyMobFarm.MOB_EXTRA_DROPS) {
			if (rule == null || rule.trim().isEmpty()) continue;
			String trimmed = rule.trim();
			int lastColon = trimmed.lastIndexOf(':');
			if (lastColon <= 0 || lastColon >= trimmed.length() - 1) continue;
			String left = trimmed.substring(0, lastColon);
			String chanceStr = trimmed.substring(lastColon + 1);
			double chance;
			try {
				chance = Double.parseDouble(chanceStr);
			} catch (NumberFormatException ex) {
				continue;
			}
			if (chance > 1.0d) chance = chance / 100.0d;
			if (chance <= 0.0d) continue;

			String itemPart = left;
			int atIndex = left.lastIndexOf('@');
			int meta = 0;
			if (atIndex >= 0 && atIndex < left.length() - 1) {
				itemPart = left.substring(0, atIndex);
				try {
					meta = Integer.parseInt(left.substring(atIndex + 1));
				} catch (NumberFormatException ex) {
					meta = 0;
				}
			}

			String[] parts = itemPart.split(":");
			if (parts.length < 4) continue;
			String entityKey = parts[0] + ":" + parts[1];
			String altEntityKey = null;
			String namespacePrefix = parts[0] + ".";
			if (parts[1].startsWith(namespacePrefix)) {
				altEntityKey = parts[0] + ":" + parts[1].substring(namespacePrefix.length());
			}
			if (!entityKey.equals(entityId) && (altEntityKey == null || !altEntityKey.equals(entityId))) continue;
			StringBuilder itemIdBuilder = new StringBuilder();
			for (int i = 2; i < parts.length; i++) {
				if (i > 2) itemIdBuilder.append(":");
				itemIdBuilder.append(parts[i]);
			}
			String itemId = itemIdBuilder.toString();
			Item item = Item.getByNameOrId(itemId);
			if (item == null) continue;
			if (this.world.rand.nextDouble() <= chance) {
				ItemStack stack = new ItemStack(item, 1, Math.max(0, meta));
				if (!stack.isEmpty()) drops.add(stack);
			}
			}
		}
		drops.addAll(MobFarmOutputRegistry.getExtraDrops(entityId, this.world.rand));
	}
	
	private void updateModel() {
		if (this.world.isRemote) {
			if (this.getLasso().isEmpty()) {
				this.model = null;
			} else {
				NBTTagCompound nbt = NBTHelper.getBaseTag(this.getLasso());
				String mobName = nbt.getString(NBTHelper.MOB_NAME);
				if (this.model == null || !this.model.getName().equals(mobName)) {
					NBTTagCompound entityData = nbt.getCompoundTag(NBTHelper.MOB_DATA);
					Entity newModel = EntityList.createEntityFromNBT(entityData, this.world);
					
					if (newModel != null && newModel instanceof EntityLiving) {
						this.model = (EntityLiving) newModel;
						this.modelFacing = this.world.getBlockState(this.pos).getValue(BlockMobFarm.FACING);
					}
				}
			}
		}
	}
	
	public boolean isWorking() {
		if (this.mobFarmData == null || this.getLasso().isEmpty() || this.isPowered()) return false;
		ItemStack lasso = this.getLasso();
		String entityId = NBTHelper.getBaseTag(lasso).getCompoundTag(NBTHelper.MOB_DATA).getString("id");
		TinyMobFarmWorkEvent event = new TinyMobFarmWorkEvent(this.world, this.pos, lasso, entityId);
		if (MinecraftForge.EVENT_BUS.post(event)) return false;
		if (event.isForcePass()) return true;
		return this.mobFarmData.isLassoValid(lasso);
	}
	
	public void updateRedstone() {
		this.powered = this.world.isBlockPowered(this.pos);
	}
	
	public ItemStack getLasso() {
		return this.inventory.getStackInSlot(0);
	}
	
	public void setMobFarmData(EnumMobFarm mobFarmData) {
		this.mobFarmData = mobFarmData;
	}
	
	public boolean isPowered() {
		return this.powered;
	}
	
	@Deprecated
	public ItemStackHandler getInventory() {
		return this.inventory;
	}
	
	public double getScaledProgress() {
		if (this.mobFarmData == null) return 0;
		return this.currProgress / (double) this.mobFarmData.getMaxProgress();
	}
	
	public EntityLiving getModel() {
		return this.model;
	}
	
	public EnumFacing getModelFacing() {
		return this.modelFacing;
	}
	
	public String getUnlocalizedName() {
		if (this.mobFarmData == null) return "block." + Reference.MOD_ID + ".default_mob_farm";
		return this.mobFarmData.getUnlocalizedName();
	}
	
	public void saveAndSync() {
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
		this.world.notifyBlockUpdate(pos, state, state, 3);
		this.markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.mobFarmData = EnumMobFarm.values()[nbt.getInteger(NBTHelper.MOB_FARM_DATA)];
		this.currProgress = nbt.getInteger(NBTHelper.CURR_PROGRESS);
		this.inventory.deserializeNBT(nbt.getCompoundTag(NBTHelper.INVENTORY));
		this.shouldUpdate = true;
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (this.mobFarmData == null) return nbt;
		nbt.setInteger(NBTHelper.MOB_FARM_DATA, this.mobFarmData.ordinal());
		nbt.setInteger(NBTHelper.CURR_PROGRESS, this.currProgress);
		nbt.setTag(NBTHelper.INVENTORY, this.inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readFromNBT(packet.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

}
