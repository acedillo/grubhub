package com.grubhub.mvi.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.grubhub.mvi.intent.MviIntent
import com.grubhub.mvi.model.BaseModel
import com.grubhub.mvi.model.MviState

/**
 * The MviFragment is the base [MviView] for fragment level presentation layers of the MVI pattern.
 * All subsequent fragments will need to extend this class.
 *
 * Created by matthew.ewers on 2019-05-17
 */
abstract class MviFragment<S : MviState, I : MviIntent> : Fragment(), MviView<S> {
    abstract fun getModel(): BaseModel<I, in S>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getModel().provideContext(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getModel().register(this, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        getModel().resume(this)
    }

    override fun onPause() {
        super.onPause()
        getModel().pause(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getModel().preserveState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        getModel().unregister(this)
    }
}