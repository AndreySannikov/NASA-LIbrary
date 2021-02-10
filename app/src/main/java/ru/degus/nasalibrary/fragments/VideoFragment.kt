package ru.degus.nasalibrary.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.activities.MainActivity
import ru.degus.nasalibrary.databinding.VideoFragmentBinding
import ru.degus.nasalibrary.models.Item
import ru.degus.nasalibrary.util.VIDEO
import ru.degus.nasalibrary.util.normalDate
import ru.degus.nasalibrary.util.replaceHTTPS
import ru.degus.nasalibrary.viewmodels.VideoViewModel
import javax.inject.Inject


class VideoFragment : BaseFragment<VideoFragmentBinding>(R.layout.video_fragment) {
    override fun injectDagger() {
        App.instance.getAppComponent()
            .activitySubComponentBuilder()
            .with(navigator as AppCompatActivity)
            .build()
            .inject(this)

        App.instance.getViewModelSubComponent().inject(viewModel)
    }

    @Inject
    lateinit var viewModel: VideoViewModel                                                          //инъекция MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModel()                                                                          //подписка на изменения состаяния viewModel
        load()                                                                                      //подгрузка данных при создании фрагмента
    }

    private fun observeViewModel() {
        viewModel.errorData.observe(viewLifecycleOwner,
            Observer {
                showInformDialog(getString(R.string.error), it.message) {
                    navigator.closeApp()                                                            //закрытия приложения при ошибке в получении запроса
                }
            }
        )

        viewModel.videoUrlData.observe(viewLifecycleOwner,
            Observer {
                it?.get(2)
                    ?.let { videoUrl -> bindVideo(videoUrl) }                                       //вызов метода привязки данных видео
            }
        )

        viewModel.videoData.observe(viewLifecycleOwner,
            Observer {
                it.collection?.items?.get(0)
                    ?.let { item -> bind(item) }                                                    //вызов метода привязки данных элемента полученного по id
            }
        )
    }


    private fun bind(item: Item) {
        val data = item.data?.get(0)
        binding.apply {
            tvName.text = data?.title ?: getString(R.string.unknown)                                                  //установка названия видео
            tvDate.text = data?.date_created?.normalDate() ?: getString(R.string.unknown)                             //даты создания в нормальном формате
            tvDescription.text = data?.description ?: getString(R.string.unknown)                                     //описания видео
        }
        if ((navigator as MainActivity).isConnected()) viewModel.downloadVideoUrl(item.href)        //загрузка списка данных видео по ссылки из элемента
        else navigator.toast(getString(R.string.no_internet))
    }

    private fun bindVideo(videoUrl: String) {
        val videoView = binding.videoView
        videoView.setVideoURI(Uri.parse(videoUrl.replaceHTTPS()))                                   //воспроизведение видео из сети
        videoView.setMediaController(MediaController(navigator as FragmentActivity))                //с заменной на https запрос
        videoView.requestFocus(0)                                                          //для совместимости с Android 9.0 и выше
        videoView.start()                                                                           //воспроизведение автоматически
        binding.pbLoadingVideo.visibility = View.VISIBLE
        videoView.setOnPreparedListener { mp ->
            mp.start()
            mp.setOnVideoSizeChangedListener { mp, _, _ ->
                binding.pbLoadingVideo.visibility = View.GONE                                       //отключение прогрессбара, когда видео подготовленно
                mp.start()
            }
            mp.setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) binding.pbLoadingVideo.visibility = View.VISIBLE        //установка прогрессбара при буфферизации видео
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) binding.pbLoadingVideo.visibility = View.GONE
                false
            }
        }
        videoView.setOnErrorListener { _, _, _ ->
            navigator.toast(getString(R.string.video_error))
            false
        }
    }

    private fun load() {
        val id = arguments?.getString(VIDEO)                                                        //получение строки из MainFragment
        Log.d("onArgument", id.toString())
        if ((navigator as MainActivity).isConnected()) viewModel.downloadVideo(id)                  //попытка загрузить данные
        else navigator.toast(getString(R.string.no_internet))                                              //при наличии интернет соединения
    }
}