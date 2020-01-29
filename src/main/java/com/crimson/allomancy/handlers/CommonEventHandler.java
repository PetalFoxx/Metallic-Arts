package com.crimson.allomancy.handlers;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.item.metalmind.ZincMetalMind;
import com.crimson.allomancy.item.metalmind.BrassMetalMind;
import com.crimson.allomancy.item.metalmind.MetalMindItem;
import com.crimson.allomancy.item.metalmind.MetalMindItem.METALMIND_ACTION;
import com.crimson.allomancy.item.metalmind.PewterMetalMind;
import com.crimson.allomancy.item.metalmind.TinMetalMind;
import com.crimson.allomancy.network.NetworkHelper;
import com.crimson.allomancy.network.packets.AllomancyCapabilityPacket;
import com.crimson.allomancy.network.packets.ChangeEmotionPacket;
import com.crimson.allomancy.util.AllomancyCapability;
import com.crimson.allomancy.util.AllomancyConfig;
import com.crimson.allomancy.util.AllomancyUtils;
import com.crimson.allomancy.util.DrainDamage;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

//@Mod.EventBusSubscriber(modid = Allomancy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEventHandler {

    @SubscribeEvent
    public void onAttachCapability(final AttachCapabilitiesEvent<Entity> event) {
    	event.addCapability(AllomancyCapability.IDENTIFIER, new AllomancyCapability());

    }



    @SubscribeEvent
    public void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().world.isRemote) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                AllomancyCapability cap = AllomancyCapability.forPlayer(player);

                //Handle random misting case
                if (AllomancyConfig.random_mistings && !cap.isAllomancer()) {
                	int random = (int) (Math.random() * 10);
                	if(random < 3) 
                	{
                		byte randomMisting = (byte) (Math.random() * 8);
                        cap.setCanBurn(randomMisting, true);
                        cap.setIsAllomancer(true);
                	} else if (random < 7) {
                		byte randomFerring = (byte) (Math.random() * 8);
                        cap.setCanStore(randomFerring, true);
                        cap.setIsAllomancer(true);
                	} else {
                		byte randomMisting = (byte) (Math.random() * 8);
                        cap.setCanBurn(randomMisting, true);
                        
                        byte randomFerring = (byte) (Math.random() * 8);
                        cap.setCanStore(randomFerring, true);
                        
                        cap.setIsAllomancer(true);
                	}
                    
                }

                //Sync cap to client
                NetworkHelper.sync(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(final net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (!event.getPlayer().world.isRemote()) {

            PlayerEntity player = event.getPlayer();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player); // the clone's cap

            PlayerEntity old = event.getOriginal();
            old.revive();
            
            		
            old.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(oldCap -> {
            	for (int i = 0; i < 8; i++) {
            		if (oldCap.canBurn(i))
            			cap.setCanBurn(i, true);
            	}
            	
            	for (int i = 0; i < 8; i++) {
            		cap.setBurnStrength(i, oldCap.getBurnStrength(i));
            	}
            	
            	cap.setIsAllomancer(oldCap.isAllomancer());
            

                if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
                    for (int i = 0; i < 8; i++) {
                        cap.setMetalAmounts(i, oldCap.getMetalAmounts(i));
                    }
                }
            });


            NetworkHelper.sync(player);
        }
    }

    @SubscribeEvent
    public void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote()) {
            NetworkHelper.sync(event.getPlayer());
        }
    }


    @SubscribeEvent
    public void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote()) {
            NetworkHelper.sync(event.getPlayer());
        }
    }


    @SubscribeEvent
    public void onStartTracking(final net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!event.getTarget().world.isRemote) {
            if (event.getTarget() instanceof ServerPlayerEntity) {
                ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getTarget();
                NetworkHelper.sendTo(new AllomancyCapabilityPacket(AllomancyCapability.forPlayer(playerEntity), playerEntity.getEntityId()), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }
    
    
    

    @SubscribeEvent
    public void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof LivingEntity) {
        	LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
        	//if(source != null)
        	//{
	            AllomancyCapability cap = AllomancyCapability.forPlayer(source);
	
	            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
	                event.setAmount(event.getAmount() + (2 * (cap.getBurnStrength(AllomancyCapability.PEWTER) / 10) ));
	            }
	            
	            if(cap.hasMetalmind(AllomancyCapability.BRASS))
	        	{
	        		BrassMetalMind metalmind = (BrassMetalMind) cap.getMetalmind(AllomancyCapability.BRASS).getItem();
	        		if(metalmind.getAction() == METALMIND_ACTION.TAPPING && MetalMindItem.canLose(cap.getMetalmind(AllomancyCapability.BRASS))){
	        			event.getEntityLiving().attackEntityFrom(DamageSource.ON_FIRE, MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.BRASS)) / 5);
	        			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.BRASS));
	        		}
	        	}
	            
	            if(cap.hasMetalmind(AllomancyCapability.PEWTER))
	        	{
	        		PewterMetalMind metalmind = (PewterMetalMind) cap.getMetalmind(AllomancyCapability.PEWTER).getItem();
	        		if(metalmind.getAction() == METALMIND_ACTION.TAPPING && MetalMindItem.canLose(cap.getMetalmind(AllomancyCapability.PEWTER))){
	        			event.setAmount(event.getAmount() * (1.2f + (0.2f * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.PEWTER)))));
	        			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.PEWTER));
	        		}
	        		if(metalmind.getAction() == METALMIND_ACTION.FILLING  && MetalMindItem.canGain(cap.getMetalmind(AllomancyCapability.PEWTER))){
	        			event.setAmount(event.getAmount() * (0.8f - (0.2f * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.PEWTER)))));
	        			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.PEWTER));
	        		}
	        	}
	            
	        //}
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof LivingEntity) {
            AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityLiving());
            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                event.setAmount(event.getAmount() - (1 * (cap.getBurnStrength(AllomancyCapability.PEWTER) / 10) ));
                // Note that they took damage, will come in to play if they stop burning
                cap.setDamageStored(cap.getDamageStored() + (int) (0.5 * (cap.getBurnStrength(AllomancyCapability.PEWTER) / 10) ));
            }
            
            if(cap.hasMetalmind(AllomancyCapability.BRASS))
        	{
        		BrassMetalMind metalmind = (BrassMetalMind) cap.getMetalmind(AllomancyCapability.BRASS).getItem();
        		
        		if(metalmind.getAction() == METALMIND_ACTION.FILLING && MetalMindItem.canGain(cap.getMetalmind(AllomancyCapability.BRASS))) {
        			if(event.getSource().isFireDamage())
        			{
        				event.setAmount(event.getAmount() * (0.75f - (0.25f * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.BRASS)))));
        				MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.BRASS));
        			}
        		}
        	}
            
            
            
            
        }
        
        if(event.getEntity() instanceof WitherEntity) {
        	WitherEntity wither = (WitherEntity) event.getEntity();
        	AllomancyCapability witherCap = AllomancyCapability.forPlayer(wither);
        	if(!witherCap.isAllomancer())
        	{
	        	NetworkHelper.sync(wither);
	        	
	        	witherCap.setIsAllomancer(true);
	        	witherCap.setMetalAmounts(AllomancyCapability.PEWTER, 5);
	        	witherCap.setCanBurn(AllomancyCapability.PEWTER, true);
	        	witherCap.setBurnStrength(AllomancyCapability.PEWTER, 20);
	        	witherCap.setMetalBurning(AllomancyCapability.PEWTER, true);
	        	
	        	NetworkHelper.sync(wither);
	        	
        	}
        	
        	if(wither.getHealth() < (wither.getMaxHealth() / 2))
        	{
        		witherCap.setMetalFlaring(AllomancyCapability.PEWTER, true);
        	}
        }
    }
    
    

    
    @SubscribeEvent
    public static void onEntityJoinedWorld(EntityJoinWorldEvent event) {
    	if(event.getEntity() instanceof ArrowEntity) {
    		ArrowEntity arrow = (ArrowEntity) event.getEntity();
    		Entity firer = arrow.getShooter();
        	
        	if(firer instanceof LivingEntity) {
        		AllomancyCapability cap = AllomancyCapability.forPlayer(firer);
        		if(cap.hasMetalmind(AllomancyCapability.TIN)) {
        			
        			TinMetalMind metalmind = (TinMetalMind) cap.getMetalmind(AllomancyCapability.TIN).getItem();
        			
        			if(metalmind.getAction() == METALMIND_ACTION.FILLING && MetalMindItem.canGain(cap.getMetalmind(AllomancyCapability.TIN))) {
                        
        				arrow.addVelocity(2 * (event.getWorld().rand.nextDouble() - 0.5) * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.TIN)), 2 * (event.getWorld().rand.nextDouble() - 0.5) * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.TIN)), 2 * (event.getWorld().rand.nextDouble() - 0.5) * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.TIN)));
        				
        				if(MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.TIN)) > 1)
        					arrow.setIsCritical(false);
        				MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.TIN));
        			}
        			if(metalmind.getAction() == METALMIND_ACTION.TAPPING && metalmind.canLose(cap.getMetalmind(AllomancyCapability.TIN))) {
        				arrow.setDamage(arrow.getDamage() * (1.2 + (0.2 * metalmind.getStrength(cap.getMetalmind(AllomancyCapability.TIN)))));
        				if(MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.TIN)) >= 10)
        					arrow.setIsCritical(true);
        				MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.TIN));
        			}
        		}
        		
        		
        		if(cap.getMetalBurning(AllomancyCapability.TIN)) {
        			arrow.setDamage(arrow.getDamage() * (1 + (0.02 * cap.getBurnStrength(AllomancyCapability.TIN))));
        			if(cap.getBurnStrength(AllomancyCapability.TIN) >= 30)
        				arrow.setIsCritical(true);
        		}
        		
        		if(cap.getMetalBurning(AllomancyCapability.STEEL)) {
        			arrow.setDamage(arrow.getDamage() * (1 + (0.01 * cap.getBurnStrength(AllomancyCapability.STEEL))));
        			Vec3d motion = arrow.getMotion();
        			for(int i = 0; i < (cap.getBurnStrength(AllomancyCapability.STEEL)/10); i++) {
        				motion.add(motion);
        			}
        			arrow.setMotion(motion);
        		}
        	}
    	}
    	if (event.getEntity() instanceof ThrowableEntity) {
    		
    	}
    	
    	
    }
    
    



    @SubscribeEvent
    public void onLootTableLoad(final LootTableLoadEvent event) {
        String name = event.getName().toString();
        if (name.equals("minecraft:chests/simple_dungeon") || name.equals("minecraft:chests/desert_pyramid")
                || name.equals("minecraft:chests/jungle_temple") || name.equals("minecraft:chests/woodland_mansion")
                || name.equals("minecraft:chests/abandoned_mineshaft") || name.equals("minecraft:chests/stronghold_library")
                || name.equals("minecraft:chests/buried_treasure") || name.equals("minecraft:chests/shipwreck_treasure")
                || name.equals("minecraft:chests/nether_bridge") || name.equals("chests/end_city_treasure")) {
            //Inject a Lerasium loot table into the above vanilla tables
            event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(Allomancy.MODID, "inject/lerasium"))).build());
        }
    }


    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            World world = (World) event.world;
            List<? extends PlayerEntity> list = world.getPlayers();
            
            
            for (PlayerEntity curPlayer : list) {
                
                List<LivingEntity> nearby;
                
                int xLoc = (int) curPlayer.posX, yLoc = (int) curPlayer.posY, zLoc = (int) curPlayer.posZ;
                BlockPos negative = new BlockPos(xLoc - 20, yLoc - 20, zLoc - 20);
                BlockPos positive = new BlockPos(xLoc + 20, yLoc + 20, zLoc + 20);
                nearby = curPlayer.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null);
                
                for(LivingEntity entity : nearby)
                {
                	if(AllomancyCapability.forPlayer(entity) != null)
                		runAllomancy(entity);
                }
                runAllomancy(curPlayer);
            }
        }
    }
    
    public void runAllomancy(LivingEntity allomancer) {
    	World world = (World) allomancer.world;
    	if(!world.isRemote()) {
	    	try {
		    	if(AllomancyCapability.forPlayer(allomancer).isInCoppercloud() > 0)
		    		AllomancyCapability.forPlayer(allomancer).setIsInCoppercloud(0);
		    	
		    	AllomancyCapability cap = AllomancyCapability.forPlayer(allomancer);
		    	if (cap.isAllomancer()) {
		            // Run the necessary updates on the player's metals
		            if (allomancer instanceof ServerPlayerEntity) {
		                AllomancyUtils.updateMetalBurnTime(cap, (ServerPlayerEntity) allomancer);
		            }
		            // Damage the player if they have stored damage and pewter cuts out
		            if (!cap.getMetalBurning(AllomancyCapability.PEWTER) && (cap.getDamageStored() > 0)) {
		                int damageRemoved = cap.getDamageStored() / 10;
		                if(damageRemoved == 0)
		                {
		                	damageRemoved = 1;
		                }
		            	
		            	cap.setDamageStored(cap.getDamageStored() - damageRemoved);
		            	allomancer.attackEntityFrom(new DrainDamage(), 2 * damageRemoved);
		            }
		            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
		                //Add jump boost and speed to pewter burners
		            	int strength = (int) cap.getBurnStrength(AllomancyCapability.PEWTER) / 10;
		            	allomancer.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 30, strength, true, false));
		            	allomancer.addPotionEffect(new EffectInstance(Effects.SPEED, 30, strength - 1, true, false));
		            	allomancer.addPotionEffect(new EffectInstance(Effects.HASTE, 30, strength - 1, true, false));
		
		                if (cap.getDamageStored() > 0) {
		                    if (world.rand.nextInt(200) == 0) {
		                        cap.setDamageStored(cap.getDamageStored() - (1 * ((int) cap.getBurnStrength(AllomancyCapability.PEWTER) / 20)));
		                    }
		                }
		
		            }
		            if (cap.getMetalBurning(AllomancyCapability.TIN)) {
		            	int strength = (int) cap.getBurnStrength(AllomancyCapability.TIN) / 10;
		                // Add night vision to tin-burners
		            	allomancer.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, Short.MAX_VALUE, strength, true, false));
		                // Remove blindness for tin burners
		                if (allomancer.isPotionActive(Effects.BLINDNESS)) {
		                	allomancer.removePotionEffect(Effects.BLINDNESS);
		                } else {
		                    EffectInstance eff;
		                    eff = allomancer.getActivePotionEffect(Effects.NIGHT_VISION);
		
		                }
		
		            }
		            // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
		            if ((!cap.getMetalBurning(AllomancyCapability.TIN)) && (allomancer.getActivePotionEffect(Effects.NIGHT_VISION) != null)) {
		            	allomancer.removePotionEffect(Effects.NIGHT_VISION);
		            }
		            
		            
		            
		             
		        }
	    	} finally {}
    	}
    	
    }
}
