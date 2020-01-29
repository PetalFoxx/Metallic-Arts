package com.crimson.allomancy.item;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.entity.NuggetEntity;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NuggetItem extends Item {
	private static int metal;

	public NuggetItem(int metal) {
		super(new Item.Properties().group(Registry.allomancy_group).maxStackSize(64));
        this.metal = metal;
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, Registry.metals[metal] + "_nugget"));
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		
        ItemStack itemstack = player.getHeldItem(hand);

        if (AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.STEEL)) {    // make sure there is always an item available
            if (!world.isRemote) {

                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                float strength = AllomancyCapability.forPlayer(player).getBurnStrength(AllomancyCapability.STEEL);
                NuggetEntity nuggetEntity = new NuggetEntity(Registry.getNuggetEntities()[metal], player, world, metal);
                nuggetEntity.setStrength(strength);
                nuggetEntity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.5F * (strength / 10), 1.0F);
                
                world.addEntity(nuggetEntity);

                return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));

            }

        }
        
        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
    }

}
