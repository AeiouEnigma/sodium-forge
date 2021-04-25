package me.jellysquid.mods.phosphor.common.chunk.light;

import net.minecraft.world.chunk.IChunk;

import java.util.concurrent.CompletableFuture;

public interface ServerLightingProviderAccess {
    CompletableFuture<IChunk> setupLightmaps(IChunk chunk);
}
