/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2020. João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.forcetower.uefs.aeri.core.storage.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.forcetower.uefs.AppExecutors
import com.forcetower.uefs.aeri.R
import com.forcetower.uefs.aeri.core.model.Announcement
import com.forcetower.uefs.aeri.core.storage.database.AERIDatabase
import com.forcetower.core.interfaces.notification.NotifyMessage
import com.google.android.play.core.splitcompat.SplitCompat
import dev.forcetower.oversee.Oversee
import timber.log.Timber
import javax.inject.Inject

class AERIRepository @Inject constructor(
    context: Context,
    private val database: AERIDatabase,
    private val executors: AppExecutors
) {
    private val notificationTitle: String
    init {
        val success = SplitCompat.install(context)
        notificationTitle = if (!success) {
            Timber.e("Failed to install split compat")
            "AERI"
        } else {
            context.getString(R.string.aeri_notification_title)
        }
    }

    fun refreshNewsAsync(): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        executors.networkIO().execute {
            refreshNews()
            database.news().markAllNotified()
            result.postValue(true)
        }
        return result
    }

    @WorkerThread
    fun refreshNews() {
        Oversee.initialize()
        val news = Oversee.instance.getAERINews().reversed()
        database.news().insert(news)
    }

    fun getAnnouncements(): LiveData<PagedList<Announcement>> {
        return LivePagedListBuilder(database.news().getAnnouncementsPaged(), 20).build()
    }

    @WorkerThread
    fun update(): Int {
        refreshNews()
        return 0
    }

    @WorkerThread
    fun getNotifyMessages(): List<NotifyMessage> {
        val notify = database.news().getNewAnnouncements()
        database.news().markAllNotified()
        return notify.map { NotifyMessage(notificationTitle, it.title, it.imageUrl, it.link) }
    }
}