package me.jellysquid.mods.sodium.client.render.chunk.tasks;

import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkMeshData;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Objects;

/**
 * Rebuilds all the meshes of a chunk for each given render pass with non-occluded blocks. The result is then uploaded
 * to graphics memory on the main thread.
 *
 * This task takes a slice of the world from the thread it is created on. Since these slices require rather large
 * array allocations, they are pooled to ensure that the garbage collector doesn't become overloaded.
 */
public class ChunkRenderRebuildTask<T extends ChunkGraphicsState> extends ChunkRenderBuildTask<T> {
    private final ChunkRenderContainer<T> render;
    private final ChunkBuilder<T> chunkBuilder;
    private final Vector3d camera;
    private final WorldSlice slice;
    private final BlockPos offset;

    public ChunkRenderRebuildTask(ChunkBuilder<T> chunkBuilder, ChunkRenderContainer<T> render, WorldSlice slice, BlockPos offset) {
        this.chunkBuilder = chunkBuilder;
        this.render = render;
        this.camera = chunkBuilder.getCameraPosition();
        this.slice = slice;
        this.offset = offset;
    }

    @Override
    public ChunkBuildResult<T> performBuild(ChunkRenderContext pipeline, ChunkBuildBuffers buffers, CancellationSource cancellationSource) {
        ChunkRenderData.Builder renderData = new ChunkRenderData.Builder();
        VisGraph occluder = new VisGraph();
        ChunkRenderBounds.Builder bounds = new ChunkRenderBounds.Builder();

        buffers.init();
        pipeline.init(this.slice, this.slice.getOrigin());

        int baseX = this.render.getOriginX();
        int baseY = this.render.getOriginY();
        int baseZ = this.render.getOriginZ();

        BlockPos.Mutable pos = new BlockPos.Mutable();
        BlockPos offset = this.offset;

        for (int relY = 0; relY < 16; relY++) {
            if (cancellationSource.isCancelled()) {
                return null;
            }

            for (int relZ = 0; relZ < 16; relZ++) {
                for (int relX = 0; relX < 16; relX++) {
                    BlockState blockState = this.slice.getOriginBlockState(relX, relY, relZ);

                    if (blockState.isAir()) {
                        continue;
                    }

                    Block block = blockState.getBlock();

                    int x = baseX + relX;
                    int y = baseY + relY;
                    int z = baseZ + relZ;

                    if (block.getRenderType(blockState) == BlockRenderType.MODEL) {
                        buffers.setRenderOffset(x - offset.getX(), y - offset.getY(), z - offset.getZ());
                        for (RenderType layer : RenderType.getBlockRenderTypes()) {
                            if (!RenderTypeLookup.canRenderInLayer(blockState, layer)) {
                                continue;
                            }

                            ForgeHooksClient.setRenderLayer(layer);

                            IModelData modelData = ModelDataManager.getModelData(Objects.requireNonNull(Minecraft.getInstance().world), pos);
                            if (modelData == null) {
                                modelData = EmptyModelData.INSTANCE;
                            }
                            if (pipeline.renderBlock(this.slice, blockState, pos.setPos(x, y, z), buffers.get(layer), true, modelData)) {
                                bounds.addBlock(relX, relY, relZ);
                            }
                            ForgeHooksClient.setRenderLayer(null);
                        }
                  }

                    FluidState fluidState = block.getFluidState(blockState);

                    if (!fluidState.isEmpty()) {
                        buffers.setRenderOffset(x - offset.getX(), y - offset.getY(), z - offset.getZ());

                        RenderType layer = RenderTypeLookup.getRenderType(fluidState);

                        if (pipeline.renderFluid(this.slice, fluidState, pos.setPos(x, y, z), buffers.get(layer))) {
                            bounds.addBlock(relX, relY, relZ);
                        }
                    }

                    if (blockState.hasTileEntity()) {
                        TileEntity entity = this.slice.getBlockEntity(pos.setPos(x, y, z), Chunk.CreateEntityType.CHECK);

                        if (entity != null) {
                            TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(entity);

                            if (renderer != null) {
                                renderData.addBlockEntity(entity, !renderer.isGlobalRenderer(entity));

                                bounds.addBlock(relX, relY, relZ);
                            }
                        }
                    }

                    if (blockState.isOpaqueCube(this.slice, pos)) {
                        occluder.setOpaqueCube(pos);
                    }
                }
            }
        }

        for (BlockRenderPass pass : BlockRenderPass.VALUES) {
            ChunkMeshData mesh = buffers.createMesh(pass);

            if (mesh != null) {
                renderData.setMesh(pass, mesh);
            }
        }

        renderData.setOcclusionData(occluder.computeVisibility());
        renderData.setBounds(bounds.build(this.render.getChunkPos()));

        return new ChunkBuildResult<>(this.render, renderData.build());
    }

    @Override
    public void releaseResources() {
        this.chunkBuilder.releaseWorldSlice(this.slice);
    }
}
