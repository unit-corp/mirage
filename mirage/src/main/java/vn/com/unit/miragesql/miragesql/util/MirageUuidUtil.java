package vn.com.unit.miragesql.miragesql.util;

import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/**
 * UUID utilities.
 * <p>
 * Entities whose primary key is annotated with
 * {@link vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType#UUID_V7} get their id on insert; use
 * {@link #nextV7()} only when the id is needed before the entity is saved.
 */
public final class MirageUuidUtil {

    private static final TimeBasedEpochGenerator V7_GENERATOR = Generators.timeBasedEpochGenerator();

    private MirageUuidUtil() {
    }

    /**
     * Generates a time-ordered UUID (version 7, RFC 9562) backed by java-uuid-generator.
     * <p>
     * Values are monotonically increasing, including within the same millisecond, and the method is thread-safe.
     *
     * @return a new UUID v7
     */
    public static UUID nextV7() {
        return V7_GENERATOR.generate();
    }
}
