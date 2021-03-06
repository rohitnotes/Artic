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
package com.aliumujib.artic.articledetails.presentation

import com.aliumujib.artic.articledetails.presentation.ArticleDetailsResult.*
import com.aliumujib.artic.domain.usecases.articles.SetArticleBookmarkStatus
import com.aliumujib.artic.domain.usecases.articles.GetArticleDetails
import com.aliumujib.artic.domain.usecases.articles.SetArticleBookmarkStatus.Params.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class ArticleDetailActionProcessor @Inject constructor(
    private val setArticleBookmarkStatus: SetArticleBookmarkStatus,
    private val getArticleDetails: GetArticleDetails
) {

    fun actionToResultTransformer(action: ArticleDetailsAction): Flow<ArticleDetailsResult> {
        return when (action) {
            is ArticleDetailsAction.LoadArticleDetailsAction -> {
                loadArticleDetailsResult(flowOf(action))
            }
            is ArticleDetailsAction.RefreshArticleDetailsAction -> {
                pullToRefreshResult(flowOf(action))
            }
            is ArticleDetailsAction.BookmarkArticleAction -> {
                bookmarkArticleResult(flowOf(action))
            }
        }
    }


    private fun loadArticleDetailsResult(actionsFlow: Flow<ArticleDetailsAction.LoadArticleDetailsAction>): Flow<ArticleDetailsResult> {
        return actionsFlow.flatMapMerge { action ->
            getArticleDetails.build(GetArticleDetails.Params.make(action.article.id))
                .map { article ->
                    LoadArticleDetailsResult.LoadedComments(data = article) as ArticleDetailsResult
                }
                .onStart { emit(LoadArticleDetailsResult.LoadingComments(action.article)) }
                .catch {
                    Timber.e(it)
                    emit(LoadArticleDetailsResult.Error(it))
                }
        }
    }

    private fun pullToRefreshResult(actionsFlow: Flow<ArticleDetailsAction.RefreshArticleDetailsAction>): Flow<ArticleDetailsResult> =
        actionsFlow.flatMapMerge { action ->
            getArticleDetails.build(GetArticleDetails.Params.make(action.articleId))
                .map { article ->
                    LoadArticleDetailsResult.LoadedComments(data = article) as ArticleDetailsResult
                }
                .onStart { emit(RefreshArticleDetailsResult.Refreshing) }
                .catch {
                    Timber.e(it)
                    emit(RefreshArticleDetailsResult.Error(it))
                }
        }

    private fun bookmarkArticleResult(actionsFlow: Flow<ArticleDetailsAction.BookmarkArticleAction>): Flow<ArticleDetailsResult> {
        return actionsFlow.flatMapMerge { action ->
            flow {
                emit(setArticleBookmarkStatus.invoke(SetArticleBookmarkStatus.Params.make(action.article, action.bookmarked)))
            }.map {
                SetBookmarkStatusResult.Success(it!!) as ArticleDetailsResult //IF for any reason this is null, we have an error.
            }.catch {
                Timber.e(it)
                emit(SetBookmarkStatusResult.Error(it))
            }
        }
    }

}
