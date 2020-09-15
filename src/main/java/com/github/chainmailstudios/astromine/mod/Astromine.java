package com.github.chainmailstudios.astromine.mod;

import com.github.chainmailstudios.astromine.AstromineClient;
import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.AstromineDedicated;
import com.github.chainmailstudios.astromine.discoveries.AstromineDiscoveriesClient;
import com.github.chainmailstudios.astromine.discoveries.AstromineDiscoveriesCommon;
import com.github.chainmailstudios.astromine.discoveries.AstromineDiscoveriesDedicated;
import com.github.chainmailstudios.astromine.foundations.AstromineFoundationsClient;
import com.github.chainmailstudios.astromine.foundations.AstromineFoundationsCommon;
import com.github.chainmailstudios.astromine.technologies.AstromineTechnologiesClient;
import com.github.chainmailstudios.astromine.technologies.AstromineTechnologiesCommon;
import com.github.chainmailstudios.astromine.technologies.AstromineTechnologiesDedicated;
import com.github.chainmailstudios.astromine.transportations.AstromineTransportationsClient;
import com.github.chainmailstudios.astromine.transportations.AstromineTransportationsCommon;
import com.github.chainmailstudios.astromine.transportations.AstromineTransportationsDedicated;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

import java.util.Arrays;
import java.util.List;

@Mod(AstromineCommon.MOD_ID)
public class Astromine {
    private final List<AstromineCommon> modulesCommon;
    private final List<AstromineClient> modulesClient;
    private final List<AstromineDedicated> modulesServer;

    public Astromine() {
        this.modulesCommon = Arrays.asList(new AstromineCommon(), new AstromineDiscoveriesCommon(), new AstromineFoundationsCommon(), new AstromineTechnologiesCommon(), new AstromineTransportationsCommon());
        this.modulesClient = Arrays.asList(new AstromineClient(), new AstromineDiscoveriesClient(), new AstromineFoundationsClient(), new AstromineTechnologiesClient(), new AstromineTransportationsClient());
        this.modulesServer = Arrays.asList(new AstromineDedicated(), new AstromineDiscoveriesDedicated(), new AstromineTechnologiesDedicated(), new AstromineTransportationsDedicated());
    }

    public void commonSetup(FMLCommonSetupEvent e) {
        this.modulesCommon.forEach(AstromineCommon::onInitialize);
    }

    public void clientSetup(FMLClientSetupEvent e) {
        this.modulesClient.forEach(AstromineClient::onInitializeClient);
    }

    public void serverSetup(FMLDedicatedServerSetupEvent e) {
        this.modulesServer.forEach(AstromineDedicated::onInitializeServer);
    }
}
