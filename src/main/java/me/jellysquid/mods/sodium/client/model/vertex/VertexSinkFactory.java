package me.jellysquid.mods.sodium.client.model.vertex;

import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.client.model.vertex.transformers.VertexTransformer;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public interface VertexSinkFactory<T extends VertexSink> {
    T createBufferWriter(VertexBufferView buffer, boolean direct);

    T createFallbackWriter(IVertexBuilder consumer);

    T createTransformingSink(T sink, VertexTransformer transformer);
}
