package com.grubhub.mvi.model

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Kotlin objects are inherently singletons so we will store all needed state here to preserve it across
 * orientation changes and background/foreground operations. The state cache also allows us to save computation time
 * by accessing the last known state and obtaining those values which we are certain will not be changed by an operation
 * within the model.
 *
 * Created by matthew.ewers on 2019-05-17
 */
internal object StateCache {
    /**
     * The state cache is written to main memory and all access to it is synchronized to ensure
     * maximum thread safety
     */
    @Volatile
    private var cache = ConcurrentHashMap<KClass<*>, MviState>()

    /**
     * Stores any state for a registered view. States must implement the [MviState] interface
     *
     * @param state The state to store
     */
    fun <S : MviState> storeState(state: S) {
        cache.remove(state::class)
        cache[state::class] = state
    }

    /**
     * Retrieves a state of the given type.
     *
     * @param type: The type as a KClass of the state we wish to retrieve
     * @return The state associated with the given type if found, otherwise null.
     */
    fun <S : MviState> retrieveState(type: KClass<S>) : MviState? {
        return cache[type]
    }

    /**
     * Removes all states from the cache
     */
    fun clearState() {
        cache.clear()
    }

    /**
     * Removes a state from the cache of the given type.
     *
     * @param type The type of the cache to remove
     */
    fun <S : MviState> clearState(type: KClass<S>) {
        cache.remove(type)
    }
}