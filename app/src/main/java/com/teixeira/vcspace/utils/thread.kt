/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import okhttp3.internal.wait

fun <R> runOnUiThread(block: () -> R): R {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return block()
    }

    val lock = Any()
    var result: R? = null

    Handler(Looper.getMainLooper()).post {
        synchronized(lock) {
            result = block()
            lock.notify()
        }
    }

    synchronized(lock) {
        while (result == null) {
            lock.wait()
        }
    }

    return result!!
}

fun <R> CoroutineScope.execute(block: suspend () -> R): R {
    var result: R? = null
    launch {
        result = block()
    }
    while (result == null) {
        Thread.sleep(100)
    }
    return result!!
}
