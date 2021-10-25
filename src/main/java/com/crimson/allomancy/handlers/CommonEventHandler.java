package com.crimson.allomancy.handlers;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.entity.TimeBubble;
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
import com.crimson.allomancy.util.Metal;
import com.crimson.allomancy.util.Registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.apache.logging.log4j.Marker;

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
                		int randomMisting = (int) (Math.random() * Metal.getMetals());
                		int power = assignMisting();
                        cap.setCanBurn(power, true);
                        cap.setBurnStrength(power, 10);
                        cap.setIsAllomancer(true);
                	} else if (random < 7) {
                        cap.setCanStore(assignFerring(), true);
                        cap.setIsAllomancer(true);
                	} else {
                		int power = assignMisting();
                        cap.setCanBurn(power, true);
                        cap.setBurnStrength(power, 10);
                        cap.setCanStore(assignFerring(), true);
                        
                        cap.setIsAllomancer(true);
                	}
                    
                }

                //Sync cap to client
                NetworkHelper.sync(event.getPlayer());
            }
        }
    }
    
    public int assignFerring() {
    	int power;
    	power = (int) (Math.random() * Metal.getMetals());
    	while(power == 7 || power == 5 || power == 16 || power == 17) {
    		power = (int) (Math.random() * Metal.getMetals());
    	}
    	return power;
    }
    
    public int assignMisting() {
    	int power;
    	power = (int) (Math.random() * Metal.getMetals());
    	while(power == 16 || power == 17) {
    		power = (int) (Math.random() * Metal.getMetals());
    	}
    	return power;
    }
    
    

    @SubscribeEvent
    public void onPlayerClone(final net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (!event.getPlayer().world.isRemote()) {

            PlayerEntity player = event.getPlayer();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player); // the clone's cap

            PlayerEntity old = event.getOriginal();
            old.revive();
            
            		
            old.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(oldCap -> {
            	for (int i = 0; i < Metal.getMetals(); i++) {
            		if (oldCap.canBurn(i))
            			cap.setCanBurn(i, true);
            	}
            	
            	for (int i = 0; i < Metal.getMetals(); i++) {
            		if (oldCap.canStore(i))
            			cap.setCanStore(i, true);
            	}
            	
            	for (int i = 0; i < Metal.getMetals(); i++) {
            		cap.setBurnStrength(i, oldCap.getTrueBurnStrength(i));
            	}
            	
            	for (int i = 0; i < Metal.getMetals(); i++) {
            		cap.setSavant(i, oldCap.getSavant(i));
            	}
            	
            	cap.setIsAllomancer(oldCap.isAllomancer());
            

                if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
                    for (int i = 0; i < Metal.getMetals(); i++) {
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
    public void onCrit(final CriticalHitEvent event) {
    	if (event.getPlayer() instanceof LivingEntity) {
        	
        	LivingEntity source = (LivingEntity) event.getPlayer();
        	AllomancyCapability cap = AllomancyCapability.forPlayer(source);
        	
        	if(cap.getMetalBurning(Metal.ATIUM.getNumber()))
        	{
        		event.setResult(Result.ALLOW);
        	}
    	}
    }
    

    @SubscribeEvent
    public void onDamage(final LivingHurtEvent event) {
    	
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof LivingEntity) {
        	
        	LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
        	if(source!=null)
        		AllomancyUtils.LOGGER.info("Attacker: " + source.getEntityString());
        	//if(source != null)
        	//{
	            AllomancyCapability cap = AllomancyCapability.forPlayer(source);
	            AllomancyUtils.LOGGER.info("burning pewter: " + cap.getMetalBurning(Metal.PEWTER.getNumber()));
	            AllomancyUtils.LOGGER.info("burning pewter str: " + cap.getCalcBurnStrength(Metal.PEWTER.getNumber()));
	
	            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
	            	AllomancyUtils.LOGGER.info("Increasing Damage");
	                event.setAmount(event.getAmount() + (2 * (cap.getCalcBurnStrength(AllomancyCapability.PEWTER) / 10) ));
	            }
	            
	            if (cap.getMetalBurning(Metal.CHROMIUM.getNumber())) {
	            	
	                AllomancyCapability tarCap = AllomancyCapability.forPlayer(event.getEntityLiving());
	                for(int i = 0; i < Metal.getMetals(); i++) {
	                	tarCap.setBurnTime(i, 0);//tarCap.getBurnTime(i) - cap.getCalcBurnStrength(Metal.CHROMIUM.getNumber())*100);
	                	tarCap.setMetalAmounts(i,tarCap.getMetalAmounts(i) - cap.getCalcBurnStrength(Metal.CHROMIUM.getNumber()) / 10);
	                }
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
                event.setAmount(event.getAmount() - (1 * (cap.getCalcBurnStrength(AllomancyCapability.PEWTER) / 10) ));
                // Note that they took damage, will come in to play if they stop burning
                cap.setDamageStored(cap.getDamageStored() + (int) (0.5 * (cap.getCalcBurnStrength(AllomancyCapability.PEWTER) / 10) ));
            }
            
            if(cap.getMetalBurning(Metal.ATIUM.getNumber()))
        	{
            	if (event.getSource().getTrueSource() instanceof LivingEntity) {
                	LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
                	AllomancyCapability sourceCap = AllomancyCapability.forPlayer(source);
                	
                	if(sourceCap.getMetalBurning(Metal.ELECTRUM.getNumber())) {
                		float ratio = sourceCap.getCalcBurnStrength(Metal.ELECTRUM.getNumber()) / (cap.getCalcBurnStrength(Metal.ATIUM.getNumber() * 2));
                		event.setAmount(event.getAmount() * ratio);
                	} else if(sourceCap.getMetalBurning(Metal.ATIUM.getNumber())) {
                		float ratio = sourceCap.getCalcBurnStrength(Metal.ATIUM.getNumber()) / cap.getCalcBurnStrength(Metal.ATIUM.getNumber());
                		event.setAmount(event.getAmount() * ratio);
                	} else {
                		event.setAmount(0);
                	}
                	
            	} else {
            		event.setAmount(0);
            	}
        	}
            
            if(cap.getMetalBurning(Metal.ELECTRUM.getNumber()))
        	{
            	float dodge = cap.getCalcBurnStrength(10 + Metal.ELECTRUM.getNumber() * 2);
            	Random rand = new Random();
            	rand.nextInt(100);
            	if (event.getSource().getTrueSource() instanceof LivingEntity) {
                	LivingEntity source = (LivingEntity) event.getSource().getTrueSource();
                	AllomancyCapability sourceCap = AllomancyCapability.forPlayer(source);
                	
                	if(sourceCap.getMetalBurning(Metal.ELECTRUM.getNumber())) {
                		float ratio = sourceCap.getCalcBurnStrength(Metal.ELECTRUM.getNumber()) / (cap.getCalcBurnStrength(Metal.ELECTRUM.getNumber()));
                		dodge = dodge * ratio;
                		if(rand.nextInt(100) < (int) dodge)
                			event.setAmount(0);
                	} else if(sourceCap.getMetalBurning(Metal.ATIUM.getNumber())) {
                		float ratio = (sourceCap.getCalcBurnStrength(Metal.ATIUM.getNumber()) * 2) / cap.getCalcBurnStrength(Metal.ELECTRUM.getNumber());
                		dodge = dodge * ratio;
                		if(rand.nextInt(100) < (int) dodge)
                			event.setAmount(0);
                	} else {
                		if(rand.nextInt(100) < (int) dodge)
                			event.setAmount(0);
                	}
                	
            	} else {
            		if(rand.nextInt(100) < (int) dodge)
            			event.setAmount(0);
            	}
        	}
            
            if (cap.getMetalBurning(Metal.CHROMIUM.getNumber())) {
                if(event.getSource().getTrueSource() instanceof LivingEntity) {
	                AllomancyCapability tarCap = AllomancyCapability.forPlayer((LivingEntity) event.getSource().getTrueSource());
	                
	                if(tarCap != null) {
		                for(int i = 0; i < Metal.getMetals(); i++) {
		                	tarCap.setBurnTime(i, tarCap.getBurnTime(i) - cap.getCalcBurnStrength(Metal.CHROMIUM.getNumber()) * 50);
		                }
	                }
                }
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
	        	witherCap.setMetalBurning(AllomancyCapability.PEWTER, true, wither);
	        	
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
    	Random rand = new Random();
    	if(event.getEntity() instanceof ArrowEntity) {
    		ArrowEntity arrow = (ArrowEntity) event.getEntity();
    		Entity firer = arrow.getShooter();
        	
        	if(firer instanceof LivingEntity) {
        		AllomancyCapability cap = AllomancyCapability.forPlayer((LivingEntity) firer);
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
        			arrow.setDamage(arrow.getDamage() * (1 + (0.02 * cap.getCalcBurnStrength(AllomancyCapability.TIN))));
        			if(cap.getCalcBurnStrength(AllomancyCapability.TIN) >= 30)
        				arrow.setIsCritical(true);
        		}
        		
        		if(cap.getMetalBurning(AllomancyCapability.STEEL)) {
        			arrow.setDamage(arrow.getDamage() * (1 + (0.01 * cap.getCalcBurnStrength(AllomancyCapability.STEEL))));
        			Vec3d motion = arrow.getMotion();
        			for(int i = 0; i < (cap.getCalcBurnStrength(AllomancyCapability.STEEL)/10); i++) {
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
    public void onDropXP(LivingExperienceDropEvent event) {
    	int xp = event.getDroppedExperience();
    	
    	AllomancyUtils.LOGGER.info("drop xp");
    	PlayerEntity player = event.getAttackingPlayer();
    	AllomancyCapability cap = AllomancyCapability.forPlayer(player);
    	if(cap.hasMetalmind(AllomancyCapability.ZINC))
    	{
    		AllomancyUtils.LOGGER.info("feru xp");
    		ZincMetalMind metalmind = (ZincMetalMind) cap.getMetalmind(AllomancyCapability.ZINC).getItem();
    		if(metalmind.getAction() == METALMIND_ACTION.TAPPING && MetalMindItem.canLose(cap.getMetalmind(AllomancyCapability.ZINC))){
    			xp = (int) (xp * (1.2 + (0.2 * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.ZINC)))));
    			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.ZINC));
    		}
    		if(metalmind.getAction() == METALMIND_ACTION.FILLING && MetalMindItem.canGain(cap.getMetalmind(AllomancyCapability.ZINC))) {
    			xp = (int) (xp * (0.8 - (0.2 * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.ZINC)))));
    			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.ZINC));
    		}
    	}
    	
    	if(cap.getMetalBurning(Metal.GOLD.getNumber())) {
    		AllomancyUtils.LOGGER.info("allo xp");
    		Random rand = new Random();
    		if(rand.nextInt(10) == 0) {
	    		player.sendMessage(new TranslationTextComponent("You glimpse a life you could have lead, a different turn in the path."));
	    		xp = (int) (xp * cap.getCalcBurnStrength(Metal.GOLD.getNumber()));
    		}
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


//    @SubscribeEvent
//    public void onWorldTick(final TickEvent.WorldTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//
//            World world = (World) event.world;
//            List<? extends PlayerEntity> list = world.getPlayers();
//            
//            
//            for (PlayerEntity curPlayer : list) {
//                
//                List<LivingEntity> nearby;
//                
//                int xLoc = (int) curPlayer.posX, yLoc = (int) curPlayer.posY, zLoc = (int) curPlayer.posZ;
//                BlockPos negative = new BlockPos(xLoc - 20, yLoc - 20, zLoc - 20);
//                BlockPos positive = new BlockPos(xLoc + 20, yLoc + 20, zLoc + 20);
//                nearby = curPlayer.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive), entity -> entity != null);
//                
//                for(LivingEntity entity : nearby)
//                {
//                	if(AllomancyCapability.forPlayer(entity) != null)
//                		runAllomancy(entity);
//                }
//                //runAllomancy(curPlayer);
//            }
//        }
//    }
    
    @SubscribeEvent
    public static void onExperiencePickup(PlayerPickupXpEvent event) {
    	AllomancyUtils.LOGGER.info("Picked up xp");
    	PlayerEntity player = event.getPlayer();
    	AllomancyCapability cap = AllomancyCapability.forPlayer(player);
    	if(cap.hasMetalmind(AllomancyCapability.ZINC))
    	{
    		AllomancyUtils.LOGGER.info("feru xp");
    		ZincMetalMind metalmind = (ZincMetalMind) cap.getMetalmind(AllomancyCapability.ZINC).getItem();
    		if(metalmind.getAction() == METALMIND_ACTION.TAPPING && MetalMindItem.canLose(cap.getMetalmind(AllomancyCapability.ZINC))){
    			event.getOrb().xpValue = (int) (event.getOrb().xpValue * (1.2 + (0.2 * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.ZINC)))));
    			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.ZINC));
    		}
    		if(metalmind.getAction() == METALMIND_ACTION.FILLING && MetalMindItem.canGain(cap.getMetalmind(AllomancyCapability.ZINC))) {
    			event.getOrb().xpValue = (int) (event.getOrb().xpValue * (0.8 - (0.2 * MetalMindItem.getStrength(cap.getMetalmind(AllomancyCapability.ZINC)))));
    			MetalMindItem.setUsed(true, cap.getMetalmind(AllomancyCapability.ZINC));
    		}
    	}
    	
    	if(cap.getMetalBurning(Metal.GOLD.getNumber())) {
    		AllomancyUtils.LOGGER.info("allo xp");
    		Random rand = new Random();
    		if(true) {//rand.nextInt(10) == 0) {
	    		player.sendMessage(new TranslationTextComponent("You glimpse a life you could have lead, a different turn in the path."));
	    		event.getOrb().xpValue = (int) (event.getOrb().xpValue * cap.getCalcBurnStrength(Metal.GOLD.getNumber()));
    		}
    	}
    }
    

    
	@SubscribeEvent
	public void livingTick(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		if(!entity.world.isRemote)
			if(AllomancyCapability.forPlayer(entity) != null && AllomancyCapability.forPlayer(entity).isAllomancer())
			{
				runAllomancy(entity);
				if(AllomancyCapability.forPlayer(entity).getMetalBurning(Metal.ATIUM.getNumber())) {
					runAtium(entity);
				}
			}
		
		
	}
    
    public void runAtium(LivingEntity entity) {
    	
    	
    	BlockPos negative = new BlockPos(entity.posX - 10, entity.posY - 5/*max*/, entity.posZ - 10);
        BlockPos positive = new BlockPos(entity.posX + 10, entity.posY + 5/*max*/, entity.posZ + 10);
        // Add metal entities to metal list
        List<LivingEntity> entities = entity.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive));
        
        for(LivingEntity living : entities) {
        	AllomancyCapability livCap = AllomancyCapability.forPlayer(living);
        	AllomancyCapability cap = AllomancyCapability.forPlayer(entity);
        	if(!(livCap.getMetalBurning(Metal.ATIUM.getNumber()) || livCap.getMetalBurning(Metal.ELECTRUM.getNumber()))) {
        		living.addPotionEffect(new EffectInstance(Effects.HASTE, 5, -1 * (cap.getCalcBurnStrength(Metal.ATIUM.getNumber()) / 5), false, false, false));
        		living.addPotionEffect(new EffectInstance(Effects.SPEED, 5, -1 * (cap.getCalcBurnStrength(Metal.ATIUM.getNumber()) / 5), false, false, false));
        	}
        }
    }
    
    
    
    
    
    public void runAllomancy(LivingEntity allomancer) {
    	World world = (World) allomancer.world;
    	//if(!world.isRemote()) {
	    	try {
	    		
		    	if(AllomancyCapability.forPlayer(allomancer).isInCoppercloud() > 0)
		    		AllomancyCapability.forPlayer(allomancer).setIsInCoppercloud(0);
		    	
		    	AllomancyCapability cap = AllomancyCapability.forPlayer(allomancer);
		    	if (cap.isAllomancer()) {
		            // Run the necessary updates on the player's metals
		            if (allomancer instanceof LivingEntity) {
		                AllomancyUtils.updateMetalBurnTime(cap, (LivingEntity) allomancer);
		            }
		            if(cap.getNicro() > 0) {
		            	cap.setNicro(cap.getNicro() - 1);
		            	if(cap.getNicro() == 0) {
		            		cap.setNicroStr(0);
		            	}
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
		            	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.PEWTER) / 10;
		            	allomancer.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 30, strength, true, false, true));
		            	//allomancer.addPotionEffect(new EffectInstance(Effects.SPEED, 30, strength - 1, true, false, false));
		            	//allomancer.addPotionEffect(new EffectInstance(Effects.HASTE, 30, strength - 1, true, false, false));
		
		                if (cap.getDamageStored() > 0) {
		                    if (world.rand.nextInt(200) == 0) {
		                        cap.setDamageStored(cap.getDamageStored() - (1 * ((int) cap.getCalcBurnStrength(AllomancyCapability.PEWTER) / 20)));
		                    }
		                }
		
		            }
		            if (cap.getMetalBurning(Metal.ATIUM.getNumber())) {
		            	int strength = (int) cap.getCalcBurnStrength(Metal.ATIUM.getNumber()) / 10;
		            	allomancer.addPotionEffect(new EffectInstance(Effects.SPEED, 30, strength - 1, true, false));
		            	allomancer.addPotionEffect(new EffectInstance(Effects.HASTE, 30, strength - 1, true, false));
		            }
		            if (cap.getMetalBurning(AllomancyCapability.TIN)) {
		            	int strength = (int) cap.getCalcBurnStrength(AllomancyCapability.TIN) / 10;
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
		            
		            if(cap.getMetalBurning(Metal.BENDALLOY.getNumber())) {
		            	if(cap.bubble != null) {
		            		cap.bubble = new TimeBubble(EntityType.AREA_EFFECT_CLOUD, world, true, allomancer);
		            	}
		            		
		            }
		            
		            
		             
		        }
	    	} finally {}
    	//}
    	
    }
}
