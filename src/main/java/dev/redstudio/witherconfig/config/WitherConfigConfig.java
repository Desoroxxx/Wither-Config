package dev.redstudio.witherconfig.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static dev.redstudio.witherconfig.utils.ModReference.ID;
import static dev.redstudio.witherconfig.utils.ModReference.NAME;

@Config(modid = ID, name = NAME)
public class WitherConfigConfig {

    public static final Common common = new Common();

    public static class Common {

        public final SummonSequence summonSequence = new SummonSequence();
        public final Skulls skulls = new Skulls();

        // The Wither attributes
        public double maxHealth = 300;
        public double movementSpeed = 0.6; // The "default movement speed of the Wither, it's following speed is "added" to it"
        public double followRange = 40; // Range for the Wither to look for it's target to follow it
        public double armor = 4;

        public float unarmoredFlyHeight = 5;
        public float followDistance = 9;

        public boolean breakBlocksWhenTargetingPlayer = false;

        public static class SummonSequence {

            public int length = 220;
            public float endExplosionStrength = 7;
        }

        public static class Skulls {

            public float damage = 8;
            public float magicDamage = 5;
            public float healOnKill = 5;
            public float explosionStrength = 1;

            public String[] effects = new String[]{"minecraft:wither;20;1"};
        }
    }

    @Mod.EventBusSubscriber(modid = ID)
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
            if (onConfigChangedEvent.getModID().equals(ID))
                ConfigManager.sync(ID, Config.Type.INSTANCE);
        }
    }
}