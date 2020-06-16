package cl.jesualex.tooltip.main.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import cl.jesualex.tooltip.R
import cl.jesualex.tooltip.main.adapter.ExampleAdapter
import kotlinx.android.synthetic.main.example_fragment.*

class ExampleFragment : Fragment() {
    private var tooltip: Tooltip? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.example_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        efExampleRV.adapter = ExampleAdapter{ tooltip = it }

        efExampleRV?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                tooltip?.moveTooltip(dx, dy)
            }
        })

        fragmentButton.setOnClickListener {
            Tooltip
                .on(exampleTextView)
                .text(R.string.example)
                .lineHeight(2f, 2f)
                .iconStart(android.R.drawable.ic_dialog_info)
                .iconStartSize(30, 30)
                .color(resources.getColor(R.color.colorPrimary))
                .border(Color.BLACK, 1f)
                .clickToHide(true)
                .corner(15)
                .position(Position.BOTTOM)
                .show()
        }
    }
}
