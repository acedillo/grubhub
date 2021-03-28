package com.grubhub.mvi.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grubhub.mvi.intent.MviIntent
import com.grubhub.mvi.model.BaseModel
import com.grubhub.mvi.model.MviState

/**
 * The MviActivity is the base [MviView] for activity level presentation layers of the MVI pattern.
 * All subsequent activities will need to extend this class.
 *
 * Created by matthew.ewers on 2019-05-17
 */
abstract class MviActivity<S : MviState, I : MviIntent> : AppCompatActivity(), MviView<S> {
    abstract fun getModel(): BaseModel<I, in S>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getModel().register(this, savedInstanceState)
        getModel().provideContext(this)
    }

    override fun onRestart() {
        super.onRestart()
        getModel().register(this)
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
        getModel().unregister(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        getModel().unregister(this)
    }
}