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

package com.forcetower.uefs.feature.messages.dynamic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.forcetower.uefs.R
import com.forcetower.core.injection.Injectable
import com.forcetower.uefs.core.vm.EventObserver
import com.forcetower.uefs.core.vm.UViewModelFactory
import com.forcetower.uefs.databinding.FragmentAeriMessagesBinding
import com.forcetower.uefs.feature.messages.MessagesDFMViewModel
import com.forcetower.uefs.feature.shared.UFragment
import com.forcetower.uefs.feature.shared.extensions.inTransaction
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import javax.inject.Inject

class AERIMessageFragment : UFragment(), Injectable {
    @Inject
    lateinit var factory: UViewModelFactory

    init {
        displayName = "AERI"
    }

    private val dynamicFeatureViewModel: MessagesDFMViewModel by activityViewModels { factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAeriMessagesBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dynamicFeatureViewModel.isAeriInstalled()) {
            moveToAeriNews()
        } else {
            replaceFragment(AERINotInstalledFragment(), "aeri_not_installed")
            dynamicFeatureViewModel.sessionState.observe(viewLifecycleOwner, EventObserver {
                when (it.status()) {
                    SplitInstallSessionStatus.INSTALLED -> moveToAeriNews()
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                        dynamicFeatureViewModel.requestUserConfirmation(it, requireActivity())
                    }
                    else -> Unit
                }
            })
        }
    }

    private fun moveToAeriNews() {
        val fragment = dynamicFeatureViewModel.aeriReflectInstance()
        replaceFragment(fragment, "aeri_news")
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        childFragmentManager.inTransaction {
            replace(R.id.dynamic_fragment_host, fragment, tag)
        }
    }
}