package com.grubhub.mvi.intent

/**
 * The MviIntent is a representation of a user's intention while interacting with the application's UI. Intents
 * should consist of a sealed class which implements this interface. The sealed class should contain all possible
 * interactions for a given logical screen (which may consist of an activity and/or fragments). These intents will
 * be published to the view's model for processing.
 *
 * Created by matthew.ewers on 2019-05-17
 */
interface MviIntent