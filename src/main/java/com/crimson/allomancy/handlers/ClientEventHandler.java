package com.crimson.allomancy.handlers;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.entity.particle.SoundParticle;
import com.crimson.allomancy.gui.MetalSelectScreen;
import com.crimson.allomancy.item.metalmind.MetalMindItem;
import com.crimson.allomancy.item.metalmind.ZincMetalMind;
import com.crimson.allomancy.item.metalmind.MetalMindItem.METALMIND_ACTION;
import com.crimson.allomancy.network.NetworkHelper;
import com.crimson.allomancy.network.packets.ChangeEmotionPacket;
import com.crimson.allomancy.network.packets.NicroBurstPacket;
import com.crimson.allomancy.network.packets.TryPushPullBlock;
import com.crimson.allomancy.network.packets.TryPushPullEntity;
import com.crimson.allomancy.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;


//@Mod.EventBusSubscriber(modid = Allomancy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandler {


    private final Minecraft mc = Minecraft.getInstance();

    private Set<Entity> metal_entities = new HashSet<>();
    private Set<BlockPos> metal_blocks = new HashSet<>();
    private Set<LivingEntity> nearby_allomancers = new HashSet<>();
    private boolean activateMarks = false;

   

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
    	
    	// Populate the metal lists
        metal_blocks.clear();
        metal_entities.clear();
        
        
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isGamePaused() && this.mc.player != null && this.mc.player.isAlive()) {

            PlayerEntity player = mc.player;
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);
            
            /*if(!cap.getMetalminds().isEmpty())
            {
            	for(ItemStack metalMind : cap.getMetalminds())
            	{
            		metalMind.getItem().;
.update();
            	}
            }*/

            
            if (cap.getMetalBurning(AllomancyCapability.IRON) || cap.getMetalBurning(AllomancyCapability.STEEL)) {
            	List<Entity> entities;
                Stream<BlockPos> blocks;
                
                int xLoc = (int) player.posX, yLoc = (int) player.posY, zLoc = (int) player.posZ;
                //int max = AllomancyConfig.max_metal_detection;
                int power = 10;
                if(cap.getMetalBurning(AllomancyCapability.IRON) && cap.getMetalBurning(AllomancyCapability.STEEL))
                {
                	if(cap.getCalcBurnStrength(AllomancyCapability.IRON) > cap.getCalcBurnStrength(AllomancyCapability.STEEL))
                	{
                		power = (int) cap.getCalcBurnStrength(AllomancyCapability.IRON);
                	} else {
                		power = (int) cap.getCalcBurnStrength(AllomancyCapability.STEEL);
                	}
                } else if (cap.getMetalBurning(AllomancyCapability.IRON)) {
                	power = (int) cap.getCalcBurnStrength(AllomancyCapability.IRON);
                } else {
                	power = (int) cap.getCalcBurnStrength(AllomancyCapability.STEEL);
                }
                
                
                if(power > 40) {
                	power = 40;
                }
                
                int max = 5 + (power / 4);
                BlockPos negative = new BlockPos(xLoc - max, yLoc - 10/*max*/, zLoc - max);
                BlockPos positive = new BlockPos(xLoc + max, yLoc + 10/*max*/, zLoc + max);
                final float finPower = power;
                // Add metal entities to metal list
                entities = player.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(negative, positive));
                entities.forEach(entity -> {
                    if (AllomancyUtils.isEntityMetal(entity, finPower)) {
                        metal_entities.add(entity);
                    }
                });

                // Add metal blocks to metal list
                blocks = BlockPos.getAllInBox(negative, positive);
                blocks.forEach(bp -> {
                    BlockPos imBlock = bp.toImmutable();
                    if (AllomancyUtils.isBlockMetal(player.world.getBlockState(imBlock).getBlock())) {
                        metal_blocks.add(imBlock);
                    }
                });

            }
            

            if (cap.isAllomancer()) {
            	
            	if (cap.getMetalBurning(AllomancyCapability.COPPER))
	            {
	            	
	            	float strength = cap.getCalcBurnStrength(AllomancyCapability.COPPER);
	            	int xLoc = (int) player.posX, yLoc = (int) player.posY, zLoc = (int) player.posZ;

	                BlockPos negative = new BlockPos(xLoc - 20, yLoc - 10, zLoc - 20);
	                BlockPos positive = new BlockPos(xLoc + 20, yLoc + 10, zLoc + 20);
	                
	                List<LivingEntity> nearby;
	                nearby = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null);
	                
	                for(LivingEntity entity : nearby)
	                {
	                	if(AllomancyCapability.forPlayer(entity) != null)
	                		AllomancyCapability.forPlayer(entity).setIsInCoppercloud(cap.getCalcBurnStrength(AllomancyCapability.COPPER) + 5);

	                	if(entity instanceof MonsterEntity) {//CreeperEntity || entity instanceof SkeletonEntity || entity instanceof WitherSkeletonEntity || entity instanceof WitherEntity) {
	                		if(entity.getDistance(player) > (80 / strength) && entity.getDistance(player) < 15)
	                    	{
	                    		NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), ChangeEmotionPacket.emotion.IGNORE, cap.getCalcBurnStrength(AllomancyCapability.COPPER)));
	                    	}
	                	}
	                }
	            }
            	
            	if (cap.getMetalBurning(AllomancyCapability.IRON) && activateMarks) {
            		for(BlockPos mark : cap.getMarkedLocations()) {
	                	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.IRON);
	                	
	                	BlockPos tarBlock = null;

		                for(BlockPos block : metal_blocks) {
		                    if(block.equals(mark))
		                    {
		                    	tarBlock = block;
		                    }
		                }

	                    //RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
	
	                        if (tarBlock != null) {
	                            //BlockPos bp = ((BlockRayTraceResult) trace).getPos();
	                            
	                            NetworkHelper.sendToServer(new TryPushPullBlock(tarBlock, AllomancyUtils.PULL, cap.getCalcBurnStrength(AllomancyCapability.IRON)));
	                        }
	                }
            	}
            	
            	if (cap.getMetalBurning(AllomancyCapability.STEEL) && activateMarks) {
            		for(BlockPos mark : cap.getMarkedLocations()) {
	                	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.STEEL);
	                	
	                	BlockPos tarBlock = null;

		                for(BlockPos block : metal_blocks) {
		                    if(block.equals(mark))
		                    {
		                    	tarBlock = block;
		                    }
		                }

	                    //RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
	
	                        if (tarBlock != null) {
	                            //BlockPos bp = ((BlockRayTraceResult) trace).getPos();
	                            
	                            NetworkHelper.sendToServer(new TryPushPullBlock(tarBlock, AllomancyUtils.PUSH, cap.getCalcBurnStrength(AllomancyCapability.STEEL)));
	                            
	                        }
	                    
	                }
            	}
            	
            	
            	

            	
                // Handle our input-based powers
//                if (this.mc.gameSettings.keyBindAttack.isKeyDown() && (player.isSneaking() || player.isOnePlayerRiding())) {
//                    // Ray trace 30 blocks
//                	
//                    // All iron pulling powers
//                    if (cap.getMetalBurning(AllomancyCapability.IRON)) {
//                    	activeIron();
//                    }
//                    
//                    
//                    
//                    
//                    
//                    // All zinc powers
//                    if (cap.getMetalBurning(AllomancyCapability.ZINC) && (player.isSneaking() || player.isOnePlayerRiding())) {
//                    	activeZinc();
//                    }
//                }
//
//                
//                
//                if (this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
//                	
//                    // All steel pushing powers
//                    if (cap.getMetalBurning(AllomancyCapability.STEEL) && (player.isSneaking() || player.isOnePlayerRiding())) {
//                    	activeSteel();
//                    	
//                    }
//                    // All brass powers
//                    if (cap.getMetalBurning(AllomancyCapability.BRASS) && player.isSneaking()) {
//                    	activeBrass();
//                    
//                    }
//                }


                
                
                // Populate our list of nearby allomancy users
                
            	nearby_allomancers.clear();
                
             // Add metal burners to a list

                if (cap.getMetalBurning(AllomancyCapability.BRONZE)) {
                	
                	float strength = cap.getCalcBurnStrength(AllomancyCapability.BRONZE);
                	int xLoc = (int) player.posX, yLoc = (int) player.posY, zLoc = (int) player.posZ;
                    BlockPos negative = new BlockPos(xLoc - strength * 2, yLoc - strength, zLoc - strength * 2);
                    BlockPos positive = new BlockPos(xLoc + strength * 2, yLoc + strength, zLoc + strength * 2);

                    List<LivingEntity> nearby;
                    nearby = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null);
                    for(LivingEntity entity : nearby) {
                    	try {
                    		AllomancyCapability capOther = AllomancyCapability.forPlayer(entity);
                    		if(capOther.isBurningMetal() && !entity.equals(player))
		                    	//if(!(capOther.isInCoppercloud() > 0) || (cap.getCalcBurnStrength(AllomancyCapability.BRONZE) > 40 && cap.getCalcBurnStrength(AllomancyCapability.BRONZE) > capOther.isInCoppercloud())) {
		                    		nearby_allomancers.add(entity);
		                    	//}
	                    	
                    	} finally {}
                    	
                    }
                    
                    
                    
                    
                    List<ServerPlayerEntity> players = player.world.getEntitiesWithinAABB(ServerPlayerEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null);
                    for(ServerPlayerEntity entity : players) {
                    	try {
                    		AllomancyCapability capOther = AllomancyCapability.forPlayer(entity);
                    		if(capOther.isBurningMetal() && !entity.equals(player))
		                    	//if(!(capOther.isInCoppercloud() > 0) || (cap.getCalcBurnStrength(AllomancyCapability.BRONZE) > 40 && cap.getCalcBurnStrength(AllomancyCapability.BRONZE) > capOther.isInCoppercloud())) {
		                    		nearby_allomancers.add(entity);
		                    	//}
	                    	
                    	} finally {}
                    	
                    }
                    
                }
            }
            
        }
        
        
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
    	if(Registry.activeIron.isPressed()) {
    		activeIron();
    	}
    	
    	if(Registry.activeZinc.isPressed()) {
    		activeZinc();
    	}
    	
    	if(Registry.activeSteel.isPressed()) {
    		activeSteel();
    	}
    	
    	if(Registry.activeBrass.isPressed()) {
    		activeBrass();
    	}
    	
    	if(Registry.activeNicro.isPressed()) {
    		activeNicro();
    	}
    	
    	
    	
        if (Registry.burn.isPressed()) {
            PlayerEntity player = mc.player;
            AllomancyCapability cap;
            if (mc.currentScreen == null) {
                if (player == null || !this.mc.isGameFocused()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);

                	for(byte i = 0; i < 8; i++)
                	{
                		if(cap.canBurn(i))
                			ClientUtils.toggleMetalBurn(i, cap);
                	}
                
            }
            
        }
        
        
        if (Registry.selection.isPressed()) {
            PlayerEntity player = mc.player;
            AllomancyCapability cap;
	            if (mc.currentScreen == null) {
	                if (player == null || !this.mc.isGameFocused()) {
	                    return;
	                }
	                
	                this.mc.displayGuiScreen(new MetalSelectScreen());
	                
	                //System.out.println(AllomancyCapability.PLAYER_CAP.toString());

            	
	            }
        }
        
        if (Registry.flare.isPressed()) {
            PlayerEntity player = mc.player;
            AllomancyCapability cap;
            if (mc.currentScreen == null) {
                if (player == null || !this.mc.isGameFocused()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);

                	for(int i = 0; i < Metal.getMetals(); i++)
                	{
                		if(cap.getMetalBurning(i))
                			ClientUtils.toggleMetalFlare(i, cap);
                	} 
            }

        }
        
        
        if (Registry.mind.isPressed()) {
        	PlayerEntity player = mc.player;
        	if(player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof MetalMindItem) {
        		if(player.isSneaking() && MetalMindItem.getStrength(player.getHeldItem(Hand.MAIN_HAND)) > 0) {
        			MetalMindItem.setStrength(MetalMindItem.getStrength(player.getHeldItem(Hand.MAIN_HAND)) - 1, player.getHeldItem(Hand.MAIN_HAND));
        		} else if (MetalMindItem.getStrength(player.getHeldItem(Hand.MAIN_HAND)) <= 25) {
        			MetalMindItem.setStrength(MetalMindItem.getStrength(player.getHeldItem(Hand.MAIN_HAND)) + 1, player.getHeldItem(Hand.MAIN_HAND));
        		}	
        	}
        }
        
        
        if(Registry.mark.isPressed()) {
        	if(!mc.player.isSneaking()) {
	        	mc.player.sendMessage(new TranslationTextComponent("Enter M"));
	        	BlockPos foundPos = null;
	        	double dist = 10000f;
	        	
	        	RayTraceResult pos = ClientUtils.getMouseOverExtended(100);
	        	BlockPos tarBlock = null;
	        	
	        	if(pos != null) {
	            	if(pos.getType() == RayTraceResult.Type.ENTITY ) {
	            		foundPos = ((EntityRayTraceResult) pos).getEntity().getPosition();
	            		mc.player.sendMessage(new TranslationTextComponent("FoundPos!"));
	            	} else if (pos.getType() == RayTraceResult.Type.BLOCK) {
	            		foundPos = ((BlockRayTraceResult) pos).getPos();
	            		mc.player.sendMessage(new TranslationTextComponent("FoundPos!"));
	            	}
	        	
	            	if(foundPos != null) {
	                	for(BlockPos block : metal_blocks) {
	                		if(block.distanceSq(foundPos) < dist)
	                		{
	                			tarBlock = block;
	                			dist = block.distanceSq(foundPos);
	                		}
	                	}
	            	}
	        	}
	        	
	        	if(tarBlock != null) {
	        		AllomancyCapability.forPlayer(mc.player).addMarkedLocation(tarBlock);
	        		mc.player.sendMessage(new TranslationTextComponent("Added Target!"));
	        	}
        	} else if (mc.player.isSneaking() && AllomancyCapability.forPlayer(mc.player).getMarkedLocations().size() > 0) {
        		if(!activateMarks) {
            		activateMarks = true;
            		mc.player.sendMessage(new TranslationTextComponent("Activate Marks"));
            	} else {
            		activateMarks = false;
            		AllomancyCapability.forPlayer(mc.player).resetMarkedLocation();
            		mc.player.sendMessage(new TranslationTextComponent("Deactivate Marks!"));
            	}
        	}
        }
    }
    
    public void activeIron()
    {
    	PlayerEntity player = mc.player;
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);
        
		if (cap.getMetalBurning(AllomancyCapability.IRON)) {
        	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.IRON);
        	
        	BlockPos foundPos = null;
        	double dist = 10000f;
        	
        	RayTraceResult pos = ClientUtils.getMouseOverExtended(100);
        	Entity tarEnt = null;
        	BlockPos tarBlock = null;
        	double blockDist = 1000;
        	double entDist = 1000;
        	Boolean blockClosest = false;
        	
        	if(pos != null) {
            	if(pos.getType() == RayTraceResult.Type.ENTITY )
            	{
            		foundPos = ((EntityRayTraceResult) pos).getEntity().getPosition();
            	} else if (pos.getType() == RayTraceResult.Type.BLOCK) {
            		foundPos = ((BlockRayTraceResult) pos).getPos();
            	}
        	
        	
        	
            	if(foundPos != null) {
                	for(Entity entity : metal_entities) {
                		if(entity.getPosition().distanceSq(foundPos) < dist)
                		{
                			entDist = entity.getPosition().distanceSq(foundPos);
                			tarEnt = entity;
                			dist = entity.getPosition().distanceSq(foundPos);
                		}
                	}
                	
                	for(BlockPos block : metal_blocks) {
                		if(block.distanceSq(foundPos) < dist)
                		{
                			blockDist = block.distanceSq(foundPos);
                			tarBlock = block;
                			dist = block.distanceSq(foundPos);
                		}
                	}
            	}
        	}
        	
        	if(blockDist < entDist)
        		blockClosest = true;

            //RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));

                if (tarEnt != null && !blockClosest) {
                	NetworkHelper.sendToServer(new TryPushPullEntity(tarEnt.getEntityId(), AllomancyUtils.PULL, cap.getCalcBurnStrength(AllomancyCapability.IRON)));
                } else if (tarBlock != null) {
                    //BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                    if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(tarBlock).getBlock()) || (player.getHeldItemMainhand().getItem() == Registry.coin_bag && player.isSneaking())) {
                    	NetworkHelper.sendToServer(new TryPushPullBlock(tarBlock, AllomancyUtils.PULL, cap.getCalcBurnStrength(AllomancyCapability.IRON)));
                    }
                }
            
        }
    }
    
    public void activeSteel()
    {
    	PlayerEntity player = mc.player;
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);
        if (cap.getMetalBurning(AllomancyCapability.STEEL)) {
        	BlockPos foundPos = null;
        	double dist = 10000f;
        	double blockDist = 1000;
        	double entDist = 1000;
        	Boolean blockClosest = false;
        	
        	RayTraceResult pos = ClientUtils.getMouseOverExtended(100);
        	Entity tarEnt = null;
        	BlockPos tarBlock = null;
        	
        	if(pos != null) {
            	if(pos.getType() == RayTraceResult.Type.ENTITY )
            	{
            		foundPos = ((EntityRayTraceResult) pos).getEntity().getPosition();
            	} else if (pos.getType() == RayTraceResult.Type.BLOCK) {
            		foundPos = ((BlockRayTraceResult) pos).getPos();
            	}
        	
            	if(foundPos != null) {
                	for(Entity entity : metal_entities) {
                		if(entity.getPosition().distanceSq(foundPos) < dist)
                		{
                			entDist = entity.getPosition().distanceSq(foundPos);
                			tarEnt = entity;
                			dist = entity.getPosition().distanceSq(foundPos);
                		}
                	}
                	
                	
                	for(BlockPos block : metal_blocks) {
                		if(block.distanceSq(foundPos) < dist)
                		{
                			blockDist = block.distanceSq(foundPos);
                			tarBlock = block;
                			dist = block.distanceSq(foundPos);
                		}
                	}
            	}
        	}
        	

        	if(blockDist < entDist)
        		blockClosest = true;
        	
            //RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
        		int power;
        		if(player.isSneaking())
        			power = 5;
        		else
        			power = cap.getCalcBurnStrength(AllomancyCapability.STEEL);
        			
                if (tarEnt != null && !blockClosest) {
                    NetworkHelper.sendToServer(new TryPushPullEntity(tarEnt.getEntityId(), AllomancyUtils.PUSH, power));
                } else if (tarBlock != null) {
                    //BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                    if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(tarBlock).getBlock()) || (player.getHeldItemMainhand().getItem() == Registry.coin_bag && player.isSneaking())) {
                    	NetworkHelper.sendToServer(new TryPushPullBlock(tarBlock, AllomancyUtils.PUSH, power));
                    }
                }
        }
    }
    
    public void activeZinc()
    {
    	PlayerEntity player = mc.player;
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);
        
		if (cap.getMetalBurning(AllomancyCapability.ZINC)) {
        	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.ZINC);
            RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
            Entity entity;
            if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                entity = ((EntityRayTraceResult) trace).getEntity();
                if(strength > 40)
                {
                	int xLoc = (int) entity.posX, yLoc = (int) entity.posY, zLoc = (int) entity.posZ;
	                BlockPos negative = new BlockPos(xLoc - (strength /2), yLoc - (strength /2), zLoc - (strength /2));
	                BlockPos positive = new BlockPos(xLoc + (strength /2), yLoc + (strength /2), zLoc + (strength /2));
	                
	                List<LivingEntity> nearby;
	                nearby = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entityb -> entityb != null);
	                
	                for(LivingEntity entityb : nearby)
	                {	
	                	if(!AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.COPPER))
	                	{
	                		NetworkHelper.sendToServer(new ChangeEmotionPacket(entityb.getEntityId(), ChangeEmotionPacket.emotion.AGGRO, cap.getCalcBurnStrength(AllomancyCapability.ZINC)));
		                }
	                }
                } else if(!AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.COPPER))
                		NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), ChangeEmotionPacket.emotion.AGGRO, cap.getCalcBurnStrength(AllomancyCapability.ZINC)));
                
            }
        }
    }
    
    public void activeBrass()
    {
    	PlayerEntity player = mc.player;
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);
        
        if (cap.getMetalBurning(AllomancyCapability.BRASS)) {
        	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.BRASS);
            RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
            Entity entity;
            if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                entity = ((EntityRayTraceResult) trace).getEntity();
                if(strength > 40)
                {
                	int xLoc = (int) entity.posX, yLoc = (int) entity.posY, zLoc = (int) entity.posZ;
	                BlockPos negative = new BlockPos(xLoc - (strength /2), yLoc - (strength /2), zLoc - (strength /2));
	                BlockPos positive = new BlockPos(xLoc + (strength /2), yLoc + (strength /2), zLoc + (strength /2));
	                
	                List<LivingEntity> nearby;
	                nearby = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entityb -> entityb != null);
	                
	                for(LivingEntity entityb : nearby)
	                {	
	                	if(!AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.COPPER))
	                	{
		                	NetworkHelper.sendToServer(new ChangeEmotionPacket(entityb.getEntityId(), ChangeEmotionPacket.emotion.CALM, cap.getCalcBurnStrength(AllomancyCapability.BRASS)));
	                	} 		
	                }
                } else if(!AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.COPPER)) 
                		NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), ChangeEmotionPacket.emotion.CALM, cap.getCalcBurnStrength(AllomancyCapability.BRASS)));
                
                
            }
        }
    }
    
    public void activeNicro()
    {
    	PlayerEntity player = mc.player;
        AllomancyCapability cap;
        cap = AllomancyCapability.forPlayer(player);
        player.sendMessage(new TranslationTextComponent("Success!"));
        if (cap.getMetalBurning(Metal.NICROSIL.getNumber())) {
        	int strength = (int) cap.getCalcBurnStrength(Metal.NICROSIL.getNumber());
            RayTraceResult trace = ClientUtils.getMouseOverExtended(5 + (strength / 2));
            Entity entity;
            if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                entity = ((EntityRayTraceResult) trace).getEntity();
                if(strength > 40)
                {
                	int xLoc = (int) entity.posX, yLoc = (int) entity.posY, zLoc = (int) entity.posZ;
	                BlockPos negative = new BlockPos(xLoc - (strength /2), yLoc - (strength /2), zLoc - (strength /2));
	                BlockPos positive = new BlockPos(xLoc + (strength /2), yLoc + (strength /2), zLoc + (strength /2));
	                
	                List<LivingEntity> nearby;
	                nearby = player.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entityb -> entityb != null);
	                
	                for(LivingEntity entityb : nearby)
	                {
		                	if (entity instanceof LivingEntity && !entityb.equals(player)) 
		                		NetworkHelper.sendToServer(new NicroBurstPacket(entityb.getEntityId(), cap.getCalcBurnStrength(Metal.NICROSIL.getNumber()), player.getEntityId()));
	                		
	                }
                } else if (entity instanceof LivingEntity) {
                	LivingEntity lEntity = (LivingEntity) entity;
                	AllomancyCapability mobCap = AllomancyCapability.forPlayer(lEntity);

                	
                	NetworkHelper.sendToServer(new NicroBurstPacket(lEntity.getEntityId(), cap.getCalcBurnStrength(Metal.NICROSIL.getNumber()), player.getEntityId()));
                	if(!lEntity.world.isRemote)
                		NetworkHelper.sync(lEntity);
                	
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!this.mc.isGameFocused() || !this.mc.player.isAlive()) {
            return;
        }
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen) && !(this.mc.currentScreen instanceof MetalSelectScreen)) {
            return;
        }

        ClientUtils.drawMetalOverlay();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGUIScreen(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof MetalSelectScreen && !event.isCancelable()) {
            ClientUtils.drawMetalOverlay();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        PlayerEntity player = mc.player;
        if (player == null || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);

        if (!cap.isAllomancer()) {
            return;
        }

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        // Iron and Steel lines
        if ((cap.getMetalBurning(AllomancyCapability.IRON) || cap.getMetalBurning(AllomancyCapability.STEEL))) {

            for (Entity entity : metal_entities) {
                ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 1.25 + entity.getHeight() / 2.0, entity.posZ, 1.5F, 0F, 0.6F, 1F);
            }

            for (BlockPos v : metal_blocks) {
                ClientUtils.drawMetalLine(playerX, playerY, playerZ, v.getX() + 0.5, v.getY() - 1.0, v.getZ() + 0.5, 1.5F, 0F, 0.6F, 1F);
            }
        }

        if ((cap.getMetalBurning(AllomancyCapability.BRONZE) && !cap.getMetalBurning(AllomancyCapability.COPPER))) {
            for (LivingEntity entity : nearby_allomancers) {
            	if(cap.getCalcBurnStrength(AllomancyCapability.BRONZE) < 15)
            		ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, 3.0F, 0.7F, 0.15F, 0.15F);
            	else
            	{
            		AllomancyCapability entityCap = AllomancyCapability.forPlayer(entity);
            		if(entityCap.getMetalBurning(AllomancyCapability.IRON))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.IRON)/4, 0.5F, 0.0F, 0.0F);
            		if(entityCap.getMetalBurning(AllomancyCapability.STEEL))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.STEEL)/4, 0.5F, 0.25F, 0.0F);
            		if(entityCap.getMetalBurning(AllomancyCapability.COPPER))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.COPPER)/4, 0.0F, 0.5F, 0.0F);
            		if(entityCap.getMetalBurning(AllomancyCapability.BRONZE))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.BRONZE)/4, 0.0F, 0.5F, 0.25F);
            		if(entityCap.getMetalBurning(AllomancyCapability.TIN))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.TIN)/4, 0.0F, 0.0F, 0.5F);
            		if(entityCap.getMetalBurning(AllomancyCapability.PEWTER))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.PEWTER)/4, 0.25F, 0.0F, 0.5F);
            		if(entityCap.getMetalBurning(AllomancyCapability.ZINC))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.ZINC)/4, 0.0F, 0.5F, 0.5F);
            		if(entityCap.getMetalBurning(AllomancyCapability.BRASS))
            			ClientUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 0.5, entity.posZ, entityCap.getCalcBurnStrength(AllomancyCapability.BRASS)/4, 0.25F, 0.5F, 0.5F);
            		
            	}
            }

        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        double motionX, motionY, motionZ, magnitude;

        PlayerEntity player = mc.player;
        ISound sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }

        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        if (cap.getMetalBurning(AllomancyCapability.TIN)) {
        	
        	float strength = cap.getCalcBurnStrength(AllomancyCapability.TIN);

            magnitude = Math.sqrt(Math.pow((player.posX - sound.getX()), 2) + Math.pow((player.posY - sound.getY()), 2) + Math.pow((player.posZ - sound.getZ()), 2));

            if (((magnitude) > (3 * strength)) || ((magnitude) < 2)) {
                return;
            }
            // Spawn sound particles
            String soundName = sound.getSoundLocation().toString();
            if (soundName.contains("entity") || soundName.contains("step") || soundName.contains("block")) {
                motionX = ((player.posX - (event.getSound().getX() + .5)) * 0.7) / magnitude;
                motionY = ((player.posY - (event.getSound().getY() + .2)) * 0.7) / magnitude;
                motionZ = ((player.posZ - (event.getSound().getZ() + .5)) * 0.7) / magnitude;
                Particle particle = new SoundParticle(player.world, event.getSound().getX() + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d), event.getSound().getY() + .2, event.getSound().getZ() + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
                        motionY, motionZ, sound.getCategory(), strength);
                this.mc.particles.addEffect(particle);
            }
            
        }
    }
}
