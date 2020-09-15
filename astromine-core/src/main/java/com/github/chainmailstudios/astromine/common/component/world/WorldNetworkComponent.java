/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.common.component.world;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.github.chainmailstudios.astromine.common.network.NetworkInstance;
import com.github.chainmailstudios.astromine.common.network.NetworkMemberNode;
import com.github.chainmailstudios.astromine.common.network.NetworkNode;
import com.github.chainmailstudios.astromine.common.network.type.base.NetworkType;
import com.github.chainmailstudios.astromine.common.registry.NetworkTypeRegistry;
import nerdhub.cardinal.components.api.component.Component;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Sets;
import java.util.Set;

public class WorldNetworkComponent {
	private final Set<NetworkInstance> instances = Sets.newConcurrentHashSet();

	public void addInstance(NetworkInstance instance) {
		if (!instance.nodes.isEmpty())
			this.instances.add(instance);
	}

	public void removeInstance(NetworkInstance instance) {
		this.instances.remove(instance);
	}

	public NetworkInstance getInstance(NetworkType type, BlockPos position) {
		return this.instances.stream().filter(instance -> instance.getType() == type && instance.nodes.stream().anyMatch(node -> node.getBlockPos().equals(position))).findFirst().orElse(NetworkInstance.EMPTY);
	}

	public boolean containsInstance(NetworkType type, BlockPos position) {
		return getInstance(type, position) != NetworkInstance.EMPTY;
	}

	@NotNull
	public CompoundNBT toTag(CompoundNBT tag) {
		ListNBT instanceTags = new ListNBT();

		for (NetworkInstance instance : instances) {
			ListNBT nodeList = new ListNBT();
			for (NetworkNode node : instance.nodes) {
				nodeList.add(LongNBT.valueOf(node.getPos()));
			}

			ListNBT memberList = new ListNBT();
			for (NetworkMemberNode member : instance.members) {
				memberList.add(member.toTag(new CompoundNBT()));
			}

			CompoundNBT data = new CompoundNBT();

			data.putString("type", NetworkTypeRegistry.INSTANCE.getKey(instance.getType()).toString());
			data.put("nodes", nodeList);
			data.put("members", memberList);
			data.put("additionalData", instance.getAdditionalData());

			instanceTags.add(data);
		}

		tag.put("instanceTags", instanceTags);

		return tag;
	}

	public void fromTag(CompoundNBT tag) {
		ListNBT instanceTags = tag.getList("instanceTags", NbtType.COMPOUND);
		for (INBT instanceTag : instanceTags) {
			CompoundNBT dataTag = (CompoundNBT) instanceTag;
			ListNBT nodeList = dataTag.getList("nodes", NbtType.LONG);
			ListNBT memberList = dataTag.getList("members", NbtType.COMPOUND);

			NetworkType type = NetworkTypeRegistry.INSTANCE.get(new ResourceLocation(dataTag.getString("type")));
			NetworkInstance instance = new NetworkInstance( type);

			for (INBT nodeKey : nodeList) {
				instance.addNode(NetworkNode.of(((LongNBT) nodeKey).getAsLong()));
			}

			for (INBT memberTag : memberList) {
				instance.addMember(NetworkMemberNode.fromTag((CompoundNBT) memberTag));
			}

			if (dataTag.contains("additionalData")) {
				instance.setAdditionalData(dataTag.getCompound("additionalData"));
			}

			addInstance(instance);
		}
	}

	public void tick(World world) {
		this.instances.removeIf(NetworkInstance::isStupidlyEmpty);
		this.instances.forEach(instance -> instance.tick(world));
	}
}
