package com.pratama.dany.keyaccess

import android.app.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageFragmentAdapter(fragmentManager: FragmentManager,
                          val idHome: String, val homeName: String)
    : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {

        return when(position) {

            0 -> {
                ActivityFragment(idHome, homeName)
            }

            1 -> {
                NotificationFragment(idHome)
            }

            else -> {
                AlarmFragment(idHome)
            }

        }

    }

    override fun getCount(): Int {

        return 3

    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position) {

            0 -> {
                "Activity"
            }

            1 -> {
                "Device Info"
            }

            else -> {
                "Alarm"
            }

        }

    }

}