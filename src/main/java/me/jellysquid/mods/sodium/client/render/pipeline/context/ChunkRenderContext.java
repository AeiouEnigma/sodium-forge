package me.jellysquid.mods.sodium.client.render.pipeline.context;

import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.cache.ArrayLightDataCache;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.render.pipeline.RenderContextCommon;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

public class ChunkRenderContext {
    private final ArrayLightDataCache lightDataCache;

    private final BlockRenderer blockRenderer;
    private final FluidRenderer fluidRenderer;

    private final BlockModelShapes models;

    public ChunkRenderContext(Minecraft client) {
        this.lightDataCache = new ArrayLightDataCache();

        LightPipelineProvider lightPipelineProvider = new LightPipelineProvider(this.lightDataCache);
        BiomeColorBlender biomeColorBlender = RenderContextCommon.createBiomeColorBlender();

        this.blockRenderer = new BlockRenderer(client, lightPipelineProvider, biomeColorBlender);
        this.fluidRenderer = new FluidRenderer(client, lightPipelineProvider, biomeColorBlender);

        this.models = client.getModelManager().getBlockModelShapes();
    }

    public boolean renderBlock(IBlockDisplayReader world, BlockState state, BlockPos pos, ChunkModelBuffers buffers, boolean cull, IModelData modelData) {
        IBakedModel model = this.models.getModel(state);
        long seed = state.getPositionRandom(pos);

        return this.blockRenderer.renderModel(world, state, pos, model, buffers, cull, seed, modelData);
    }

    public boolean renderFluid(IBlockDisplayReader world, FluidState fluidState, BlockPos.Mutable pos, ChunkModelBuffers buffers) {
        return this.fluidRenderer.render(world, fluidState, pos, buffers);
    }

    public void init(IBlockDisplayReader world, SectionPos pos) {
        this.lightDataCache.init(world, pos);
    }
}
