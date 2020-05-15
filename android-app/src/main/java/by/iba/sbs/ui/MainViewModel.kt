package by.iba.ecl.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class ActiveTabEnum(var index: Int) {
    ID_HOME(1),
    ID_DASHBOARD(3),
    ID_NOTIFICATION(4),
    ID_SETTINGS(5)
}

class MainViewModel : ViewModel() {

    val activeTab: MutableLiveData<Int> = MutableLiveData(ActiveTabEnum.ID_HOME.index)
}