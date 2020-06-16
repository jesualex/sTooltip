package cl.jesualex.tooltip.main.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip
import cl.jesualex.tooltip.R
import cl.jesualex.tooltip.main.fragment.ExampleFragment

import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        showTooltipButton.setOnClickListener {
            Tooltip.on(exampleTextView)
                .text(R.string.example)
                .iconStart(android.R.drawable.ic_dialog_info)
                .iconStartSize(30, 30)
                .color(resources.getColor(R.color.colorPrimary))
                .overlay(resources.getColor(R.color.overlay))
                .iconEnd(android.R.drawable.ic_dialog_info)
                .iconEndSize(30, 30)
                .border(Color.BLACK, 5f)
                .clickToHide(true)
                .corner(10)
                .position(Position.END)
                .show(3000)
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