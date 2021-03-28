package com.grubhub.mvi.model

import android.os.Parcel
import android.os.Parcelable

/**
 * There are times when we want to pass an ephemeral message back to the view without it's payload
 * being tracked in the persisted state. The [MviMessage] class allows us to send a typed payload
 * to the view that nullifies its contents as soon as it is accessed so subsequent servings of the
 * last known state do not redeliver the message contents.
 *
 * While this class must implement [Parcelable] to work within the context of the [MviState]
 * interface, due to it's one-way delivery strategy, it will not actually write it's payload to the
 * parcel.
 *
 * Created by matthew.ewers on 2019-09-20
 */
class MviMessage<T : Any> private constructor(msg: T?) : Parcelable {
    companion object CREATOR : Parcelable.Creator<MviMessage<Any>> {
        /**
         * Takes a non-nullable payload to deliver once to the view layer via the [MviMessage]
         * instance.
         */
        fun <T : Any> packageOneTimePayload(msg: T) = MviMessage(msg)

        override fun createFromParcel(parcel: Parcel): MviMessage<Any> {
            return MviMessage(null)
        }

        override fun newArray(size: Int): Array<MviMessage<Any>?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Our message reference
     */
    private var message: T? = msg

    /**
     * Deliver the message and immediately nullify our internal reference
     */
    fun deliver(): T? {
        val msg = message
        message = null
        return msg
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }
}