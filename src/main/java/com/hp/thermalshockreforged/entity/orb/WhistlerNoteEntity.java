package com.hp.thermalshockreforged.entity.orb;

import com.hp.thermalshockreforged.content.MaterialContent;
import com.hp.thermalshockreforged.entity.fauna.FaunaEntities;
import com.hp.thermalshockreforged.entity.projectile.LegacyArrowEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class WhistlerNoteEntity extends LegacyArrowEntity {

    public WhistlerNoteEntity(EntityType<? extends WhistlerNoteEntity> type, Level level) { super(type, level); setImpactKnockback(5); }
    public WhistlerNoteEntity(LivingEntity owner, Level level) { super(OrbContent.WHISTLER_NOTE.get(), owner, level, new ItemStack(MaterialContent.items().get("note_projectile_item").get())); setImpactKnockback(5); }
    @Override protected ItemStack projectileItem() { return new ItemStack(MaterialContent.items().get("note_projectile_item").get()); }
    @Override protected boolean fliesWithoutGravity() { return true; }
    @Override protected int lifetimeTicks() { return 40; }
    @Override public void tick() {
        super.tick();
        if (level().isClientSide) level().addParticle(FaunaEntities.WHITE_ORB.get(), getX(), getY(), getZ(), 0, 0, 0);
    }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random) { return shoot(level, owner, random, .1F, 5, 5); }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random, float pullingPower) { return shoot(level, owner, random, pullingPower * .1F, 5, 5); }
    public static WhistlerNoteEntity shoot(Level level, LivingEntity owner, RandomSource random, float power, double damage, int knockback) {
        var note = new WhistlerNoteEntity(owner, level);
        Vec3 view = owner.getViewVector(1);
        note.shoot(view.x, view.y, view.z, power * 2, 0);
        note.setSilent(true);
        note.setCritArrow(false);
        note.setBaseDamage(damage);
        note.setImpactKnockback(knockback);
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
        note.setImpactKnockback(5);
        note.setCritArrow(false);
        owner.level().addFreshEntity(note);
        return note;
    }
}
