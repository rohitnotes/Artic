package com.aliumujib.artic.data

import com.aliumujib.artic.data.model.ArticleEntity
import com.aliumujib.artic.data.model.AuthorEntity
import com.aliumujib.artic.data.model.CategoryEntity
import com.aliumujib.artic.data.model.CommentEntity
import com.aliumujib.artic.domain.models.Article
import com.aliumujib.artic.domain.models.Author
import com.aliumujib.artic.domain.models.Comment
import konveyor.base.randomBuild

object DummyDataFactory {

    fun makeRandomArticleEntity(): ArticleEntity {
        return ArticleEntity(
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            mutableListOf(),
            randomBuild(),
            randomBuild()
        )
    }

    fun makeRandomCommentEntity(): CommentEntity {
        return randomBuild()
    }

    fun makeRandomAuthorEntity(): AuthorEntity {
        return randomBuild()
    }

    fun makeRandomAuthor(): Author {
        return randomBuild()
    }


    fun makeRandomComment(): Comment {
        return randomBuild()
    }


    fun makeRandomCategoryEntity(): CategoryEntity {
        return randomBuild()
    }

    fun makeArticlesEntitiesList(count: Int): List<ArticleEntity> {
        val articles = mutableListOf<ArticleEntity>()
        repeat(count) {
            articles.add(makeRandomArticleEntity())
        }
        return articles
    }

    fun makeRandomArticle(): Article {
        return Article(
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            randomBuild(),
            mutableListOf(),
            randomBuild(),
            randomBuild()
        )
    }

    fun makeRandomCategoryEntityList(count: Int): List<CategoryEntity> {
        val mutableList = mutableListOf<CategoryEntity>()
        repeat(count) {
            mutableList.add(makeRandomCategoryEntity())
        }
        return mutableList
    }
}