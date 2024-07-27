package app.simple.inure.dialogs.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.simple.inure.R
import app.simple.inure.adapters.search.AdapterSearchKeywordDatabase
import app.simple.inure.constants.BundleConstants
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.extensions.fragments.ScopedBottomSheetFragment
import app.simple.inure.viewmodels.search.SearchKeywordDatabaseViewModel

class SearchKeywordDatabase : ScopedBottomSheetFragment() {

    private lateinit var recyclerView: CustomVerticalRecyclerView
    private lateinit var searchKeywordDatabaseViewModel: SearchKeywordDatabaseViewModel
    private lateinit var searchKeywordDatabaseCallback: SearchKeywordDatabaseCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_search_keyword_database, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchKeywordDatabaseViewModel = ViewModelProvider(this)[SearchKeywordDatabaseViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (requireArguments().getInt(BundleConstants.mode)) {
            PERMISSIONS -> {
                searchKeywordDatabaseViewModel.getPermissions().observe(viewLifecycleOwner) { strings ->
                    recyclerView.adapter = AdapterSearchKeywordDatabase(strings) {
                        searchKeywordDatabaseCallback.onSearchKeywordDatabaseClicked(it)
                        dismiss()
                    }
                }
            }
            TRACKERS -> {
                searchKeywordDatabaseViewModel.getTrackers().observe(viewLifecycleOwner) { strings ->
                    recyclerView.adapter = AdapterSearchKeywordDatabase(strings) {
                        searchKeywordDatabaseCallback.onSearchKeywordDatabaseClicked(it)
                        dismiss()
                    }
                }
            }
        }
    }

    fun setSearchKeywordDatabaseCallback(searchKeywordDatabaseCallback: SearchKeywordDatabaseCallback) {
        this.searchKeywordDatabaseCallback = searchKeywordDatabaseCallback
    }

    companion object {
        fun newInstance(mode: Int): SearchKeywordDatabase {
            val args = Bundle()
            args.putInt(BundleConstants.mode, mode)
            val fragment = SearchKeywordDatabase()
            fragment.arguments = args
            return fragment
        }

        fun Fragment.showSearchKeywordDatabase(mode: Int): SearchKeywordDatabase {
            val searchKeywordDatabase = newInstance(mode)
            searchKeywordDatabase.show(parentFragmentManager, searchKeywordDatabase.tag)
            return searchKeywordDatabase
        }

        interface SearchKeywordDatabaseCallback {
            fun onSearchKeywordDatabaseClicked(keyword: String)
        }

        const val PERMISSIONS = 0
        const val TRACKERS = 1
        const val TAG = "SearchKeywordDatabase"
    }
}
