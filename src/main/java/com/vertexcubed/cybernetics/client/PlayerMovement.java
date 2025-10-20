package com.vertexcubed.cybernetics.client;

import com.vertexcubed.cybernetics.Cybernetics;
import com.vertexcubed.cybernetics.common.item.DoubleJumpItem;
import com.vertexcubed.cybernetics.common.registry.CybAbilities;
import com.vertexcubed.cybernetics.common.registry.CybItems;
import com.vertexcubed.cybernetics.common.registry.CybKeyMappings;
import com.vertexcubed.cybernetics.common.util.AbilityHelper;
import com.vertexcubed.cybernetics.common.util.CyberwareHelper;
import com.vertexcubed.cybernetics.server.network.BidirectionalAbilityEventPayload;
import com.vertexcubed.cybernetics.server.network.C2SDoubleJumpPayload;
import com.vertexcubed.cybernetics.server.network.C2SSpikeShockwavePayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;


/**
 * Class that handles all player movement abilities. Uses a state machine to cleanly control what states can switch to what (i.e. can't jump out of a slam but CAN dash out of one)
 */
public class PlayerMovement {
    public static final PlayerMovement INSTANCE = new PlayerMovement();
    public static PlayerMovement getInstance() {
        return INSTANCE;
    }

    public static boolean canDoubleJump(LocalPlayer player, int numJumps) {
        return numJumps > 0 && CyberwareHelper.hasCyberware(CybItems.REINFORCED_TENDONS.get(), player);
    }
    public static boolean canSpike(LocalPlayer player) {
        return AbilityHelper.hasAbility(player, CybAbilities.KINETIC_DISCHARGER.get()) && !AbilityHelper.isOnCooldown(player, CybAbilities.KINETIC_DISCHARGER.get());
    }
    public static boolean canDash(LocalPlayer player) {
        return AbilityHelper.hasAbility(player, CybAbilities.DASH.get()) && !AbilityHelper.isOnCooldown(player, CybAbilities.DASH.get());
    }


    private State state;
    private PlayerMovement() {
        this.state = new GroundedState();
    }






    public void tick(LocalPlayer player) {
        this.state.tick(this, player);
//        Cybernetics.LOGGER.debug("Is enabled: {}", AbilityHelper.isEnabled(player, CybAbilities.KINETIC_DISCHARGER.get()));
    }

    public void setState(State state, LocalPlayer player) {
        this.state.onExit(this, player);
        this.state = state;
        this.state.onEnter(this, player);
    }

    public void reset(LocalPlayer player) {
        this.setState(new GroundedState(), player);
    }


    public abstract static class State {
        abstract void tick(PlayerMovement context, LocalPlayer player);
        public void onEnter(PlayerMovement context, LocalPlayer player) {}
        public void onExit(PlayerMovement context, LocalPlayer player) {}
    }
}

class GroundedState extends PlayerMovement.State {

    private boolean releasedDash = false;

    @Override
    public void onEnter(PlayerMovement context, LocalPlayer player) {
//        Cybernetics.LOGGER.debug("Grounded");
    }

    @Override
    void tick(PlayerMovement context, LocalPlayer player) {
        if(player.isInLiquid()) {
            return;
        }

        if(!CybKeyMappings.DASH.get().isDown()) {
            releasedDash = true;
        }
        else if(releasedDash && canDash(player)) {
            context.setState(new DashState(1), player);
            return;
        }


        if(!(player.onGround() || player.onClimbable())) {
            //TODO: change 1 to number of double jumps
            context.setState(new AirState(1), player);
            return;
        }
    }

    boolean canDash(LocalPlayer player) {
        return AbilityHelper.hasAbility(player, CybAbilities.DASH.get()) && !AbilityHelper.isOnCooldown(player, CybAbilities.DASH.get());
    }
}

class AirState extends PlayerMovement.State {

    private boolean releasedJump = false;
    private boolean releasedDash = false;
    private final int numJumps;
    public AirState(int numJumps) {
        this.numJumps = numJumps;
    }


    @Override
    public void onEnter(PlayerMovement context, LocalPlayer player) {
//        Cybernetics.LOGGER.debug("Airborne");
    }

    @Override
    void tick(PlayerMovement context, LocalPlayer player) {
        if(player.onGround() || player.onClimbable() || player.isInLiquid()) {
            context.setState(new GroundedState(), player);
            return;
        }
        // If flying, can't do anything
        if(player.getAbilities().flying) {
            return;
        }

        // if player is NOT jumping, set releasedJump to true
        if(!player.input.jumping) {
            releasedJump = true;
        }
        // else if player IS jumping, check if we can double jump
        else if(releasedJump && PlayerMovement.canDoubleJump(player, numJumps)) {
            // set next state to double jump
            context.setState(new DoubleJumpState(numJumps), player);
            return;
        }

        // Spiking
        if(player.isShiftKeyDown() && PlayerMovement.canSpike(player) && isHighEnoughToSpike(player)) {
            context.setState(new SpikeState(numJumps), player);
            return;
        }
        // Dashing
        if(!CybKeyMappings.DASH.get().isDown()) {
            releasedDash = true;
        }
        else if(releasedDash && PlayerMovement.canDash(player)) {
            context.setState(new DashState(numJumps), player);
            return;
        }
    }

    boolean isHighEnoughToSpike(Player player) {
        if(player.blockPosition().getY() > player.level().getMaxBuildHeight()) {
            return player.blockPosition().getY() - player.level().getMaxBuildHeight() >= 3;
        }

        for(int i = 1; i <= 3; i++) {
            BlockPos pos = player.blockPosition().below(i);
            if(!player.level().getBlockState(pos).isAir()) return false;
        }
        return true;
    }
}
class DoubleJumpState extends PlayerMovement.State {

    private final int numJumps;
    public DoubleJumpState(int numJumps) {
        this.numJumps = numJumps;
    }

    @Override
    public void onEnter(PlayerMovement context, LocalPlayer player) {
//        Cybernetics.LOGGER.debug("Double Jump");
        PacketDistributor.sendToServer(new C2SDoubleJumpPayload());
        DoubleJumpItem.doubleJump(player);
        context.setState(new AirState(numJumps - 1), player);
    }

    @Override
    void tick(PlayerMovement context, LocalPlayer player) { }
}
class SpikeState extends PlayerMovement.State {


    private boolean releasedDash = false;
    private int time = 0;
    // Spike packet needs this cuz jumps get passed around a lot lol.
    private final int numJumps;
    SpikeState(int numJumps) {
        this.numJumps = numJumps;
    }

    @Override
    public void onEnter(PlayerMovement context, LocalPlayer player) {
//        Cybernetics.LOGGER.debug("Spike");
        if(!AbilityHelper.enableAbility(player, CybAbilities.KINETIC_DISCHARGER.get())) {
            Cybernetics.LOGGER.warn("Could not enable kinetic discharger ability for player {}, this should not happen!", player.getDisplayName().getString());
        }
        PacketDistributor.sendToServer(new BidirectionalAbilityEventPayload(BidirectionalAbilityEventPayload.Mode.ENABLE, CybAbilities.KINETIC_DISCHARGER.get(), player.getId()));
    }

    @Override
    void tick(PlayerMovement context, LocalPlayer player) {
        time++;
        // if player starts flying, switch to airborne, pass through jumps, disable
        if(player.getAbilities().flying) {
            context.setState(new AirState(numJumps), player);
            return;
        }

        // can cancel with a dash but not a jump
        if(!CybKeyMappings.DASH.get().isDown()) {
            releasedDash = true;
        }
        else if(releasedDash && PlayerMovement.canDash(player)) {
            context.setState(new DashState(numJumps), player);
            return;
        }

        // Either of these: cancel, switch to ground
        if(player.onClimbable() || player.isInLiquid()) {
            context.setState(new GroundedState(), player);
            return;
        }
        // If on ground, send shockwave
        if(player.onGround()) {
//            Cybernetics.LOGGER.debug("Shockwave");
            PacketDistributor.sendToServer(new C2SSpikeShockwavePayload(this.time));
            context.setState(new GroundedState(), player);
            return;
        }


    }

    @Override
    public void onExit(PlayerMovement context, LocalPlayer player) {
        AbilityHelper.disableAbility(player, CybAbilities.KINETIC_DISCHARGER.get());
        PacketDistributor.sendToServer(new BidirectionalAbilityEventPayload(BidirectionalAbilityEventPayload.Mode.DISABLE, CybAbilities.KINETIC_DISCHARGER.get(), player.getId()));
    }
}

class DashState extends PlayerMovement.State {

    private final int numJumps;
    public DashState(int numJumps) {
        this.numJumps = numJumps;
    }

    @Override
    public void onEnter(PlayerMovement context, LocalPlayer player) {
//        Cybernetics.LOGGER.debug("Dash");
        if(!AbilityHelper.enableAbility(player, CybAbilities.DASH.get())) {
            Cybernetics.LOGGER.warn("Could not enable dash ability for player {}, this should not happen!", player.getDisplayName().getString());
        }
        PacketDistributor.sendToServer(new BidirectionalAbilityEventPayload(BidirectionalAbilityEventPayload.Mode.ENABLE, CybAbilities.DASH.get(), player.getId()));

        if(player.onGround()) {
            context.setState(new GroundedState(), player);
        }
        else {
            context.setState(new AirState(numJumps), player);
        }
    }

    @Override
    void tick(PlayerMovement context, LocalPlayer player) {}
}