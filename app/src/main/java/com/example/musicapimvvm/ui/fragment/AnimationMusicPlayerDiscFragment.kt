package com.example.musicapimvvm.ui.fragment

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.service.MusicService
import kotlinx.android.synthetic.main.fragment_animation_music_player_disc.*


class AnimationMusicPlayerDiscFragment : Fragment(), ServiceConnection {

    private lateinit var musicService: MusicService
    private var lavProcess = 0.0F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animation_music_player_disc, container, false)
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(requireContext(), MusicService::class.java)

        requireContext().applicationContext.bindService(
            serviceIntent,
            this,
            Context.BIND_AUTO_CREATE
        )
        registerLocalBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(localBroadCastReceiver)
        requireContext().applicationContext.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
        refreshView()
    }

    private fun refreshView() {
        if (::musicService.isInitialized) {
            if (musicService.isPlaying) {
                lav_music_disc.setMinProgress(lavProcess)
                lav_music_disc.playAnimation()
                lav_music_disc.setMinProgress(0.0F)
            } else {
                lav_music_disc.pauseAnimation()
                lavProcess = lav_music_disc.progress
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    private val localBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionName = intent?.getStringExtra("action_music")
            if (actionName == "action_music") {
                refreshView()
            }
        }
    }

    private fun registerLocalBroadcastReceiver() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(localBroadCastReceiver, IntentFilter("send_data_to_activity"))
    }
}