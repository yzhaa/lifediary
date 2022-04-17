package com.yzh.permissonx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


/**
 * Basic interfaces for developers to use PermissionX functions.
 * @author guolin
 * @since 2019/11/2
 */
object PermissionX {

    private const val TAG = "InvisibleFragment"

    fun request(fragment: Fragment, vararg permissions: String, callback: PermissionCallback) {
        val fragmentManager = fragment.activity?.supportFragmentManager
        val existedFragment = fragmentManager?.findFragmentByTag(TAG)
        val invisibleFragment = if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager?.beginTransaction()?.add(invisibleFragment, TAG)?.commitNow()
            invisibleFragment
        }
        invisibleFragment.requestNow(callback, *permissions)
    }

    fun request(activity: FragmentActivity, vararg permissions: String, callback: PermissionCallback) {
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        val fragment = if (existedFragment != null) {
            existedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.requestNow(callback, *permissions)
    }

}