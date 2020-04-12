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

public class SteelMetalMind extends MetalMindItem {

	public SteelMetalMind() {
		super(new Item.Properties().group(Registry.allomancy_group));
		this.setRegistryName(Allomancy.MODID, "steel_metal_mind");
		this.metal = AllomancyCapability.STEEL;
		// TODO Auto-generated constructor stub
	}
	
	BlockPos lastPos = null;

	@Override
	void inactiveAction(ItemStack item, World worldIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void fillingAction(ItemStack item, World worldIn) {
		int calStrength = item.getTag().getInt("strength");
		if(item.getTag().getInt("strength") > 3)
			calStrength = 3;
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.SPEED, 10, -1 - calStrength, true, false));
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 10, (-1 - calStrength) / 2, true, false));
		if(canGain(item) && !worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition().equals(lastPos))
			gain(item);
		lastPos = worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition();
	}

	@Override
	void tappingAction(ItemStack item, World worldIn) {
		if(canLose(item))
		{
			//worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(Registry.STORE_IRON.getEffects().get(0));
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.SPEED, 10, 0 + item.getTag().getInt("strength"), true, false));
			if(item.getTag().getInt("strength") >= 1)
				worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 10, (0 + item.getTag().getInt("strength")) / 2, true, false));
			
			if(!worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition().equals(lastPos))
				lose(item);
		}
		lastPos = worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition();
		
	}

	@Override
	int getInvestitureCap() {
		return 100000;
	}
	

}
