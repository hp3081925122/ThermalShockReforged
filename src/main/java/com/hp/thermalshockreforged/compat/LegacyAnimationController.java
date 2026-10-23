package com.hp.thermalshockreforged.compat;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DataTickets;

public class LegacyAnimationController<T extends GeoAnimatable> extends com.geckolib.animation.AnimationController<T> {
    @FunctionalInterface
    public interface LegacyAnimationHandler<A extends GeoAnimatable> {
        PlayState handle(LegacyAnimationState<A> state);
    }

    public enum State { STOPPED, RUNNING }

    public LegacyAnimationController(T animatable, String name, int transitionTicks, LegacyAnimationHandler<T> handler) {
        super(name, transitionTicks, state -> handler.handle(new LegacyAnimationState<>(state)));
    }

    public LegacyAnimationController(String name, int transitionTicks, LegacyAnimationHandler<T> handler) {
        super(name, transitionTicks, state -> handler.handle(new LegacyAnimationState<>(state)));
    }

    public State getAnimationState() { return hasAnimationFinished() ? State.STOPPED : State.RUNNING; }
    public void forceAnimationReset() { reset(); }

    public static final class LegacyAnimationState<A extends GeoAnimatable> {
        private final AnimationTest<A> delegate;
        private LegacyAnimationState(AnimationTest<A> delegate) { this.delegate = delegate; }
        public <D> D getData(com.geckolib.constant.dataticket.DataTicket<D> ticket) { return delegate.getData(ticket); }
        public PlayState setAndContinue(RawAnimation animation) { return delegate.setAndContinue(animation); }
        public void setAnimation(RawAnimation animation) { delegate.setAnimation(animation); }
        public boolean isMoving() { return delegate.isMoving(); }
        public float getLimbSwingAmount() { return 0.0F; }
        public float getPartialTick() { return delegate.getDataOrDefault(DataTickets.PARTIAL_TICK, 0.0F); }
        public boolean isCurrentAnimation(RawAnimation animation) { return delegate.isCurrentAnimation(animation); }
        public LegacyAnimationController<A> getController() { return (LegacyAnimationController<A>) delegate.controller(); }
    }
}
