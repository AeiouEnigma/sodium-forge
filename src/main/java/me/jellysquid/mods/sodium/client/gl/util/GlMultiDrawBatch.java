package me.jellysquid.mods.sodium.client.gl.util;

import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Provides a fixed-size queue for batching draw calls for vertex data in the same buffer. This internally
 * uses {@link GL14#glMultiDrawArrays(int, IntBuffer, IntBuffer)} and should be compatible on any relevant platform.
 */
public class GlMultiDrawBatch {
    private final IntBuffer bufIndices;
    private final IntBuffer bufLen;
    private int count;
    private boolean isBuilding;

    public GlMultiDrawBatch(int capacity) {
        this.bufIndices = MemoryUtil.memAllocInt(capacity);
        this.bufLen = MemoryUtil.memAllocInt(capacity);
    }

    public IntBuffer getIndicesBuffer() {
        return this.bufIndices;
    }

    public IntBuffer getLengthBuffer() {
        return this.bufLen;
    }

    public void begin() {
        ((Buffer)this.bufIndices).clear();
        ((Buffer)this.bufLen).clear();
        this.count = 0;

        this.isBuilding = true;
    }

    public void end() {
        ((Buffer)this.bufIndices).limit(this.count);
        ((Buffer)this.bufLen).limit(this.count);

        this.isBuilding = false;
    }

    public boolean isEmpty() {
        return this.count <= 0;
    }

    public void addChunkRender(int first, int count) {
        int i = this.count++;
        this.bufIndices.put(i, first);
        this.bufLen.put(i, count);
    }

    public boolean isBuilding() {
        return this.isBuilding;
    }

    public void delete() {
        MemoryUtil.memFree(this.bufIndices);
        MemoryUtil.memFree(this.bufLen);
    }
}
