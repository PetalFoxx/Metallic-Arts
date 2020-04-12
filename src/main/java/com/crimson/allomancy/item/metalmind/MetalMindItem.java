package com.crimson.allomancy.item.metalmind;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.crimson.allomancy.item.metalmind.MetalMindItem.METALMIND_ACTION;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.Registry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.server.command.TextComponentHelper;

public abstract class MetalMindItem extends Item {
	public MetalMindItem(Properties properties, PlayerEntity owner) {
		super(properties);
	}
	
	public MetalMindItem(Properties properties) {
		super(properties);
	}


	protected int investiture;
	protected int investitureCap;
	protected int strength;
	protected int metal;
	protected PlayerEntity owner;
	protected METALMIND_ACTION action;
	protected Boolean active = false;
	protected Boolean used = false;
	
	public void changeAction(String action, World world, ItemStack item) {
		item.getTag().putString("action", action);
		world.getPlayerByUuid(item.getTag().getUniqueId("owner")).sendMessage(new TranslationTextComponent(action.toString()));
		world.getPlayerByUuid(item.getTag().getUniqueId("owner")).sendMessage(new TranslationTextComponent("__________________"));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(stack.hasTag()) {
//			if(stack.hasTag()) {
//				CompoundNBT ret = stack.getTag();
//				investiture = (int) ret.getFloat("investiture");
//				owner = worldIn.getPlayerByUuid(ret.getUniqueId("owner"));
//			}
			
			if(stack.getTag().getString("action") == "tapping")
				tappingAction(stack, worldIn);
			if(stack.getTag().getString("action") == "filling")
				fillingAction(stack, worldIn);
			if(stack.getTag().getString("action") == "inactive")
				inactiveAction(stack, worldIn);


//			CompoundNBT nbt = new CompoundNBT();
//			nbt.putFloat("investiture", investiture);
//			nbt.putFloat("strength", strength);
////			nbt.putUniqueId("owner", owner.getUniqueID());
//			stack.setTag(nbt);
		}
		
	}
	
//	@Nullable
//    public static MetalMindItem getLivingArmourFromStack(ItemStack stack) {
//		CompoundNBT livingTag = getArmourTag(stack);
//
//        MetalMindItem metalmind =
//        metalmind.updateItemStackNBT(livingTag);
//
//        return metalmind;
//    }
//	
//	public static CompoundNBT getArmourTag(ItemStack stack) {
//        if (!stack.hasTag()) {
//            stack.setTag(new CompoundNBT());
//        }
//
//        CompoundNBT tag = stack.getTag();
//        return tag.getTag();
//    }
	
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
        	ITextComponent invest = new TranslationTextComponent("Power: " + (int) stack.getTag().getFloat("investiture"));
        	invest.setStyle(invest.getStyle().setColor(TextFormatting.GRAY));
        	ITextComponent strength;
        	if((int) stack.getTag().getInt("strength") > 4)
        		strength = new TranslationTextComponent("Strength: " + (int) stack.getTag().getInt("strength") + " (3)");
        	else
        		strength = new TranslationTextComponent("Strength: " + (int) stack.getTag().getInt("strength"));
        	strength.setStyle(strength.getStyle().setColor(TextFormatting.GRAY));
        	ITextComponent own = new TranslationTextComponent("Owner: " + worldIn.getPlayerByUuid(stack.getTag().getUniqueId("owner")).getName().getUnformattedComponentText());
        	invest.setStyle(invest.getStyle().setColor(TextFormatting.GRAY));
            tooltip.add(own);
            tooltip.add(strength);
            tooltip.add(invest);
        }
    }
	
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(!world.isRemote)
		{
			ItemStack item = player.getHeldItem(hand);
			AllomancyCapability cap = AllomancyCapability.forPlayer(player);
			if(cap.canStore(metal))
			{
				if(!item.hasTag() /*|| !active*/) {
					CompoundNBT nbt = new CompoundNBT();
					nbt.putFloat("investiture", 0);
					nbt.putInt("strength", 0);
					nbt.putString("action", "inactive");
					nbt.putBoolean("used", false);
					nbt.putUniqueId("owner", player.getUniqueID());
					item.setTag(nbt);
					cap.addActiveMetalMind(player.getHeldItem(hand), metal);
					world.getPlayerByUuid(item.getTag().getUniqueId("owner")).sendMessage(new TranslationTextComponent("Owner Set"));
				} else {
					if(world.getPlayerByUuid(item.getTag().getUniqueId("owner")).equals(player)) {
						if(cap.getMetalmind(metal) == null || !cap.getMetalmind(metal).equals(item))
							cap.addActiveMetalMind(player.getHeldItem(hand), metal);
						
						if(player.isSneaking() && item.getTag().getString("action") != "filling") {
							changeAction("filling", world, item);
						} else if (item.getTag().getString("action") != "tapping") {
							changeAction("tapping", world, item);
						} else {
							changeAction("inactive", world, item);
						}
					} else {
						player.sendMessage(new TranslationTextComponent("This Metalmind refuses you"));
					}
				}
			} else {
				player.sendMessage(new TranslationTextComponent("You feel no attachment to this item"));
			}
		}
		
		return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	public static int getStrength(ItemStack item) {
		int calcStrength = item.getTag().getInt("strength");
		if(/*action == METALMIND_ACTION.FILLING &&*/ item.getTag().getInt("strength") > 3)
			calcStrength = 3;
		return calcStrength;
	}
	
	public METALMIND_ACTION getAction() {
		return action;
	}
	
	public static void setStrength(int strength, ItemStack item) {
		item.getTag().putInt("strength", strength);
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public static void setUsed(boolean used, ItemStack item) {
		item.getTag().putBoolean("used", true);
	}
	
	public static boolean canGain(ItemStack item) {
		int calStrength = item.getTag().getInt("strength");
		if(item.getTag().getInt("strength") > 3)
			calStrength = 3;
		
		return (item.getTag().getFloat("investiture") + 1 + (1 * calStrength)) <= 10000;//getInvestitureCap();
	}
	
	public static boolean canLose(ItemStack item) {
		return 0 <= item.getTag().getFloat("investiture") - 1 - (1 * ((item.getTag().getInt("strength") * item.getTag().getInt("strength")) / 2));
	}
	
	public static void gain(ItemStack item) {
		int calStrength = item.getTag().getInt("strength");
		if(item.getTag().getInt("strength") > 3)
			calStrength = 3;
		
		item.getTag().putFloat("investiture", item.getTag().getFloat("investiture") + 1 + (1 * calStrength));
	}
	
	public static void lose(ItemStack item) {
		item.getTag().putFloat("investiture", item.getTag().getFloat("investiture") - 1 - (1 * ((item.getTag().getInt("strength") * item.getTag().getInt("strength")) / 2)));
	}
	
	abstract void inactiveAction(ItemStack item, World worldIn);

	abstract void fillingAction(ItemStack item, World worldIn);

	abstract void tappingAction(ItemStack item, World worldIn);
	
	abstract int getInvestitureCap();
	
	
	@Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (group == Registry.allomancy_group) {
            items.add(new ItemStack(this, 1));

            ItemStack resultItem = new ItemStack(this, 1);

            CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("investiture", 100000);
			nbt.putInt("strength", 0);
			nbt.putString("action", "inactive");
			nbt.putBoolean("used", false);
			nbt.putUniqueId("owner", Minecraft.getInstance().player.getUniqueID());
			resultItem.setTag(nbt);
			
            resultItem.setTag(nbt);
            items.add(resultItem);
        }
    }


	public enum METALMIND_ACTION {
        TAPPING,
        FILLING,
        INACTIVE
    }
	
}
