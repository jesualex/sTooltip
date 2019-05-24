package cl.jesualex.tooltip

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import cl.jesualex.stooltip.Position
import cl.jesualex.stooltip.Tooltip

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        showTooltipButton.setOnClickListener {
            Tooltip.on(exampleTextView)
                .text(R.string.example)
                .icon(android.R.drawable.ic_dialog_info)
                .iconSize(30, 30)
                .color(resources.getColor(R.color.colorPrimary))
                .overlay(resources.getColor(R.color.overlay))
                .border(Color.BLACK, 1f)
                .clickToHide(true)
                .corner(5)
                .position(Position.TOP)
                .show(3000)
        }
    }
}