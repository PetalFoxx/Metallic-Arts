package com.crimson.allomancy.item.metalmind;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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

public class IronMetalMind extends MetalMindItem {
	public IronMetalMind() {
		super(new Item.Properties().group(Registry.allomancy_group));
		this.setRegistryName(Allomancy.MODID, "iron_metal_mind");
		this.metal = AllomancyCapability.IRON;
		// TODO Auto-generated constructor stub
	}
	
	BlockPos lastPos = null;
	boolean hasBonus = false;
	boolean hasLoss = false;

	@Override
	void inactiveAction(ItemStack item, World worldIn) {
		// TODO Auto-generated method stub
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).removeModifier(new AttributeModifier("IronMind bonus", 1 +strength * 0.05, AttributeModifier.Operation.ADDITION));
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).removeModifier(new AttributeModifier("IronMind loss", -1 -strength * 0.05, AttributeModifier.Operation.ADDITION));
		
	}

	@Override
	void fillingAction(ItemStack item, World worldIn) {
		int calStrength = strength;
		if(strength > 3)
			calStrength = 3;
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 10, 0 + calStrength, true, false));

		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).removeModifier(new AttributeModifier("IronMind bonus", 1 +strength * 0.05, AttributeModifier.Operation.ADDITION));
		
		worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("IronMind loss", -1 -strength * 0.05, AttributeModifier.Operation.ADDITION));
		hasLoss = true;

		if(canGain(item) && !worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition().equals(lastPos))
			gain(item);
		
		lastPos = worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition();
	}

	@Override
	void tappingAction(ItemStack item, World worldIn) {
		if(canLose(item))
		{
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 10, -1 - strength, true, false));
			
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).removeModifier(new AttributeModifier("IronMind loss", -1 -strength * 0.05, AttributeModifier.Operation.ADDITION));
			
			worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier("IronMind bonus", 1 + strength * 0.05, AttributeModifier.Operation.ADDITION));
			hasBonus = true;
			
			
			
			//attributes.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(getBaubleUUID(stack), "Knockback Belt", 1, 0).setSaved(false));
			if(canLose(item) && !worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition().equals(lastPos))
				lose(item);
		}
		
		lastPos = worldIn.getPlayerByUuid(item.getTag().getUniqueId("owner")).getPosition();
	}
	


	@Override
	int getInvestitureCap() {
		return 100000;
	}

}
