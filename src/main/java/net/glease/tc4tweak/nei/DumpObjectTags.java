package net.glease.tc4tweak.nei;

import codechicken.nei.config.DataDumper;
import net.glease.tc4tweak.CommonUtils;
import net.glease.tc4tweak.modules.objectTag.GetObjectTags;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.io.File;

public class DumpObjectTags extends DataDumper {
    public DumpObjectTags() {
        super("tools.dump.tc4tweaks.tc4tag");
    }

    @Override
    public String[] header() {
        return new String[]{"ItemStack Display Name", "Item Unlocalized Name", "Item Numerical ID", "Meta", "Aspect List"};
    }

    @Override
    public Iterable<String[]> dump(int mode) {
        return () -> GetObjectTags.stream()
                .filter(e -> e.getKey().getItem() != null && e.getValue() != null)
                .map(e -> new String[]{
                        safeGetDisplayName(e.getKey()),
                        e.getKey().getItem().getUnlocalizedName(),
                        String.valueOf(Item.getIdFromItem(e.getKey().getItem())),
                        String.valueOf(Items.feather.getDamage(e.getKey())),
                        CommonUtils.toString(e.getValue())
                }).iterator();
    }

    private static String safeGetDisplayName(ItemStack is) {
        try {
            return is.getDisplayName();
        } catch (Exception e) {
            return "~~ERROR~~";
        }
    }

    @Override
    public IChatComponent dumpMessage(File file) {
        return new ChatComponentTranslation("nei.options.tools.dump.tc4tweaks.tc4tags.dumped", "dumps/" + file.getName());
    }

    @Override
    public int modeCount() {
        return 1;
    }
}
