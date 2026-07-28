package me.phoenixra.atumvr.core.input.body;

import me.phoenixra.atumvr.api.input.body.AtumVRBodyView;
import me.phoenixra.atumvr.api.input.body.AtumVRBodyJoint;
import me.phoenixra.atumvr.api.misc.pose.AtumVRPose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Composition of VRBodyView sources
 */
public class XRBody implements AtumVRBodyView {

    private final List<AtumVRBodyView> sources = new ArrayList<>();
    private final List<AtumVRBodyView> sourcesView = Collections.unmodifiableList(sources);

    private final EnumMap<AtumVRBodyJoint, AtumVRPose> joints =
            new EnumMap<>(AtumVRBodyJoint.class);
    private final Set<AtumVRBodyJoint> trackedJoints =
            Collections.unmodifiableSet(joints.keySet());

    private final BiConsumer<AtumVRBodyJoint, AtumVRPose> sink = joints::putIfAbsent;

    @Override
    public @Nullable AtumVRPose getJointPose(@NotNull AtumVRBodyJoint joint) {
        return joints.get(joint);
    }

    @Override
    public boolean isJointTracked(@NotNull AtumVRBodyJoint joint) {
        return joints.containsKey(joint);
    }

    @Override
    public @NotNull Set<AtumVRBodyJoint> getTrackedJoints() {
        return trackedJoints;
    }


    public void update() {
        joints.clear();
        for (AtumVRBodyView source : sources) {
            source.collectJoints(sink);
        }
    }


    public void addSource(@NotNull AtumVRBodyView source) {
        if (source == AtumVRBodyView.EMPTY || sources.contains(source)) {
            return;
        }
        sources.add(source);
    }

    public void removeSource(@NotNull AtumVRBodyView source) {
        if (sources.remove(source)) {
            joints.clear();
        }
    }

    public void clearSources() {
        sources.clear();
        joints.clear();
    }


    public @NotNull List<AtumVRBodyView> getSources() {
        return sourcesView;
    }
}
