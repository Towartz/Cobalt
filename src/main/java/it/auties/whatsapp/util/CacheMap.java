package it.auties.whatsapp.util;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.delayedExecutor;

@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {
    public static final int DEFAULT_TIMEOUT = 300;

    Executor executor;

    public CacheMap(){
        this(DEFAULT_TIMEOUT);
    }

    public CacheMap(long timeout){
        this.executor = delayedExecutor(timeout, TimeUnit.SECONDS);
    }

    @Override
    public V put(K key, V value) {
        var result = super.put(key, value);
        scheduleRemoval(value);
        return result;
    }

    public V putAndGetValue(K key, V value){
        put(key, value);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        map.forEach((ignored, value) -> scheduleRemoval(value));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        var result = super.putIfAbsent(key, value);
        scheduleRemoval(value);
        return result;
    }

    public Optional<V> getOptional(K key) {
        return Optional.ofNullable(super.get(key));
    }

    public void scheduleRemoval(V key){
        executor.execute(() -> remove(key));
    }
}