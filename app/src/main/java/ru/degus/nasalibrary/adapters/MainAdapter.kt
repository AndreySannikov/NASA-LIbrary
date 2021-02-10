package ru.degus.nasalibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.databinding.SearchItemViewBinding
import ru.degus.nasalibrary.models.Item
import ru.degus.nasalibrary.util.DiffCallback
import ru.degus.nasalibrary.util.normalDate

enum class Type(val type: String?) {                                        //класс перечислений типов элементов списка
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio");

    companion object {
        fun create(type: String?): Type {
            return when (type) {
                "image" -> IMAGE
                "video" -> VIDEO
                "audio" -> AUDIO
                else -> throw IllegalStateException()
            }
        }
    }
}

class MainAdapter(var onAlbumClickListener: OnAlbumClickListener)  : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var items: List<Item> = ArrayList()


    interface OnAlbumClickListener {                                                     //интерфейс слушатель для клика по элементу
        fun onAlbumClick(item: Item)
    }

    fun setItems(newItems: List<Item>) {                                                // установка нового списка в Adapter
        val diffResult = DiffUtil.calculateDiff(DiffCallback(items, newItems), false)   //перерасчет списка через DiffUtil
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {  // создание ViewHolder
        val li = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SearchItemViewBinding>(
            li,
            R.layout.search_item_view,
            parent,
            false
        )
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.itemView.setOnClickListener {                                //обработка клика на элемент списка
            items[position].let { onAlbumClickListener.onAlbumClick(it) }
        }
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class MainViewHolder(private val binding: SearchItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Item){
            when (Type.create(item.data?.get(0)?.media_type)) {
                Type.IMAGE -> {                                             //привязка данных для изображений
                    standardBind(item)
                    picassoBind(item)
                }

                Type.AUDIO -> {                                             //привязка данных для аудиоэлементов
                    standardBind(item)
                    binding.ivAudioPlay.visibility = View.VISIBLE           //установка иконки аудио
                    Picasso.get()                                           //установка черного фона для превью аудиоэлементов
                        .load(R.color.black)
                        .placeholder(R.color.gray).error(R.color.gray)
                        .into(binding.ivSearchImage)
                }

                Type.VIDEO -> {                                             //привязка данных для видеоэлементов
                    standardBind(item)
                    picassoBind(item)
                    binding.ivVideoPlay.visibility = View.VISIBLE           //установка иконки видео
                }
            }

        }

        private fun standardBind(item: Item) {                                      //привязка данных для всех типов (аудио, видео, изображение)
            binding.apply {
                tvName.text = item.data?.get(0)?.title ?: ""                        //установка названия
                tvDate.text = item.data?.get(0)?.date_created?.normalDate() ?: ""   //установка даты
                ivVideoPlay.visibility = View.GONE
                ivAudioPlay.visibility = View.GONE
            }
        }

        private fun picassoBind(item: Item) {
            Picasso.get()                                           //загрузка и кэширование фотографии
                .load(item.links?.get(0)?.href)                     //ссылка на изображения полученные в JSON для видео и изображений
                .placeholder(R.color.gray).error(R.color.gray)
                .into(binding.ivSearchImage)
        }
    }
}
