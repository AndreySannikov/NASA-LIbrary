package ru.degus.nasalibrary.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import ru.degus.nasalibrary.R
import ru.degus.nasalibrary.activities.MainNavigator

abstract class BaseFragment<B : ViewDataBinding>(private val layoutId: Int) : Fragment() {

    protected lateinit var navigator: MainNavigator
    protected lateinit var binding: B

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as MainNavigator
    }

    abstract fun injectDagger()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        injectDagger()

        binding = DataBindingUtil.inflate(                                                          //создание объекта привязки
            inflater, layoutId, container, false
        )

        return binding.root
    }

    fun showInformDialog(title: String, message: String?, call: (() -> Unit)? = null) {             //показ сообщения с колбэком
        AlertDialog.Builder(navigator as Context)
            .setTitle(title)
            .setMessage(message ?: getString(R.string.no_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                call?.invoke()
            }
            .create()
            .show()
    }
}