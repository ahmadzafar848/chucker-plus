package com.chuckerteam.chucker.internal.ui

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.chuckerteam.chucker.internal.data.entity.HttpTransaction
import com.chuckerteam.chucker.internal.data.entity.HttpTransactionTuple
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.support.NotificationHelper
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {
    private val currentFilter = MutableLiveData("")

    val transactions: LiveData<List<HttpTransactionTuple>> =
        currentFilter.switchMap { searchQuery ->
            with(RepositoryProvider.transaction()) {
                when {
                    searchQuery.isNullOrBlank() -> {
                        getSortedTransactionTuples()
                    }

                    searchQuery.isDigitsOnly() -> {
                        getFilteredTransactionTuples(searchQuery, "")
                    }

                    else -> {
                        getFilteredTransactionTuples("", searchQuery)
                    }
                }
            }
        }

    suspend fun getAllTransactions(): List<HttpTransaction> = RepositoryProvider.transaction().getAllTransactions()

    fun updateItemsFilter(searchQuery: String) {
        currentFilter.value = searchQuery
    }

    fun clearTransactions() {
        viewModelScope.launch {
            RepositoryProvider.transaction().deleteAllTransactions()
        }
        NotificationHelper.clearBuffer()
    }
}
