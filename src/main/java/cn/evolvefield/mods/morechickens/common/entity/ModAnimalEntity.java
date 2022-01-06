package cn.evolvefield.mods.morechickens.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

public abstract class ModAnimalEntity extends AnimalEntity {

    protected ModAnimalEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    protected abstract int getBreedingTimeout();

    @Override
    public void aiStep() {
        super.aiStep();
        final int timeout = getBreedingTimeout();
        if(getAge() > timeout)
            setAge(timeout);
        if(timeout > 6000){
            if(getAge() == 5999)
                setAge(timeout);
            else if(getAge() == 6000)
                setAge(5999);
        }
    }

}
