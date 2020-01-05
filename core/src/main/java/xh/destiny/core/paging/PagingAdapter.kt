package xh.destiny.core.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xh.destiny.core.vo.NetworkState

abstract class PagingAdapter<T>(diff: DiffUtil.ItemCallback<T>,
                                private val retryCallback: () -> Unit)
    : PagedListAdapter<T, RecyclerView.ViewHolder>(diff) {

    private var networkState: NetworkState? = null

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_CONTENT -> {
                ItemViewHolder(
                    LayoutInflater.from(parent.context).inflate(itemLayout(), parent, false)
                )
            }
            ITEM_TYPE_NETWORK_STATE -> NetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_CONTENT -> {
                bindItemView(holder.itemView, getItem(position))
            }
            ITEM_TYPE_NETWORK_STATE -> (
                    holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            ITEM_TYPE_NETWORK_STATE
        } else {
            ITEM_TYPE_CONTENT
        }
    }

    // getItemCount 先于 getItemViewType 调用
    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
//        return super.getItemCount() + 1
    }

//    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
    private fun hasExtraRow() = networkState != null

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()

        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
//            notifyItemChanged(itemCount - 1)  // 这里会导致recycler view自动滚动到最后一项
            notifyDataSetChanged()

//            if (hadExtraRow) {
//                // 这里指从Loading或Error变为Success
//                notifyItemRemoved(super.getItemCount())
//            } else {
//                // 这里表示从Success变为Loading或Error
//                notifyItemInserted(super.getItemCount())
//            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyDataSetChanged()

            // 这里指Loading和ERROR的状态，如果两个状态互相切换，通知尾项的变化
//            notifyItemChanged(itemCount - 1)
        }
    }

    abstract fun itemLayout() : Int

    abstract fun bindItemView(v: View, item: T?)

    companion object {
        private const val ITEM_TYPE_CONTENT = 0
        private const val ITEM_TYPE_NETWORK_STATE = 1
    }
}