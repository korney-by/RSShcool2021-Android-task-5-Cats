package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.data.Cat
import com.korneysoft.rsshcool2021_android_task_5_cats.data.CatIndexed
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.interfaces.SaveImageInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.view.Gravity

private const val WRITE_PERMISSION_REQUEST_CODE = 21021

class MainActivity : AppCompatActivity(), SaveImageInterface {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO("NIGHT Theme")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerObserverStateOnline()
        registerObserverShowingDetailsCat()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        if (savedInstanceState == null) {
            loadCatListFragment()
        }
    }

    private fun registerObserverShowingDetailsCat() {
        viewModel.getShownCat().observe(this,
            Observer {
                it ?: return@Observer
                loadCatDetailsFragment(it)
                viewModel.setShowingCat(null)
            })
    }

    private fun registerObserverStateOnline() {
        viewModel.isOnline.observe(this,
            {
                if (!it) {
                    loadOfflineFragment()
                } else {
                    closeOfflineFragment()
                }
            })
    }

    private fun isFragmentVisible(tag: String): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        return (fragment != null && fragment.isVisible())
    }

    private fun loadCatListFragment() {
        if (isFragmentVisible(CatListFragment::class.java.simpleName)) return
        val fragment: Fragment =
            CatListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment, CatListFragment::class.java.simpleName)
            .commit()
    }

    private fun loadOfflineFragment() {
        if (isFragmentVisible(OfflineFragment::class.java.simpleName)) return
        val fragment: Fragment =
            OfflineFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView, fragment, OfflineFragment::class.java.simpleName)
            .addToBackStack(OfflineFragment::class.java.simpleName)
            .commit()
    }

    private fun closeOfflineFragment() {
        supportFragmentManager.popBackStack(
            OfflineFragment::class.java.simpleName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun loadCatDetailsFragment(catIndexed: CatIndexed) {
        viewModel.lastShowingCat = catIndexed
        val fragment =
            supportFragmentManager.findFragmentByTag(CatListFragment::class.java.simpleName)
        if (fragment != null && fragment.isVisible) {
            val sourceFragment = fragment as CatListFragment
            val destinationFragment: Fragment = CatDetailsFragment.newInstance()
            val view = sourceFragment.getSelectedView() ?: return

            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(view, view.transitionName)
                .replace(
                    R.id.fragmentContainerView,
                    destinationFragment,
                    CatDetailsFragment::class.java.simpleName
                )
                .addToBackStack(CatDetailsFragment::class.java.simpleName)
                .commit()

        }
    }

    private fun getSavePermission(): Boolean {
        // return true if permission granted successfully
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                requestPermissions(
                    arrayOf(WRITE_EXTERNAL_STORAGE),
                    WRITE_PERMISSION_REQUEST_CODE
                )
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_write_granted),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_write_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadFiles.containsKey(id)) {
                validDownload(this@MainActivity, id)
            }
        }
    }

    @SuppressLint("Range")
    private fun validDownload(context: Context, downloadId: Long) {
        val dm = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val fileName = downloadFiles.get(downloadId)
        dm.query(DownloadManager.Query().setFilterById(downloadId))?.use { cursor ->
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                var message=""
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    message = getString(R.string.download_successfull, fileName)
                } else if (status == DownloadManager.STATUS_FAILED) {
                    message = getString(R.string.download_failed, fileName)
                }
                if (message.length>0) {
                    downloadFiles.remove(downloadId)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun saveImage(cat: Cat?) {
        if (!getSavePermission()) return

        cat?.imageUrl?.let { url ->
            val filename = url.substringAfterLast("/")
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(filename)
                .setDescription("Download: $filename")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                request.allowScanningByMediaScanner()
            }

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = manager.enqueue(request)
            downloadFiles.put(downloadId, filename)
        }
    }

    companion object {
        val downloadFiles = mutableMapOf<Long, String>()
    }


}
