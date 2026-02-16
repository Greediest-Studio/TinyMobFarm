package mods.tinymobfarm;

import cn.davidma.tinymobfarm.core.util.MobFarmOutputRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.tinymobfarm.TinyMobFarm")
public class TinyMobFarm {

	@ZenMethod
	public static void addOutput(IEntityDefinition entity, IItemStack item, float possibility) {
		if (entity == null || item == null) return;
		CraftTweakerAPI.apply(new ActionAddOutput(entity.getId(), CraftTweakerMC.getItemStack(item), possibility));
	}

	@ZenMethod
	public static void setBlacklist(IEntityDefinition entity) {
		if (entity == null) return;
		CraftTweakerAPI.apply(new ActionBlacklist(entity.getId()));
	}

	@ZenMethod
	public static void removeOutput(IEntityDefinition entity, IItemStack item) {
		if (entity == null || item == null) return;
		CraftTweakerAPI.apply(new ActionRemoveOutput(entity.getId(), CraftTweakerMC.getItemStack(item)));
	}

	@ZenMethod
	public static void removeAllOutput(IEntityDefinition entity) {
		if (entity == null) return;
		CraftTweakerAPI.apply(new ActionRemoveAllOutput(entity.getId()));
	}

	private static final class ActionAddOutput implements IAction {
		private final String entityId;
		private final ItemStack stack;
		private final float chance;

		private ActionAddOutput(String entityId, ItemStack stack, float chance) {
			this.entityId = entityId;
			this.stack = stack;
			this.chance = chance;
		}

		@Override
		public void apply() {
			MobFarmOutputRegistry.addOutput(entityId, stack, chance);
		}

		@Override
		public String describe() {
			return "Adding TinyMobFarm output for " + entityId + ": " + stack;
		}
	}

	private static final class ActionBlacklist implements IAction {
		private final String entityId;

		private ActionBlacklist(String entityId) {
			this.entityId = entityId;
		}

		@Override
		public void apply() {
			MobFarmOutputRegistry.addBlacklist(entityId);
		}

		@Override
		public String describe() {
			return "Blacklisting TinyMobFarm entity: " + entityId;
		}
	}

	private static final class ActionRemoveOutput implements IAction {
		private final String entityId;
		private final ItemStack stack;

		private ActionRemoveOutput(String entityId, ItemStack stack) {
			this.entityId = entityId;
			this.stack = stack;
		}

		@Override
		public void apply() {
			MobFarmOutputRegistry.removeOutput(entityId, stack);
		}

		@Override
		public String describe() {
			return "Removing TinyMobFarm output for " + entityId + ": " + stack;
		}
	}

	private static final class ActionRemoveAllOutput implements IAction {
		private final String entityId;

		private ActionRemoveAllOutput(String entityId) {
			this.entityId = entityId;
		}

		@Override
		public void apply() {
			MobFarmOutputRegistry.removeAllOutput(entityId);
		}

		@Override
		public String describe() {
			return "Removing all TinyMobFarm outputs for " + entityId;
		}
	}
}
