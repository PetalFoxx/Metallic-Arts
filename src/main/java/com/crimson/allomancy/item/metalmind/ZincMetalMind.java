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

public class ZincMetalMind extends MetalMindItem {

	public ZincMetalMind() {
		super(new Item.Properties().group(Registry.allomancy_group));
		this.setRegistryName(Allomancy.MODID, "zinc_metal_mind");
		this.metal = AllomancyCapability.ZINC;
		// TODO Auto-generated constructor stub
	}
	
	BlockPos lastPos = null;

	@Override
	void inactiveAction(ItemStack item, World worldIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void fillingAction(ItemStack item, World worldIn) {
		if(item.getTag().getBoolean("used") && canGain(item)) {
			gain(item);
			item.getTag().putBoolean("used", false);
		}
	}

	@Override
	void tappingAction(ItemStack item, World worldIn) {
		if(item.getTag().getBoolean("used") && canLose(item)) {
			lose(item);
			item.getTag().putBoolean("used", false);
		}
	}

	@Override
	int getInvestitureCap() {
		return 100000;
	}

}
