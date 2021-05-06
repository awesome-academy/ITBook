package com.example.itbook.ui.home

import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.itbook.R
import com.example.itbook.base.BaseFragment
import com.example.itbook.data.model.Book
import com.example.itbook.data.model.PreviewCategory
import com.example.itbook.data.repository.BooksRepository
import com.example.itbook.data.source.local.BooksLocalDataSource
import com.example.itbook.data.source.local.Database.BooksDatabase
import com.example.itbook.data.source.local.dao.BookDaoImp
import com.example.itbook.data.source.remote.BookRemoteHandler
import com.example.itbook.data.source.remote.BooksRemoteDataSource
import com.example.itbook.ui.adapter.CategoryAdapter
import com.example.itbook.ui.adapter.PreviewCategoryAdapter
import com.example.itbook.ui.detailbook.DetailBookFragment
import com.example.itbook.ui.detailcategory.DetailCategoryFragment
import com.example.itbook.ui.dialog.LoadingDialogFragment
import com.example.itbook.ui.search.SearchFragment
import com.example.itbook.utils.showError
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.preview_category.*
import org.json.JSONException

class HomeFragment : BaseFragment(), HomeContract.View {
    override val layoutResource = R.layout.fragment_home

    private var categories: List<String>? = null
    private var previewCategories = mutableListOf<PreviewCategory>()
    private var presenter: HomePresenter? = null
    private val categoryAdapter = CategoryAdapter(this::onItemCategoryClick)
    private val previewCategoryAdapter =
        PreviewCategoryAdapter(this::onItemPreviewCategoryClick, this::onItemBookClick)
    private var loadingDialogFragment: LoadingDialogFragment? = null

    override fun initViews() {
        recyclerViewCategory.adapter = categoryAdapter
        recyclerViewPreviewCategory.adapter = previewCategoryAdapter

        loadingDialogFragment = LoadingDialogFragment()
        loadingDialogFragment?.isCancelable = false
    }

    override fun initData() {
        var bookRepository: BooksRepository? = null
        categories = resources.getStringArray(R.array.categories).toList()
        activity?.let {
            bookRepository = BooksRepository.getInstance(
                BooksRemoteDataSource.getInstance(BookRemoteHandler()),
                BooksLocalDataSource.getInstance(BookDaoImp(BooksDatabase.getInstance(it)))
            )
        }

        categories?.let {
            bookRepository?.let { it1 ->
                presenter = HomePresenter(this, it1, it)
                showCategories(it)
            }
        }
        presenter?.start()
    }

    override fun initListeners() {
        textSearch.setOnClickListener { addFragment(SearchFragment()) }
    }

    override fun showCategories(categories: List<String>) {
        categories.let { categoryAdapter.updateData(it.toList()) }
    }

    override fun showPreviewCategories(previewCategories: List<PreviewCategory>) {
        previewCategoryAdapter.updateData(previewCategories)
        this.previewCategories = previewCategories.toMutableList()
    }

    override fun showError(error: Exception?) {
        var errorMessage = ""
        when (error) {
            is JSONException -> errorMessage = resources.getString(R.string.error_internet_not_connection)
        }
        isSetup = false
        activity?.showError(errorMessage)
    }

    override fun showLoading(isShow: Boolean) {
        loadingDialogFragment?.let {
            if (isShow) {
                activity?.let { it1 -> it.show(it1.supportFragmentManager, "") }
            } else {
                it.dismiss()
            }
        }
    }

    private fun onItemCategoryClick(position: Int) {
        categories?.let {
            val fragment = DetailCategoryFragment()
            fragment.arguments = bundleOf(Book.CATEGORY to it[position])
            addFragment(fragment)
        }
    }

    private fun onItemPreviewCategoryClick(position: Int) {
        val fragment = DetailCategoryFragment()
        fragment.arguments = bundleOf(
            Book.CATEGORY to previewCategories[position].name
        )
        addFragment(fragment)
    }

    private fun onItemBookClick(currentCategory: Int, position: Int) {
        val fragment = DetailBookFragment()
        fragment.arguments = bundleOf(
            Book.ISBN13 to previewCategories[currentCategory].books[position].isbn13
        )
        addFragment(fragment)
    }
}
