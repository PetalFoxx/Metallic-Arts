package com.crimson.allomancy.util;

import com.crimson.allomancy.Allomancy;
import com.crimson.allomancy.ModBlocks;
import com.crimson.allomancy.block.AlloyingSmelter;
import com.crimson.allomancy.block.IronButtonBlock;
import com.crimson.allomancy.block.IronLeverBlock;
import com.crimson.allomancy.block.MetalPurifier;
import com.crimson.allomancy.effect.MetalEffect;
import com.crimson.allomancy.entity.GoldNuggetEntity;
import com.crimson.allomancy.entity.IronNuggetEntity;
import com.crimson.allomancy.entity.NuggetEntity;
import com.crimson.allomancy.entity.TimeBubble;
import com.crimson.allomancy.item.*;
import com.crimson.allomancy.item.metalmind.BrassMetalMind;
import com.crimson.allomancy.item.metalmind.BronzeMetalMind;
import com.crimson.allomancy.item.metalmind.CopperMetalMind;
import com.crimson.allomancy.item.metalmind.IronMetalMind;
import com.crimson.allomancy.item.metalmind.PewterMetalMind;
import com.crimson.allomancy.item.metalmind.SteelMetalMind;
import com.crimson.allomancy.item.metalmind.TinMetalMind;
import com.crimson.allomancy.item.metalmind.ZincMetalMind;
import com.crimson.allomancy.item.recipe.VialItemRecipe;
import com.crimson.allomancy.mobs.AllomanticZombie;
import com.crimson.allomancy.network.packets.*;
import com.crimson.allomancy.tileentity.AlloyingSmelterTileEntity;
import com.crimson.allomancy.tileentity.MetalPurifierTileEntity;
import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = Allomancy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registry {

    // Item Holders
    @ObjectHolder("allomancy:allomantic_grinder")
    public static Item allomantic_grinder;
    @ObjectHolder("allomancy:tin_ingot")
    public static Item tin_ingot;
    @ObjectHolder("allomancy:pewter_ingot")
    public static Item pewter_ingot;
    @ObjectHolder("allomancy:lead_ingot")
    public static Item lead_ingot;
    @ObjectHolder("allomancy:copper_ingot")
    public static Item copper_ingot;
    @ObjectHolder("allomancy:zinc_ingot")
    public static Item zinc_ingot;
    @ObjectHolder("allomancy:bronze_ingot")
    public static Item bronze_ingot;
    @ObjectHolder("allomancy:brass_ingot")
    public static Item brass_ingot;
    @ObjectHolder("allomancy:steel_ingot")
    public static Item steel_ingot;
    
    @ObjectHolder("allomancy:chromium_ingot")
    public static Item chromium_ingot;
    @ObjectHolder("allomancy:nicrosil_ingot")
    public static Item nicrosil_ingot;
    @ObjectHolder("allomancy:aluminium_ingot")
    public static Item aluminium_ingot;
    @ObjectHolder("allomancy:duralumin_ingot")
    public static Item duralumin_ingot;
    @ObjectHolder("allomancy:cadmium_ingot")
    public static Item cadmium_ingot;
    @ObjectHolder("allomancy:bendalloy_ingot")
    public static Item bendalloy_ingot;
    @ObjectHolder("allomancy:electrum_ingot")
    public static Item electrum_ingot;
    
    @ObjectHolder("allomancy:atium_bead")
    public static Item atium_bead;
    
    
    @ObjectHolder("allomancy:coin_bag")
    public static Item coin_bag;
    @ObjectHolder("allomancy:mistcloak")
    public static MistcloakItem mistcloak;
    @ObjectHolder("allomancy:lerasium_nugget")
    public static LerasiumItem lerasium_nugget;
    @ObjectHolder("allomancy:vial")
    public static VialItem vial;
    public static Item[] flakes;
    public static LerasiumAlloy[] lerasiumAlloys;
    
    //@ObjectHolder("allomancy:metal_purifier")
    //public static MetalPurifier metal_purifier = new MetalPurifier();
    //@ObjectHolder("allomancy:alloying_smelter")
    //public static AlloyingSmelter alloying_smelter; //= new AlloyingSmelter();


    // Block Holders
    @ObjectHolder("allomancy:tin_ore")
    public static Block tin_ore;
    @ObjectHolder("allomancy:lead_ore")
    public static Block lead_ore;
    @ObjectHolder("allomancy:copper_ore")
    public static Block copper_ore;
    @ObjectHolder("allomancy:zinc_ore")
    public static Block zinc_ore;
    
    @ObjectHolder("allomancy:chromium_ore")
    public static Block chromium_ore;
    @ObjectHolder("allomancy:aluminium_ore")
    public static Block aluminium_ore;
    @ObjectHolder("allomancy:cadmium_ore")
    public static Block cadmium_ore;
    @ObjectHolder("allomancy:silver_ore")
    public static Block silver_ore;
    @ObjectHolder("allomancy:ati_ore")
    public static Block ati_ore;
    //@ObjectHolder("allomancy:iron_lever")
    //public static IronLeverBlock iron_lever;
    //@ObjectHolder("allomancy:iron_button")
    //public static IronButtonBlock iron_button;

    //Recipe holder
    @ObjectHolder("allomancy:vial_filling")
    public static SpecialRecipeSerializer<VialItemRecipe> vial_recipe_serializer;

    //EntityType holders
    @ObjectHolder("allomancy:iron_nugget")
    public static EntityType<IronNuggetEntity> iron_nugget;
    @ObjectHolder("allomancy:gold_nugget")
    public static EntityType<GoldNuggetEntity> gold_nugget;

    @OnlyIn(Dist.CLIENT)
    public static KeyBinding burn;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding flare;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding selection;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding mark;

    @OnlyIn(Dist.CLIENT)
    public static KeyBinding mind;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding activeIron;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding activeSteel;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding activeZinc;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding activeBrass;
    
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding activeNicro;
    
    
    
    
    /*public static final Potion STORE_IRON = null;
    public static final Potion TAP_IRON = null;
    public static final Potion STORE_STEEL = null;
    public static final Potion TAP_STEEL = null;
    public static final Potion STORE_COPPER = null;
    public static final Potion TAP_COPPER = null;
    public static final Potion STORE_BRONZE = null;
    public static final Potion TAP_BRONZE = null;
    public static final Potion STORE_TIN = null;
    public static final Potion TAP_TIN = null;
    public static final Potion STORE_PEWTER = null;
    public static final Potion TAP_PEWTER = null;
    public static final Potion STORE_ZINC = null;
    public static final Potion TAP_ZINC = null;
    public static final Potion STORE_BRASS = null;
    public static final Potion TAP_BRASS = null; //MobEffects.HASTE;*/
    public static Effect EFFECT[] = new Effect[Metal.getMetals()];
    
    @ObjectHolder("allomancy:time_bubble")
    public static EntityType<TimeBubble> time_bubble = null;
    
    //@ObjectHolder("allomancy:allomantic_zombie")
    //public static EntityType<AllomanticZombie> allomantic_zombie = null;
    
    //@ObjectHolder("allomancy:spawn_allomantic_zombie")
    //public static final Item spawn_allomantic_zombie = null;
    
    
    
    
    
    
    
    
    
    
    public static final String[] allomanctic_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze"};

    public static final String[] metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze", "lead"};
    

    //@SubscribeEvent
    /*public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(
                new Potion("Store Iron", new EffectInstance(Effects.LUCK, 0, 0, false, false)).setRegistryName("store_iron"),
                new Potion("Tap Iron").setRegistryName("tap_iron"),
                new Potion("Store Steel").setRegistryName("store_steel"),
                new Potion("Tap Steel").setRegistryName("tap_steel"),
                new Potion("Store Copper").setRegistryName("store_copper"),
                new Potion("Tap Copper").setRegistryName("tap_copper"),
                new Potion("Store Bronze").setRegistryName("store_bronze"),
                new Potion("Tap Bronze").setRegistryName("tap_bronze"),
                new Potion("Store Tin").setRegistryName("store_tin"),
                new Potion("Tap Tin").setRegistryName("tap_tin"),
                new Potion("Store Pewter").setRegistryName("store_pewter"),
                new Potion("Tap Pewter").setRegistryName("tap_pewter"),
                new Potion("Store Zinc").setRegistryName("store_zinc"),
                new Potion("Tap Zinc").setRegistryName("tap_zinc"),
                new Potion("Store Brass").setRegistryName("store_brass"),
                new Potion("Tap Brass").setRegistryName("tap_brass")
        );
    }*/

    
    
    
    
    //public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Allomancy.MODID, "networking"))
    //        .clientAcceptedVersions(s -> true)
    //       .serverAcceptedVersions(s -> true)
    //        .networkProtocolVersion(() -> "1.0.0")
    //        .simpleChannel();
    
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Allomancy.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
   

    public static ItemGroup allomancy_group = new ItemGroup(Allomancy.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registry.mistcloak);
        }
    };


    public static IArmorMaterial WoolArmor = new IArmorMaterial() {
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 250;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return slotIn == EquipmentSlotType.CHEST ? 4 : 0;
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(Items.GRAY_WOOL);
        }

        @Override
        public String getName() {
            return "allomancy:wool";
        }

        @Override
        public float getToughness() {
            return 0;
        }
    };


    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", GLFW.GLFW_KEY_R, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
        
        flare = new KeyBinding("key.flare", GLFW.GLFW_KEY_F, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(flare);
        
        selection = new KeyBinding("key.selection", GLFW.GLFW_KEY_C, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(selection);
        
        mark = new KeyBinding("key.mark", GLFW.GLFW_KEY_M, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(mark);
        
        mind = new KeyBinding("key.mind", GLFW.GLFW_KEY_Z, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(mind);
        
        activeIron = new KeyBinding("key.activeIron", GLFW.GLFW_KEY_X, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(activeIron);
        
        activeSteel = new KeyBinding("key.activeSteel", GLFW.GLFW_KEY_X, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(activeSteel);
        
        activeBrass = new KeyBinding("key.activeBrass", GLFW.GLFW_KEY_X, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(activeBrass);
        
        activeZinc = new KeyBinding("key.activeZinc", GLFW.GLFW_KEY_X, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(activeZinc);
        
        activeNicro = new KeyBinding("key.activeNicro", GLFW.GLFW_KEY_X, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(activeNicro);
    }

    public static void registerPackets() {
        int index = 0;
        NETWORK.registerMessage(index++, AllomancyCapabilityPacket.class, AllomancyCapabilityPacket::encode, AllomancyCapabilityPacket::decode, AllomancyCapabilityPacket::handle);
        NETWORK.registerMessage(index++, UpdateBurnPacket.class, UpdateBurnPacket::encode, UpdateBurnPacket::decode, UpdateBurnPacket::handle);
        NETWORK.registerMessage(index++, UpdateFlarePacket.class, UpdateFlarePacket::encode, UpdateFlarePacket::decode, UpdateFlarePacket::handle);
        NETWORK.registerMessage(index++, ChangeEmotionPacket.class, ChangeEmotionPacket::encode, ChangeEmotionPacket::decode, ChangeEmotionPacket::handle);
        NETWORK.registerMessage(index++, TryPushPullEntity.class, TryPushPullEntity::encode, TryPushPullEntity::decode, TryPushPullEntity::handle);
        NETWORK.registerMessage(index++, TryPushPullBlock.class, TryPushPullBlock::encode, TryPushPullBlock::decode, TryPushPullBlock::handle);
        NETWORK.registerMessage(index, NicroBurstPacket.class, NicroBurstPacket::encode, NicroBurstPacket::decode, NicroBurstPacket::handle);
    }


    @SubscribeEvent
    public static void onRegisterRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new VialItemRecipe.Serializer());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        Item.Properties prop_generic = new Item.Properties().group(allomancy_group).maxStackSize(64);
        Item.Properties prop_rare = new Item.Properties().group(allomancy_group).maxStackSize(64).rarity(Rarity.RARE);
        event.getRegistry().registerAll(
                new GrinderItem(),
                new CoinBagItem(),
                new MistcloakItem(),
                new LerasiumItem(),
                new VialItem(),
                new SteelMetalMind(),
                new PewterMetalMind(),
                new CopperMetalMind(),
                new IronMetalMind(),
                new TinMetalMind(),
                new ZincMetalMind(),
                new BrassMetalMind(),
                new BronzeMetalMind(),
                // Register ingots and add them to the ore dictionary
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "bronze_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "brass_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "pewter_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "steel_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "chromium_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "nicrosil_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "aluminium_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "duralumin_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "cadmium_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "electrum_ingot")),
                new Item(prop_rare).setRegistryName(new ResourceLocation(Allomancy.MODID, "ati_bead"))

        );
     

        // Register flakes
        for (int i = 0; i < metals.length; i++) {
            event.getRegistry().registerAll(
                    new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, metals[i] + "_flakes")),
                    new NuggetItem(i)
                    );
           
        }
        
     // Register lerasium alloys
        for (int i = 0; i < Metal.getMetals(); i++) {
            event.getRegistry().register(
                    new LerasiumAlloy(i));
           
        }


        //Register ore block items
        event.getRegistry().registerAll(
                new BlockItem(tin_ore, prop_generic).setRegistryName(tin_ore.getRegistryName()),
                new BlockItem(lead_ore, prop_generic).setRegistryName(lead_ore.getRegistryName()),
                new BlockItem(copper_ore, prop_generic).setRegistryName(copper_ore.getRegistryName()),
                new BlockItem(zinc_ore, prop_generic).setRegistryName(zinc_ore.getRegistryName()),
                new BlockItem(chromium_ore, prop_generic).setRegistryName(chromium_ore.getRegistryName()),
                new BlockItem(cadmium_ore, prop_generic).setRegistryName(cadmium_ore.getRegistryName()),
                new BlockItem(silver_ore, prop_generic).setRegistryName(silver_ore.getRegistryName()),
                new BlockItem(aluminium_ore, prop_generic).setRegistryName(aluminium_ore.getRegistryName()),
                new BlockItem(ati_ore, prop_rare).setRegistryName(ati_ore.getRegistryName()),
                new BlockItem(ModBlocks.IRON_LEVER, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(ModBlocks.IRON_LEVER.getRegistryName()),
                new BlockItem(ModBlocks.IRON_BUTTON, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(ModBlocks.IRON_BUTTON.getRegistryName()),
                new BlockItem(ModBlocks.METAL_PURIFIER, prop_generic).setRegistryName(ModBlocks.METAL_PURIFIER.getRegistryName()),
                new BlockItem(ModBlocks.ALLOYING_SMELTER, prop_generic).setRegistryName(ModBlocks.ALLOYING_SMELTER.getRegistryName())

        );
        flakes = getFlakeItems();
        
        
    }
    


    public static Item[] getFlakeItems() {
        Item[] flakes = new Item[9];
        for (int i = 0; i < flakes.length; i++) {
            flakes[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Allomancy.MODID, metals[i] + "_flakes"));
        }
        return flakes;
    }
    
    public static Item[] getNuggetItems() {
        Item[] nuggets = new Item[9];
        for (int i = 0; i < nuggets.length; i++) {
        	nuggets[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Allomancy.MODID, metals[i] + "_nugget"));
        }
        return nuggets;
    }
    
    public static EntityType<? extends ProjectileItemEntity>[] getNuggetEntities() {
    	EntityType<? extends ProjectileItemEntity>[] nuggets = new EntityType[9];
        for (int i = 0; i < nuggets.length; i++) {
        	nuggets[i] = (EntityType<? extends ProjectileItemEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(Allomancy.MODID, metals[i] + "_nugget"));
        }
        return nuggets;
    }


    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        Block.Properties prop = Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2);
        Block.Properties propRare = Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).harvestLevel(3);
        event.getRegistry().registerAll(
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "chromium_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "cadmium_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "silver_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "aluminium_ore")),
                new Block(propRare).setRegistryName(new ResourceLocation(Allomancy.MODID, "ati_ore")),
                new IronLeverBlock(),
                new IronButtonBlock(),
                new MetalPurifier(),
                new AlloyingSmelter()
        );

    }

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders() {
        //Use renderSnowball for nugget projectiles
        RenderingRegistry.registerEntityRenderingHandler(GoldNuggetEntity.class, manager -> new SpriteRenderer<GoldNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IronNuggetEntity.class, manager -> new SpriteRenderer<IronNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
    }


    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
//        event.getRegistry().register(EntityType.Builder.<IronNuggetEntity>create(IronNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
//                .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new IronNuggetEntity(iron_nugget, world)).size(0.25F, 0.25F).build("iron_nugget")
//                .setRegistryName(Allomancy.MODID, "iron_nugget"));
        event.getRegistry().register(EntityType.Builder.<GoldNuggetEntity>create(GoldNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
                .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new GoldNuggetEntity(gold_nugget, world)).size(0.25F, 0.25F).build("gold_nugget")
                .setRegistryName(Allomancy.MODID, "gold_nugget"));
        
        for (int i = 0; i < metals.length; i++) {
        	final int test = i;
	        event.getRegistry().register(EntityType.Builder.<NuggetEntity>create(NuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
	                .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new NuggetEntity(getNuggetEntities()[test], world)).size(0.25F, 0.25F).build(getNuggetItems()[i].getRegistryName().toString())
	                .setRegistryName(getNuggetItems()[i].getRegistryName().toString()));
        }
        
        //event.getRegistry().register(allomantic_zombie);
    }
    
   /* @SubscribeEvent
    public static void registerSpawnEggs(final RegistryEvent.Register<Item> event) {
    	genEntity();
        event.getRegistry().registerAll(
                new SpawnEggItem(allomantic_zombie, 0, 0,
                        new Item.Properties().group(ItemGroup.MISC)
                ).setRegistryName(Allomancy.MODID, "spawn_allomantic_zombie")
        );
    }
    
    private static void genEntity() {
    	allomantic_zombie = (EntityType<AllomanticZombie>) EntityType.Builder
                .<AllomanticZombie>create(AllomanticZombie::new, EntityClassification.MONSTER)
                .size(2F, 1F)
                .build("allomantic_zombie").setRegistryName(Allomancy.MODID, "allomantic_zombie");
    }
    
    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
        		allomantic_zombie
        );
    }*/
    
    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
        		//new TimeBubble(time_bubble, null).setRegistryName(Allomancy.MODID, "time_bubble")
        		(EntityType<TimeBubble>) EntityType.Builder
                .<TimeBubble>create(TimeBubble::new, EntityClassification.AMBIENT)
                .size(1F, 1F)
                .build("time_bubble").setRegistryName(Allomancy.MODID, "time_bubble")
        );
    }
    

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> e) {
    	e.getRegistry().registerAll(
    			setup(TileEntityType.Builder.create((Supplier<TileEntity>) MetalPurifierTileEntity::new, ModBlocks.METAL_PURIFIER).build(null), "metal_purifier"),
    			setup(TileEntityType.Builder.create((Supplier<TileEntity>) AlloyingSmelterTileEntity::new, ModBlocks.ALLOYING_SMELTER).build(null), "alloying_smelter")
        );
    }
    
    @Nonnull
	private static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry, @Nonnull final String name) {
		Preconditions.checkNotNull(name, "Name to assign to entry cannot be null!");
		return setup(entry, new ResourceLocation(Allomancy.MODID, name));
	}
    
    @Nonnull
	private static <T extends IForgeRegistryEntry<T>> T setup(@Nonnull final T entry, @Nonnull final ResourceLocation registryName) {
		Preconditions.checkNotNull(entry, "Entry cannot be null!");
		Preconditions.checkNotNull(registryName, "Registry name to assign to entry cannot be null!");
		entry.setRegistryName(registryName);
		return entry;
	}
    
    
    @SubscribeEvent
    public static void registerEffects(final RegistryEvent.Register<Effect> event) {	
    	for(int i = 0; i < Metal.getMetals(); i++) {
    		EFFECT[i] = new MetalEffect(EffectType.NEUTRAL, i, Metal.getMetal(i).getName()).setRegistryName(new ResourceLocation(Allomancy.MODID, "effect_" + Metal.getMetal(i).getName()));
    		event.getRegistry().register(EFFECT[i]);
    	}
    }

}
