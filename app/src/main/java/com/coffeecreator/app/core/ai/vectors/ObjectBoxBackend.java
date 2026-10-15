package com.coffeecreator.app.core.ai.vectors;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.ObjectWithScore;
import io.objectbox.query.Query;

/** Java annotation processing keeps Room/Hilt on KSP; no second Kotlin processor. */
public final class ObjectBoxBackend implements AutoCloseable {
    private final BoxStore store;
    private final Box<ChunkVector> box;

    public ObjectBoxBackend(Context context, String name) {
        store = MyObjectBox.builder().androidContext(context.getApplicationContext()).name(name).maxSizeInKByte(65536).build();
        box = store.boxFor(ChunkVector.class);
    }

    public synchronized float[] get(String key) {
        try (Query<ChunkVector> query = box.query(ChunkVector_.key.equal(key)).build()) {
            ChunkVector entry = query.findFirst();
            return entry == null ? null : entry.vector;
        }
    }

    public synchronized void putAll(Map<String, float[]> entries) {
        store.runInTx(() -> {
            List<ChunkVector> batch = new ArrayList<>();
            for (Map.Entry<String, float[]> entry : entries.entrySet()) {
                validate(entry.getValue());
                try (Query<ChunkVector> query = box.query(ChunkVector_.key.equal(entry.getKey())).build()) {
                    ChunkVector existing = query.findFirst();
                    ChunkVector value = new ChunkVector(entry.getKey(), entry.getValue());
                    if (existing != null) value.id = existing.id;
                    batch.add(value);
                }
            }
            box.put(batch);
        });
    }

    public synchronized Map<String, Double> search(float[] vector, int limit) {
        validate(vector);
        Map<String, Double> result = new LinkedHashMap<>();
        try (Query<ChunkVector> query = box.query().nearestNeighbors(ChunkVector_.vector, vector, limit).build()) {
            for (ObjectWithScore<ChunkVector> hit : query.findWithScores()) {
                result.put(hit.get().key, 1.0 - hit.getScore());
            }
        }
        return result;
    }

    public synchronized void retain(Set<String> keys) {
        store.runInTx(() -> {
            for (ChunkVector entry : box.getAll()) {
                if (!keys.contains(entry.key)) box.remove(entry.id);
            }
        });
    }

    public synchronized void clear() { box.removeAll(); }
    @Override public synchronized void close() { store.close(); }

    private static void validate(float[] vector) {
        if (vector.length != 384) throw new IllegalArgumentException("Expected MiniLM's 384 dimensions.");
        for (float value : vector) {
            if (!Float.isFinite(value)) throw new IllegalArgumentException("Nonfinite vector.");
        }
    }
}
