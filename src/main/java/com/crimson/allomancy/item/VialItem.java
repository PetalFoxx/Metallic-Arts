package com.crimson.allomancy.item;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Metal;
import com.crimson.allomancy.util.Registry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class VialItem extends Item {

    public VialItem() {
        super(new Item.Properties().group(Registry.allomancy_group).maxStackSize(16));
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "vial"));

    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity livingEntity) {

        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(livingEntity);


        if (!stack.hasTag()) {
            return stack;
        }

        for (int i = 0; i < Metal.getMetals(); i++) {
            if (stack.getTag().contains(Metal.getMetal(i).getName()) && stack.getTag().getBoolean(Metal.getMetal(i).getName())) {
                if (cap.getMetalAmounts(i) < 10) {
                    cap.setMetalAmounts(i, 10);
                }
            }
        }

        if (!((PlayerEntity) (livingEntity)).abilities.isCreativeMode) {
            stack.shrink(1);
            ((PlayerEntity) (livingEntity)).inventory.addItemStackToInventory(new ItemStack(Registry.vial, 1));
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 6;
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(playerIn);
        //If all the ones being filled are full, don't allow
        int filling = 0, full = 0;
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (itemStackIn.hasTag()) {
            for (int i = 0; i < Metal.getMetals(); i++) {
                if (itemStackIn.getTag().contains(Metal.getMetal(i).getName()) && itemStackIn.getTag().getBoolean(Metal.getMetal(i).getName())) {
                    filling++;
                    if (cap.getMetalAmounts(i) >= 10) {
                        full++;
                    }
                }
            }

            if (filling == full) {
                return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
            }

            playerIn.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);
        }
        return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            for (int i = 0; i < Metal.getMetals(); i++) {
                if (stack.getTag().getBoolean(Metal.getMetal(i).getName())) {
                    ITextComponent metal = new TranslationTextComponent("metals." + Metal.getMetal(i));
                    metal.setStyle(metal.getStyle().setColor(TextFormatting.GRAY));
                    tooltip.add(metal);
                }
            }

        }
    }


    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.hasTag() ? Rarity.UNCOMMON : Rarity.COMMON;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (group == Registry.allomancy_group) {
            items.add(new ItemStack(this, 1));

            ItemStack resultItem = new ItemStack(Registry.vial, 1);
            CompoundNBT nbt = new CompoundNBT();
            for (int i = 0; i < Metal.getMetals(); i++) {
                nbt.putBoolean(Metal.getMetal(i).getName(), true);
            }
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }

}
