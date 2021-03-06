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
package com.aliumujib.artic.data.repositories.settings

import com.aliumujib.artic.data.repositories.settings.cache.ISettingsCache
import com.aliumujib.artic.domain.repositories.settings.ISettingsRepository
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(var settingsCache: ISettingsCache) :
    ISettingsRepository {

    override suspend fun setCurrentViewMode(isGrid: Boolean): Boolean {
        return settingsCache.setArticleListViewMode(isGrid)
    }

    override suspend fun getCurrentViewMode(): Boolean {
        return settingsCache.getArticleListViewMode()
    }

}
