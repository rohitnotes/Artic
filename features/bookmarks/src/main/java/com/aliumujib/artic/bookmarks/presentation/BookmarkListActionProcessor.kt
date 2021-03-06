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
package com.aliumujib.artic.bookmarks.presentation

import com.aliumujib.artic.bookmarks.presentation.BookmarkListResult.*
import com.aliumujib.artic.domain.models.Article
import com.aliumujib.artic.domain.usecases.articles.GetAllBookmarkedArticles
import com.aliumujib.artic.domain.usecases.articles.SetArticleBookmarkStatus
import com.aliumujib.artic.domain.usecases.settings.FetchViewModeSettings
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class BookmarkListActionProcessor @Inject constructor(
    private val fetchBookmarks: GetAllBookmarkedArticles,
    private val setArticleBookmarkStatus: SetArticleBookmarkStatus,
    private val fetchViewModeSettings: FetchViewModeSettings
) {

    fun actionToResultTransformer(action: BookmarkListAction): Flow<BookmarkListResult> {
        return when (action) {
            is BookmarkListAction.StreamBookmarksAction -> {
                loadArticleListResult(flowOf(action))
            }
            is BookmarkListAction.RemoveBookmarkAction -> {
                bookmarkArticleResult(flowOf(action))
            }
        }
    }

    private fun loadArticleListResult(actionsFlow: Flow<BookmarkListAction.StreamBookmarksAction>): Flow<BookmarkListResult> {
        return actionsFlow.flatMapMerge { action ->
            fetchBookmarks.build()
                .combine(flow { emit(fetchViewModeSettings.invoke()) })
                { list: List<Article>, isGrid: Boolean ->
                    StreamBookmarksResults.Success(list, isGrid)
                }.map { result ->
                    result as BookmarkListResult
                }
                .onStart { emit(StreamBookmarksResults.Loading) }
                .catch {
                    Timber.e(it)
                    emit(StreamBookmarksResults.Error(it))
                }
        }
    }


    private fun bookmarkArticleResult(actionsFlow: Flow<BookmarkListAction.RemoveBookmarkAction>): Flow<BookmarkListResult> {
        return actionsFlow.flatMapMerge { action ->
            flow {
                emit(setArticleBookmarkStatus.invoke(SetArticleBookmarkStatus.Params.make(action.article, action.isCurrentlyBookmarked)))
            }.map {
                RemoveBookmarkResults.Success(it!!) as BookmarkListResult
            }.catch {
                Timber.e(it)
                emit(RemoveBookmarkResults.Error(it))
            }
        }
    }

}
