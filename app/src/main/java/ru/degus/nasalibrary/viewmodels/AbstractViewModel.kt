package ru.degus.nasalibrary.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class AbstractViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    protected fun addDisposable(d: Disposable?) {
        if (d != null)
            disposables.add(d)
    }

    override fun onCleared() {
        Log.d("AbstractViewModel", "disposed")
        disposables.dispose()                                           //очистка всех подписок при удалении viewModel
    }
}