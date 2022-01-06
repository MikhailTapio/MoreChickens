package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Quaternion;

import javax.annotation.Nonnull;

public class BaitRenderer extends TileEntityRenderer<BaitTileEntity> {
    public BaitRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BaitTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        final BaitType baitType = tileEntity.getBaitType();
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        matrixStack.pushPose();
        matrixStack.translate(0.45, 0.05f, 0.45);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(new Quaternion(90f, 0f, 0f, true));
        if (!baitType.getDisplayItemFirst().isEmpty()) {
            itemRenderer.renderStatic(baitType.getDisplayItemFirst(), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn);
        }
        matrixStack.translate(0.1f, 0f, -0.05f);
        matrixStack.mulPose(new Quaternion(5f, 0f, 0f, true));
        if (!baitType.getDisplayItemSecond().isEmpty()) {
            itemRenderer.renderStatic(baitType.getDisplayItemSecond(), ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn);
        }
        matrixStack.popPose();
    }
}
