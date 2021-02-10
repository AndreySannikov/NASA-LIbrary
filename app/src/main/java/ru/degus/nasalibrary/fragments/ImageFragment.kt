package ru.degus.nasalibrary.fragments

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.activities.MainActivity
import ru.degus.nasalibrary.databinding.ImageFragmentBinding
import ru.degus.nasalibrary.models.Item
import ru.degus.nasalibrary.util.IMAGE
import ru.degus.nasalibrary.util.normalDate
import ru.degus.nasalibrary.viewmodels.ImageViewModel
import javax.inject.Inject

class ImageFragment : BaseFragment<ImageFragmentBinding>(R.layout.image_fragment) {
    override fun injectDagger() {
        App.instance.getAppComponent()
            .activitySubComponentBuilder()
            .with(navigator as AppCompatActivity)
            .build()
            .inject(this)

        App.instance.getViewModelSubComponent().inject(viewModel)
    }

    @Inject
    lateinit var viewModel: ImageViewModel                                                          //инъекция MainViewModel

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

        viewModel.imageData.observe(viewLifecycleOwner,
            Observer {
                it.collection?.items?.get(0)?.let { item -> bind(item) }                            //вызов метода привязки данных элемента полученного по id
            }
        )
    }

    private fun bind(item: Item) {
        val data = item.data?.get(0)

        binding.apply {
            tvName.text = data?.title ?: getString(R.string.unknown)                                                  //установка названия фотографии
            tvDate.text = data?.date_created?.normalDate() ?: getString(R.string.unknown)                             //даты создания в нормальном формате
            tvDescription.text = data?.description ?: getString(R.string.unknown)                                     //описания фотографии
        }

        Picasso.get()                                                                               //загрузка и кэширование фотографии
            .load(item.links?.get(0)?.href)
            .placeholder(R.color.gray).error(R.color.gray)
            .into(binding.photoView)
    }

    private fun load() {
        val id = arguments?.getString(IMAGE)                                                        //получение строки из MainFragment
        Log.d("onArgument", id.toString())
        if ((navigator as MainActivity).isConnected()) viewModel.downloadImage(id)                  //попытка загрузить данные
        else navigator.toast(getString(R.string.no_internet))                                              //при наличии интернет соединения
    }
}