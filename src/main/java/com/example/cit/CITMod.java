package com.example.cit;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CITMod implements ModInitializer {
    public static final String MOD_ID = "cit-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CIT Mod initialized for 1.21.4");
    }
}
