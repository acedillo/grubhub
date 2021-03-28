package com.grubhub.mvi.model

import com.grubhub.mvi.intent.MviIntent

/**
 * The MviModel is the workhorse of the application. It should be used to abstract as much work as possible away from
 * the views (activities and fragments). This will ensure greater testability of the application as well as heightened
 * maintainability by way of a clean separation of concerns. DI NOTE: If a model represents more than one view it is
 * important to declare a Singleton injection to ensure the same instance handles the state of all registered views.
 *
 * Created by matthew.ewers on 2019-05-17
 */
interface MviModel<I : MviIntent> {
    fun publish(intent: I)
}