package com.grubhub.mvi.model

import android.os.Parcelable

/**
 * MviState represents the visual state of a single activity or fragment. This can mean everything from representing
 * a view's visibility to an adapter's data set. All states must implement the parcelable interface so the last known
 * state can be preserved in the event the application is killed by the OS while in the background.
 *
 * Created by matthew.ewers on 2019-05-17
 */
interface MviState : Parcelable