package net.glease.tc4tweak.asm;

import net.glease.tc4tweak.ConfigurationHandler;
import net.glease.tc4tweak.modules.findRecipes.FindRecipes;
import net.glease.tc4tweak.modules.generateItemHash.GenerateItemHash;
import net.glease.tc4tweak.modules.getResearch.GetResearch;
import net.glease.tc4tweak.modules.objectTag.GetObjectTags;
import net.glease.tc4tweak.network.NetworkedConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.container.ContainerDummy;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ASMCallhookServer {
	private ASMCallhookServer() {
	}

	/**
	 * Called from both {@link ItemWandCasting#getFocusItem(ItemStack)} and {@link ItemWandCasting#getFocus(ItemStack)}
	 *
	 * @param stack reconstructed focus stack, not wand stack
	 * @return true if the stack is valid
	 */
	@Callhook
	public static boolean isValidFocusItemStack(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemFocusBasic;
	}

	@Callhook
	public static ResearchItem getResearch(String key) {
		return GetResearch.getResearch(key);
	}

	/**
	 * Called from {@link thaumcraft.common.lib.crafting.ThaumcraftCraftingManager#findMatchingArcaneRecipe(IInventory, EntityPlayer)}
	 */
	@Callhook
	public static ItemStack findMatchingArcaneRecipe(IInventory awb, EntityPlayer player) {
		IArcaneRecipe recipe = FindRecipes.findArcaneRecipe(awb, player);
		return recipe == null ? null : recipe.getCraftingResult(awb);
	}

	/**
	 * Called from {@link thaumcraft.common.lib.crafting.ThaumcraftCraftingManager#findMatchingArcaneRecipeAspects(IInventory, EntityPlayer)}
	 */
	@Callhook
	public static AspectList findMatchingArcaneRecipeAspects(IInventory awb, EntityPlayer player) {
		IArcaneRecipe recipe = FindRecipes.findArcaneRecipe(awb, player);
		return recipe == null ? new AspectList() : recipe.getAspects() == null ? recipe.getAspects(awb) : recipe.getAspects();
	}

	@Callhook
	public static int generateItemHash(Item item, int meta) {
		return GenerateItemHash.generateItemHash(item, meta);
	}

	@Callhook
	public static AspectList getObjectTags(ItemStack itemstack) {
		return GetObjectTags.getObjectTags(itemstack);
	}

	/**
	 * Called from {@link thaumcraft.common.container.ContainerArcaneWorkbench#onCraftMatrixChanged(IInventory)}
	 */
	@Callhook
	public static void onArcaneWorkbenchChanged(TileArcaneWorkbench tileEntity, InventoryPlayer ip) {
		// only check synced config if in remote world
		if (ConfigurationHandler.INSTANCE.isCheckWorkbenchRecipes() && (!tileEntity.getWorldObj().isRemote || NetworkedConfiguration.isCheckWorkbenchRecipes())) {
			InventoryCrafting ic = new InventoryCrafting(new ContainerDummy(), 3, 3);
			for (int a = 0; a < 9; ++a) {
				ic.setInventorySlotContents(a, tileEntity.getStackInSlot(a));
			}
			tileEntity.setInventorySlotContentsSoftly(9, CraftingManager.getInstance().findMatchingRecipe(ic, tileEntity.getWorldObj()));
		} else {
			tileEntity.setInventorySlotContentsSoftly(9, null);
		}
		if (tileEntity.getStackInSlot(9) == null && tileEntity.getStackInSlot(10) != null && tileEntity.getStackInSlot(10).getItem() instanceof ItemWandCasting) {
			ItemWandCasting wand = (ItemWandCasting) tileEntity.getStackInSlot(10).getItem();
			if (wand.consumeAllVisCrafting(tileEntity.getStackInSlot(10), ip.player, ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(tileEntity, ip.player), false)) {
				tileEntity.setInventorySlotContentsSoftly(9, ThaumcraftCraftingManager.findMatchingArcaneRecipe(tileEntity, ip.player));
			}
		}
	}

	@Callhook
	public static void postThaumcraftApiClinit() {
		ThaumcraftApi.objectTags = GetObjectTags.newReplacementObjectTagsMap();
	}

}
