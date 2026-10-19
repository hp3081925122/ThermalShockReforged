package com.hp.thermalshockreforged.entity.orb;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class WhistlerNoteEntity extends AbstractArrow implements ItemSupplier {
    private int remainingTicks = 40;
    private double impactKnockback = 5;

    public WhistlerNoteEntity(EntityType<? extends WhistlerNoteEntity> type, Level level) { super(type, level); }
    public WhistlerNoteEntity(LivingEntity owner, Level level) { super(OrbContent.WHISTLER_NOTE.get(), owner, level, new ItemStack(MaterialContent.items().get("note_projectile_item").get()), null); }
    @Override public ItemStack getItem() { return new ItemStack(MaterialContent.items().get("note_projectile_item").get()); }
    @Override protected ItemStack getDefaultPickupItem() { return getItem(); }
    @Override protected void doPostHurtEffects(LivingEntity entity) { super.doPostHurtEffects(entity); entity.setArrowCount(entity.getArrowCount() - 1); }
    @Override protected void doKnockback(LivingEntity target, DamageSource source) {
        Vec3 direction = getDeltaMovement().multiply(1, 0, 1).normalize();
        double resistance = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        if (direction.lengthSqr() > 0 && resistance > 0) {
            target.knockback(impactKnockback * .6 * resistance, -direction.x, -direction.z);
            target.hurtMarked = true;
        }
    }
    @Override public void tick() {
        super.tick();
        setNoGravity(true);
        if (level().isClientSide) level().addParticle(FaunaEntities.WHITE_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
        if (inGround || !level().isClientSide && --remainingTicks <= 0) discard();
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("WhistlerRemainingTicks", remainingTicks); tag.putDouble("WhistlerKnockback", impactKnockback); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); remainingTicks = Math.max(1, Math.min(40, tag.getInt("WhistlerRemainingTicks"))); impactKnockback = Double.isFinite(tag.getDouble("WhistlerKnockback")) ? Math.max(0, Math.min(20, tag.getDouble("WhistlerKnockback"))) : 5; }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .1F, 5, 5); }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .1F, 5, 5); }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var note = new WhistlerNoteEntity(owner, level);
        Vec3 view = owner.getViewVector(1);
        note.shoot(view.x, view.y, view.z, power * 2, 0);
        note.setSilent(true);
        note.setCritArrow(false);
        note.setBaseDamage(damage);
        note.impactKnockback = Math.max(0, knockback);
        level.addFreshEntity(note);
        return note;
    }
    public static WhistlerNoteEntity shoot(LivingEntity owner, LivingEntity target) {
        var note = new WhistlerNoteEntity(owner, owner.level());
        double dx = target.getX() - owner.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - owner.getZ();
        note.shoot(dx, dy - note.getY() + Math.hypot(dx, dz) * .2F, dz, .2F, 12);
        note.setSilent(true);
        note.setBaseDamage(5);
        note.impactKnockback = 5;
        note.setCritArrow(false);
        owner.level().addFreshEntity(note);
        return note;
    }
}
