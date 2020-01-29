package com.crimson.allomancy.item.metalmind;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CopperMetalMind extends MetalMindItem {

	public CopperMetalMind() {
		super(new Item.Properties().group(Registry.allomancy_group));
		this.setRegistryName(Allomancy.MODID, "copper_metal_mind");
		this.metal = AllomancyCapability.COPPER;
		// TODO Auto-generated constructor stub
	}

	@Override
	void inactiveAction(ItemStack item, World worldIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void fillingAction(ItemStack item, World worldIn) {
		int calStrength = item.getTag().getInt("strength");
		if(item.getTag().getInt("strength") > 3)
			calStrength = 3;
		
		if(canGain(item) && (worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).experienceTotal -1 -calStrength >= 0 )) {
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).giveExperiencePoints(-1 - calStrength);
			item.getTag().putFloat("investiture", item.getTag().getFloat("investiture") + 1 + (1 * calStrength));
		}
	}

	@Override
	void tappingAction(ItemStack item, World worldIn) {
		if(0 <= (item.getTag().getFloat("investiture")) - 1 - (1 * item.getTag().getInt("strength")))
		{
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).giveExperiencePoints(1 + item.getTag().getInt("strength"));
			item.getTag().putFloat("investiture", item.getTag().getFloat("investiture") - 1 - (1 * item.getTag().getInt("strength")));
		}
		
	}

	@Override
	int getInvestitureCap() {
		return 100000;
	}

}
