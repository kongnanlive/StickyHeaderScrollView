package com.kongnan.headerscrollview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {

    val isRefreshing = MutableLiveData<Boolean>()
}