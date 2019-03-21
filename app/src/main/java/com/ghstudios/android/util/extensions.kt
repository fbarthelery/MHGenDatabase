package com.ghstudios.android.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.ghstudios.android.GenericActionBarActivity


// A collection of extension functions used by the app.
// Only those that could reasonably belong to a separate library should be here.
// Anything with stronger coupling on non-android objects should not be here

/**
 * Adds a bundle to a fragment and then returns the fragment.
 * A lambda is used to set values to the bundle
 */
fun <T: Fragment> T.applyArguments(block: Bundle.() -> Unit): T {
    val bundle = Bundle()
    bundle.block()
    this.arguments = bundle
    return this
}

/**
 * Uses the cursor, automatically closing after executing the process.
 * Use this instead of use {} because Cursor's are not "Closeable" pre API 16
 */
inline fun <J : Cursor, R> J.useCursor(process: (J) -> R): R {
    try {
        return process(this)
    } catch (e: Throwable) {
        throw e
    } finally {
        try {
            close()
        } catch (closeException: Throwable) {}
    }
}

/**
 * Extension function that iterates over a cursor, doing an operation for each step
 * The cursor is closed at the completion of this method.
 */
inline fun <T, J : Cursor> J.forEach(process: (J) -> T) {
    this.useCursor {
        while (moveToNext()) {
            process.invoke(this)
        }
    }
}

/**
 * Extension function that converts a cursor to a list of objects using a transformation function.
 * The cursor is closed at the completion of this method.
 */
fun <T, J : Cursor> J.toList(process: (J) -> T) : List<T> {
    return MHUtils.cursorToList(this, object: MHUtils.CursorProcessFunction<T, J> {
        override fun getValue(c: J): T = process(c)
    })
}


/**
 * Extension function that pulls one entry from a cursor using a transform function,
 * or throws a NoSuchElementException if it doesn't exist.
 */
fun <T, J : Cursor> J.first(process: (J) -> T): T {
    return this.toList(process).first()
}

/**
 * Extension function that pulls one entry from a cursor using a transform function,
 * or null if the cursor is empty.
 * The cursor is closed at the completion of this method.
 */
fun <T, J : Cursor> J.firstOrNull(process: (J) -> T) : T? {
    // todo: optimize. Use cursor.moveToFirst() and check cursor.isAfterLast
    return this.toList(process).firstOrNull()
}

/**
 * Extension: Retrieves a drawable associated with a resource id
 * via ContextCompat using the called context.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

/**
 * Extension: Retrieves a color associated with a resource id
 * via ContextCompat using the called context.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

/**
 * Creates a block where "this" is the editor of the shared preferences.
 * The changes are commited asynchronously.
 */
inline fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    block(editor)
    editor.apply()
}

/**
 * Extension function that sends the activity result. If target fragment is not null,
 * it will call onActivityResult on the fragment. Otherwise, it will call it on the activity.
 *
 * This was created to allow a uniform interface between fragments and activities.
 */
fun DialogFragment.sendDialogResult(resultCode: Int, intent: Intent) {
    if (this.targetFragment != null) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
        return
    }

    val activity = activity as? GenericActionBarActivity
    activity ?: throw TypeCastException("sendDialogResult() only works on fragments and GenericActionBarActivity")
    activity.sendActivityResult(targetRequestCode, resultCode, intent)
}

/**
 * Creates a livedata from a block of code that is run in another thread.
 * The other thread is run in a background thread, and not on the UI thread.
 */
fun <T> createLiveData(block: () -> T): LiveData<T> {
    val result = MutableLiveData<T>()
    loggedThread("createLiveData") {
        result.postValue(block())
    }
    return result
}