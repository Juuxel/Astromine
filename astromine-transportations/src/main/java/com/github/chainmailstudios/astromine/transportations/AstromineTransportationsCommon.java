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

package com.github.chainmailstudios.astromine.transportations;

import com.github.chainmailstudios.astromine.AstromineClient;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.AstromineDedicated;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlockEntityTypes;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlocks;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsItems;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsSoundEvents;
import com.github.chainmailstudios.astromine.transportations.registry.client.AstromineTransportationsItemGroups;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod("astrominetransportations")
public class AstromineTransportationsCommon extends AstromineCommon {
	@Override
	public void onInitialize(IEventBus modBus, IEventBus forgeBus) {
		AstromineTransportationsBlocks.initialize();
		AstromineTransportationsItems.initialize();
		AstromineTransportationsItemGroups.initialize();
		AstromineTransportationsBlockEntityTypes.initialize();
		AstromineTransportationsSoundEvents.initialize(modBus);
	}

	@Override
	public Supplier<? extends AstromineClient> getClientInitializer() {
		return AstromineTransportationsClient::new;
	}

	@Override
	public Supplier<? extends AstromineDedicated> getServerInitializer() {
		return AstromineTransportationsDedicated::new;
	}
}
