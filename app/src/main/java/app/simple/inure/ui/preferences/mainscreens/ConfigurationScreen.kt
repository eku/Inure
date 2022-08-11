package app.simple.inure.ui.preferences.mainscreens

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleRelativeLayout
import app.simple.inure.decorations.switchview.SwitchView
import app.simple.inure.extensions.fragments.ScopedFragment
import app.simple.inure.preferences.ConfigurationPreferences
import app.simple.inure.ui.preferences.subscreens.Language
import app.simple.inure.ui.preferences.subscreens.Shortcuts
import app.simple.inure.util.FragmentHelper
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfigurationScreen : ScopedFragment() {

    private lateinit var keepScreenOnSwitchView: SwitchView
    private lateinit var shortcuts: DynamicRippleRelativeLayout
    private lateinit var language: DynamicRippleRelativeLayout
    private lateinit var rootSwitchView: SwitchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.preferences_configuration, container, false)

        keepScreenOnSwitchView = view.findViewById(R.id.configuration_switch_keep_screen_on)
        shortcuts = view.findViewById(R.id.configuration_shortcuts)
        language = view.findViewById(R.id.configuration_language)
        rootSwitchView = view.findViewById(R.id.configuration_root_switch_view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()

        keepScreenOnSwitchView.setChecked(ConfigurationPreferences.isKeepScreenOn())
        rootSwitchView.setChecked(ConfigurationPreferences.isUsingRoot())

        keepScreenOnSwitchView.setOnSwitchCheckedChangeListener { isChecked ->
            ConfigurationPreferences.setKeepScreenOn(isChecked)

            if (isChecked) {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        shortcuts.setOnClickListener {
            clearExitTransition()
            FragmentHelper.openFragment(parentFragmentManager, Shortcuts.newInstance(), "shortcuts")
        }

        language.setOnClickListener {
            clearExitTransition()
            FragmentHelper.openFragment(parentFragmentManager, Language.newInstance(), "language")
        }

        rootSwitchView.setOnSwitchCheckedChangeListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                if (it && Shell.rootAccess()) {
                    ConfigurationPreferences.setUsingRoot(true)

                    withContext(Dispatchers.Main) {
                        rootSwitchView.setChecked(true)
                    }
                } else {
                    ConfigurationPreferences.setUsingRoot(false)

                    withContext(Dispatchers.Main) {
                        rootSwitchView.setChecked(false)
                    }
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            ConfigurationPreferences.isUsingRoot -> {
                rootSwitchView.setChecked(ConfigurationPreferences.isUsingRoot())
            }
        }
    }

    companion object {
        fun newInstance(): ConfigurationScreen {
            val args = Bundle()
            val fragment = ConfigurationScreen()
            fragment.arguments = args
            return fragment
        }
    }
}