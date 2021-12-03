package app.simple.inure.popups.usagestats

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.extension.popup.BasePopupWindow
import app.simple.inure.extension.popup.PopupLinearLayout
import app.simple.inure.preferences.StatsPreferences

class PopupAppsCategoryUsageStats(view: View) : BasePopupWindow() {

    private val system: DynamicRippleTextView
    private val user: DynamicRippleTextView
    private val both: DynamicRippleTextView

    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_apps_category, PopupLinearLayout(view.context))

        system = contentView.findViewById(R.id.popup_category_system)
        user = contentView.findViewById(R.id.popup_category_user)
        both = contentView.findViewById(R.id.popup_category_both)

        when (StatsPreferences.getAppsCategory()) {
            USER -> user.isSelected = true
            SYSTEM -> system.isSelected = true
            BOTH -> both.isSelected = true
        }

        system.onClick(SYSTEM)
        user.onClick(USER)
        both.onClick(BOTH)

        init(contentView, view)
    }

    private fun TextView.onClick(category: String) {
        this.setOnClickListener {
            StatsPreferences.setAppsCategory(category)
            dismiss()
        }
    }

    companion object {
        const val SYSTEM = "system"
        const val USER = "user"
        const val BOTH = "both"
    }
}
