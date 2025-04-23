package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.vertexcubed.cybernetics.Cybernetics.modLoc;

public class CybSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Cybernetics.MOD_ID);

    public static final Supplier<SoundEvent>
            CYBERWARE_OPEN = register("cyberware_open"),
            CYBERWARE_CLOSE = register("cyberware_close"),
            CYBERWARE_CONFIRM = register("cyberware_confirm"),
            CYBERWARE_BUTTON = register("cyberware_button"),
            CYBERWARE_BACK = register("cyberware_back"),
            CYBERWARE_PAGE = register("cyberware_page"),

            DOUBLE_JUMP = register("double_jump")
    ;

    public static Supplier<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(modLoc(name)));
    }


    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
