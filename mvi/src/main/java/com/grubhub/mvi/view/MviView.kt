package com.grubhub.mvi.view

import com.grubhub.mvi.model.MviState
import kotlin.reflect.KClass

/**
 * The MviView represents the presentation layer of the MVI pattern. This layer is responsible for rendering
 * states returned from the [com.cg_project.mvi.model.MviModel]. Classes implementing this interface should keep
 * logical code to a minimum. This layer is solely for wiring up the display of information and reporting user
 * interactivity within the context of the displayed UI.
 *
 * Created by matthew.ewers on 2019-05-17
 */
interface MviView<S : MviState> {
    fun render(state: S)
    fun getStateType(): KClass<S>
}