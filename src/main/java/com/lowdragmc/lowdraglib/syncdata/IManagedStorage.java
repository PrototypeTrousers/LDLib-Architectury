package com.lowdragmc.lowdraglib.syncdata;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedKey;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author KilaBash
 * @date 2023/2/17
 * @implNote IManagedStorage
 */
public interface IManagedStorage {

    IManaged[] getManaged();

    /**
     * get field ref from given managed key
     */
    IRef getFieldByKey(ManagedKey key);

    /**
     * get managed non lazy fields
     */
    IRef[] getNonLazyFields();

    /**
     * get managed sync dirty fields
     */
    boolean hasDirtySyncFields();

    /**
     * get managed persisted dirty fields
     */
    boolean hasDirtyPersistedFields();

    /**
     * get managed persisted fields
     */
    IRef[] getPersistedFields();

    /**
     * get managed sync fields
     */
    IRef[] getSyncFields();

    /**
     * add a listener to field update
     *
     * @param <T>      field type;
     * @param key      managed key
     * @param listener listener
     * @return callback that you can unsubscribe
     */
    <T> ISubscription addSyncUpdateListener(ManagedKey key, IFieldUpdateListener<T> listener);

    /**
     * remove all syncing listeners of given key
     */
    void removeAllSyncUpdateListener(ManagedKey key);

    /**
     * whether given key has listeners
     */
    boolean hasSyncListener(ManagedKey key);

    <T> void notifyFieldUpdate(ManagedKey key, T newVal, T oldVal);

    /**
     * Marks a field as changed, so it will be synced.
     *
     * @param key the key of the field
     */
    default void markDirty(ManagedKey key) {
        getFieldByKey(key).markAsDirty();
    }

    /**
     * Marks all field as changed, so they will be synced.
     */
    default void markAllDirty() {
        for (IRef syncField : getSyncFields()) {
            syncField.markAsDirty();
        }
    }

    /**
     * it should be called when class initialization finished but field haven't been changed yet.
     * you can call it in the {@link BlockEntity#clearRemoved()}.
     */
    void init();

}
