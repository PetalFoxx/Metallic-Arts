package com.crimson.allomancy.item.metalmind;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;

public class MetalMindPotion extends Potion {

    public MetalMindPotion(String name, boolean badEffect, int potionColor, int iconIndexX, int iconIndexY) {
        super(name, potionColor);
        this.setPotionName(name);
        //this.setIconIndex(iconIndexX, iconIndexY);
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return true;
    }

    public PotionEffect apply(EntityLivingBase entity, int duration) {
        return apply(entity, duration, 0);
    }

    public PotionEffect apply(EntityLivingBase entity, int duration, int level) {
        //PotionEffect effect = new PotionEffect(this, duration, level, false, false);
    	EffectInstance effect = new EffectInstance(this);//, duration, level, false, false);
        entity.addPotionEffect(effect);
        return effect;
    }

    public int getLevel(EntityLivingBase entity) {
        PotionEffect effect = entity.getActivePotionEffect(this);
        if (effect != null) {
            return effect.getAmplifier();
        }
        return 0;
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        return super.getStatusIconIndex();
    }
}
