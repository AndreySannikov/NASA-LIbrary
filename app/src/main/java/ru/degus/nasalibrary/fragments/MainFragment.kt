package ru.degus.nasalibrary.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.launch
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.activities.Layout
import ru.degus.nasalibrary.activities.MainActivity
import ru.degus.nasalibrary.adapters.MainAdapter
import ru.degus.nasalibrary.adapters.Type
import ru.degus.nasalibrary.databinding.MainFragmentBinding
import ru.degus.nasalibrary.models.Item
import ru.degus.nasalibrary.util.AUDIO
import ru.degus.nasalibrary.util.IMAGE
import ru.degus.nasalibrary.util.VIDEO
import ru.degus.nasalibrary.viewmodels.MainViewModel
import javax.inject.Inject


class MainFragment : BaseFragment<MainFragmentBinding>(R.layout.main_fragment) {
    override fun injectDagger() {
        App.instance.getAppComponent()
            .activitySubComponentBuilder()
            .with(navigator as AppCompatActivity)
            .build()
            .inject(this)

        App.instance.getViewModelSubComponent().inject(viewModel)
    }

    @Inject
    lateinit var viewModel: MainViewModel                                                           //инъекция MainViewModel

    private lateinit var adapter: MainAdapter

    private lateinit var toolbar: Toolbar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar = binding.toolbarActionbar
        (navigator as MainActivity).setSupportActionBar(toolbar)                                    //замена ActionBar на ToolBar

        createRecyclerView()                                                                        //создания recyclerView с переферией
        observeViewModel()                                                                          //подписка на изменения состаяния viewModel
        setHasOptionsMenu(true)
        load()                                                                                      //подгрузка данных при создании фрагмента
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {                          //создание меню
        inflater.inflate(R.menu.menu_main_fragment, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        viewLifecycleOwner.lifecycleScope.launch {                                                  //установка значений чекбоксов
            menu.findItem(R.id.action_choice_image).isChecked = viewModel.isImage                   //из соответсвуещих переменных из viewModel
            menu.findItem(R.id.action_choice_video).isChecked = viewModel.isVideo
            menu.findItem(R.id.action_choice_audio).isChecked = viewModel.isAudio
        }

        searchView.queryTextChanges()                                                               //отслеживание измение строки SearchView
            .debounce(400, java.util.concurrent.TimeUnit.MILLISECONDS)                      //отправка запроса после 400 миллисекунд
            .observeOn(AndroidSchedulers.mainThread())                                              //если за это время строка не изменилась
            .subscribe {
                viewModel.searchString = it.toString()                                              //установка изменённой строки в поисковую строку viewModel
                if (it.isNotEmpty()) load()                                                         //подгрузка данных
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_choice_image -> {
                item.isChecked = !item.isChecked
                viewModel.isImage = item.isChecked                                                  //установка значений чекбоксов
                load()                                                                              //подгрузка данных
                true
            }
            R.id.action_choice_video -> {
                item.isChecked = !item.isChecked
                viewModel.isVideo = item.isChecked
                load()
                true
            }
            R.id.action_choice_audio -> {
                item.isChecked = !item.isChecked
                viewModel.isAudio = item.isChecked
                load()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.errorData.observe(viewLifecycleOwner,
            Observer {
                showInformDialog(getString(R.string.error), it.message) {
                    navigator.closeApp()                                                            //закрытия приложения при ошибке в получении запроса
                }
            }
        )

        viewModel.searchData.observe(viewLifecycleOwner,
            Observer {
                if (it != null) {
                    it.collection?.items?.let { it1 -> setItems(it1) }                              //установка нового списка в recyclerView
                    binding.pbLoadingItem.visibility = View.GONE
                }
            }
        )
    }

    private fun setItems(results: List<Item>) {
        if (results.isEmpty()) binding.tvNoFind.visibility = View.VISIBLE                           //отображение Nothing found
        else binding.tvNoFind.visibility = View.INVISIBLE                                           //если список пуст
        adapter.setItems(results)                                                                   //установка нового списка
    }

    private fun load() {
        if ((navigator as MainActivity).isConnected()) {                                            //попытка загрузить данные
            binding.pbLoadingItem.visibility = View.VISIBLE                                         //при наличии интернет соединения
            viewModel.search()
        }
        else navigator.toast(getString(R.string.no_internet))
    }

    private fun createRecyclerView() {                                                              //создание recyclerView
        val llm = GridLayoutManager((navigator as Context), 2)                            //создание GridLayoutManager в 2 столбца
        binding.rvSearch.layoutManager = llm

        val onAlbumClickListener: MainAdapter.OnAlbumClickListener = object :                       //реализация интерфейса слушателя клика по элементу списка
            MainAdapter.OnAlbumClickListener {
            override fun onAlbumClick(item: Item) {
                Log.d("onClick", item.data?.get(0)?.media_type.toString())

                when (Type.create(item.data?.get(0)?.media_type)) {
                    Type.IMAGE -> {                                                                 //если клик по элементу изображения
                        navigator.navigateTo(                                                       //переход в ImageFragment
                            Layout.IMAGE,                                                           //и передача id элемента
                            IMAGE to item.data?.get(0)?.nasa_id
                        )
                    }
                    Type.VIDEO -> {                                                                 //если клик по элементу видео
                        navigator.navigateTo(                                                       //переход в VideoFragment
                            Layout.VIDEO,                                                           //и передача id элемента
                            VIDEO to item.data?.get(0)?.nasa_id
                        )
                    }
                    Type.AUDIO -> {                                                                 //если клик по элементу аудио
                        navigator.navigateTo(                                                       //переход в AudioFragment
                            Layout.AUDIO,                                                           //и передача id элемента
                            AUDIO to item.data?.get(0)?.nasa_id
                        )
                    }
                }
            }
        }

        adapter = MainAdapter(onAlbumClickListener)                                                 //создание адаптера
        binding.rvSearch.adapter = adapter
    }
}