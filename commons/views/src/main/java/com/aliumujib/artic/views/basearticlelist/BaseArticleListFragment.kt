/*
 * Copyright 2020 Abdul-Mujeeb Aliu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aliumujib.artic.views.basearticlelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aliumujib.artic.views.basearticlelist.adapter.ArticleClickListener
import com.aliumujib.artic.views.basearticlelist.adapter.ArticleListAdapter
import com.aliumujib.artic.views.databinding.ArticleListFragmentBinding
import com.aliumujib.artic.views.ext.dpToPx
import com.aliumujib.artic.views.ext.hide
import com.aliumujib.artic.views.ext.removeAllDecorations
import com.aliumujib.artic.views.ext.show
import com.aliumujib.artic.views.models.ArticleUIModel
import com.aliumujib.artic.views.recyclerview.GridSpacingItemDecorator
import com.aliumujib.artic.views.recyclerview.ListSpacingItemDecorator
import com.aliumujib.artic.views.recyclerview.ListState
import com.aliumujib.artic.views.cleanup.autoDispose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
abstract class BaseArticleListFragment : Fragment(), ArticleClickListener {

    @Inject
    lateinit var articlesAdapter: ArticleListAdapter


    private var _binding: ArticleListFragmentBinding by autoDispose()
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ArticleListFragmentBinding.inflate(inflater, container, false)
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
    }


    private fun provideStaggeredGridLayoutManager(): StaggeredGridLayoutManager {
        return StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    }

    private fun provideListLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun initializeViews() {
        binding.articles.apply {
            removeAllDecorations()
            addItemDecoration(GridSpacingItemDecorator(2, context.dpToPx(16), true))
            layoutManager = provideStaggeredGridLayoutManager()
            adapter = articlesAdapter
        }
    }

    protected open fun setListMode(isGrid: Boolean) {
        if (isGrid && binding.articles.layoutManager !is LinearLayoutManager) {
            binding.articles.apply {
                removeAllDecorations()
                addItemDecoration(ListSpacingItemDecorator(context.dpToPx(32), context.dpToPx(16)))
                layoutManager = provideListLayoutManager()
            }
            articlesAdapter.setLayout(ArticleListAdapter.LAYOUT.LIST)
        } else if (isGrid.not() && binding.articles.layoutManager !is StaggeredGridLayoutManager) {
            binding.articles.apply {
                removeAllDecorations()
                addItemDecoration(GridSpacingItemDecorator(2, context.dpToPx(16), true))
                layoutManager = provideStaggeredGridLayoutManager()
            }
            articlesAdapter.setLayout(ArticleListAdapter.LAYOUT.GRID)
        }
    }

    protected open fun presentSuccessState(data: List<ArticleUIModel>, grid: Boolean) {
        setListMode(grid)
        articlesAdapter.setListState(ListState.Idle)
        binding.loading.hide()
        binding.swipeContainer.isRefreshing = false
        if (data.isNotEmpty()) {
            binding.emptyView.hide()
            binding.errorView.hide()
            binding.articles.show()
        } else {
            binding.emptyView.show()
            binding.errorView.hide()
            binding.articles.hide()
        }

        articlesAdapter.submitList(data)
    }

    protected open fun presentErrorState(
        error: Throwable,
        isLoadingMoreData: Boolean,
        isEmptyList: Boolean
    ) {
        binding.swipeContainer.isRefreshing = false
        binding.emptyView.hide()
        binding.loading.hide()
        if (isLoadingMoreData) {
            binding.articles.show()
            articlesAdapter.setListState(ListState.Error(error))
        } else if (isEmptyList && isLoadingMoreData.not()) {
            binding.articles.hide()
            binding.errorView.show()
        }
        error.message?.let {
            binding.errorView.setErrorViewText(it)
        }
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }

    protected open fun presentLoadingState(isLoadingMoreData: Boolean) {
        when {
            isLoadingMoreData -> {
                binding.articles.show()
                articlesAdapter.setListState(ListState.Loading)
            }
            else -> {
                binding.articles.hide()
                binding.loading.show()
            }
        }
        binding.emptyView.hide()
        binding.errorView.hide()
    }


    override fun onShareBtnClicked(articleUIModel: ArticleUIModel) {
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
            .setType("text/plain")
            .setText(articleUIModel.url)
            .intent
        if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(shareIntent)
        }
    }

    override fun onCommentBtnClicked(articleUIModel: ArticleUIModel) {

    }

    override fun onDestroy() {
//        _binding.articles.adapter = null
        super.onDestroy()
    }

}
