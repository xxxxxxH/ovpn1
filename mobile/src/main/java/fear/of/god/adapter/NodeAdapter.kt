package fear.of.god.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.github.shadowsocks.R
import fear.of.god.entity.ServerEntity

@SuppressLint("UseCompatLoadingForDrawables")
class NodeAdapter(data: MutableList<ServerEntity>?) :
    BaseQuickAdapter<ServerEntity, BaseViewHolder>(R.layout.item_node, data) {
    override fun convert(holder: BaseViewHolder, item: ServerEntity) {
        holder.getView<LinearLayout>(R.id.itemNode).apply {
            isSelected = item.select
        }
        holder.getView<TextView>(R.id.itemTitle).apply {
            text = item.name
            setTextColor(if (item.select) Color.parseColor("#ffffff") else Color.parseColor("#000000"))
        }
        holder.getView<TextView>(R.id.itemEs).apply {
            text = item.countryCode
            setTextColor(if (item.select) Color.parseColor("#ffffff") else Color.parseColor("#000000"))
        }
    }
}