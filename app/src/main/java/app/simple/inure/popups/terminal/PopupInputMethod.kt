package app.simple.inure.popups.terminal

import android.view.LayoutInflater
import android.view.View
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.extensions.popup.BasePopupWindow
import app.simple.inure.extensions.popup.PopupLinearLayout
import app.simple.inure.preferences.TerminalPreferences

class PopupInputMethod(view: View) : BasePopupWindow() {

    private var character: DynamicRippleTextView
    private var word: DynamicRippleTextView

    init {
        val contentView = LayoutInflater.from(view.context).inflate(R.layout.popup_input_method, PopupLinearLayout(view.context))

        character = contentView.findViewById(R.id.popup_character_based)
        word = contentView.findViewById(R.id.popup_word_based)

        character.setOnClickListener {
            if (TerminalPreferences.setInputMethod(0)) {
                dismiss()
            }
        }

        word.setOnClickListener {
            if (TerminalPreferences.setInputMethod(1)) {
                dismiss()
            }
        }

        when (TerminalPreferences.getInputMethod()) {
            0 -> character.isSelected = true
            1 -> word.isSelected = true
        }

        init(contentView, view)
    }
}
