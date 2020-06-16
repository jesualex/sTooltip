package cl.jesualex.tooltip.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import cl.jesualex.tooltip.R
import kotlinx.android.synthetic.main.example_item.view.*

/**
 * Created by jesualex on 2020-01-29.
 */
class ExampleAdapter(val onTooltipShow: (tooltip: Tooltip) -> Unit): RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>(){
    val text = "Tap this!"
    val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
    private val exampleList = listOf(text, text, text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        return ExampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.example_item, parent, false))
    }

    override fun getItemCount(): Int {
        return exampleList.size
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        holder.initView(exampleList[position])
    }

    inner class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun initView(exampleItem: String){
            itemView.eiExampleTV.text = exampleItem

            itemView.eiExampleTV.setOnClickListener {
                onTooltipShow(Tooltip
                    .on(itemView.eiExampleTV, false)
                    .text(longText)
                    .position(Position.BOTTOM)
                    .clickToHide(true)
                    .show())
            }
        }
    }
}