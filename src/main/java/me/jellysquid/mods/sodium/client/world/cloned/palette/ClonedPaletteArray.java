package me.jellysquid.mods.sodium.client.world.cloned.palette;

public class ClonedPaletteArray<K> implements ClonedPalette<K> {
    private final K[] array;
    private final K defaultValue;

    public ClonedPaletteArray(K[] array, K defaultValue) {
        this.array = array;
        this.defaultValue = defaultValue;
    }

    @Override
    public K get(int id) {
        K value = this.array[id];

        if (value == null) {
            return this.defaultValue;
        }

        return value;
    }
}
