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

package dev.forcetower.event.core.injection

import com.forcetower.core.injection.annotation.FeatureScope
import com.forcetower.uefs.core.injection.AppComponent
import dagger.Component
import dev.forcetower.event.core.injection.module.ViewModelModule
import dev.forcetower.event.core.work.CreateEventWorker
import dev.forcetower.event.feature.create.CreateEventFragment
import dev.forcetower.event.feature.details.EventDetailsActivity
import dev.forcetower.event.feature.listing.EventFragment

@FeatureScope
@Component(
    modules = [
        ViewModelModule::class
    ],
    dependencies = [AppComponent::class]
)
interface EventComponent {
    fun inject(activity: EventDetailsActivity)
    fun inject(fragment: EventFragment)
    fun inject(fragment: CreateEventFragment)
    fun inject(worker: CreateEventWorker)
}