package com.grubhub.challenge.service.domain.impl

import android.content.Context
import java.lang.ref.WeakReference

abstract class BaseDomainService(ctx: Context) {

    private val contextRef = WeakReference(ctx.applicationContext)

    protected val context: Context
        get() = contextRef.get()!!

}