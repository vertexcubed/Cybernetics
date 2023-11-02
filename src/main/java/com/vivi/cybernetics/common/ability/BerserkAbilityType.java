package com.vivi.cybernetics.common.ability;

import com.vivi.cybernetics.Cybernetics;
import com.vivi.cybernetics.client.shader.BerserkRenderer;
import com.vivi.cybernetics.client.util.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BerserkAbilityType extends AbilityType {

    protected final int duration;
    private static final UUID speedUUID = UUID.fromString("346cc9b7-3c4a-4bee-9c8a-360daa1381ea");
    public BerserkAbilityType(int duration, int maxCooldown) {
        super(maxCooldown);
        this.duration = duration;
    }

    @Override
    public void onEnable(Ability ability, Level level, Player player) {
        super.onEnable(ability, level, player);
        if(level.isClientSide) {
            //enable orange post shader
            BerserkRenderer.getInstance().start(20, Easing.EXPO_IN_OUT);
//            Minecraft.getInstance().gameRenderer.loadEffect(new ResourceLocation(Cybernetics.MOD_ID, "shaders/post/tint.json"));
        }
    }

    @Override
    public void onDisable(Ability ability, Level level, Player player) {
        super.onDisable(ability, level, player);
        //remove speed boost attribute
        AttributeInstance playerAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if(playerAttribute != null) {
            playerAttribute.removeModifier(speedUUID);
        }
        if(player.hasEffect(MobEffects.DAMAGE_RESISTANCE) && player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() == 3) {
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        }

        if(level.isClientSide) {
            //disable orange post shader
            BerserkRenderer.getInstance().stop(20, Easing.EXPO_IN_OUT);
//            Minecraft.getInstance().gameRenderer.shutdownEffect();
        }
    }

    @Override
    public void tick(Ability ability, Level level, Player player) {
        super.tick(ability, level, player);
        if(!ability.isEnabled()) return;
        //unable to die, maybe regen 1?


        AttributeInstance playerAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if(playerAttribute != null && playerAttribute.getModifier(speedUUID) == null) {
            playerAttribute.addPermanentModifier(new AttributeModifier(speedUUID, "Berserk Speed", 0.3, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 319, 3, false, true, true));
        if(ability.getElapsedTime() >= duration) {
            ability.disable(player);
        }
    }
}