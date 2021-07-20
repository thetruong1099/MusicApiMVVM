package com.example.mydiary.util

import android.annotation.SuppressLint
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.musicapimvvm.R
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.api.SongApi

fun NavController.popBackStackAllInstances(destination: Int, inclusive: Boolean): Boolean {
    var popped: Boolean
    while (true) {
        popped = popBackStack(destination, inclusive)
        if (!popped) {
            break
        }
    }
    return popped
}

@SuppressLint("RestrictedApi")
fun NavController.removeElementOverlap(navController: NavController) {
    var arr = ArrayList<NavBackStackEntry>()
    var backstack = navController.backStack

    for (i in backstack) arr.add(i)

    var count = 2
    var checkDuplicate = false

    if (arr.size > 3 && arr[arr.size - 2].destination.id == R.id.rankFragment) {
        navController.popBackStack(R.id.rankFragment, true)
        navController.navigate(arr[arr.size - 1].destination.id)
    }

    for (i in 2 until arr.size - 1) {
        count++
        if (arr[i].destination.id == arr[arr.size - 1].destination.id) {
            navController.popBackStackAllInstances(arr[i].destination.id, true)
            checkDuplicate = true
            break
        }
    }

    if (checkDuplicate) {
        for (i in count until arr.size) {
            navController.navigate(arr[i].destination.id)
        }
    }
}

fun formatTime(time: Int): String {
    var totalOut = ""
    var totalNew = ""
    val seconds = (time % 60).toString()
    val minutes = (time / 60).toString()
    totalOut = "$minutes:$seconds"
    totalNew = "$minutes:0$seconds"

    if (seconds.length == 1) {
        return totalNew
    } else {
        return totalOut
    }
}