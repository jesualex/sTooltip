package cl.jesualex.tooltip.main.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import cl.jesualex.tooltip.R
import kotlinx.android.synthetic.main.test_fragment.*

class TestFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentButton.setOnClickListener {
            Tooltip.on(exampleTextView)             .text(R.string.example)
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
