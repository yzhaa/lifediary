package com.yzh.permissonx

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Invisible fragment to embedded into activity for handling permission requests.
 * @author guolin
 * @since 2019/11/2
 */

typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment : Fragment() {

    private var callback: PermissionCallback? = null

    fun requestNow(cb: PermissionCallback, vararg permissions: String) {
        callback = cb
        val list = LinkedList<String>()
        for (i in permissions) {
            if (activity?.let { ContextCompat.checkSelfPermission(it.application, i) } != PackageManager.PERMISSION_GRANTED) {
                list.add(i)
            }
        }
        if (list.size > 0) {
            val realP: Array<String> = list.toArray(arrayOfNulls<String>(list.size))
            requestPermissions(realP, 1)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted, deniedList) }
        }
    }

}