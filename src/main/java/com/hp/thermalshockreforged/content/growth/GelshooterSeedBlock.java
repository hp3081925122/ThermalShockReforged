package com.hp.thermalshockreforged.content.growth;

import com.hp.thermalshockreforged.entity.growth.GrowthEntities;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class GelshooterSeedBlock extends Block {
    private static final Map<ServerLevel,Set<BlockPos>> ACTIVE=new WeakHashMap<>();
    public GelshooterSeedBlock() { super(Properties.of().mapColor(MapColor.COLOR_MAGENTA).sound(SoundType.GLASS).strength(1,10)); }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos) { return 15; }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving) {
        super.onPlace(state,level,pos,previous,moving);
        if(previous.is(this)||!(level instanceof ServerLevel server)||!level.getBlockState(pos).is(this)) return;
        BlockPos key=pos.immutable();
        synchronized(ACTIVE) { if(!ACTIVE.computeIfAbsent(server,s->new HashSet<>()).add(key))return; }
        boolean reserved=false;
        boolean success=false;
        net.minecraft.world.entity.Entity spawned=null;
        try {
            reserved=server.removeBlock(pos,false);
            if(!reserved)return;
            spawned=GrowthEntities.GELSHOOTER.get().create(server,null,pos,MobSpawnType.MOB_SUMMONED,false,false);
            if(spawned!=null)server.tryAddFreshEntityWithPassengers(spawned);
            success=spawned!=null&&spawned.isAddedToLevel()&&!spawned.isRemoved();
            if(!success)return;
            server.sendParticles(ParticleTypes.POOF,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,20,.5,.5,.5,0);
            server.playSound(null,pos,SoundEvents.GRASS_PLACE,SoundSource.BLOCKS,1,2);
        } finally {
            try {
                if(spawned!=null&&spawned.isAddedToLevel()&&!spawned.isRemoved())success=true;
                if(reserved&&!success) {
                    if(server.getBlockState(pos).isAir())server.setBlock(pos,state,3);
                    else popResource(server,pos,new ItemStack(this));
                }
            } finally {
                synchronized(ACTIVE) { var positions=ACTIVE.get(server); if(positions!=null) { positions.remove(key); if(positions.isEmpty())ACTIVE.remove(server); } }
            }
        }
    }
}
