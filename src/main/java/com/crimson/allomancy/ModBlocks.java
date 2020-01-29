package com.crimson.allomancy;

import com.crimson.allomancy.block.AlloyingSmelter;
import com.crimson.allomancy.block.IronButtonBlock;
import com.crimson.allomancy.block.IronLeverBlock;
import com.crimson.allomancy.block.MetalPurifier;

import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Allomancy.MODID)
public class ModBlocks {
	@ObjectHolder("allomancy:alloying_smelter")
	public static final AlloyingSmelter ALLOYING_SMELTER = ModUtil._null();
	@ObjectHolder("allomancy:metal_purifier")
	public static final MetalPurifier METAL_PURIFIER = ModUtil._null();
	@ObjectHolder("allomancy:iron_button")
	public static final IronButtonBlock IRON_BUTTON = ModUtil._null();
	@ObjectHolder("allomancy:iron_lever")
	public static final IronLeverBlock IRON_LEVER = ModUtil._null();
}
