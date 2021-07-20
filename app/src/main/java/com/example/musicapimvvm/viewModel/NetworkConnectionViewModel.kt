package com.example.musicapimvvm.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicapimvvm.utils.ConnectionLiveData
import java.lang.IllegalArgumentException

class NetworkConnectionViewModel(application: Application) : ViewModel() {

    private val networkConnection = ConnectionLiveData(application)

    fun getNetworkConnection(): ConnectionLiveData = networkConnection

    class NetworkConnectionViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NetworkConnectionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NetworkConnectionViewModel(application) as T
            }

            throw IllegalArgumentException("unable construct viewModel")
        }

    }

}