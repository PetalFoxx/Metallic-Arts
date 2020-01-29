package com.crimson.allomancy.util;

import net.minecraft.util.DamageSource;

public class DrainDamage extends DamageSource {

	public DrainDamage() {
		super("Metallic Damage");

		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}
	
}
