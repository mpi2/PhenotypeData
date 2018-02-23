package org.mousephenotype.cda.loads.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMapAllowNull<K,V> extends ConcurrentHashMap<K,V> implements ConcurrentMap<K,V>, Serializable {



    /**
     * Change the default behavior of ConcurrentHashMap so that performing a get with
     * a null key returns null.  i.e., mimic the behaviour of HashMap
     *
     * see: https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html#get-java.lang.Object-
     *
     * @param key possible key
     * @return null when the key is null; else return the result from superclass
     */
    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        } else {
            return super.get(key);
        }
    }


    /*
        Override all constructors for the super class to enable the same instantiation behaviour
     */

    public ConcurrentHashMapAllowNull() {
        super();
    }

    public ConcurrentHashMapAllowNull(int initialCapacity) {
        super(initialCapacity);
    }

    public ConcurrentHashMapAllowNull(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public ConcurrentHashMapAllowNull(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ConcurrentHashMapAllowNull(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }


}
