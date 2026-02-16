package cn.davidma.tinymobfarm.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;

public final class MobFarmOutputRegistry {

	private static final List<OutputRule> EXTRA_OUTPUTS = new ArrayList<OutputRule>();
	private static final Set<String> EXTRA_BLACKLIST = new HashSet<String>();

	private MobFarmOutputRegistry() {
	}

	public static void addOutput(String entityId, ItemStack stack, float chance) {
		if (entityId == null || entityId.isEmpty() || stack == null || stack.isEmpty()) return;
		if (chance > 1.0f) chance = chance / 100.0f;
		if (chance <= 0.0f) return;
		EXTRA_OUTPUTS.add(new OutputRule(entityId, stack.copy(), chance));
	}

	public static void removeOutput(String entityId, ItemStack stack) {
		if (entityId == null || entityId.isEmpty() || stack == null || stack.isEmpty()) return;
		Iterator<OutputRule> iterator = EXTRA_OUTPUTS.iterator();
		while (iterator.hasNext()) {
			OutputRule rule = iterator.next();
			if (rule.matches(entityId, stack)) iterator.remove();
		}
	}

	public static void removeAllOutput(String entityId) {
		if (entityId == null || entityId.isEmpty()) return;
		Iterator<OutputRule> iterator = EXTRA_OUTPUTS.iterator();
		while (iterator.hasNext()) {
			OutputRule rule = iterator.next();
			if (rule.entityId.equalsIgnoreCase(entityId)) iterator.remove();
		}
	}

	public static List<ItemStack> getExtraDrops(String entityId, Random rand) {
		List<ItemStack> result = new ArrayList<ItemStack>();
		if (entityId == null || entityId.isEmpty()) return result;
		for (OutputRule rule : EXTRA_OUTPUTS) {
			if (!rule.entityId.equalsIgnoreCase(entityId)) continue;
			if (rand.nextFloat() <= rule.chance) result.add(rule.stack.copy());
		}
		return result;
	}

	public static void addBlacklist(String entityId) {
		if (entityId == null || entityId.isEmpty()) return;
		EXTRA_BLACKLIST.add(entityId.toLowerCase(Locale.ROOT));
	}

	public static boolean isBlacklisted(String entityId) {
		if (entityId == null || entityId.isEmpty()) return false;
		return EXTRA_BLACKLIST.contains(entityId.toLowerCase(Locale.ROOT));
	}

	private static final class OutputRule {
		private final String entityId;
		private final ItemStack stack;
		private final float chance;

		private OutputRule(String entityId, ItemStack stack, float chance) {
			this.entityId = entityId;
			this.stack = stack;
			this.chance = chance;
		}

		private boolean matches(String entityId, ItemStack otherStack) {
			if (!this.entityId.equalsIgnoreCase(entityId)) return false;
			if (!ItemStack.areItemsEqual(this.stack, otherStack)) return false;
			return ItemStack.areItemStackTagsEqual(this.stack, otherStack);
		}
	}
}
