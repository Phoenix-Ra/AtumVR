package me.phoenixra.atumvr.core.input.profile;

import me.phoenixra.atumvr.core.enums.ControllerType;
import me.phoenixra.atumvr.core.input.action.ActionIdentifier;
import me.phoenixra.atumvr.core.input.action.data.VRActionData;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataButton;
import me.phoenixra.atumvr.core.input.action.data.VRActionDataVec2;
import me.phoenixra.atumvr.core.VRProvider;
import me.phoenixra.atumvr.core.input.action.VRActionSet;
import me.phoenixra.atumvr.core.input.action.types.multi.FloatButtonMultiAction;
import me.phoenixra.atumvr.core.input.profile.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Abstract base class for interaction profile.
 *
 * <p>
 *     It is an action set made for specific {@link VRInteractionProfileType interaction profile}.<br>
 *     Contains all the actions supported by it.
 * </p>
 *
 * @see HpMixedRealityProfile
 * @see OculusTouchProfile
 * @see ValveIndexProfile
 * @see ViveProfile
 * @see ViveCosmosProfile
 * @see WindowsMotionProfile
 */
public abstract class VRInteractionProfile extends VRActionSet {


    public VRInteractionProfile(@NotNull VRProvider vrProvider,
                                @NotNull String name,
                                @NotNull String localizedName,
                                int priority) {
        super(vrProvider, name, localizedName, priority);
    }

    /**
     * Get interaction profile type
     *
     * @return the type
     */
    public abstract @NotNull VRInteractionProfileType getType();


    /**
     * Get trigger value action (float button)
     * <p>
     *     It is a common action for all {@link VRInteractionProfileType interaction profiles}
     * </p>
     *
     * @return the action
     */
    public abstract @NotNull FloatButtonMultiAction getTriggerValue();


    /**
     * Get all action ids
     *
     * @return the action ids
     */
    public abstract Collection<ActionIdentifier> getActionIds();

    /**
     * Get action by specified id or null if not found
     *
     * @param id the action identifier
     * @return the action or null
     */
    public abstract @Nullable VRActionData getAction(@NotNull ActionIdentifier id);


    /**
     * Get all button action ids
     *
     * @return the button action ids
     */
    public abstract Collection<ActionIdentifier> getButtonIds();

    /**
     * Get button action by specified id or null if not found
     *
     * @param id the action identifier
     * @return the button action or null
     */
    public abstract @Nullable VRActionDataButton getButton(@NotNull ActionIdentifier id);


    /**
     * Get all vec2 action ids
     *
     * @return the vec2 action ids
     */
    public abstract Collection<ActionIdentifier> getVec2Ids();

    /**
     * Get vec2 action by specified id or null if not found
     *
     * @param id the action identifier
     * @return action data or null
     */
    public abstract @Nullable VRActionDataVec2 getVec2(@NotNull ActionIdentifier id);


    /**
     * If this interaction profile is active.
     * <p>
     *     Only one interaction profile can be active at the same time
     * </p>
     *
     * @return true/false
     */
    public boolean isProfileActive(){
        return getTriggerValue().getHandSubaction(ControllerType.LEFT).isActive()
                || getTriggerValue().getHandSubaction(ControllerType.RIGHT).isActive();
    }
}
