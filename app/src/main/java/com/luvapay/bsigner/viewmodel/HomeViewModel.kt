package com.luvapay.bsigner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.luvapay.bsigner.R

class HomeViewModel (app: Application) : AndroidViewModel(app) {

    var canModify: MutableLiveData<Boolean> = MutableLiveData(false)

    var navSelectedItemId = R.id.nav_home

}