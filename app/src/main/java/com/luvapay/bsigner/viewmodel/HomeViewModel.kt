package com.luvapay.bsigner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class HomeViewModel (app: Application) : AndroidViewModel(app) {

    var canModify: MutableLiveData<Boolean> = MutableLiveData()

    init {
        canModify.value = false
    }

}