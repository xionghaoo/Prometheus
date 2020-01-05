package xh.destiny.core.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xh.destiny.core.R
import xh.destiny.core.vo.NetworkState
import xh.destiny.core.vo.Status

class NetworkStateViewHolder(view: View,
                             private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<View>(R.id.progress_bar)
    private val retry = view.findViewById<Button>(R.id.retry_button)
    private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
    private val noMoreData = view.findViewById<TextView>(R.id.no_more_data)
    init {
        retry.setOnClickListener {
            retryCallback()
        }
    }
    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = toVisibility(networkState?.status == Status.LOADING)
        retry.visibility = toVisibility(networkState?.status == Status.ERROR)
        errorMsg.visibility = toVisibility(networkState?.msg != null)
//        errorMsg.text = networkState?.msg
        errorMsg.text = "加载失败, 请重试"
        noMoreData.visibility = toVisibility(networkState?.status == Status.SUCCESS)
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.network_state_item, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }

        fun toVisibility(constraint : Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}