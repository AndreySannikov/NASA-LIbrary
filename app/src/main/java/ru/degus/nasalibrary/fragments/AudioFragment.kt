package ru.degus.nasalibrary.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.activities.MainActivity
import ru.degus.nasalibrary.databinding.AudioFragmentBinding
import ru.degus.nasalibrary.models.Item
import ru.degus.nasalibrary.util.AUDIO
import ru.degus.nasalibrary.util.normalDate
import ru.degus.nasalibrary.util.replaceHTTPS
import ru.degus.nasalibrary.viewmodels.AudioViewModel
import javax.inject.Inject


class AudioFragment : BaseFragment<AudioFragmentBinding>(R.layout.audio_fragment) {
    override fun injectDagger() {
        App.instance.getAppComponent()
            .activitySubComponentBuilder()
            .with(navigator as AppCompatActivity)
            .build()
            .inject(this)

        App.instance.getViewModelSubComponent().inject(viewModel)
    }

    @Inject
    lateinit var viewModel: AudioViewModel                                                          //инъекция MainViewModel

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

        viewModel.audioUrlData.observe(viewLifecycleOwner,
            Observer {
                it?.get(0)
                    ?.let { audioUrl -> bindAudio(audioUrl) }                                       //вызов метода привязки данных аудио
            }
        )

        viewModel.audioData.observe(viewLifecycleOwner,
            Observer {
                it.collection?.items?.get(0)
                    ?.let { item -> bind(item) }                                                    //вызов метода привязки данных элемента полученного по id
            }
        )
    }


    private fun bind(item: Item) {
        val data = item.data?.get(0)
        binding.apply {
            tvName.text = data?.title ?: getString(R.string.unknown)                                                  //установка названия аудио
            tvDate.text = data?.date_created?.normalDate() ?: getString(R.string.unknown)                             //даты создания в нормальном формате
            tvDescription.text = data?.description ?: getString(R.string.unknown)                                     //описания аудио
        }

        if ((navigator as MainActivity).isConnected()) viewModel.downloadAudioUrl(item.href)        //загрузка списка данных аудио по ссылки из элемента
        else navigator.toast(getString(R.string.no_internet))
    }

    private fun bindAudio(audioUrl: String) {

        val videoView = binding.videoView
        videoView.setVideoURI(Uri.parse(audioUrl.replaceHTTPS()))                                   //воспроизведение аудио из сети
                                                                                                    //с заменной на https запрос
        val mc = MediaController(context)                                                           //для совместимости с Android 9.0 и выше
        videoView.setMediaController(mc)
        videoView.requestFocus(0)
        videoView.start()                                                                           //воспроизведение автоматически
        mc.show()
    }

    private fun load() {
        val id = arguments?.getString(AUDIO)                                                        //получение строки из MainFragment
        Log.d("onArgument", id.toString())
        if ((navigator as MainActivity).isConnected()) viewModel.downloadAudio(id)                  //попытка загрузить данные
        else navigator.toast(getString(R.string.no_internet))                                              //при наличии интернет соединения
    }
}