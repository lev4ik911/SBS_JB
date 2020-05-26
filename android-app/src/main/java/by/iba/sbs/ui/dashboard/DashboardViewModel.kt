package by.iba.sbs.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.iba.mvvmbase.BaseViewModel
import by.iba.sbs.library.model.Category
import by.iba.sbs.library.model.Guideline

class DashboardViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    val categories = MutableLiveData<List<Category>>().apply {
        val mData = ArrayList<Category>()
        mData.add(Category("Кулинария", "", true, "#f08c8c"))
        mData.add(Category("Медицина", "", false, "#c1cefa"))
        mData.add(Category("Ремонт авто", "", false, "#fad1c1"))
        mData.add(Category("Выживание", "", true, "#060054"))
        mData.add(Category("IBA info", "", true, "#246801"))
        mData.add(Category("Документы", "", true, "#246801"))
        mData.add(Category("СМК", "", true, "#246801"))
        mData.add(Category("Строительство", "", true, "#246801"))
        mData.add(Category("Экстренная помощь", "", true, "#246801"))
        mData.add(Category("IBA docs", "", true, "#246801"))
        value = mData
    }
    val recommended = MutableLiveData<List<Guideline>>().apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("1", "Как стать счастливым", "Dobry"))
        mData.add(Guideline("2", "Отпадный шашлычок", "Dobry"))
        mData.add(Guideline("1", "Как попасть на проект, подготовка к интервью", "Author 2"))
        value = mData
    }
    val favorite = MutableLiveData<List<Guideline>>().apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("7", "Как сдать СМК на отлично!", "Dobry", isFavorite = true))

        mData.add(Guideline("3", "Как стать счастливым", "Dobry", isFavorite = true))
        mData.add(Guideline("2", "Отпадный шашлычок", "Dobry", isFavorite = true))
        mData.add(Guideline("4", "Что делать, если вы заразились", "Доктор"))

        value = mData
    }
    val popular = MutableLiveData<List<Guideline>>().apply {
        val mData = ArrayList<Guideline>()
        mData.add(Guideline("1", "Как стать счастливым", "Dobry"))
        mData.add(Guideline("2", "Отпадный шашлычок", "Dobry"))
        mData.add(Guideline("5", "Как поставить на учет автомобиль", "Dobry"))
        mData.add(Guideline("6", "Как оформить командировку", "Dobry"))
        value = mData
    }

    fun onViewFavoritesClick() {

    }

    fun onViewRecommendedClick() {

    }

    fun onViewCategoriesClick() {

    }
}