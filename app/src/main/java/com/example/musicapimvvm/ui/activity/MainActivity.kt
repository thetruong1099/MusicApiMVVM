package com.example.musicapimvvm.ui.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.service.MusicService
import com.example.musicapimvvm.viewModel.NetworkConnectionViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), ServiceConnection {

    private val currentData = CurrentData.instance

    private lateinit var navController: NavController

    private lateinit var musicService: MusicService

    private var lavProcess: Float = 0.0F

    private val localBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionName = intent?.getStringExtra("action_music")
            if (actionName == "action_music") {
                refreshView()
            }
        }
    }

    private lateinit var job: Job

    private val networkConnectionViewModel by lazy {
        ViewModelProvider(
            this,
            NetworkConnectionViewModel.NetworkConnectionViewModelFactory(this.application)
        )[NetworkConnectionViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationControl()

        job = Job()

        checkNetworkConnection()
    }

    override fun onStart() {
        super.onStart()
        if (currentData.getCurrentId() != null) {

            val intent = Intent(this, MusicService::class.java)
            ContextCompat.startForegroundService(this, intent)

            applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)

            layout_my_song.visibility = View.VISIBLE

            initListenerBtn()

            registerLocalBroadcastReceiver()

            startMediaPlayerActivity()

            refreshView()

            handleProcessBar()

        } else {
            layout_my_song.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.unbindService(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadCastReceiver)
    }

    @SuppressLint("RestrictedApi")
    private fun navigationControl() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
        refreshView()
        musicService.initListener()
        val serviceMsg = musicService.messenger
        try {
            val msg = Message.obtain(null, MusicService.MSG_REGISTER_CLIENT)
            msg.replyTo = messenger
            serviceMsg.send(msg)
        } catch (ignore: RemoteException) {
        }
    }

    private val messenger = Messenger(IncomingHandler())

    inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MusicService.MSG_COMPLETED) {
                refreshView()
            } else super.handleMessage(msg)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }

    private fun refreshView() {
        currentData.getCurrentId()?.let {
            val music = currentData.getCurrentSong()
            if (::musicService.isInitialized) {
                if (musicService.isPlaying) {
                    btn_play_main.setImageResource(R.drawable.baseline_pause_24)
                    lav_music_disc_main.setMinProgress(lavProcess)
                    lav_music_disc_main.playAnimation()
                    lav_music_disc_main.setMinProgress(0.0F)
                } else {
                    btn_play_main.setImageResource(R.drawable.baseline_play_arrow_24)
                    lav_music_disc_main.pauseAnimation()
                    lavProcess = lav_music_disc_main.progress
                }

                progress_bar.max = musicService.duration / 1000
                progress_bar.progress = musicService.currentPosition / 1000
            }

            tv_name_song_main.text = music.name
            if (music.artist == null) {
                tv_name_singer_main.text = "Unknown Artist"
            } else {
                tv_name_singer_main.text = "${music.artist}"
            }
        }
    }

    private fun initListenerBtn() {
        if (::musicService.isInitialized) {
            btn_play_main.setOnClickListener {
                if (musicService.isPlaying) {
                    btn_play_main.setImageResource(R.drawable.baseline_play_arrow_24)
                    lav_music_disc_main.pauseAnimation()
                    lavProcess = lav_music_disc_main.progress
                    job.cancel()
                } else {
                    btn_play_main.setImageResource(R.drawable.baseline_pause_24)
                    lav_music_disc_main.setMinProgress(lavProcess)
                    lav_music_disc_main.playAnimation()
                    lav_music_disc_main.setMinProgress(0.0F)
                    handleProcessBar()
                }
                musicService.playPauseMusic()
            }

            btn_next_main.setOnClickListener {
                musicService.nextMusic()
                refreshView()
            }
        }
    }

    private fun handleProcessBar() {
        job = CoroutineScope(newSingleThreadContext("myThread")).launch {
            while (true) {
                if (::musicService.isInitialized) {
                    updateUiProcessBar(
                        musicService.mediaPlayer.currentPosition,
                        musicService.duration
                    )
                }
                delay(1000)
            }
        }
    }

    private suspend fun updateUiProcessBar(position: Int, positionMax: Int) {
        withContext(Dispatchers.Main) {
            progress_bar.max = positionMax / 1000
            progress_bar.progress = position / 1000
        }
    }

    private fun checkNetworkConnection() {
        networkConnectionViewModel.getNetworkConnection().observe(this) { isConnected ->
            currentData.setCheckInternet(isConnected)
        }
    }

    private fun startMediaPlayerActivity() {
        layout_my_song.setOnClickListener {
            val music = currentData.getCurrentSong()
            val intent = Intent(this, MusicPlayerActivity::class.java)
            intent.putExtra("music", music)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_stationary)
        }
    }

    private fun registerLocalBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(localBroadCastReceiver, IntentFilter("send_data_to_activity"))
    }

}