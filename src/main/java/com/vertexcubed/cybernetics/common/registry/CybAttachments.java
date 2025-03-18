package com.vertexcubed.cybernetics.common.registry;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.storage.CyberwareInventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CybAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Cybernetics.MOD_ID);

    public static final Supplier<AttachmentType<CyberwareInventory>> CYBERWARE_INVENTORY = ATTACHMENT_TYPES.register(
            "cyberware_inventory", () -> AttachmentType.serializable(CyberwareInventory::create).copyOnDeath().build()
    );



    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
