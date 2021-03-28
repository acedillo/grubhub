package com.grubhub.mvi.model

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.grubhub.mvi.intent.MviIntent
import com.grubhub.mvi.view.MviView
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * The BaseModel provides common functionality needed by all models. The model consumes key lifecycle events sent
 * from the base views, [com.cg_project.mvi.view.MviActivity] and [com.cg_project.mvi.view.MviFragment]. This class
 * is also required to provide protected access to the MVI libraries internal [StateCache].
 *
 * Created by matthew.ewers on 2019-05-17
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseModel<I : MviIntent, S : MviState> : MviModel<I> {
    private val registeredViews = mutableMapOf<KClass<out S>, ViewState>()

    /**
     * A reference to the application context provided by the first registered view
     */
    private var contextRef: WeakReference<Context>? = null

    /**
     * A reference to the activity which is currently displaying MVI content
     */
    private var activityRef: WeakReference<Activity>? = null

    /**
     * Attempts to load the last known state from the cache, and failing that will look for an iniitial
     * state of the given type VS. This method may return null.
     */
    protected inline fun <reified VS : S> getLastState(): VS? {
        return getFromCache(VS::class) ?: getInitialStates().firstOrNull() { it::class == VS::class } as? VS
    }

    /**
     * Provides the last known state asserting it is not null. If you cannot be certain there is already
     * either a state in the cache or a model-provided initial state, consider using [getLastState]
     * instead to avoid KNPEs.
     */
    protected inline fun <reified VS : S> requireLastState(): VS {
        return getLastState()!!
    }

    /**
     * A simple collection to provide both a reference to the registered view as well as a boolean flag
     * to indicate whether or not the view is eligible to be dispatch state objects via render() calls.
     */
    private data class ViewState(
            private val viewRef: WeakReference<out MviView<out MviState>>,
            var isActive: Boolean = false
    ) {
        fun getView() = viewRef.get()
    }

    /**
     * Registers an [MviView] to the model. This method also accepts an optional bundle for propagating
     * restored state following a instance where the OS has terminated our app in the background to free resources.
     *
     * @param view The [MviView] to register
     * @param savedInstanceState The optional state bundle which allows the model to restore state objects from
     *                           a previously destroyed session.
     */
    fun <VS : S> register(view: MviView<VS>, savedInstanceState: Bundle? = null) {
        val stateType = view.getStateType()
        if (registeredViews.contains(stateType)) {
            return
        }

        // If we don't yet have access to the activity/application context - grab a weak reference now.
        if (activityRef?.get() == null) {
            if (view is Activity) {
                activityRef = WeakReference(view)
            } else if (view is androidx.fragment.app.Fragment) {
                activityRef = WeakReference(view.activity!!)
            }
        }

        restoreState(stateType, savedInstanceState)

        val viewRef = WeakReference(view)
        val first = !registeredViews.any()
        registeredViews[view.getStateType()] = ViewState(viewRef)

        if (first) onFirstViewRegistered()
        getInitialStates().firstOrNull { it::class == view.getStateType() }?.let { dispatchStates(it) }
        onViewRegistered(view::class)
    }

    /**
     * Provides the model with an application context provided by a previously registered view.
     */
    val context: Context
        get() = contextRef?.get()!!

    /**
     * Provides the model with access to the activity under which it is operating
     */
    val activity: Activity
        get() = activityRef?.get()!!

    /**
     * Provides the base model with it's weak reference to the application context
     */
    fun provideContext(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    /**
     * Ensures the registered view is marked as eligible to receive dispatched state objects via render() calls.
     *
     * @param view The [MviView] to resume
     */
    fun <VS : S> resume(view: MviView<VS>) {
        val stateType = view.getStateType()
        registeredViews[stateType]?.let { it.isActive = true }
        dispatchFromCache(stateType)
    }

    /**
     * Ensures the registered view is marked as ineligible to receive dispatched states.
     *
     * @param view The [MviView] to pause
     */
    fun <VS : S> pause(view: MviView<VS>) = registeredViews[view.getStateType()]?.let { it.isActive = false }

    /**
     * This call is made from the view's onSaveInstanceState() lifecycle method and allows our model to persist its
     * state as the application heads to the background. This way if the OS has to kill our app, when restored we will
     * receive our last known states by way of the savedInstanceState bundle.
     *
     * @param view The [MviView] which has state needing preservation
     * @param outState The saveInstanceState bundle through which this session's state may be preserved
     */
    fun <VS : S> preserveState(view: MviView<VS>, outState: Bundle?) {
        outState?.let { bundle ->
            val stateType = view.getStateType()
            getFromCache(stateType)?.let { state ->
                val stateKey = stateType.java.canonicalName
                bundle.putParcelable(stateKey, state)
            }
        }
    }

    /**
     * Unregisters the view from the model by way of clean up. This is typically invoked from the view's
     * onDestroy() or onDestroyView() lifecycle events. This ensures our [registeredViews] collection stays relevant
     * and clean.
     *
     * @param view The [MviView] to unregister from this model
     * @param outState The saveInstanceState bundle through which this session's state may be preserved
     */
    fun <VS : S> unregister(view: MviView<VS>, outState: Bundle? = null) {
        var removeKey: KClass<out MviState>? = null
        val stateType = view.getStateType()
        for (key in registeredViews.keys) {
            if (stateType == key) {
                removeKey = key as KClass<VS>
                break
            }
        }

        removeKey?.let {
            registeredViews.remove(stateType)
            onViewUnregistered(view::class)
            if (registeredViews.none()) { onAllViewsUnregistered() }
        }

        if (outState == null) {
            StateCache.clearState(stateType)
        } else {
            preserveState(view, outState)
        }
    }

    /**
     * This can be overridden in a subclass to hook into the event of the first view being
     * registered to the model.
     *
     * @param viewClass The type of view which has just been registered to the model
     */
    open fun onFirstViewRegistered() {
        // No op
    }

    /**
     * This can be overridden in a subclass to hook into the event of a view registers to the model.
     * This could be an opportunity to create coroutine jobs or initialize lazy members.
     *
     * @param viewClass The type of view which has just been registered to the model
     */
    open fun onViewRegistered(viewClass: KClass<*>) {
        // No op
    }

    /**
     * This can be overridden in a subclass to hook into the event of a view unregistering from the model.
     * This could be an opportunity to cancel asynchronous operations, dispatch updated states to remaining
     * views, or clear the state from the cache if, for example, the user is navigating down the back stack.
     *
     * @param viewClass The type of view which has just been unregistered from the model
     */
    open fun onViewUnregistered(viewClass: KClass<*>) {
        // No op
    }

    /**
     * This can be overridden in a subclass to hook into the event of the last view unregistering from the model.
     */
    open fun onAllViewsUnregistered() {
        // No op
    }

    /**
     * This method may be overridden by subclasses to provide the initial states the model optionally supports. If
     * the [StateCache] does not contain a given state object, and it is not provided by this collection, the
     * render() call will not be made when a [MviView] calls the model's resume() method.
     *
     * @return A list of state objects
     */
    open fun getInitialStates() = emptyList<S>()

    /**
     * For views that have data binding available in their layouts, this method may be overridden to
     * take the dispatched state and bind it to the data-bound layout.
     *
     * @param state A state object of type S which was dispatched by this [BaseModel] super class.
     */
    open fun bindData(state: S) {
        // No op
    }

    /**
     * Allows subclass models to access the MVI library's internal [StateCache] for the purpose of
     * retrieving states of a given type.
     *
     * @param classType The type of state object to return
     * @return The state object of the given type if found, otherwise null
     */
    protected fun <S : MviState> getFromCache(classType: KClass<S>) = StateCache.retrieveState(classType) as? S

    /**
     * Allows subclass models to access the MVI library's internal [StateCache] for the purpose of
     * clearing all state stored in the cache.
     */
    protected fun clearCache() = StateCache.clearState()

    /**
     * Takes a calculated state and using the stored [registeredViews] map, discerns how to appropriately
     * route the given state to the correct view's render() method.
     *
     * @param states A vararg collection of [MviState] objects to dispatch to their respective views.
     */
    protected fun <VS : S> dispatchStates(vararg states: VS) {
        states.forEach { state ->
            StateCache.storeState(state)
            for (entry in registeredViews) {
                if (state::class != entry.key) continue

                if (entry.value.isActive) {
                    (entry.value.getView() as? MviView<VS>)?.render(state)
                    bindData(state)
                }
            }
        }
    }

    /**
     * This call is made after popping the back stack in a fragment transaction or during view
     * recreation following an orientation change. When the view was destroyed it's state is
     * removed from the StateCache to conserve memory, however it was at the same time written to
     * the savedInstanceState bundle. As the view is recreated it is passed back this bundle from
     * the Android OS and we attempt to retrieve any previously stored state from the bundle by
     * using the state models canonical name as a key.
     *
     * @param stateType The type of the state we're attempting to retrieve from the bundle
     * @param savedInstanceState The savedInstanceState bundle served to the view upon recreation
     *                           from the operating system.
     */
    private fun <VS : S> restoreState(stateType: KClass<VS>, savedInstanceState: Bundle?) {
        savedInstanceState?.let { bundle ->
            if (StateCache.retrieveState(stateType) == null) {
                val bundleKey = stateType.java.canonicalName
                bundle.getParcelable<VS>(bundleKey)?.let { StateCache.storeState(it) }
            }
        }
    }

    /**
     * Dispatches state of a given type from the cache, if present, to the appropriate registered/resumed view.
     *
     * @param classType The type of the state to render
     */
    private fun <VS : S> dispatchFromCache(classType: KClass<VS>) {
        getFromCache(classType)?.let { state ->
            dispatchStates(state)
        } ?: run {
            // We didn't find state in the cache - look for initial state of the requested type
            for (state in getInitialStates()) {
                if (state::class == classType) {
                    dispatchStates(state)
                    break
                }
            }
        }
    }

    @VisibleForTesting
    fun setContext(context: Context) {
        this.contextRef = WeakReference(context)
    }
}