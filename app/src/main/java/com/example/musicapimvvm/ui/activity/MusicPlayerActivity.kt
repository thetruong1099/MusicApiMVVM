package com.example.musicapimvvm.ui.activity

import android.content.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapimvvm.R
import com.example.musicapimvvm.currentData.CurrentData
import com.example.musicapimvvm.model.Song
import com.example.musicapimvvm.model.database.SongDownloadTable
import com.example.musicapimvvm.model.database.SongFavoriteTable
import com.example.musicapimvvm.service.MusicService
import com.example.musicapimvvm.service.MusicService.Companion.REPEAT_ALL
import com.example.musicapimvvm.service.MusicService.Companion.REPEAT_ONE
import com.example.musicapimvvm.service.MusicService.Companion.SHUFFLE_ALL
import com.example.musicapimvvm.ui.adapter.ViewPagerAdapter
import com.example.musicapimvvm.ui.fragment.AnimationMusicPlayerDiscFragment
import com.example.musicapimvvm.ui.fragment.InfoSongFragment
import com.example.musicapimvvm.utils.Status
import com.example.musicapimvvm.viewModel.MusicViewModel
import com.example.mydiary.util.formatTime
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_music_player.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.*

class MusicPlayerActivity : AppCompatActivity(), ServiceConnection {

    private val currentData = CurrentData.instance

    private lateinit var musicService: MusicService

    private lateinit var music: Song

    private lateinit var job: Job

    private val musicViewModel by lazy {
        ViewModelProvider(
            this,
            MusicViewModel.MusicViewModelFactory(this.application)
        )[MusicViewModel::class.java]
    }

    private var statusFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        job = Job()

        setViewPager()

        getDataFromIntent()

        initListenerBtn()

        handleSeekBar()

    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        applicationContext.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        registerLocalBroadcastReceiver()
    }

    override fun onResume() {
        super.onResume()
        handleSeekBarCoroutine()
    }

    override fun onPause() {
        super.onPause()
        applicationContext.unbindService(this)
        job.cancel()
    }

    private fun setViewPager() {

        val listFragment: MutableList<Fragment> = mutableListOf(
            InfoSongFragment(),
            AnimationMusicPlayerDiscFragment()
        )

        val adapterViewPagers = ViewPagerAdapter(listFragment, supportFragmentManager, lifecycle)

        view_pager_info_song.apply {
            adapter = adapterViewPagers
            setCurrentItem(listFragment.size / 2, false)
        }

        TabLayoutMediator(tab_dot, view_pager_info_song) { tab, position ->
        }.attach()

    }

    private fun getDataFromIntent() {
        music = intent.getSerializableExtra("music") as Song
    }

    private fun initListenerBtn() {

        btn_back.setOnClickListener {
            finish()
        }

        btn_play.setOnClickListener { playPauseBtnClicked() }
        btn_previous_song.setOnClickListener { prevBtnClicked() }
        btn_next_song.setOnClickListener { nextBtnClicked() }

        btn_shuffle.setOnClickListener {
            var currentShuffle = currentData.getCurrentShuffle()
            if (currentShuffle == 4) currentData.setCurrentShuffle(1)
            else {
                currentShuffle++
                currentData.setCurrentShuffle(currentShuffle)
            }
            setUiBtnShuffle()
        }

        btn_relate_song.setOnClickListener {
            val intent = Intent(this, RelateSongActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        btn_download.setOnClickListener {
            currentData.getCurrentId()?.let { idSong ->
                downloadFileFromApi(idSong)
            }
        }
    }

    private fun setUiBtnShuffle() {
        when (currentData.getCurrentShuffle()) {
            REPEAT_ONE -> {
                btn_shuffle.setImageResource(R.drawable.baseline_repeat_one_24)
            }
            REPEAT_ALL -> {
                btn_shuffle.setImageResource(R.drawable.baseline_repeat_24)
            }
            SHUFFLE_ALL -> {
                btn_shuffle.setImageResource(R.drawable.baseline_shuffle_24)
            }
        }
    }

    private fun playPauseBtnClicked() {
        if (musicService.isPlaying) {
            btn_play.setImageResource(R.drawable.baseline_play_arrow_24)
            job.cancel()
        } else {
            btn_play.setImageResource(R.drawable.baseline_pause_24)
            handleSeekBarCoroutine()
        }
        musicService.playPauseMusic()
    }

    private fun prevBtnClicked() {
        musicService.previousMusic()
        refreshView()
        job.cancel()
        handleSeekBarCoroutine()
    }

    private fun nextBtnClicked() {
        musicService.nextMusic()
        refreshView()
        job.cancel()
        handleSeekBarCoroutine()

    }

    private fun refreshView() {

        setUiForPlayingMusic()

        music = currentData.getCurrentSong()

        tv_name_song.text = music.name
        tv_name_song.isSelected = true

        if (music.artist == null) {
            tv_artist.text = "Unknown Artist"
        } else {
            tv_artist.text = music.artist
        }

        tv_artist.isSelected = true

        setUiBtnShuffle()

        progress_bar_download.progress = 0

        initListenerFavorite()
    }

    private fun setUiForPlayingMusic() {
        if (::musicService.isInitialized) {

            if (musicService.isPlaying) {
                btn_play.setImageResource(R.drawable.baseline_pause_24)
            } else {
                btn_play.setImageResource(R.drawable.baseline_play_arrow_24)
            }

            seekBar.max = musicService.duration / 1000
            val mCurrentPosition = musicService.currentPosition / 1000
            seekBar.progress = mCurrentPosition
            tv_current_time.text = formatTime(mCurrentPosition)
            tv_duration_time.text = formatTime(musicService.duration / 1000)
        }
    }

    private fun initListenerFavorite() {
        val songId = currentData.getCurrentId()
        songId?.let {
            musicViewModel.getStatusFavorite(songId).observe(this) { status ->
                if (status != null) {
                    if (status) {
                        statusFavorite = true
                        btn_favorite.setImageResource(R.drawable.baseline_favorite_24)
                    } else {
                        statusFavorite = false
                        btn_favorite.setImageResource(R.drawable.baseline_favorite_border_24)
                    }
                } else {
                    statusFavorite = false
                    btn_favorite.setImageResource(R.drawable.baseline_favorite_border_24)
                }
            }
            initListenBtnFavorite(songId)
        }
    }

    private fun initListenBtnFavorite(songId: String) {
        btn_favorite.setOnClickListener {
            if (statusFavorite) {
                statusFavorite = false
                btn_favorite.setImageResource(R.drawable.baseline_favorite_border_24)
                musicViewModel.deleteFavoriteSong(songId)
            } else {
                statusFavorite = true
                btn_favorite.setImageResource(R.drawable.baseline_favorite_24)

                val song = currentData.getCurrentSong()
                val name = song.name
                val artist = song.artist
                val thumbnail = song.thumbnail
                val code = song.code

                musicViewModel.insertFavoriteSong(
                    SongFavoriteTable(
                        songId,
                        name,
                        artist,
                        thumbnail,
                        true,
                        code
                    )
                )

            }
        }
    }

    private fun handleSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (::musicService.isInitialized && fromUser) {
                    musicService.seekTo(progress * 1000)
                    seekBar!!.progress = progress
                    tv_current_time.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun handleSeekBarCoroutine() {
        job = CoroutineScope(newSingleThreadContext("myThread")).launch {
            while (true) {
                if (::musicService.isInitialized) {
                    val currentPosition = musicService.mediaPlayer.currentPosition / 1000
                    updateUi(currentPosition)
                }
                delay(1000)
            }
        }
    }

    private suspend fun updateUi(process: Int) {
        withContext(Dispatchers.Main) {
            seekBar.progress = process
            tv_current_time.text = formatTime(process)
        }
    }

    private fun downloadFileFromApi(idSong: String) {
        musicViewModel.getAllMusicDownload().observe(this) {
            var checkDuplicate = true
            for (i in it) {
                if (idSong == i.idSong) {
                    checkDuplicate = false
                    break
                }
            }

            if (checkDuplicate) {

                val url = "http://api.mp3.zing.vn/api/streaming/audio/${idSong}/128"

                musicViewModel.downloadFile(url).observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { responseBody ->
                                    musicViewModel.writeFile(
                                        responseBody,
                                        idSong,
                                        packageName,
                                        this
                                    )

                                }
                            }
                            Status.ERROR -> {
                                Log.d("aaaa", "refreshData: ${it.message}")
                                progress_bar_download.visibility = View.GONE
                            }
                            Status.LOADING -> {
                                progress_bar_download.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            } else {
                progress_bar_download.visibility = View.GONE
                Toast.makeText(
                    this@MusicPlayerActivity,
                    "File has been downloaded ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerLocalBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(localBroadCastReceiver, IntentFilter("send_data_to_activity"))
    }

    private val localBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionName = intent?.getStringExtra("action_music")
            if (actionName == "action_music") {
                refreshView()
                job.cancel()
                handleSeekBarCoroutine()
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
        var position = currentData.getCurrentSongList().indexOf(music)
        if (music.idSong != currentData.getCurrentId()) {
            musicService.playMusic(position)
        }
        musicService.initListener()
        refreshView()
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadCastReceiver)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_stationary, R.anim.slide_out_down)
    }
}