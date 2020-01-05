package xh.destiny.core.paging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class PagingViewModel<R, T>(repo: PagingRepository<R, T>) : ViewModel() {
    private val query = MutableLiveData<List<String>>()
    private val result = Transformations.map(query) {
        repo.loadData(it)
    }
    val itemList = Transformations.switchMap(result) { it.pagedList }
    val networkState = Transformations.switchMap(result) { it.networkState }
    val refreshState = Transformations.switchMap(result) { it.refreshState }
    val extraData = Transformations.switchMap(result) {it.extraData}

    fun refresh() {
        result.value?.refresh?.invoke()
    }

    fun showList(q: List<String> = emptyList()): Boolean {
//        if (subredditName.value == subreddit) {
//            return false
//        }
        query.value = q
        return true
    }

    fun retry() {
        val listing = result?.value
        listing?.retry?.invoke()
    }


}