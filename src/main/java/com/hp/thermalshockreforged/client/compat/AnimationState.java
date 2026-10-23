package com.hp.thermalshockreforged.client.compat;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.AnimationTest;

public final class AnimationState<T extends GeoAnimatable> {
    private final AnimationTest<T> delegate;
    public AnimationState(AnimationTest<T> delegate) { this.delegate = delegate; }
    public <D> D getData(com.geckolib.constant.dataticket.DataTicket<D> ticket) { return delegate.getData(ticket); }
}
