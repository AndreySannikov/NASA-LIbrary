package ru.degus.nasalibrary.activities

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.degus.nasalibrary.App
import ru.degus.nasalibrary.R
import java.io.Serializable

enum class Layout(val id: Int) {                                        //класс перечислений id фрагментов для перемещений в навигации
    MAIN(R.id.mainFragment),
    IMAGE(R.id.action_mainFragment_to_imageFragment),
    VIDEO(R.id.action_mainFragment_to_videoFragment),
    AUDIO(R.id.action_mainFragment_to_audioFragment)
}
interface MainNavigator {                                               //интерфейс навигации по приложению
    fun navigateTo(layout: Layout, vararg pairs: Pair<String, Any?>)    //переход в фрагмент с аргументом
    fun closeApp()                                                      //закрытие приложения
    fun toast(msg: String)                                              //показ тоста
}

class MainActivity : AppCompatActivity(), MainNavigator {

    private lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav = Navigation.findNavController(this, R.id.my_nav_host_fragment)             //привязка контролера навигации к хосту
    }

    override fun navigateTo(layout: Layout, vararg pairs: Pair<String, Any?>) {                 //метод для перемещения между фрагментами
        Log.d("MainActivity", "onNextFragment = $layout")                             //и передачей информации между ними через Bundle
        nav.navigate(layout.id, bundleOf(pairs))
    }

    private fun bundleOf(pairs: Array<out Pair<String, Any?>>) =                                //метод создания Bundle для передачи во фрагменты
        Bundle(pairs.size).apply {
            for (bundle in pairs)
                when (bundle.second) {
                    is Parcelable -> this.putParcelable(bundle.first, bundle.second as Parcelable)
                    is Serializable -> this.putSerializable(bundle.first, bundle.second as Serializable)
                    is String -> this.putString(bundle.first, bundle.second as String)
                }
        }

    override fun closeApp() {                                                                   //метод закрытия приложения
        finish()
    }

    override fun toast(msg: String) {                                                           //метод вызова Toast сообщения
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun isConnected(): Boolean {                                                    //стандартная проверка на доступ к сети
        var connected = false
        try {
            val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
            return connected
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message.toString())
        }
        return connected
    }
}