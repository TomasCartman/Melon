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

package com.forcetower.uefs.dashboard.feature

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.forcetower.uefs.core.storage.database.accessors.LocationWithGroup
import com.forcetower.uefs.dashboard.R
import java.util.Calendar

@SuppressLint("DefaultLocale")
@BindingAdapter("accountDashboardName")
fun accountDashboardName(tv: TextView, name: String?) {
    name ?: return
    val context = tv.context
    val firstName = name.split(" ")[0].capitalize()
    tv.text = context.getString(R.string.greetings_dashboard, firstName)
}

@BindingAdapter("disciplineStartsEnds")
fun disciplineStartsEnds(tv: TextView, location: LocationWithGroup?) {
    val start = location?.location?.startsAt ?: "????"
    val ends = location?.location?.endsAt ?: "????"
    tv.text = tv.context.getString(R.string.dash_schedule_separator, start, ends)
}

@SuppressLint("DefaultLocale")
@BindingAdapter("disciplineLocation")
fun disciplineLocation(tv: TextView, location: LocationWithGroup?) {
    val room = location?.location?.room?.toUpperCase()
    val module = location?.location?.modulo?.toLowerCase()?.capitalize()
    val campus = location?.location?.campus?.toUpperCase()

    val text = listOfNotNull(room, module, campus).joinToString(separator = " - ")
    tv.text = text
}

@BindingAdapter("disciplineStartDifference")
fun disciplineStartDifference(tv: TextView, location: LocationWithGroup?) {
    val context = tv.context
    val starts = location?.location?.startsAt
    if (starts != null) {
        try {
            val split = starts.split(":")
            val hour = split[0].toInt()
            val minute = split[1].toInt()
            val classMin = hour * 60 + minute

            val calendar = Calendar.getInstance()
            val nowMin = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

            val diff = (classMin - nowMin)
            if (diff <= 120) {
                tv.text = context.getString(R.string.dash_schedule_disc_starts_in, diff.toString())
            } else {
                tv.visibility = View.INVISIBLE
            }
            return
        } catch (t: Throwable) { }
    }
    tv.text = context.getString(R.string.dash_schedule_disc_starts_in, "???")
}