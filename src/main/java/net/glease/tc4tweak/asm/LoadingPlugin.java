package net.glease.tc4tweak.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

import static net.glease.tc4tweak.asm.TC4Transformer.log;

@TransformerExclusions("net.glease.tc4tweak.asm")
@MCVersion("1.7.10")
@Name("TC4TweakCoreMod")
@SortingIndex(2000)
public class LoadingPlugin implements IFMLLoadingPlugin {
    static boolean dev;
    static boolean gt6;
    static File debugOutputDir;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"net.glease.tc4tweak.asm.TC4Transformer"};
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
    public void injectData(Map<String, Object> data) {
        dev = !(boolean) data.get("runtimeDeobfuscationEnabled");
        gt6 = ((List<?>) data.get("coremodList")).stream().anyMatch(o -> o.toString().contains("Greg-ASM"));
        if (((List<?>) data.get("coremodList")).stream().anyMatch(o -> o.toString().contains("BTPlugin"))) {
            String errorMessage = "Remove NotEnoughThaumcraftTabs. TC4Tweaks now comes with the same functionality and is incompatible with it.";
            if (!GraphicsEnvironment.isHeadless())
                JOptionPane.showMessageDialog(null, errorMessage);
            log.error("#################################################################################");
            log.error("#################################################################################");
            log.error("#################################################################################");
            log.error(errorMessage);
            log.error(errorMessage);
            log.error(errorMessage);
            log.error("#################################################################################");
            log.error("#################################################################################");
            log.error("#################################################################################");
            throw new RuntimeException(errorMessage);
        }
        debugOutputDir = new File((File) data.get("mcLocation"), ".asm");
        //noinspection ResultOfMethodCallIgnored
        debugOutputDir.mkdir();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public static boolean isDev() {
        return dev;
    }
}
