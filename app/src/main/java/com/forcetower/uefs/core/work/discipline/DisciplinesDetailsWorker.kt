/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2019.  João Paulo Sena <joaopaulo761@gmail.com>
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

package com.forcetower.uefs.core.work.discipline

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.forcetower.uefs.UApplication
import com.forcetower.uefs.core.storage.repository.DisciplineDetailsRepository
import com.forcetower.uefs.core.work.enqueueUnique
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DisciplinesDetailsWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @Inject
    lateinit var repository: DisciplineDetailsRepository

    @WorkerThread
    override fun doWork(): Result {
        (applicationContext as UApplication).component.inject(this)
        return try {
            repository.experimentalDisciplines(partialLoad = true, notify = false)
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "discipline_details_worker"
        private const val NAME = "discipline_details"

        fun createWorker(context: Context) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val request = OneTimeWorkRequestBuilder<DisciplinesDetailsWorker>()
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .addTag(TAG)
                    .build()

            request.enqueueUnique(context, NAME, true)
        }
    }
}
