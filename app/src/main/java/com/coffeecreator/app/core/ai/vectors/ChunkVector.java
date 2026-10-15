package com.coffeecreator.app.core.ai.vectors;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.HnswIndex;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.annotation.VectorDistanceType;

/** Derived cache: key contains the embedding revision, chunk ID and text hash. */
@Entity
public class ChunkVector {
    @Id public long id;
    @Unique public String key;
    @HnswIndex(dimensions = 384, distanceType = VectorDistanceType.COSINE, vectorCacheHintSizeKB = 8192)
    public float[] vector;

    public ChunkVector() {}

    public ChunkVector(String key, float[] vector) {
        this.key = key;
        this.vector = vector;
    }
}
