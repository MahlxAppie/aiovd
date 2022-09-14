@file:Suppress("DEPRECATION")

package com.infusiblecoder.allinonevideodownloader.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.*
import com.github.javiersantos.piracychecker.PiracyChecker
import com.github.javiersantos.piracychecker.callbacks.PiracyCheckerCallback
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import com.google.android.gms.ads.MobileAds
import com.infusiblecoder.allinonevideodownloader.BuildConfig
import com.infusiblecoder.allinonevideodownloader.R
import com.infusiblecoder.allinonevideodownloader.databinding.ActivityMainBinding
import com.infusiblecoder.allinonevideodownloader.extraFeatures.ExtraFeaturesFragment
import com.infusiblecoder.allinonevideodownloader.fragments.DownloadMainFragment
import com.infusiblecoder.allinonevideodownloader.inappbilling.BillingClientSetup
import com.infusiblecoder.allinonevideodownloader.snapchatstorysaver.SnapChatBulkStoryDownloader
import com.infusiblecoder.allinonevideodownloader.statussaver.StatusSaverMainFragOld
import com.infusiblecoder.allinonevideodownloader.statussaver.StatusSaverMainFragment
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager
import com.infusiblecoder.allinonevideodownloader.utils.Constants
import com.infusiblecoder.allinonevideodownloader.utils.LocaleHelper
import com.infusiblecoder.allinonevideodownloader.utils.iUtils
import com.infusiblecoder.allinonevideodownloader.webservices.DownloadVideosMain
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import java.util.concurrent.Executor
import kotlin.system.exitProcess


@Keep
class MainActivity : AppCompatActivity() {

    private var myString: String? = ""
    private val REQUEST_PERMISSION_CODE = 1001
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    private val REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    private lateinit var progressDralogGenaratinglink: ProgressDialog

    private val APP_UPDATE_REQUEST_CODE = 261

    private var billingClient: BillingClient? = null
    private var skuDetails: SkuDetails? = null

    var fragment: Fragment? = null
    private lateinit var binding: ActivityMainBinding
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {


            binding = ActivityMainBinding.inflate(layoutInflater)
            val view = binding.root
            view.keepScreenOn = true
            setContentView(view)

        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {


//            if (!isNeedGrantPermission()) {
//                Log.d("isNeedGrantPermission", " workk")
//                setLayout()
//            }

            // Sets up permissions request launcher.
            requestPermissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    setLayout()
                    if (SDK_INT < 33) {
                        restartApp()
                    }

                }

            if (requiredPermissions.all { isPermissionGranted(it) }) {
                setLayout()
            } else {
                requestPermissionsLauncher.launch(requiredPermissions)
            }

            if (SDK_INT >= 33) {

                val prefs = getSharedPreferences(
                    "whatsapp_pref",
                    MODE_PRIVATE
                )
                val android13permissions = prefs.getBoolean("android13permissions", false)
                if (!android13permissions) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.yourdeviceisandroid13))
                    builder.setCancelable(false)
                    builder.setPositiveButton(R.string.open) { _, _ ->

                        val sharedPreference = getSharedPreferences("whatsapp_pref", MODE_PRIVATE)
                        val editor = sharedPreference.edit()
                        editor.putBoolean("android13permissions", true)
                        editor.apply()

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                    builder.show()
                }
            }


            progressDralogGenaratinglink = ProgressDialog(this@MainActivity)
            progressDralogGenaratinglink.setMessage(resources.getString(R.string.genarating_download_link))
            progressDralogGenaratinglink.setCancelable(false)

            val action = intent.action
            val type = intent.type

            if (Intent.ACTION_SEND == action && type != null) {
                if ("text/plain" == type) {
                    handleSendText(intent) // Handle text being sent
                }
            }

            AppRating.Builder(this)
                .setMinimumLaunchTimes(5)
                .setMinimumDays(5)
                .setMinimumLaunchTimesToShowAgain(5)
                .setMinimumDaysToShowAgain(10)
                .setRatingThreshold(RatingThreshold.FOUR)
                .showIfMeetsConditions()



            MobileAds.initialize(
                this@MainActivity
            ) { initializationStatus ->
                val statusMap = initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d(
                        "MyApp",
                        String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass,
                            status!!.description,
                            status.latency
                        )
                    )
                }
                if (Constants.show_Ads) {
                    val prefs: SharedPreferences =
                        getSharedPreferences("whatsapp_pref", Context.MODE_PRIVATE)
                    val pp =
                        prefs.getString("inappads", "nnn") //"No name defined" is the default value.
                    if (pp.equals("nnn")) {
                        AdsManager.loadBannerAdsAdapter(this@MainActivity, binding.bannerContainer)
                    } else {
                        binding.bannerContainer.visibility = View.GONE
                    }
                }
            }



            PiracyChecker(this@MainActivity)
                .enableGooglePlayLicensing(getString(R.string.GOOGLE_PLAY_App_Liscencekey))
                .enableUnauthorizedAppsCheck()
                .saveResultToSharedPreferences("allvideoprefs", "allvideoprefs_valid_license")
                .callback(object : PiracyCheckerCallback() {
                    override fun allow() {}
                    override fun doNotAllow(error: PiracyCheckerError, @Nullable app: PirateApp?) {

                    }

                    override fun onError(error: PiracyCheckerError) {}
                })
                .start()



            if (!BuildConfig.DEBUG) {
                verifyAppInstall(iUtils.verifyInstallerId(this@MainActivity))
                // iUtils.showCookiesLL(this@MainActivity);
            }
            //iUtils.showCookiesLL(this@MainActivity);
            try {

                val biometricManager: BiometricManager = BiometricManager.from(this@MainActivity)
                when {
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS -> {
                        iUtils.isBioAvaliable = true
                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        iUtils.isBioAvaliable = false
                        Toast.makeText(
                            this@MainActivity,
                            "This device does not have a fingerprint sensor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Toast.makeText(
                            this@MainActivity,
                            "The biometric sensor is currently unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                        iUtils.isBioAvaliable = false
                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Your device doesn't have fingerprint saved,please check your security settings",
                            Toast.LENGTH_SHORT
                        ).show()
                        iUtils.isBioAvaliable = false
                    }
                }


                val executor: Executor = ContextCompat.getMainExecutor(this@MainActivity)
                biometricPrompt =
                    BiometricPrompt(
                        this@MainActivity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)

                                Toast.makeText(
                                    this@MainActivity,
                                    "Error in Authentication $errString",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        GalleryActivity::class.java
                                    )
                                )
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                            }
                        })

                promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Verify Identity")
                    .setDescription("Use your fingerprint or device credentials to Access Gallery ")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    // .setNegativeButtonText("Cancel")
                    .build()


            } catch (e: Throwable) {
                e.printStackTrace()
            }


            initIInAppBillingAcknologement()

            binding.subscriptionFab.setOnClickListener {
                try {

                    if (iUtils.isSubactive) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.youhavealready) + "",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(Intent(this@MainActivity, SubscriptionActivity::class.java))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }


    fun getMyData(): String? {
        return myString
    }


    fun setmydata(mysa: String) {
        this.myString = mysa
    }

    private fun verifyAppInstall(isinatll: Boolean) {
        try {
            println("myappisvalid $isinatll")
            if (!isinatll) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.please_install))
                    .setCancelable(false)
                    .setMessage(
                        getString(R.string.isappfromplaystore)
                    )
                    .setPositiveButton(
                        resources.getString(R.string.installapp)
                    )
                    { dialog, _ ->
                        dialog.dismiss()
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                        )

                    }
                    .setNegativeButton(
                        resources.getString(R.string.useanyway)
                    )
                    { dialog, _ ->
                        dialog.dismiss()

                    }
                    .setIcon(R.drawable.ic_appicon)
                    .show()
            }
        } catch (e: java.lang.Exception) {

            println("appupdater error rrrr $e")
            e.printStackTrace()
        }
    }


    private fun handleSendText(intent: Intent) {
        try {
            this.intent = null
            var url = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (url.equals("") && iUtils.checkURL(url)) {
                iUtils.ShowToast(
                    this@MainActivity,
                    this@MainActivity.resources?.getString(R.string.enter_valid)
                )
                return
            }

            if (url?.contains("myjosh.in")!!) {
                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12 myjosh ", url.trim())
                }

            } else if (url.contains("chingari")) {
                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12 chingari ", url.trim())
                }
            } else if (url.contains("story.snapchat.com")) {
                try {
                    url = iUtils.extractUrls(url)[0]
                    val i = Intent(
                        this@MainActivity,
                        SnapChatBulkStoryDownloader::class.java
                    )
                    i.putExtra("urlsnap", url?.trim())
                    startActivity(i)
                } catch (e: Exception) {

                }

                if (url != null) {
                    Log.e("downloadFileName12 urlsnap ", url.trim())
                }
            } else if (url.contains("bemate")) {

                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12 bemate ", url.trim())
                }
            } else if (url.contains("instagram.com")) {


                val bundle = Bundle()
                bundle.putString("myinstaurl", url)
                val fragobj = DownloadMainFragment()
                fragobj.arguments = bundle
                this.setmydata(url)

                Log.e("downloadFileName12 insta ", url)
            } else if (url.contains("sck.io") || url.contains("snackvideo")) {
                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12 snack ", url.trim())
                }
            } else {
                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }

                val myurl = url
                val bundle = Bundle()
                bundle.putString("myinstaurl", myurl)
                val fragobj = DownloadMainFragment()
                fragobj.arguments = bundle

                this.setmydata(myurl!!)


            }
        } catch (e: Exception) {

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        try {
            Log.e(
                "myhdasbdhf newintent ",
                intent?.getStringExtra(Intent.EXTRA_TEXT) + "_46237478234"
            )
            intent?.let { newIntent ->
                //  handleSendText(newIntent)
                if (fragment is DownloadMainFragment) {
                    Log.e("myhdasbdhf downlaod ", newIntent.getStringExtra(Intent.EXTRA_TEXT) + "")

                    val my: DownloadMainFragment? = fragment as DownloadMainFragment?
                    // Pass intent or its data to the fragment's method

                    my?.requireView()?.findViewById<EditText>(R.id.etURL)
                        ?.setText(newIntent.getStringExtra(Intent.EXTRA_TEXT).toString())
                    my?.DownloadVideo(newIntent.getStringExtra(Intent.EXTRA_TEXT).toString())
                } else {
                    handleSendText(newIntent)
                    Log.e(
                        "myhdasbdhf notdownload ",
                        newIntent.getStringExtra(Intent.EXTRA_TEXT) + ""
                    )

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLayout() {
        try {
            Log.d("isNeedGrantPermission", " layout")

            setupViewPager(binding.viewpager)

            binding.bottomNavBar.onItemSelected = {

                when (it) {
                    0 -> binding.viewpager.currentItem = 0

                    1 -> binding.viewpager.currentItem = 1

                    2 -> binding.viewpager.currentItem = 2
                }

            }



            binding.viewpager.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    binding.bottomNavBar.itemActiveIndex = position

                }

                override fun onPageScrollStateChanged(state: Int) {}
            })


        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    private fun setupViewPager(viewPager: ViewPager) {
        try {
            val adapter = ViewPagerAdapter(supportFragmentManager)
            adapter.addFragment(DownloadMainFragment(), getString(R.string.download_fragment))
            // adapter.addFragment(DummyFragment(), getString(R.string.gallery_fragment))
            if (SDK_INT >= 29) {
                adapter.addFragment(StatusSaverMainFragment(), getString(R.string.StatusSaver))
            } else {
                adapter.addFragment(StatusSaverMainFragOld(), getString(R.string.StatusSaver))
            }
            adapter.addFragment(ExtraFeaturesFragment(), getString(R.string.extrafeatures))
            viewPager.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun isNeedGrantPermission(): Boolean {
        try {
            if (iUtils.hasMarsallow()) {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        REQUEST_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivity,
                            REQUEST_PERMISSION
                        )
                    ) {
                        val msg =
                            String.format(
                                getString(R.string.format_request_permision),
                                getString(R.string.app_name)
                            )

                        val localBuilder = AlertDialog.Builder(this@MainActivity)
                        localBuilder.setTitle(getString(R.string.permission_title))
                        localBuilder
                            .setMessage(msg).setNeutralButton(
                                getString(R.string.grant_option)
                            ) { _, _ ->
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(REQUEST_PERMISSION),
                                    REQUEST_PERMISSION_CODE
                                )
                            }
                            .setNegativeButton(
                                getString(R.string.cancel_option)
                            ) { paramAnonymousDialogInterface, _ ->
                                paramAnonymousDialogInterface.dismiss()
                                finish()
                            }
                        localBuilder.show()

                    } else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(REQUEST_PERMISSION),
                            REQUEST_PERMISSION_CODE
                        )
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setLayout()
                    restartApp()
                } else {
                    iUtils.ShowToast(
                        this@MainActivity,
                        getString(R.string.info_permission_denied)
                    )
                    //TODO
                    //finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))
            finish()
        }

    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val prefs: SharedPreferences = getSharedPreferences(
            "whatsapp_pref",
            Context.MODE_PRIVATE
        )
        val pp = prefs.getString("inappads", "nnn") //"No name defined" is the default value.

        if (pp.equals("nnn")) {

            menu.findItem(R.id.action_removeads).isVisible = true

        } else if (pp.equals("ppp")) {

            menu.findItem(R.id.action_removeads).isVisible = false

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_privacy -> {
                try {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.privacy_policy_url))
                        )
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                true
            }

            R.id.action_downloadtiktok -> {
                val intent = Intent(this@MainActivity, TikTokDownloadWebview::class.java)
                startActivity(intent)
                true
            }

            R.id.action_rate -> {
                try {
                    if (!isFinishing) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(getString(R.string.RateAppTitle))
                            .setMessage(getString(R.string.RateApp))
                            .setCancelable(false)
                            .setPositiveButton(
                                getString(R.string.rate_dialog)
                            ) { _, _ ->
                                val appPackageName = packageName
                                try {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=$appPackageName")
                                        )
                                    )
                                } catch (anfe: android.content.ActivityNotFoundException) {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                        )
                                    )
                                }
                            }
                            .setNegativeButton(getString(R.string.later_btn), null).show()
                    }
                } catch (e: Exception) {
                }
                true
            }

            R.id.action_share -> {


                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                    var shareMessage = """Hi, Try This Amazing App

Download Any Social Media Videos ,Short Videos Without Watermark Free & Unlimited Times! 😍🎶

Tik Tok
Likee
Vigo Video
Facebook
Instagram
IG Tv
Pinterest
Twitter
Tik Tok Sound
WhatsApp Status.. & Much More ❤🤩

App Link:  """
                    shareMessage =
                        """
                        ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                        
                        
                        """.trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }


                true
            }


            R.id.ic_whatapp -> {
                val launchIntent = packageManager.getLaunchIntentForPackage("com.whatsapp")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                    //  finish()
                } else {
                    iUtils.ShowToast(
                        this@MainActivity,
                        this.resources.getString(R.string.appnotinstalled)
                    )
                }
                true
            }

            R.id.action_language -> {
                if (!isFinishing) {
                    val dialog = Dialog(this@MainActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.dialog_change_language)

                    val l_english = dialog.findViewById(R.id.l_english) as TextView
                    l_english.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "en")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_french = dialog.findViewById(R.id.l_french) as TextView
                    l_french.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "fr")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_arabic = dialog.findViewById(R.id.l_arabic) as TextView
                    l_arabic.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_LTR, "ar")
                        recreate()
                        dialog.dismiss()

                    }
                    val l_urdu = dialog.findViewById(R.id.l_urdu) as TextView
                    l_urdu.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_LTR, "ur")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_german = dialog.findViewById(R.id.l_german) as TextView
                    l_german.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "de")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_turkey = dialog.findViewById(R.id.l_turkey) as TextView
                    l_turkey.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "tr")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_portougese = dialog.findViewById(R.id.l_portougese) as TextView
                    l_portougese.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "pt")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_chinese = dialog.findViewById(R.id.l_chinese) as TextView
                    l_chinese.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "zh")
                        recreate()
                        dialog.dismiss()
                    }


                    val l_hindi = dialog.findViewById(R.id.l_hindi) as TextView
                    l_hindi.setOnClickListener {
                        changeLanguage(application, View.LAYOUT_DIRECTION_RTL, "hi")
                        recreate()
                        dialog.dismiss()
                    }

                    dialog.show()
                }
                true
            }

            R.id.action_removeads -> {
                try {


                    if (iUtils.isSubactive) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.youhavealready) + "",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(Intent(this@MainActivity, SubscriptionActivity::class.java))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            }

            R.id.action_opengallery -> {
                try {

                    if (iUtils.isBioAvaliable && iUtils.getIsBioLoginEnabled(this@MainActivity)) {
                        biometricPrompt!!.authenticate(promptInfo!!)
                    } else {
                        startActivity(Intent(this@MainActivity, GalleryActivity::class.java))
                    }

                } catch (e: Throwable) {
                    e.printStackTrace()
                    startActivity(Intent(this@MainActivity, GalleryActivity::class.java))

                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeLanguage(context: Application?, direction: Int, language: String) {
        LocaleHelper.setLocale(context, language)
        window.decorView.layoutDirection = direction
        val editor: SharedPreferences.Editor = getSharedPreferences(
            "lang_pref",
            Context.MODE_PRIVATE
        ).edit()
        editor.putString("lang", language)
        editor.apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!isFinishing) {
            val dialog = Dialog(this@MainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog_ad_exit)
            val yesBtn = dialog.findViewById(R.id.btn_exitdialog_yes) as Button
            val noBtn = dialog.findViewById(R.id.btn_exitdialog_no) as Button
            yesBtn.setOnClickListener {

                try {
                    finishAndRemoveTask()
                    exitProcess(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("proddddd11111222 $resultCode __$data")
        if (requestCode == 200 && resultCode == RESULT_OK) {
            println("proddddd11111 $resultCode __$data")
        }
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    this,
                    getString(R.string.updatefailed),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }


        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (isPermissionGranted(REQUEST_PERMISSION)) {

                    setLayout()
                    restartApp()
                } else {
                    iUtils.ShowToast(
                        this@MainActivity,
                        getString(R.string.info_permission_denied)
                    )
                    //TODO
                    //finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))
            finish()
        }


    }


    private fun initIInAppBillingAcknologement() {
        println("mypurchase12 2 = ")
        billingClient = BillingClientSetup.getInstance(this@MainActivity) { billingResult, list ->
            if (list != null) {
                for (purchase in list) handleitemAlreadyPuchase(purchase)
            }
        }
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                println("mypurchase 2 = ")
                loadAllSubscribePackage()
                billingClient!!.queryPurchasesAsync(
                    BillingClient.SkuType.SUBS
                ) { billingResult1: BillingResult, purchases: List<Purchase> ->
                    println("mypurchase 6.5 = ")
                    if (purchases.size > 0) {
                        iUtils.isSubactive = true
                        binding.subscriptionFab.setVisibility(View.GONE)
                        for (purchase in purchases) handleitemAlreadyPuchase(purchase)
                    } else {
                        iUtils.isSubactive = false
                        binding.subscriptionFab.setVisibility(View.VISIBLE)
                        println("mypurchase 4 = " + billingResult1.responseCode)
                        val prefs = getSharedPreferences(
                            "whatsapp_pref",
                            MODE_PRIVATE
                        ).edit()
                        prefs.putString("inappads", "nnn")
                        prefs.apply()
                        println("mypurchase 9 nnndd= " + purchases[0].skus)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
//                Toast.makeText(MainActivity.this, "You are disconnected from Billing Service"
//                        , Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun loadAllSubscribePackage() {
        if (billingClient!!.isReady) {
            val skuList: MutableList<String> = java.util.ArrayList()
            skuList.add(getString(R.string.playstoresubscription_premium1month))
            skuList.add(getString(R.string.playstoresubscription_premium3month))
            skuList.add(getString(R.string.playstoresubscription_premium6months))
            skuList.add(getString(R.string.playstoresubscription_premium12months))
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

            billingClient!!.querySkuDetailsAsync(
                params.build()
            ) { billingResult, skuDetailsList ->
                println("mypurchase 1 = ")
                try {
                    iUtils.SkuDetailsList = skuDetailsList
                    assert(skuDetailsList != null)
                    println("mypurchase 0 = " + skuDetailsList!![0])
                } catch (ignored: java.lang.Exception) {
                }
            }
        } else {
            Toast.makeText(this@MainActivity, R.string.billingnotready, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleitemAlreadyPuchase(purchases: Purchase) {
        if (purchases.purchaseState == Purchase.PurchaseState.PURCHASED) {
            iUtils.isSubactive = true
            binding.subscriptionFab.setVisibility(View.INVISIBLE)
            val prefs = getSharedPreferences(
                "whatsapp_pref",
                MODE_PRIVATE
            ).edit()
            prefs.putString("inappads", "ppp")
            prefs.apply()
        }
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    private fun restartApp() {
        try {
            val i = packageManager.getLaunchIntentForPackage(
                baseContext.packageName
            )
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}