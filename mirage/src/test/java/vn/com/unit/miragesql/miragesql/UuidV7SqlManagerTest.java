package vn.com.unit.miragesql.miragesql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;
import vn.com.unit.miragesql.miragesql.dialect.HyperSQLDialect;
import vn.com.unit.miragesql.miragesql.dialect.UuidStorage;

public class UuidV7SqlManagerTest extends AbstractDatabaseTest {

    @Table(name = "UUID_ITEM")
    public static class Item {
        @PrimaryKey(generationType = GenerationType.UUID_V7)
        public UUID id;
        public String name;
    }

    @Table(name = "UUID_ITEM")
    public static class ManualItem {
        @PrimaryKey(generationType = GenerationType.APPLICATION)
        public UUID id;
        public String name;
    }

    @Table(name = "UUID_ITEM")
    public static class WrongTypeItem {
        @PrimaryKey(generationType = GenerationType.UUID_V7)
        public String id;
        public String name;
    }

    private void useStorage(UuidStorage storage, String idColumnType) throws Exception {
        sqlManager.setDialect(new HyperSQLDialect() {
            @Override
            public UuidStorage getUuidStorage() {
                return storage;
            }
        });
        sqlManager.executeUpdate(new StringSqlResource("DROP TABLE UUID_ITEM IF EXISTS"));
        sqlManager.executeUpdate(new StringSqlResource(
                "CREATE TABLE UUID_ITEM (ID " + idColumnType + " PRIMARY KEY, NAME VARCHAR(50))"));
    }

    @Override
    protected void tearDown() throws Exception {
        sqlManager.executeUpdate(new StringSqlResource("DROP TABLE UUID_ITEM IF EXISTS"));
        super.tearDown();
    }

    private void assertInsertGeneratesOrderedIds() throws Exception {
        List<UUID> inserted = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Item item = new Item();
            item.name = "n" + i;
            sqlManager.insertEntity(item);
            assertNotNull(item.id);
            assertEquals(7, item.id.version());
            inserted.add(item.id);
        }

        List<Item> items = sqlManager.getResultList(Item.class,
                new StringSqlResource("SELECT * FROM UUID_ITEM ORDER BY ID"));
        assertEquals(50, items.size());
        for (int i = 0; i < 50; i++) {
            assertEquals(inserted.get(i), items.get(i).id);
            assertEquals("n" + i, items.get(i).name);
        }
    }

    public void testInsertNative() throws Exception {
        useStorage(UuidStorage.NATIVE, "UUID");
        assertInsertGeneratesOrderedIds();
    }

    public void testInsertBinary() throws Exception {
        useStorage(UuidStorage.BINARY, "BINARY(16)");
        assertInsertGeneratesOrderedIds();
    }

    public void testInsertString() throws Exception {
        useStorage(UuidStorage.STRING, "CHAR(36)");
        assertInsertGeneratesOrderedIds();
    }

    public void testExistingIdIsKept() throws Exception {
        useStorage(UuidStorage.BINARY, "BINARY(16)");
        UUID id = UUID.fromString("018f3b9e-7a10-7000-8000-0123456789ab");
        Item item = new Item();
        item.id = id;
        item.name = "keep";
        sqlManager.insertEntity(item);
        assertEquals(id, item.id);

        Item loaded = sqlManager.findEntity(Item.class, id);
        assertNotNull(loaded);
        assertEquals("keep", loaded.name);
    }

    public void testApplicationGenerationDoesNotGenerate() throws Exception {
        useStorage(UuidStorage.NATIVE, "UUID");
        ManualItem item = new ManualItem();
        item.name = "x";
        try {
            sqlManager.insertEntity(item);
            fail("id must not be generated for APPLICATION");
        } catch (RuntimeException expected) {
            // NULL primary key
        }
    }

    public void testUuidV7RequiresUuidProperty() throws Exception {
        useStorage(UuidStorage.STRING, "CHAR(36)");
        try {
            sqlManager.insertEntity(new WrongTypeItem());
            fail();
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    public void testUpdateAndDelete() throws Exception {
        useStorage(UuidStorage.BINARY, "BINARY(16)");
        Item item = new Item();
        item.name = "before";
        sqlManager.insertEntity(item);

        item.name = "after";
        assertEquals(1, sqlManager.updateEntity(item));
        assertEquals("after", sqlManager.findEntity(Item.class, item.id).name);

        assertEquals(1, sqlManager.deleteEntity(item));
        assertNull(sqlManager.findEntity(Item.class, item.id));
    }

    public void testInsertBatch() throws Exception {
        useStorage(UuidStorage.BINARY, "BINARY(16)");
        Item a = new Item();
        a.name = "a";
        Item b = new Item();
        b.name = "b";
        sqlManager.insertBatch(a, b);
        assertNotNull(a.id);
        assertNotNull(b.id);
        assertFalse(a.id.equals(b.id));
    }
}
