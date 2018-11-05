package org.mousephenotype.cda.loads.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMapAllowNull<K,V> extends ConcurrentHashMap<K,V> implements ConcurrentMap<K,V>, Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Boolean caseInsensitive = Boolean.FALSE;

    public final static Boolean CASE_INSENSITIVE_KEYS = Boolean.TRUE;
    public final static Boolean CASE_SENSITIVE = Boolean.FALSE;


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
            // Treat the keys to this map by uppercassing the keys
            // Verify that the uppercased key is in the map and return if so
            if (caseInsensitive && key instanceof String && super.get(key.toString().toUpperCase()) != null) {
                return super.get(key.toString().toUpperCase());
            }
            return super.get(key);
        }
    }

    /*
     * If the key or the value is null, don't add it to the [ConcurrentHash]Map.
     * see: https://duckduckgo.com/?q=concurrenthashmap+null+values&atb=v68-2__&ia=qa
     *
     * Remap calls to this method to putIfAbsent() to handle concurrency.
     */
    @Override
    public V put(K key, V value) {
        if ((key == null) || (value == null)) {
            logger.warn("NULL key or value key = " + key + ". value = " + value);
            System.out.println(StringUtils.join(Thread.currentThread().getStackTrace(), "\n"));
            return null;
        }

        // If this map is supposed to be case insensitive, uppercase the key before putting it
        // in the map
        if (caseInsensitive && key instanceof String) {
            return super.put(((K) key.toString().toUpperCase()), value);
        } else {
            return super.put(key, value);
        }
    }



    /*
        Override all constructors for the super class to enable the same instantiation behaviour
     */

    public ConcurrentHashMapAllowNull() {
        super();
    }

    /**
     * Set the map to case sensitive (default) or insensitive key lookups
     *
     * @param caseInsensitive TRUE (Default) case-sensitive, FALSE case insensitive
     */
    public ConcurrentHashMapAllowNull(Boolean caseInsensitive) {
        super();
        this.caseInsensitive = caseInsensitive;
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


    public Boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }



}
