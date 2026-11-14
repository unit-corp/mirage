package vn.com.unit.miragesql.miragesql.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.type.UUIDValueType;

public class MirageUuidUtilTest extends TestCase {

    public void testVersionAndVariant() {
        UUID uuid = MirageUuidUtil.nextV7();
        assertEquals(7, uuid.version());
        assertEquals(2, uuid.variant());
    }

    public void testTimestampPrefix() {
        long before = System.currentTimeMillis();
        UUID uuid = MirageUuidUtil.nextV7();
        long after = System.currentTimeMillis();
        long millis = uuid.getMostSignificantBits() >>> 16;
        assertTrue(millis >= before && millis <= after);
    }

    public void testMonotonicallyIncreasingAsBytes() {
        byte[] prev = UUIDValueType.toBytes(MirageUuidUtil.nextV7());
        for (int i = 0; i < 200_000; i++) {
            byte[] cur = UUIDValueType.toBytes(MirageUuidUtil.nextV7());
            assertTrue("not increasing at " + i, compareUnsigned(prev, cur) < 0);
            prev = cur;
        }
    }

    public void testUniqueAcrossThreads() throws Exception {
        final int threads = 8;
        final int perThread = 50_000;
        final Set<UUID> all = Collections.newSetFromMap(new ConcurrentHashMap<>());
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    start.await();
                    Set<UUID> local = new HashSet<>();
                    for (int i = 0; i < perThread; i++) {
                        local.add(MirageUuidUtil.nextV7());
                    }
                    all.addAll(local);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        done.await();
        pool.shutdown();
        assertEquals(threads * perThread, all.size());
    }

    private static int compareUnsigned(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            int c = Integer.compare(a[i] & 0xff, b[i] & 0xff);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }
}
