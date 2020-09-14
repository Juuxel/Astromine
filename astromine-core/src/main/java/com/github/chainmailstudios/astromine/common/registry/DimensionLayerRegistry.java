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

package com.github.chainmailstudios.astromine.common.registry;

import com.github.chainmailstudios.astromine.common.entity.placer.EntityPlacer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;

public class DimensionLayerRegistry {
	public static final DimensionLayerRegistry INSTANCE = new DimensionLayerRegistry();

	private final Map<ResourceKey<Level>, Tuple<Integer, ResourceKey<Level>>> TOP_ENTRIES = new HashMap<>();
	private final Map<ResourceKey<Level>, Tuple<Integer, ResourceKey<Level>>> BOTTOM_ENTRIES = new HashMap<>();
	private final Map<ResourceKey<Level>, Tuple<EntityPlacer, EntityPlacer>> PLACERS = new HashMap<>();

	private DimensionLayerRegistry() {

	}

	public void register(Type type, ResourceKey<Level> dimension, Integer levelY, ResourceKey<Level> newDimension, EntityPlacer placer) {
		final Map<ResourceKey<Level>, Tuple<Integer, ResourceKey<Level>>> ENTRIES = type == Type.TOP ? this.TOP_ENTRIES : this.BOTTOM_ENTRIES;

		ENTRIES.put(dimension, new Tuple<>(levelY, newDimension));

		if (PLACERS.containsKey(dimension)) {
			PLACERS.put(dimension, new Tuple<>(type == Type.TOP ? placer : PLACERS.get(dimension).getA(), type == Type.BOTTOM ? placer : PLACERS.get(dimension).getB()));
		} else {
			PLACERS.put(dimension, new Tuple<>(type == Type.TOP ? placer : null, type == Type.BOTTOM ? placer : null));
		}
	}

	public int getLevel(Type type, ResourceKey<Level> dimension) {
		final Map<ResourceKey<Level>, Tuple<Integer, ResourceKey<Level>>> ENTRIES = type == Type.TOP ? this.TOP_ENTRIES : this.BOTTOM_ENTRIES;

		final Tuple<Integer, ResourceKey<Level>> pair = ENTRIES.get(dimension);

		return pair == null ? Integer.MIN_VALUE : pair.getA();
	}

	public ResourceKey<Level> getDimension(Type type, ResourceKey<Level> dimension) {
		final Map<ResourceKey<Level>, Tuple<Integer, ResourceKey<Level>>> ENTRIES = type == Type.TOP ? this.TOP_ENTRIES : this.BOTTOM_ENTRIES;

		final Tuple<Integer, ResourceKey<Level>> pair = ENTRIES.get(dimension);

		return pair == null ? null : pair.getB();
	}

	public EntityPlacer getPlacer(Type type, ResourceKey<Level> dimension) {
		return type == Type.TOP ? PLACERS.get(dimension).getA() : PLACERS.get(dimension).getB();
	}

	public enum Type {
		TOP,
		BOTTOM
	}
}
