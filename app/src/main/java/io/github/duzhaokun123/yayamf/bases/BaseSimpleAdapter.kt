package io.github.duzhaokun123.yayamf.bases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import net.matsudamper.viewbindingutil.ViewBindingUtil

abstract class BaseSimpleAdapter<BaseBinding : ViewBinding>(
    val context: Context,
    val baseBindingClass: Class<BaseBinding>
) : RecyclerView.Adapter<BaseSimpleAdapter.BaseBindVH<BaseBinding>>() {
    class BaseBindVH<BaseBinding : ViewBinding>(val baseBinding: BaseBinding) :
        RecyclerView.ViewHolder(baseBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindVH<BaseBinding> {
        val baseBind = ViewBindingUtil.inflate(
            LayoutInflater.from(context), null, false, baseBindingClass
        )
        return BaseBindVH(baseBind)
    }

    override fun onBindViewHolder(holder: BaseBindVH<BaseBinding>, position: Int) {
        initViews(holder.baseBinding, position)
        initEvents(holder.baseBinding, position)
        initData(holder.baseBinding, position)
    }

    open fun initViews(baseBinding: BaseBinding, position: Int) {}
    open fun initEvents(baseBinding: BaseBinding, position: Int) {}
    open fun initData(baseBinding: BaseBinding, position: Int) {}
}