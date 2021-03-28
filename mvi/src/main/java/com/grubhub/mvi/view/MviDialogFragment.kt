package com.grubhub.mvi.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.grubhub.mvi.intent.MviIntent
import com.grubhub.mvi.model.BaseModel
import com.grubhub.mvi.model.MviState

/**
 * Created by mewers on 2020-02-24
 */
abstract class MviDialogFragment<S : MviState, I : MviIntent> : AppCompatDialogFragment(), MviView<S> {
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

    abstract fun onCreateAlertDialog(savedInstanceState: Bundle?): androidx.appcompat.app.AlertDialog?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return onCreateAlertDialog(savedInstanceState) ?: super.onCreateDialog(savedInstanceState)
    }
}