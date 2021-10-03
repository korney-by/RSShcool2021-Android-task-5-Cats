package com.korneysoft.rsshcool2021_android_task_5_cats.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.transition.TransitionSet
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.korneysoft.rsshcool2021_android_task_5_cats.R
import com.korneysoft.rsshcool2021_android_task_5_cats.databinding.ActivityMainBinding
import com.korneysoft.rsshcool2021_android_task_5_cats.interfaces.SaveImageInterface
import com.korneysoft.rsshcool2021_android_task_5_cats.viewmodel.CatViewModel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.korneysoft.rsshcool2021_android_task_5_cats.data.retrofit.Cat
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WRITE_EXTERNAL_STORAGE


private const val WRITE_PERMISSION_REQUEST_CODE = 21021

class MainActivity : AppCompatActivity(), SetNavigationBarColor, SaveImageInterface {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO("NIGHT Theme")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerObserverStateOnline()
        registerObserverShowingCat()
        if (savedInstanceState == null) {
            loadCatListFragment()
        }
    }

    private fun registerObserverShowingCat() {
        viewModel.getPositionShowingCat().observe(this,
            Observer {
                it ?: return@Observer
                loadCatDetailsFragment(it, viewModel.getGridFragment)
                viewModel.setShowingCat(null) { null }
            })
    }

    private fun registerObserverStateOnline() {
        viewModel.isOnline.observe(this,
            Observer {
                if (!it) {
                    loadOfflineFragment()
                } else {
                    closeOfflineFragment()
                }
            })
    }

    private fun isCatDetailsFragmentHide(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.cat_details_fragment) == null)
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

    private fun isFragmentVisible(tag: String): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        return (fragment != null && fragment.isVisible())
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

    private fun loadCatDetailsFragment(position: Int, sourceFragment: CatListFragment?) {
        sourceFragment ?: return
        viewModel.lastShowingCat = position
        val destinationFragment: Fragment = CatDetailsFragment.newInstance()

        sourceFragment.getSelectedView()?.let { view ->
            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            sourceFragment.exitTransition?.let { transition ->
                if (transition is TransitionSet) transition.excludeTarget(view, true)
            }
            sourceFragment.parentFragmentManager
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

    override fun setNavigationBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
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
        permissions: Array<String>, grantResults: IntArray
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

    override fun saveImage(cat: Cat?) {
        if (!getSavePermission()) return

        cat?.imageUrl?.let { url ->
            val filename = url.substringAfterLast("/")
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(filename)
                .setDescription("Download: $filename")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                request.allowScanningByMediaScanner()
            }

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = manager.enqueue(request)

//            val broadcastReceiver = object : BroadcastReceiver() {
//                override fun onReceive(context: Context?, intent: Intent?) {
//                    intent ?: return
//                    val id: Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//                    if (id == downloadId) {
//                        Toast.makeText(
//                            this@CatDetailsFragment.context?.applicationContext,
//                            "$filename download completed",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }
        }
    }
}
