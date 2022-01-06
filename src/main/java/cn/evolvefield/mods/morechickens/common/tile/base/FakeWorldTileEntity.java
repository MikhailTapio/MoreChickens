package cn.evolvefield.mods.morechickens.common.tile.base;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FakeWorldTileEntity extends SyncableTileEntity {
    private boolean fakeWorld;
    private final BlockState defaultState;

    public FakeWorldTileEntity(TileEntityType<?> tileEntityType, BlockState defaultState) {
        super(tileEntityType);
        this.defaultState = defaultState;
    }

    public void setFakeWorld(World w) {
        level = w;
        fakeWorld = true;
    }

    public boolean isRealWorld() {
        return !fakeWorld;
    }

    @Nonnull
    @Override
    public BlockState getBlockState() {
        if (fakeWorld) {
            return defaultState;
        }
        return super.getBlockState();
    }
}
