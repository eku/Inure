package app.simple.inure.extension.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import app.simple.inure.R
import app.simple.inure.preferences.AppearancePreferences
import app.simple.inure.preferences.ConfigurationPreferences
import app.simple.inure.preferences.SharedPreferences
import app.simple.inure.themes.interfaces.ThemeChangedListener
import app.simple.inure.themes.manager.ThemeManager
import app.simple.inure.util.ThemeUtils

open class TransparentBaseActivity : AppCompatActivity(), ThemeChangedListener {

    override fun attachBaseContext(newBase: Context) {
        SharedPreferences.init(newBase)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                                   .detectLeakedClosableObjects()
                                   .penaltyLog()
                                   .build())

        /**
         * Sets window flags for keeping the screen on
         */
        if (ConfigurationPreferences.isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (!AppearancePreferences.isTransparentStatusDisabled()) {
            makeAppFullScreen()
            // fixNavigationBarOverlap()
        }

        setTheme()
        ThemeUtils.setAppTheme(resources)
        ThemeUtils.setBarColors(resources, window)
        setNavColor()
    }

    private fun setTheme() {
        when (AppearancePreferences.getAccentColor()) {
            ContextCompat.getColor(baseContext, R.color.inure) -> {
                setTheme(R.style.Inure_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.blue) -> {
                setTheme(R.style.Blue_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.blueGrey) -> {
                setTheme(R.style.BlueGrey_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.darkBlue) -> {
                setTheme(R.style.DarkBlue_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.red) -> {
                setTheme(R.style.Red_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.green) -> {
                setTheme(R.style.Green_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.orange) -> {
                setTheme(R.style.Orange_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.purple) -> {
                setTheme(R.style.Purple_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.yellow) -> {
                setTheme(R.style.Yellow_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.caribbeanGreen) -> {
                setTheme(R.style.CaribbeanGreen_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.persianGreen) -> {
                setTheme(R.style.PersianGreen_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.amaranth) -> {
                setTheme(R.style.Amaranth_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.indian_red) -> {
                setTheme(R.style.IndianRed_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.light_coral) -> {
                setTheme(R.style.LightCoral_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.pink_flare) -> {
                setTheme(R.style.PinkFlare_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.makeup_tan) -> {
                setTheme(R.style.MakeupTan_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.egg_yellow) -> {
                setTheme(R.style.EggYellow_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.medium_green) -> {
                setTheme(R.style.MediumGreen_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.olive) -> {
                setTheme(R.style.Olive_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.copperfield) -> {
                setTheme(R.style.Copperfield_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.mineral_green) -> {
                setTheme(R.style.MineralGreen_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.lochinvar) -> {
                setTheme(R.style.Lochinvar_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.beach_grey) -> {
                setTheme(R.style.BeachGrey_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.cashmere) -> {
                setTheme(R.style.Cashmere_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.grape) -> {
                setTheme(R.style.Grape_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.roman_silver) -> {
                setTheme(R.style.RomanSilver_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.horizon) -> {
                setTheme(R.style.Horizon_Transparent)
            }
            ContextCompat.getColor(baseContext, R.color.limed_spruce) -> {
                setTheme(R.style.LimedSpruce_Transparent)
            }
            else -> {
                setTheme(R.style.Inure_Transparent)
                AppearancePreferences.setAccentColor(ContextCompat.getColor(baseContext, R.color.inure))
            }
        }
    }

    @Suppress("Deprecation")
    private fun makeAppFullScreen() {
        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT in 23..29) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        } else if (Build.VERSION.SDK_INT >= 30) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }
    }

    private fun fixNavigationBarOverlap() {
        /**
         * Making the Navigation system bar not overlapping with the activity
         */
        if (Build.VERSION.SDK_INT >= 30) {
            /**
             * Root ViewGroup of my activity
             */
            val root = findViewById<CoordinatorLayout>(R.id.app_container)

            ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

                /**
                 * Apply the insets as a margin to the view. Here the system is setting
                 * only the bottom, left, and right dimensions, but apply whichever insets are
                 * appropriate to your layout. You can also update the view padding
                 * if that's more appropriate.
                 */

                view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
                    leftMargin = insets.left
                    bottomMargin = insets.bottom
                    rightMargin = insets.right
                }

                /**
                 * Return CONSUMED if you don't want want the window insets to keep being
                 * passed down to descendant views.
                 */
                WindowInsetsCompat.CONSUMED
            }

        }
    }

    private fun setNavColor() {
        if (AppearancePreferences.isAccentOnNavigationBar()) {
            window.navigationBarColor = theme.obtainStyledAttributes(intArrayOf(R.attr.colorAppAccent))
                .getColor(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ThemeManager.removeListener(this)
    }
}