package dev.redstudio.witherconfig.asm;

import dev.redstudio.witherconfig.config.WitherConfigConfig;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name()
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("dev.redstudio.witherconfig.asm")
public final class WitherConfigPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        final List<String> mixins = new ArrayList<>();

        mixins.add("mixins.witherConfig.json");

        if (WitherConfigConfig.common.boatJailFix.enabled)
            mixins.add("mixins.witherConfig.boatFix.json");

        return mixins;
    }
}
