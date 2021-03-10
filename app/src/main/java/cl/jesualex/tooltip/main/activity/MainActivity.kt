package cl.jesualex.tooltip.main.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cl.jesualex.stooltip.DisplayListener
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import cl.jesualex.stooltip.Tooltip.Companion.on
import cl.jesualex.stooltip.TooltipClickListener
import cl.jesualex.tooltip.R
import cl.jesualex.tooltip.main.fragment.ExampleFragment
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_content.*


class MainActivity : AppCompatActivity() {
    var tooltip: Tooltip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        showTooltipButton.setOnClickListener {
            val text =  "a long string : xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxxxxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx"
            on(exampleTextView)
                .text(text)
                .textGravity(Gravity.CENTER_HORIZONTAL)
                .color(resources.getColor(R.color.colorPrimary))
                .border(0, 0f)
                .clickToHide(true)
                //.corner(dp8)
                //.arrowSize(dp8, (dp8 * 1.4f) as Int)
                .position(Position.TOP)
                .show(5000)
        }

        addFragmentButton.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, ExampleFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}