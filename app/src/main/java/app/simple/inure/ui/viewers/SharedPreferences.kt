package app.simple.inure.ui.viewers

import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.R
import app.simple.inure.adapters.details.AdapterResources
import app.simple.inure.constants.BundleConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.extensions.fragments.SearchBarScopedFragment
import app.simple.inure.factories.panels.PackageInfoFactory
import app.simple.inure.viewmodels.viewers.SharedPreferencesViewModel

class SharedPreferences : SearchBarScopedFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_shared_prefs, container, false)

        recyclerView = view.findViewById(R.id.shared_prefs_recycler_view)

        val packageInfoFactory = PackageInfoFactory(packageInfo)
        sharedPreferencesViewModel = ViewModelProvider(this, packageInfoFactory)[SharedPreferencesViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()

        sharedPreferencesViewModel.getSharedPrefs().observe(viewLifecycleOwner) {
            val adapterResources = AdapterResources(it, "")
            recyclerView.adapter = adapterResources
        }
    }

    companion object {
        fun newInstance(packageInfo: PackageInfo): SharedPreferences {
            val args = Bundle()
            args.putParcelable(BundleConstants.packageInfo, packageInfo)
            val fragment = SharedPreferences()
            fragment.arguments = args
            return fragment
        }
    }
}