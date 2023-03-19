package com.lowdragmc.lowdraglib.fabric;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.ServerCommands;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.utils.fabric.ReflectionUtilsImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class LDLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LDLib.init();
        // load entry points
        for (ILDLibPlugin ldlibPugin : FabricLoader.getInstance().getEntrypoints("ldlib_pugin", ILDLibPlugin.class)) {
            ldlibPugin.onLoad();
        }
        // hook server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlatformImpl.SERVER = server;
        });
        // register server commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ServerCommands.createServerCommands().forEach(dispatcher::register));
        // init common features
        CommonProxy.init();
        // execute annotation searching
        ReflectionUtilsImpl.execute();
        // register payload
        TypedPayloadRegistries.postInit();
    }

}