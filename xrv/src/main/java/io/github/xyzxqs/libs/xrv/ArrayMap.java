package io.github.xyzxqs.libs.xrv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

class ArrayMap<K, V> {
    private final ArrayList<K> kArrayList;
    private final SparseArray<V> vSparseArray;

    public ArrayMap() {
        this(new ArrayList<K>(), new SparseArray<V>());
    }

    public ArrayMap(@NonNull ArrayList<K> kArrayList, @NonNull SparseArray<V> vSparseArray) {
        this.kArrayList = kArrayList;
        this.vSparseArray = vSparseArray;
    }

    public int indexOfKey(@NonNull K key) {
        return kArrayList.indexOf(key);
    }

    @Nullable
    public V getValueAt(int index) {
        return vSparseArray.get(index);
    }

    @Nullable
    public V getValue(@NonNull K k) {
        int index = kArrayList.indexOf(k);
        if (index < 0) {
            return null;
        } else {
            return vSparseArray.get(index);
        }
    }

    public void put(@NonNull K k, @NonNull V v) {
        int i = kArrayList.indexOf(k);
        if (i < 0) {
            kArrayList.add(k);
            vSparseArray.append(kArrayList.size() - 1, v);
        } else {
            kArrayList.add(i, k);
            vSparseArray.put(i, v);
        }
    }

    public boolean containsKey(@NonNull K k) {
        return kArrayList.contains(k);
    }

    public int size() {
        return kArrayList.size();
    }
}
