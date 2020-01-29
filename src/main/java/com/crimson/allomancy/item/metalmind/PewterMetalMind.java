package com.crimson.allomancy.item.metalmind;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.item.metalmind.MetalMindItem.METALMIND_ACTION;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class PewterMetalMind extends MetalMindItem {
	public PewterMetalMind() {
		super(new Item.Properties().group(Registry.allomancy_group));
		this.setRegistryName(Allomancy.MODID, "pewter_metal_mind");
		this.metal = AllomancyCapability.PEWTER;
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
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.HASTE, 10, (-1 - calStrength) / 2, true, false));
		if(item.getTag().getBoolean("used") && canGain(item)) {
			gain(item);
			item.getTag().putBoolean("used", false);
		}
	}

	@Override
	void tappingAction(ItemStack item, World worldIn) {
		if(canLose(item))
		{
			if(item.getTag().getInt("strength") >= 1)
				worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.HASTE, 10, (0 + item.getTag().getInt("strength")) / 2, true, false));
			if(item.getTag().getBoolean("used")) {
				lose(item);
				item.getTag().putBoolean("used", false);
			}
		}
	}

	@Override
	int getInvestitureCap() {
		return 100000;
	}

}
