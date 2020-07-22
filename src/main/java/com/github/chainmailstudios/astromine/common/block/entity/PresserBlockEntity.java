package com.github.chainmailstudios.astromine.common.block.entity;

import com.github.chainmailstudios.astromine.common.block.base.DefaultedBlockWithEntity;
import com.github.chainmailstudios.astromine.common.block.entity.base.DefaultedEnergyItemBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.compatibility.ItemInventoryFromInventoryComponent;
import com.github.chainmailstudios.astromine.common.network.NetworkMember;
import com.github.chainmailstudios.astromine.common.network.NetworkMemberType;
import com.github.chainmailstudios.astromine.common.network.NetworkType;
import com.github.chainmailstudios.astromine.common.recipe.PressingRecipe;
import com.github.chainmailstudios.astromine.registry.AstromineBlockEntityTypes;
import com.github.chainmailstudios.astromine.registry.AstromineNetworkTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class PresserBlockEntity extends DefaultedEnergyItemBlockEntity implements NetworkMember, Tickable {
	public int progress = 0;
	public int limit = 100;

	public boolean shouldTry = true;

	public boolean isActive = false;

	public boolean[] activity = {false, false, false, false, false};

	Optional<PressingRecipe> recipe = Optional.empty();

	public PresserBlockEntity(BlockEntityType<?> type) {
		super(type);

		addEnergyListener(() -> shouldTry = true);
	}

	abstract int getMachineSpeed();

	@Override
	protected ItemInventoryComponent createItemComponent() {
		return new SimpleItemInventoryComponent(2).withInsertPredicate((direction, itemStack, slot) -> {
			return slot == 1;
		}).withExtractPredicate((direction, stack, slot) -> {
			return slot == 0;
		}).withListener((inv) -> {
			shouldTry = true;
		});
	}

	@Override
	protected @NotNull Map<NetworkType, Collection<NetworkMemberType>> createMemberProperties() {
		return ofTypes(AstromineNetworkTypes.ENERGY, REQUESTER);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		progress = tag.getInt("progress");
		limit = tag.getInt("limit");
		shouldTry = true;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("progress", progress);
		tag.putInt("limit", limit);
		return super.toTag(tag);
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isClient()) return;
		if (shouldTry) {
			if (!recipe.isPresent()) {
				if (hasWorld() && !world.isClient) {
					recipe = (Optional<PressingRecipe>) world.getRecipeManager().getFirstMatch((RecipeType) PressingRecipe.Type.INSTANCE, ItemInventoryFromInventoryComponent.of(itemComponent), world);
				}
			}
			if (recipe.isPresent() && recipe.get().matches(ItemInventoryFromInventoryComponent.of(itemComponent), world)) {
				limit = recipe.get().getTime() * 2;

				double consumed = recipe.get().getEnergyConsumed() / (double) (limit / 2);

				ItemStack output = recipe.get().getOutput();

				for (int i = 0; i < getMachineSpeed(); i++) {
					boolean isEmpty = itemComponent.getStack(0).isEmpty();
					boolean isEqual = ItemStack.areItemsEqual(itemComponent.getStack(0), output) && ItemStack.areTagsEqual(itemComponent.getStack(0), output);

					if (asEnergy().use(getMachineSpeed() == 1 ? consumed * 1.25 : consumed) && (isEmpty || isEqual) && itemComponent.getStack(0).getCount() + output.getCount() <= itemComponent.getStack(0).getMaxCount()) {
						if (progress >= limit) {
							recipe.get().craft(ItemInventoryFromInventoryComponent.of(itemComponent));

							if (isEmpty) {
								itemComponent.setStack(0, output);
							} else {
								itemComponent.getStack(0).increment(output.getCount());
							}

							progress = 0;
						} else {
							++progress;
						}
						isActive = true;
					}
				}
			} else {
				shouldTry = false;
				isActive = false;
				progress = 0;
			}
		} else {
			progress = 0;
			isActive = false;
		}

		if (activity.length - 1 >= 0) System.arraycopy(activity, 1, activity, 0, activity.length - 1);

		activity[4] = isActive;

		if (isActive && !activity[0]) {
			world.setBlockState(getPos(), world.getBlockState(getPos()).with(DefaultedBlockWithEntity.ACTIVE, true));
		} else if (!isActive && activity[0]) {
			world.setBlockState(getPos(), world.getBlockState(getPos()).with(DefaultedBlockWithEntity.ACTIVE, false));
		}
	}

	public static class Primitive extends PresserBlockEntity {
		public Primitive() {
			super(AstromineBlockEntityTypes.PRIMITIVE_PRESSER);
		}

		@Override
		int getMachineSpeed() {
			return 1;
		}

		@Override
		protected int getEnergySize() {
			return 2048;
		}
	}

	public static class Basic extends PresserBlockEntity {
		public Basic() {
			super(AstromineBlockEntityTypes.BASIC_PRESSER);
		}

		@Override
		public int getMachineSpeed() {
			return 2;
		}

		@Override
		protected int getEnergySize() {
			return 8192;
		}
	}

	public static class Advanced extends PresserBlockEntity {
		public Advanced() {
			super(AstromineBlockEntityTypes.ADVANCED_PRESSER);
		}

		@Override
		public int getMachineSpeed() {
			return 4;
		}

		@Override
		protected int getEnergySize() {
			return 32767;
		}
	}

	public static class Elite extends PresserBlockEntity {
		public Elite() {
			super(AstromineBlockEntityTypes.ELITE_PRESSER);
		}

		@Override
		int getMachineSpeed() {
			return 8;
		}

		@Override
		protected int getEnergySize() {
			return 131068;
		}
	}
}
