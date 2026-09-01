package com.example.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class KinectFreeCameraEntity extends Entity {

    public KinectFreeCameraEntity(World world) {
        super(world);
        this.noClip = true;
        this.ignoreFrustumCheck = true;
        this.setSize(0.1F, 0.1F);
    }

    @Override
    protected void entityInit() {
        // nada necessário
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        // nada a carregar
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        // nada a salvar
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}