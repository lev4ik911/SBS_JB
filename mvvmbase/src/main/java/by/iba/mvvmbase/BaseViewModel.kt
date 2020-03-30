package by.iba.mvvmbase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.iba.mvvmbase.model.ToastMessage

abstract class BaseViewModel: ViewModel() {
    // FOR ERROR HANDLER
     val notificationsQueue = MutableLiveData<ToastMessage>()
     val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

}
