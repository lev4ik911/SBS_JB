package by.iba.sbs.library.viewmodel


import by.iba.sbs.library.data.remote.Response
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import by.iba.sbs.library.repository.GuidelineRepository
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.MutableLiveData

import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class GuidelineListViewModelShared(
    settings: Settings,
    override val eventsDispatcher: EventsDispatcher<EventsListener>
) : ViewModelExt(settings),
    EventsDispatcherOwner<GuidelineListViewModelShared.EventsListener> {
    val instructions = MutableLiveData<List<Guideline>>(mutableListOf())
    val suggestions = MutableLiveData<List<String>>(arrayListOf())
    var searchedText = MutableLiveData("").apply {
        value = localStorage.searchedText
        addObserver {
            localStorage.searchedText = it
        }
    }
    val searchHistoryList = mutableListOf<String>()

    @UnstableDefault
    @ImplicitReflectionSerializer
    private val repository by lazy { GuidelineRepository(localStorage) }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadInstructions(forceRefresh: Boolean) {
        loading.value = true
        viewModelScope.launch {
            try {
                repository.getAllGuidelines(forceRefresh)
                    .addObserver {
                        loading.postValue(it.status == Response.Status.LOADING)
                        if (it.isNotEmpty) {
                            if (it.error != null) {
                                offlineMode.value = true
                            }
                            else if (forceRefresh) {
                                offlineMode.value = false
                            }

                            var guidelines = it.data!!
                            if (searchedText.value.isNotEmpty()) {
                                viewModelScope.launch {
                                    guidelines = repository.getFilteredGuidelines(searchedText.value)
                                    instructions.postValue(guidelines.sortedBy { item -> item.id }
                                        .toList())
                                }
                            } else {
                                instructions.postValue(guidelines.sortedBy { item -> item.id }
                                    .toList())
                            }
                        } else if (it.error != null)
                            eventsDispatcher.dispatchEvent {
                                showToast(
                                    ToastMessage(
                                        it.error.toString(),
                                        MessageType.ERROR
                                    )
                                )
                            }
                    }
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun loadSuggestions(searchText: String) {
        viewModelScope.launch {
            try {
                suggestions.postValue(repository.getSuggestions(searchText))
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    fun getFilteredGuidelines(searchText: String) {
        viewModelScope.launch {
            try {
                instructions.postValue(repository.getFilteredGuidelines(searchText))
            } catch (e: Exception) {
                eventsDispatcher.dispatchEvent {
                    showToast(
                        ToastMessage(
                            e.toString(),
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun saveSearchHistoryList(searchText: String) {
        if (localStorage.searchHistoryCount > 0) {
            searchHistoryList.removeAll {
                searchHistoryList.indexOf(it).plus(1) >= localStorage.searchHistoryCount || it == searchText
            }
            searchHistoryList.add(0, searchText)

            Json(JsonConfiguration.Stable).apply {
                this.stringify(String.serializer().list, searchHistoryList).apply {
                    if (this.isNotEmpty())
                        localStorage.searchHistoryJson = this
                }
            }
        }
    }

    fun getSearchHistoryList() {
        val stringJson = localStorage.searchHistoryJson
        if (stringJson.isNotEmpty())
            Json(JsonConfiguration.Stable).apply {
                this.parse(String.serializer().list, stringJson).apply {
                    if (!this.isNullOrEmpty() && localStorage.searchHistoryCount > 0)
                        searchHistoryList.addAll(this.filter {
                            this.indexOf(it).plus(1) <= localStorage.searchHistoryCount
                        })
                }
            }
    }

    interface EventsListener {
        fun showToast(msg: ToastMessage)
    }
}