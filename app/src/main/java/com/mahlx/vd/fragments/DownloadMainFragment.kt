@file:Suppress("DEPRECATION")

package com.infusiblecoder.allinonevideodownloader.fragments

import android.annotation.SuppressLint
import android.app.*
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mauker.materialsearchview.MaterialSearchView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.infusiblecoder.allinonevideodownloader.R
import com.infusiblecoder.allinonevideodownloader.activities.*
import com.infusiblecoder.allinonevideodownloader.adapters.ListAllStoriesOfUserAdapter
import com.infusiblecoder.allinonevideodownloader.adapters.StoryUsersListAdapter
import com.infusiblecoder.allinonevideodownloader.databinding.FragmentDownloadBinding
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.FacebookPrivateWebview
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbadapters.FBstoryAdapter
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbadapters.FBuserRecyclerAdapter
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbmodels.FBStory
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbmodels.FBUserData
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbutils.FBhelper
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbutils.Facebookprefloader
import com.infusiblecoder.allinonevideodownloader.facebookstorysaver.fbutils.LoginWithFB
import com.infusiblecoder.allinonevideodownloader.interfaces.UserListInStoryListner
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.CarouselMedia
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.ModelInstaWithLogin
import com.infusiblecoder.allinonevideodownloader.models.storymodels.*
import com.infusiblecoder.allinonevideodownloader.receiver.Receiver
import com.infusiblecoder.allinonevideodownloader.services.ClipboardMonitor
import com.infusiblecoder.allinonevideodownloader.utils.*
import com.infusiblecoder.allinonevideodownloader.utils.AdsManager.mRewardedAd
import com.infusiblecoder.allinonevideodownloader.utils.Constants.PREF_CLIP
import com.infusiblecoder.allinonevideodownloader.utils.Constants.STARTFOREGROUND_ACTION
import com.infusiblecoder.allinonevideodownloader.utils.Constants.STOPFOREGROUND_ACTION
import com.infusiblecoder.allinonevideodownloader.utils.iUtils.ShowToast
import com.infusiblecoder.allinonevideodownloader.utils.iUtils.isMyPackedgeInstalled
import com.infusiblecoder.allinonevideodownloader.webservices.DownloadVideosMain
import com.infusiblecoder.allinonevideodownloader.webservices.api.RetrofitApiInterface
import com.infusiblecoder.allinonevideodownloader.webservices.api.RetrofitClient
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.reflect.Type
import java.net.URI
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION", "NAME_SHADOWING")
@Keep
class DownloadMainFragment : Fragment(), UserListInStoryListner {
    private var myselectedActivity: FragmentActivity? = null
    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!
    private var nn: String? = "nnn"
    private var fbstory_adapter: FBstoryAdapter? = null
    private var fbuserlistadapter: FBuserRecyclerAdapter? = null
    private var listAllStoriesOfUserAdapter: ListAllStoriesOfUserAdapter? = null
    private var storyUsersListAdapter: StoryUsersListAdapter? = null
    private var NotifyID = 1001
    private var csRunning = false
    lateinit var progressDralogGenaratinglink: ProgressDialog
    private lateinit var prefEditor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    var myVideoUrlIs: String? = null
    var myInstaUsername: String? = ""
    var myPhotoUrlIs: String? = null
    var fbstorieslist: List<FBStory> = ArrayList()
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // val view: View = inflater.inflate(R.layout.fragment_download, container, false)

        setActivityAfterAttached()
        if (isAdded) {
            val prefs = myselectedActivity!!.getSharedPreferences(
                "whatsapp_pref",
                Context.MODE_PRIVATE
            )
            nn = prefs!!.getString("inappads", "nnn")


            AdsManager.loadVideoAdAdmob(myselectedActivity, object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {

                    AdsManager.loadVideoAdAdmob(myselectedActivity,
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                val checked = binding.chkAutoDownload.isChecked
                                if (checked) {
                                    binding.chkAutoDownload.isChecked = false
                                }
                            }

                            override fun onAdShowedFullScreenContent() {

                                ShowToast(activity, getString(R.string.completad))

                                mRewardedAd = null
                            }
                        })

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    val checked = binding.chkAutoDownload.isChecked
                    if (checked) {
                        binding.chkAutoDownload.isChecked = false
                    }
                }

                override fun onAdShowedFullScreenContent() {

                    mRewardedAd = null
                }
            })



            AdsManager.loadInterstitialAd(activity as Activity?)

            progressDralogGenaratinglink = ProgressDialog(myselectedActivity!!)
            progressDralogGenaratinglink.setMessage(resources.getString(R.string.genarating_download_link))
            progressDralogGenaratinglink.setCancelable(false)
            //  addFbAd()
            pref = myselectedActivity!!.getSharedPreferences(PREF_CLIP, 0) // 0 - for private mode
            prefEditor = pref.edit()
            csRunning = pref.getBoolean("csRunning", false)
            prefEditor.apply()

            createNotificationChannel(
                myselectedActivity!!,
                NotificationManagerCompat.IMPORTANCE_LOW,
                true,
                getString(R.string.app_name),
                getString(R.string.aio_auto)
            )
//TODO
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(context)) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + myselectedActivity!!.packageName)
//                )
//                startActivityForResult(intent, 1234)
//            }
//        }


            binding.ivHelp.setOnClickListener {

                try {
                    binding.etURL.setText("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            binding.btnDownload.setOnClickListener {
                val url = binding.etURL.text.toString()
                DownloadVideo(url)
            }


            if (activity != null) {
                val activity: MainActivity? = activity as MainActivity?
                val strtext: String? = activity?.getMyData()
                println("mydatvgg222 $strtext")
                if (strtext != null && strtext != "") {
                    activity.setmydata("")
                    binding.etURL.setText(strtext)
                    val url = binding.etURL.text.toString()
                    DownloadVideo(url)
                }
            }

            binding.llFacebook.setOnClickListener {
                openAppFromPackedge(
                    "com.facebook.katana",
                    "facebook:/newsfeed",
                    myselectedActivity!!.resources.getString(R.string.install_fb)
                )
            }
            binding.llTikTok.setOnClickListener {
                openAppFromPackedge(
                    "com.zhiliaoapp.musically",
                    "https://www.tiktok.com/",
                    myselectedActivity!!.resources.getString(R.string.install_tik)
                )
            }
            binding.llInstagram.setOnClickListener {
                openAppFromPackedge(
                    "com.instagram.android",
                    "https://www.instagram.com/",
                    myselectedActivity!!.resources.getString(R.string.install_ins)
                )
            }
            binding.llTwitter.setOnClickListener {
                openAppFromPackedge(
                    "com.twitter.android",
                    "https://www.twitter.com/",
                    myselectedActivity!!.resources.getString(R.string.install_twi)
                )
            }

            if (!Constants.showyoutube) {
                binding.llytdbtn.visibility = View.GONE
            }
            binding.llytdbtn.setOnClickListener {
                openAppFromPackedge(
                    "com.google.android.youtube",
                    "https://www.youtube.com/",
                    myselectedActivity!!.resources.getString(R.string.install_ytd)
                )
            }
            try {

                val biometricManager: BiometricManager = BiometricManager.from(myselectedActivity!!)
                when {
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS -> {

                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        //loginbutton.setVisibility(View.GONE)
                        iUtils.isBioAvaliable = false
                        Toast.makeText(
                            myselectedActivity!!,
                            "This device does not have a fingerprint sensor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        Toast.makeText(
                            myselectedActivity!!,
                            "The biometric sensor is currently unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                        iUtils.isBioAvaliable = false
                        // loginbutton.setVisibility(View.GONE)
                    }
                    biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Toast.makeText(
                            myselectedActivity!!,
                            "Your device doesn't have fingerprint saved,please check your security settings",
                            Toast.LENGTH_SHORT
                        ).show()
                        iUtils.isBioAvaliable = false
                        //  loginbutton.setVisibility(View.GONE)
                    }
                }


                val executor: Executor = ContextCompat.getMainExecutor(myselectedActivity!!)
                val biometricPrompt =
                    BiometricPrompt(
                        myselectedActivity!!,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                            }

                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                startActivity(Intent(context, GalleryActivity::class.java))
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                            }
                        })


                val promptInfo: BiometricPrompt.PromptInfo =
                    BiometricPrompt.PromptInfo.Builder().setTitle("Verify Identity")
                        .setDescription("Use your fingerprint or device credentials to Access Gallery ")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        //.setNegativeButtonText("Cancel")
                        .build()



                binding.rvGallery.setOnClickListener {
                    try {
                        if (iUtils.isBioAvaliable && iUtils.getIsBioLoginEnabled(myselectedActivity)) {
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            startActivity(Intent(context, GalleryActivity::class.java))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        startActivity(Intent(context, GalleryActivity::class.java))

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()

                binding.rvGallery.setOnClickListener {
                    startActivity(Intent(context, GalleryActivity::class.java))
                }
            }

            binding.privatefblayout.setOnClickListener {
                startActivity(Intent(myselectedActivity, FacebookPrivateWebview::class.java))
            }

            binding.llroposo.setOnClickListener {
                openAppFromPackedge(
                    "com.roposo.android",
                    "https://www.roposo.com/",
                    myselectedActivity!!.resources.getString(R.string.install_roposo)
                )
            }
            binding.llsharechat.setOnClickListener {
                openAppFromPackedge(
                    "in.mohalla.sharechat",
                    "https://www.sharechat.com/",
                    myselectedActivity!!.resources.getString(R.string.install_sharechat)
                )
            }
            binding.likee.setOnClickListener {
                openAppFromPackedge(
                    "video.like",
                    "https://likee.com/",
                    myselectedActivity!!.resources.getString(R.string.install_likee)
                )
            }
            binding.videomoreBtn.setOnClickListener {
                val intent = Intent(myselectedActivity, AllSupportedApps::class.java)
                startActivity(intent)
            }
            binding.ivLink.setOnClickListener(fun(_: View) {
                val clipBoardManager =
                    myselectedActivity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val primaryClipData = clipBoardManager.primaryClip
                val clip = primaryClipData?.getItemAt(0)?.text.toString()

                binding.etURL.text = Editable.Factory.getInstance().newEditable(clip)
                DownloadVideo(clip)
            })

            if (Build.VERSION.SDK_INT < 29) {
                if (csRunning) {
                    binding.chkAutoDownload.isChecked = true
                    startClipboardMonitor()
                    setNofication(true)
                } else {
                    binding.chkAutoDownload.isChecked = false
                    stopClipboardMonitor()
                    setNofication(false)
                }
            } else {
                binding.chkAutoDownload.isChecked = false
            }

            binding.chkAutoDownload.setOnClickListener {
//TODO fix

                val checked = binding.chkAutoDownload.isChecked
                if (!checked) {
                    binding.chkAutoDownload.isChecked = false
                    stopClipboardMonitor()
                } else {
                    showAdDialog()
                }


//                ShowToast(myselectedActivity, "Currently Disabled For Testing")
//                binding.chkAutoDownload.isChecked = false

            }

            val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity)
            if (sharedPrefsFor.preference.preferencE_SESSIONID == "") {
                sharedPrefsFor.clearSharePrefs()
            }
            val map = sharedPrefsFor.preference
            if (map != null) {
                if (map.preferencE_ISINSTAGRAMLOGEDIN == "true") {
                    binding.chkdownloadPrivateMedia.isChecked = true
                    binding.linlayoutInstaStories.visibility = View.VISIBLE
                    getallstoriesapicall()
                } else {
                    binding.chkdownloadPrivateMedia.isChecked = false
                    binding.linlayoutInstaStories.visibility = View.GONE
                }
            }
            val sharedPrefsForfb = Facebookprefloader(myselectedActivity)
            if (sharedPrefsForfb.LoadPrefString().getFb_pref_key() == "") {
                sharedPrefsForfb.MakePrefEmpty()
            }
            val LoadPrefString = sharedPrefsForfb.LoadPrefString()

            val logedin = LoadPrefString.getFb_pref_isloggedin()
            if (logedin != null && logedin != "") {
                println("mydataiiii=$logedin")
                if (logedin == "true") {
                    println("meditating=$logedin")
                    binding.chkdownloadFbstories.isChecked = true
                    binding.linlayoutFbStories.visibility = View.VISIBLE
                    loadUserData()
                } else {
                    println("modalities=$logedin")
                    binding.chkdownloadFbstories.isChecked = false
                    binding.linlayoutFbStories.visibility = View.GONE
                }
            } else {
                binding.chkdownloadFbstories.isChecked = false
                binding.linlayoutFbStories.visibility = View.GONE
            }

            binding.chkdownloadPrivateMedia.setOnClickListener {
                val sharedPrefsForInstagram = SharedPrefsForInstagram(myselectedActivity)
                val map = sharedPrefsForInstagram.preference
                if (map != null && map.preferencE_ISINSTAGRAMLOGEDIN != "true") {
                    val intent = Intent(
                        myselectedActivity,
                        InstagramLoginActivity::class.java
                    )
                    startActivityForResult(intent, 200)
                } else {
                    val ab = AlertDialog.Builder(
                        myselectedActivity!!
                    )
                    ab.setPositiveButton(
                        resources.getString(R.string.yes)
                    ) { p0, _ ->
                        val sharedPrefsForInstagram2 = SharedPrefsForInstagram(myselectedActivity)
                        val map2 =
                            sharedPrefsForInstagram2.preference
                        if (sharedPrefsForInstagram2.preference != null) {
                            sharedPrefsForInstagram2.clearSharePrefs()
                            binding.linlayoutInstaStories.visibility = View.GONE
                            if (map2 != null && map2.preferencE_ISINSTAGRAMLOGEDIN == "true") {
                                binding.chkdownloadPrivateMedia.isChecked = true
                            } else {
                                binding.chkdownloadPrivateMedia.isChecked = false
                                binding.recUserList.visibility = View.GONE
                                binding.recStoriesList.visibility = View.GONE
                            }
                            p0?.dismiss()
                            binding.chkdownloadPrivateMedia.isChecked = false
                        } else {
                            sharedPrefsForInstagram2.clearSharePrefs()
                        }
                    }
                    ab.setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog, _ ->
                        dialog.cancel()
                        val asfd: Boolean = binding.chkdownloadPrivateMedia.isChecked
                        binding.chkdownloadPrivateMedia.isChecked = !asfd
                    }
                    val alert = ab.create()
                    alert.setTitle(getString(R.string.noprivatedownload))
                    alert.setMessage(getString(R.string.no_private_insta))
                    alert.show()
                }
            }


            binding.chkdownloadFbstories.setOnClickListener {
                val sharedPrefsForfb = Facebookprefloader(myselectedActivity)
                Log.d(TAG, "Inte 0")
                val LoadPrefString = sharedPrefsForfb.LoadPrefString()
                val logedin = LoadPrefString.getFb_pref_isloggedin()
                if (logedin != "true" && logedin != "") {
                    val intent = Intent(
                        myselectedActivity,
                        LoginWithFB::class.java
                    )
                    startActivityForResult(intent, 201)
                } else {
                    val ab = AlertDialog.Builder(
                        myselectedActivity!!
                    )
                    ab.setPositiveButton(
                        resources.getString(R.string.cancel)
                    ) { p0, _ ->
                        p0?.cancel()
                        val LoadPrefString = sharedPrefsForfb.LoadPrefString()
                        val logedin = LoadPrefString.getFb_pref_isloggedin()
                        if (logedin != null && logedin != "") {

                            binding.chkdownloadFbstories.isChecked = logedin == "true"
                        } else {
                            sharedPrefsForfb.MakePrefEmpty()
                        }
                    }
                    ab.setNegativeButton(
                        resources.getString(R.string.yes)
                    ) { dialog, _ ->
                        dialog.cancel()

                        binding.chkdownloadFbstories.isChecked = false
                        binding.recUserFblist.visibility = View.GONE
                        binding.recStoriesFblist.visibility = View.GONE
                        sharedPrefsForfb.MakePrefEmpty()
                        logout()
                    }
                    val alert = ab.create()
                    alert.setTitle(getString(R.string.fb_story))
                    alert.setMessage(getString(R.string.no_fb_story))
                    alert.show()
                }
            }

            binding.txtSearchClick.setOnClickListener {
                try {
                    binding.searchStory.openSearch()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.txtSearchClickFbstory.setOnClickListener { binding.searchFbstory.openSearch() }

            binding.searchFbstory.setOnClearClickListener {
                try {
                    fbuserlistadapter!!.filter.filter("")
                } catch (e: Exception) {

                }
            }
            binding.searchFbstory.setOnQueryTextListener(object :
                MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return try {
                        if (newText != null && fbuserlistadapter != null) {

                            println("dhsahdhashdk $newText")
                            fbuserlistadapter!!.filter.filter(newText)
                        }

                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ShowToast(myselectedActivity, getString(R.string.error_occ))
                        true
                    }
                }
            })

            binding.searchStory.setOnClearClickListener {

                try {
                    storyUsersListAdapter!!.filter.filter("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


            binding.searchStory.setOnQueryTextListener(object :
                MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return try {
                        if (newText != null && storyUsersListAdapter != null) {
                            storyUsersListAdapter!!.filter.filter(newText)
                        }
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ShowToast(myselectedActivity, getString(R.string.error_occ))
                        true
                    }
                }
            })

            try {
                if (Constants.show_Ads && nn == "nnn") {
                    AdsManager.loadAdmobNativeAd(
                        myselectedActivity,
                        binding.flAdplaceholder
                    )
                } else {
                    binding.flAdplaceholder.visibility = View.GONE
                }
            } catch (e: Exception) {
            }

        }

    }


    fun dismissMyDialogFrag() {
        try {
            myselectedActivity?.runOnUiThread {
                if (!(myselectedActivity as Activity).isFinishing && progressDralogGenaratinglink != null && progressDralogGenaratinglink.isShowing) {
                    progressDralogGenaratinglink.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissMyDialogErrorToastFrag() {
        try {
            myselectedActivity?.runOnUiThread {
                if (!(myselectedActivity as Activity).isFinishing && progressDralogGenaratinglink != null && progressDralogGenaratinglink.isShowing) {
                    progressDralogGenaratinglink.dismiss()
                    Toast.makeText(
                        myselectedActivity,
                        myselectedActivity!!.resources.getString(R.string.somthing),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun loadUserData() {

        binding.progressLoadingFbbar.visibility = View.VISIBLE
        val cookie = CookieManager.getInstance().getCookie("https://www.facebook.com")
        if (!FBhelper.valadateCooki(cookie)) {
            Log.e("tag2", "cookie is not valid")
            ShowToast(myselectedActivity, getString(R.string.cookiesnotvalid))
            return
        }
        val sharedPrefsForfb = Facebookprefloader(myselectedActivity)
        val LoadPrefStringol = sharedPrefsForfb.LoadPrefString()
        val LoadPrefString = LoadPrefStringol.getFb_pref_key()
        //     = sharedPrefsForfb.LoadPrefString( "key")
        Log.e("tag299", "cookie is not valid $LoadPrefString")
        Log.e("tag2", "cookie is:$cookie")
        Log.e("tag2", "key is:$LoadPrefString")
        Log.e("tag2", "start getting user data")
        AndroidNetworking.post("https://www.facebook.com/api/graphql/")
            .addHeaders("accept-language", "en,en-US;q=0.9,fr;q=0.8,ar;q=0.7")
            .addHeaders("cookie", cookie)
            .addHeaders(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"
            )
            .addHeaders("Content-Type", "application/json")
            .addBodyParameter("fb_dtsg", LoadPrefString)
            .addBodyParameter(
                "variables",
                "{\"bucketsCount\":200,\"initialBucketID\":null,\"pinnedIDs\":[\"\"],\"scale\":3}"
            )
            .addBodyParameter("doc_id", "2893638314007950")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.e("tag55", response.toString())
                    val parse = FBUserData.parse(response.toString())
                    if (parse != null) {
                        Log.e("tag1", "data succeed")
                        showFBSTORYData(parse)
                    }
                    binding.progressLoadingFbbar.visibility = View.GONE
                }

                override fun onError(error: ANError) {
                    Log.e("tag1", "data faild$error")
                    binding.progressLoadingFbbar.visibility = View.GONE
                }
            })
    }

    private fun loadFriendStories(str: String) {
        binding.progressLoadingFbbar.visibility = View.VISIBLE
        val cookie = CookieManager.getInstance().getCookie("https://www.facebook.com")
        if (!FBhelper.valadateCooki(cookie)) {
            Log.e("tag2", "cookie is not valid")

            return
        }
        val sharedPrefsForfb = Facebookprefloader(myselectedActivity!!)
        val LoadPrefStringol = sharedPrefsForfb.LoadPrefString()
        val LoadPrefString = LoadPrefStringol.getFb_pref_key()
        Log.e("tag2", "cookie is:$cookie")
        Log.e("tag2", "key is:$LoadPrefString")
        Log.e("tag2", "start getting user data")
        AndroidNetworking.post("https://www.facebook.com/api/graphql/")
            .addHeaders("accept-language", "en,en-US;q=0.9,fr;q=0.8,ar;q=0.7")
            .addHeaders("cookie", cookie)
            .addHeaders(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"
            )
            .addHeaders("Content-Type", "application/json")
            .addBodyParameter("fb_dtsg", LoadPrefString)
            .addBodyParameter(
                "variables",
                "{\"bucketID\":\"$str\",\"initialBucketID\":\"$str\",\"initialLoad\":false,\"scale\":5}"
            )
            .addBodyParameter("doc_id", "2558148157622405")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.e("tag55", response.toString())
                        fbstorieslist = FBStory.parseBulk(response.toString())
                        fbstory_adapter = FBstoryAdapter(
                            myselectedActivity!!,
                            fbstorieslist
                        )
                        binding.recStoriesFblist.layoutManager =
                            GridLayoutManager(myselectedActivity, 3)
                        binding.recStoriesFblist.adapter = fbstory_adapter
                        binding.progressLoadingFbbar.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ShowToast(myselectedActivity!!, "Failed to load stories")
                        binding.progressLoadingFbbar.visibility = View.GONE
                    }
                }

                override fun onError(error: ANError) {
                    ShowToast(myselectedActivity!!, "Failed to load stories")
                    Log.e("tag1", "data faild$error")
                    binding.progressLoadingFbbar.visibility = View.GONE
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            val activity: MainActivity? = activity as MainActivity?
            val strtext: String? = activity?.getMyData()
            println("mydatvgg222 $strtext")
            if (strtext != null && strtext != "") {
                activity.setmydata("")
                binding.etURL.setText(strtext)
                DownloadVideo(strtext)
            }
        }
        Log.e("Frontales", "resume")
    }

    fun showFBSTORYData(FBUserData: FBUserData) {
        try {
            binding.recUserFblist.layoutManager = LinearLayoutManager(
                myselectedActivity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            this.fbuserlistadapter =
                FBuserRecyclerAdapter(
                    myselectedActivity!!,
                    FBUserData.friends
                ) { id: String? ->
                    loadFriendStories(id!!)
                }
            binding.recUserFblist.adapter = this.fbuserlistadapter
            binding.progressLoadingFbbar.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    private fun logout() {
        CookieManager.getInstance().removeAllCookies(null as ValueCallback<Boolean>?)
        CookieManager.getInstance().flush()
        val sharedPrefsForfb = Facebookprefloader(myselectedActivity!!)
        sharedPrefsForfb.MakePrefEmpty()
    }

    private fun openAppFromPackedge(
        packedgename: String,
        urlofwebsite: String,
        installappmessage: String
    ) {
        if (isMyPackedgeInstalled(myselectedActivity!!, packedgename)) {
            try {
                val pm: PackageManager = myselectedActivity!!.packageManager
                val launchIntent: Intent = pm.getLaunchIntentForPackage(packedgename)!!
                myselectedActivity!!.startActivity(launchIntent)
            } catch (e: Exception) {
                try {
                    Toast.makeText(
                        myselectedActivity!!,
                        myselectedActivity!!.resources?.getString(R.string.error_occord_while),
                        Toast.LENGTH_SHORT
                    ).show()


                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlofwebsite))
                    myselectedActivity!!.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        myselectedActivity!!,
                        myselectedActivity!!.resources?.getString(R.string.error_occord_while),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            ShowToast(myselectedActivity, installappmessage)
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packedgename")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packedgename")
                    )
                )
            }
        }
    }

    private fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.e("loged112211", "Notificaion Channel Created!")
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setNofication(b: Boolean) {

        if (b) {
            val channelId =
                "${myselectedActivity!!.packageName}-${myselectedActivity!!.getString(R.string.app_name)}"
            val notificationBuilder =
                NotificationCompat.Builder(myselectedActivity!!, channelId).apply {
                    setSmallIcon(R.drawable.ic_appicon) // 3
                    // setStyle(NotificationCompat.)
                    setLargeIcon(
                        BitmapFactory.decodeResource(
                            myselectedActivity!!.resources,
                            R.drawable.ic_appicon
                        )
                    )
                    setContentTitle(myselectedActivity!!.resources?.getString(R.string.auto_download_title_notification)) // 4
                    setContentText(myselectedActivity!!.resources?.getString(R.string.auto_download_title_notification_start)) // 5
                    setOngoing(true)
                    priority = NotificationCompat.PRIORITY_LOW // 7
                    setSound(null)
                    setOnlyAlertOnce(true)
                    setAutoCancel(false)
                    addAction(
                        R.drawable.ic_close_24dp,
                        "Stop",
                        makePendingIntent("quit_action")
                    )
                    val intent = Intent(myselectedActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


                    val restartServicePendingIntent: PendingIntent? =
                        PendingIntent.getActivity(
                            myselectedActivity,
                            1,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
                        )

                    setContentIntent(restartServicePendingIntent)
                }
            with(NotificationManagerCompat.from(myselectedActivity!!)) {
                // notificationId is a unique int for each notification that you must define
                notify(NotifyID, notificationBuilder.build())
                Log.e("loged", "testing notification notify!")
            }
        } else {
            myselectedActivity?.let { NotificationManagerCompat.from(it).cancel(NotifyID) }
        }
    }

    private fun startClipboardMonitor() {
        try {
            prefEditor.putBoolean("csRunning", true)
            prefEditor.commit()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myselectedActivity!!.startForegroundService(
                    Intent(
                        myselectedActivity,
                        ClipboardMonitor::class.java
                    ).setAction(STARTFOREGROUND_ACTION)
                )
            } else {
                myselectedActivity!!.startService(
                    Intent(
                        myselectedActivity,
                        ClipboardMonitor::class.java
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopClipboardMonitor() {
        try {
            prefEditor.putBoolean("csRunning", false)
            prefEditor.commit()

            myselectedActivity!!.stopService(
                Intent(
                    myselectedActivity,
                    ClipboardMonitor::class.java
                ).setAction(STOPFOREGROUND_ACTION)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun makePendingIntent(name: String): PendingIntent {
        val intent = Intent(myselectedActivity, Receiver::class.java)
        intent.action = name

        val restartServicePendingIntent: PendingIntent? =
            PendingIntent.getBroadcast(
                myselectedActivity,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )

        return restartServicePendingIntent!!
    }

    fun DownloadVideo(url: String) {
        Log.e("myhdasbdhf urlis frag  ", url)
        if (url == "" && iUtils.checkURL(url)) {
            ShowToast(
                myselectedActivity,
                myselectedActivity!!.resources?.getString(R.string.enter_valid)
            )
        } else {
            val rand = Random()
            val rand_int1 = rand.nextInt(2)
            println("randonvalueis = $rand_int1")
            if (rand_int1 == 0) {
                showAdmobAds()
            } else {
                showAdmobAds_int_video()
            }
            Log.d("mylogissssss", "The interstitial wasn't loaded yet.")
            if (url.contains("instagram.com")) {
                if (!myselectedActivity!!.isFinishing)
                    progressDralogGenaratinglink.show()

                startInstaDownload(url)
            } else if (url.contains("myjosh.in")) {
                var myurl = url
                try {
                    myurl = iUtils.extractUrls(myurl)[0]
                } catch (e: Exception) {
                }
                DownloadVideosMain.Start(myselectedActivity, myurl.trim(), false)
                Log.e("downloadFileName12", myurl.trim())
            } else if (url.contains("audiomack")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("ok.ru")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("zili")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("tiki")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("zingmp3")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("vidlit")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("byte.co")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("fthis.gr")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("fw.tv") || url.contains("firework.tv")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("rumble")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("traileraddict")) {
                dismissMyDialogFrag()
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)
            } else if (url.contains("bemate")) {
                dismissMyDialogFrag()
                var myurl = url
                try {
                    myurl = iUtils.extractUrls(myurl)[0]
                } catch (e: Exception) {

                }
                val intent = Intent(myselectedActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", myurl)
                startActivityForResult(intent, 2)
            } else if (url.contains("chingari")) {
                var myurl = url
                try {
                    myurl = iUtils.extractUrls(myurl)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(myselectedActivity, myurl.trim(), false)
                Log.e("downloadFileName12", myurl.trim())
            } else if (url.contains("sck.io") || url.contains("snackvideo")) {
                var myurl = url
                try {
                    myurl = iUtils.extractUrls(myurl)[0]
                } catch (e: Exception) {

                }
                DownloadVideosMain.Start(myselectedActivity, myurl.trim(), false)
                Log.e("downloadFileName12", myurl.trim())
            } else {
                var myurl = url
                try {
                    myurl = iUtils.extractUrls(myurl)[0]
                } catch (e: Exception) {
                }
                DownloadVideosMain.Start(myselectedActivity, myurl.trim(), false)
                Log.e("downloadFileName12", myurl.trim())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("proddddd11111222 $resultCode __$data")

        if (requestCode == 200 && resultCode == RESULT_OK) {

            println("proddddd11111200 $resultCode __$data")

            val sharedPrefsForInstagram = SharedPrefsForInstagram(myselectedActivity)

            val map = sharedPrefsForInstagram.preference

            if (map != null) {
                println("proddddd11111  " + map.preferencE_ISINSTAGRAMLOGEDIN)

                if (map.preferencE_ISINSTAGRAMLOGEDIN != "true") {
                    binding.chkdownloadPrivateMedia.isChecked = false
                    binding.linlayoutInstaStories.visibility = View.GONE
                } else {
                    binding.chkdownloadPrivateMedia.isChecked = true
                    binding.linlayoutInstaStories.visibility = View.VISIBLE
                    getallstoriesapicall()
                }
            }
        }
        if (requestCode == 201 && resultCode == RESULT_OK) {

            println("proddddd11111201 $resultCode __$data")

            val sharedPrefsForfb = Facebookprefloader(myselectedActivity)
            val LoadPrefStringol = sharedPrefsForfb.LoadPrefString()
            val logedin = LoadPrefStringol.getFb_pref_isloggedin()

            println("proddddd11111201-1=$logedin")
            if (logedin != null && logedin != "") {
                if (logedin == "true") {
                    binding.chkdownloadFbstories.isChecked = true
                    binding.linlayoutFbStories.visibility = View.VISIBLE
                    loadUserData()
                } else {
                    binding.chkdownloadFbstories.isChecked = false
                    binding.linlayoutFbStories.visibility = View.GONE
                }
            } else {
                binding.chkdownloadFbstories.isChecked = false
                binding.linlayoutFbStories.visibility = View.GONE
            }
        }
    }

    private fun showAdDialog() {
        try {

            if (Constants.show_Ads && !myselectedActivity!!.isFinishing) {
                if (nn == "nnn") {
                    val dialogBuilder = AlertDialog.Builder(myselectedActivity!!)
                    dialogBuilder.setMessage(getString(R.string.doyouseead))
                        .setCancelable(false)
                        .setPositiveButton(
                            getString(R.string.watchad)
                        ) { _, _ ->


                            AdsManager.showVideoAdAdmob(myselectedActivity) {
                                Log.d(TAG, "User earned the reward.")

                                if (Build.VERSION.SDK_INT < 29) {
                                    binding.chkAutoDownload.isChecked = true
                                    startClipboardMonitor()
                                    setNofication(true)
                                } else {
                                    binding.chkAutoDownload.isChecked = false
                                    ShowToast(myselectedActivity, getString(R.string.olnyandrid9))
                                }

                            }

                        }.setNegativeButton(
                            getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.cancel()
                            val checked = binding.chkAutoDownload.isChecked
                            if (checked) {
                                binding.chkAutoDownload.isChecked = false
                            }
                        }
                    val alert = dialogBuilder.create()
                    alert.setTitle(getString(R.string.enabAuto))
                    alert.show()
                } else {
                    binding.chkAutoDownload.isChecked = true
                    val checked = binding.chkAutoDownload.isChecked
                    if (checked) {
                        Log.e("loged", "testing checked!")
                        startClipboardMonitor()
                        setNofication(true)
                    } else {
                        Log.e("loged", "testing unchecked!")
                        stopClipboardMonitor()
                        setNofication(false)
                    }
                    Log.d("TAG", "The rewarded ad wasn't ready yet.")
                }
            } else {
                binding.chkAutoDownload.isChecked = true
                val checked = binding.chkAutoDownload.isChecked
                if (checked) {
                    Log.e("loged", "testing checked!")
                    startClipboardMonitor()
                    setNofication(true)
                } else {
                    Log.e("loged", "testing unchecked!")
                    stopClipboardMonitor()
                    setNofication(false)
                }
                Log.d("TAG", "The rewarded ad wasn't ready yet.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //insta finctions

    @Keep
    fun startInstaDownload(Url: String) {


//         https://www.instagram.com/p/CLBM34Rhxek/?igshid=41v6d50y6u4w
//          https://www.instagram.com/p/CLBM34Rhxek/
//           https://www.instagram.com/p/CLBM34Rhxek/?__a=1&__d=dis
//           https://www.instagram.com/tv/CRyVpDSAE59/

        /*
        * https://www.instagram.com/p/CUs4eKIBscn/?__a=1&__d=dis
        * https://www.instagram.com/p/CUktqS7pieg/?__a=1&__d=dis
        * https://www.instagram.com/p/CSMYRwGna3S/?__a=1&__d=dis
        * https://www.instagram.com/p/CR6AbwDB12R/?__a=1&__d=dis
        * https://www.instagram.com/p/CR6AbwDB12R/?__a=1&__d=dis
        * */


        var Urlwi: String? = ""
        try {

            val uri = URI(Url)
            Urlwi = URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,  // Ignore the query part of the input url
                uri.fragment
            ).toString()


        } catch (ex: java.lang.Exception) {
            dismissMyDialogFrag()
            ShowToast(myselectedActivity!!, getString(R.string.invalid_url))
            return
        }

        System.err.println("workkkkkkkkk 1122112 $Url")

        var urlwithoutlettersqp: String? = Urlwi
        System.err.println("workkkkkkkkk 1122112 $urlwithoutlettersqp")


        if (urlwithoutlettersqp!!.contains("/reel/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/reel/", "/p/")
        }

        if (urlwithoutlettersqp.contains("/tv/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/tv/", "/p/")
        }

        val urlwithoutlettersqp_noa: String = urlwithoutlettersqp

        urlwithoutlettersqp = "$urlwithoutlettersqp?__a=1&__d=dis"
        System.err.println("workkkkkkkkk 87878788 $urlwithoutlettersqp")


        System.err.println("workkkkkkkkk 777777 $urlwithoutlettersqp")

        try {
            if (urlwithoutlettersqp.split("/")[4].length > 15) {

                val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity)
                if (sharedPrefsFor.preference.preferencE_SESSIONID == "") {
                    sharedPrefsFor.clearSharePrefs()
                }
                val map = sharedPrefsFor.preference
                if (map != null) {
                    if (map.preferencE_ISINSTAGRAMLOGEDIN == "false") {

                        dismissMyDialogFrag()

                        val alertDialog =
                            android.app.AlertDialog.Builder(requireActivity()).create()
                        alertDialog.setTitle(getString(R.string.logininsta))
                        alertDialog.setMessage(getString(R.string.urlisprivate))
                        alertDialog.setButton(
                            android.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.logininsta)
                        ) { dialog, _ ->
                            dialog.dismiss()


                            val intent = Intent(
                                requireActivity(),
                                InstagramLoginActivity::class.java
                            )
                            startActivityForResult(intent, 200)

                        }

                        alertDialog.setButton(
                            android.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.dismiss()


                        }
                        alertDialog.show()
                        return
                    }
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (!(myselectedActivity)!!.isFinishing) {
            val dialog = Dialog(myselectedActivity!!)
            dialog.setContentView(R.layout.tiktok_optionselect_dialog)

            val methode0 = dialog.findViewById<Button>(R.id.dig_btn_met0)
            val methode1 = dialog.findViewById<Button>(R.id.dig_btn_met1)
            val methode2 = dialog.findViewById<Button>(R.id.dig_btn_met2)
            val methode3 = dialog.findViewById<Button>(R.id.dig_btn_met3)
            val methode4 = dialog.findViewById<Button>(R.id.dig_btn_met4)
            val methode5 = dialog.findViewById<Button>(R.id.dig_btn_met5)
            val methode6 = dialog.findViewById<Button>(R.id.dig_btn_met6)
            val dig_txt_heading = dialog.findViewById<TextView>(R.id.dig_txt_heading)
            methode5.visibility = View.VISIBLE
            methode6.visibility = View.VISIBLE
            dig_txt_heading.text = myselectedActivity!!.getString(R.string.Selectdesiredinsta);

            val dig_btn_cancel = dialog.findViewById<Button>(R.id.dig_btn_cancel)

            methode0.setOnClickListener {
                dialog.dismiss()

                try {
                    System.err.println("workkkkkkkkk 4 ")

                    val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {

                        System.err.println(
                            "workkkkkkkkk 476 " + urlwithoutlettersqp + "____" +
                                    "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )

                        downloadInstagramImageOrVideodata_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )
                    } else {
                        downloadInstagramImageOrVideoResponseOkhttp(
                            urlwithoutlettersqp_noa
                        )
                        // downloadInstagramImageOrVideoResponseOkhttp(urlwithoutlettersqp_noa)
                        // downloadInstagramImageOrVideoResOkhttpM2(urlwithoutlettersqp_noa)
                        //downloadInstagramImageOrVideo_tikinfApi(urlwithoutlettersqp)
                    }
                } catch (e: java.lang.Exception) {
                    dismissMyDialogFrag()
                    System.err.println("workkkkkkkkk 5")
                    e.printStackTrace()
                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                }

            }
            methode1.setOnClickListener {
                dialog.dismiss()

                try {
                    System.err.println("workkkkkkkkk 4 ")

                    val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {

                        System.err.println(
                            "workkkkkkkkk 476 " + urlwithoutlettersqp + "____" +
                                    "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )

                        downloadInstagramImageOrVideodata_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )
                    } else {
                        downloadInstagramImageOrVideodataExternalApi2(
                            myselectedActivity!!,
                            urlwithoutlettersqp_noa
                        )
                        // downloadInstagramImageOrVideoResOkhttpM2(urlwithoutlettersqp_noa)
                        //downloadInstagramImageOrVideo_tikinfApi(urlwithoutlettersqp)
                    }
                } catch (e: java.lang.Exception) {
                    dismissMyDialogFrag()
                    System.err.println("workkkkkkkkk 5")
                    e.printStackTrace()
                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                }


            }
            methode2.setOnClickListener {
                dialog.dismiss()


                try {
                    System.err.println("workkkkkkkkk 4")
                    val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {
                        System.err.println("workkkkkkkkk 4.7")
                        downloadInstagramImageOrVideodata_old_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_USERID
                        )
                    } else {
                        System.err.println("workkkkkkkkk 4.8")
                        downloadInstagramImageOrVideodata_old(
                            urlwithoutlettersqp,
                            ""
                        )
                    }
                } catch (e: java.lang.Exception) {
                    dismissMyDialogFrag()
                    System.err.println("workkkkkkkkk 5")
                    e.printStackTrace()
                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                }

            }
            methode3.setOnClickListener {
                dialog.dismiss()

                try {
                    System.err.println("workkkkkkkkk 4")
                    val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {
                        System.err.println("workkkkkkkkk m2 5.2")
                        downloadInstagramImageOrVideodata_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )
                    } else {
                        System.err.println("workkkkkkkkk 4.5")
                        downloadInstagramImageOrVideodata(
                            urlwithoutlettersqp,
                            ""
                        )
                    }
                } catch (e: java.lang.Exception) {
                    dismissMyDialogFrag()
                    System.err.println("workkkkkkkkk 5.1")
                    e.printStackTrace()
                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                }

            }
            methode4.setOnClickListener {
                dialog.dismiss()

                try {
                    loginSnapIntaWebigram(urlwithoutlettersqp_noa)
                } catch (e: Exception) {
                    e.printStackTrace()
                    dismissMyDialogErrorToastFrag()
                }

            }
            methode5.setOnClickListener {
                dialog.dismiss()

                try {
                    loginDownloadgram(urlwithoutlettersqp_noa)
                } catch (e: Exception) {
                    e.printStackTrace()
                    dismissMyDialogErrorToastFrag()
                }

            }
            methode6.setOnClickListener {
                dialog.dismiss()

                try {
                    loginSnapIntaWeb(urlwithoutlettersqp_noa)
                } catch (e: Exception) {
                    e.printStackTrace()
                    dismissMyDialogErrorToastFrag()
                }

            }

            dig_btn_cancel.setOnClickListener {
                dialog.dismiss()
                dismissMyDialogFrag()
            }
            dialog.setCancelable(false)
            dialog.show()
        }


    }

    private fun setActivityAfterAttached() {
        try {
            if (activity != null && isAdded) {
                myselectedActivity = activity
            }
        } catch (e: Exception) {
            myselectedActivity = requireActivity()
            e.printStackTrace()
        }
    }


    private fun downloadInstagramImageOrVideo_tikinfApi(URL: String?) {
        AndroidNetworking.get("http://tikdd.infusiblecoder.com/ini/ilog.php?url=$URL")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val myresws: String = response.toString()
                    println("myresponseis111 eeee $myresws")

                    try {
                        val listType: Type =
                            object : TypeToken<ModelInstaWithLogin?>() {}.type
                        val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                            myresws,
                            listType
                        )
//                        System.out.println("workkkkk777 " + modelInstagramResponse.items.get(0).code)
                        val usernameis = modelInstagramResponse.items[0].user.username


                        if (modelInstagramResponse.items[0].mediaType == 8) {

                            val modelGetEdgetoNode = modelInstagramResponse.items[0]

                            val modelEdNodeArrayList: List<CarouselMedia> =
                                modelGetEdgetoNode.carouselMedia
                            for (i in 0 until modelEdNodeArrayList.size) {
                                if (modelEdNodeArrayList[i].mediaType == 2) {
                                    myVideoUrlIs =
                                        modelEdNodeArrayList[i].videoVersions[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity,
                                        myVideoUrlIs,
                                        usernameis + iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                        ".mp4"
                                    )


                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                            .geturl()
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity,
                                        myPhotoUrlIs,
                                        usernameis + iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                        ".png"
                                    )

                                    myPhotoUrlIs = ""

                                    dismissMyDialogFrag()

                                    // etText.setText("")
                                }
                            }
                        } else {
                            val modelGetEdgetoNode = modelInstagramResponse.items[0]
                            if (modelGetEdgetoNode.mediaType == 2) {
                                myVideoUrlIs =
                                    modelGetEdgetoNode.videoVersions[0].geturl()
                                DownloadFileMain.startDownloading(
                                    myselectedActivity,
                                    myVideoUrlIs,
                                    usernameis + iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                    ".mp4"
                                )

                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                DownloadFileMain.startDownloading(
                                    myselectedActivity,
                                    myPhotoUrlIs,
                                    usernameis + iUtils.getVideoFilenameFromURL(myPhotoUrlIs),

                                    ".png"
                                )
                                dismissMyDialogFrag()
                                myPhotoUrlIs = ""
                            }
                        }

                        dismissMyDialogFrag()

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()

                        println("myresponseis111 try exp " + e.message)

                        dismissMyDialogFrag()
                        ShowToast(
                            myselectedActivity,
                            resources.getString(R.string.somthing)
                        )
                    }
                }

                override fun onError(error: ANError) {
                    println("myresponseis111 exp " + error.message)
                    dismissMyDialogFrag()
                    ShowToast(
                        myselectedActivity,
                        resources.getString(R.string.somthing)
                    )
                }
            })


    }


    @Keep
    fun downloadInstagramImageOrVideodata(URL: String?, Coookie: String?) {

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        var Cookie = Coookie
        if (TextUtils.isEmpty(Cookie)) {
            Cookie = ""
        }
        val apiService: RetrofitApiInterface =
            RetrofitClient.getClient()

        val callResult: Call<JsonObject> = apiService.getInstagramData(
            URL,
            Cookie,
            iUtils.UserAgentsList[j]
        )
        callResult.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: retrofit2.Response<JsonObject?>
            ) {
                println("response1122334455 ress :   " + response.body())
                try {


//                                val userdata = response.body()!!.getAsJsonObject("graphql")
//                                    .getAsJsonObject("shortcode_media")
//                                binding.profileFollowersNumberTextview.setText(
//                                    userdata.getAsJsonObject(
//                                        "edge_followed_by"
//                                    )["count"].asString
//                                )
//                                binding.profileFollowingNumberTextview.setText(
//                                    userdata.getAsJsonObject(
//                                        "edge_follow"
//                                    )["count"].asString
//                                )
//                                binding.profilePostNumberTextview.setText(userdata.getAsJsonObject("edge_owner_to_timeline_media")["count"].asString)
//                                binding.profileLongIdTextview.setText(userdata["username"].asString)
//


                    val listType = object : TypeToken<ModelInstagramResponse?>() {}.type
                    val modelInstagramResponse: ModelInstagramResponse? = GsonBuilder().create()
                        .fromJson<ModelInstagramResponse>(
                            response.body().toString(),
                            listType
                        )
                    if (modelInstagramResponse != null) {
                        if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                            val modelGetEdgetoNode: ModelGetEdgetoNode =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                            myInstaUsername =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                            val modelEdNodeArrayList: List<ModelEdNode> =
                                modelGetEdgetoNode.modelEdNodes
                            for (i in modelEdNodeArrayList.indices) {
                                if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                    myVideoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.video_url
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    // etText.setText("")
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getImageFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    myPhotoUrlIs = ""
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    // etText.setText("")
                                }
                            }
                        } else {
                            val isVideo =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                            myInstaUsername =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                            if (isVideo) {
                                myVideoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myVideoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                    ".mp4"
                                )
                                try {
                                    dismissMyDialogFrag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myPhotoUrlIs,
                                    myInstaUsername + iUtils.getImageFilenameFromURL(myPhotoUrlIs),
                                    ".png"
                                )
                                try {
                                    dismissMyDialogFrag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                myPhotoUrlIs = ""
                            }
                        }
                    } else {
                        Toast.makeText(
                            myselectedActivity!!,
                            resources.getString(R.string.somthing),
                            Toast.LENGTH_SHORT
                        ).show()
                        try {
                            dismissMyDialogFrag()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    try {
                        try {
                            System.err.println("workkkkkkkkk 4")

                            downloadInstagramImageOrVideodata(
                                URL, ""
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            println("response1122334455 exe 1:   " + e.localizedMessage)
                            try {
                                dismissMyDialogFrag()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            System.err.println("workkkkkkkkk 5.1")
                            e.printStackTrace()
                            ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        myselectedActivity!!.runOnUiThread {
                            dismissMyDialogFrag()
                            if (!myselectedActivity!!.isFinishing) {
                                e.printStackTrace()
                                Toast.makeText(
                                    myselectedActivity!!,
                                    resources.getString(R.string.somthing),
                                    Toast.LENGTH_SHORT
                                ).show()
                                println("response1122334455 exe 1:   " + e.localizedMessage)
                                try {
                                    dismissMyDialogFrag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val alertDialog = AlertDialog.Builder(myselectedActivity!!).create()
                                alertDialog.setTitle(getString(R.string.logininsta))
                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE, getString(R.string.logininsta)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    val intent = Intent(
                                        myselectedActivity!!,
                                        InstagramLoginActivity::class.java
                                    )
                                    startActivityForResult(intent, 200)
                                }
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                alertDialog.show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                try {
                    println("response1122334455:   " + "Failed0" + t.message)
                    dismissMyDialogFrag()
                    Toast.makeText(
                        myselectedActivity!!,
                        resources.getString(R.string.somthing),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


    @Keep
    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    fun loginSnapIntaWeb(urlwithoutlettersqp: String) {
        try {


            binding.webViewInsta.clearCache(true)

            val cookieManager1 = CookieManager.getInstance()

            cookieManager1.setAcceptThirdPartyCookies(binding.webViewInsta, true)
            cookieManager1.setAcceptCookie(true)
            cookieManager1.acceptCookie()


            binding.webViewInsta.clearFormData()
            binding.webViewInsta.settings.saveFormData = true

            val random1 = Random()
            val j = random1.nextInt(iUtils.UserAgentsListLogin.size)

            binding.webViewInsta.settings.userAgentString = iUtils.UserAgentsListLogin[j]

            binding.webViewInsta.settings.allowFileAccess = true
            binding.webViewInsta.settings.javaScriptEnabled = true
            binding.webViewInsta.settings.defaultTextEncodingName = "UTF-8"
            binding.webViewInsta.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            binding.webViewInsta.settings.databaseEnabled = true
            binding.webViewInsta.settings.builtInZoomControls = false
            binding.webViewInsta.settings.setSupportZoom(true)
            binding.webViewInsta.settings.useWideViewPort = true
            binding.webViewInsta.settings.domStorageEnabled = true
            binding.webViewInsta.settings.loadWithOverviewMode = true
            binding.webViewInsta.settings.loadsImagesAutomatically = true
            binding.webViewInsta.settings.blockNetworkImage = false
            binding.webViewInsta.settings.blockNetworkLoads = false
            binding.webViewInsta.settings.defaultTextEncodingName = "UTF-8"

            var isdownloadstarted = false

            Log.e(
                "workkkk sel",
                "binding.loggedIn "
            )


            val handler2 = Handler()
            val listoflink_videos = ArrayList<String>()
            val listoflink_photos = ArrayList<String>()


            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()

            binding.webViewInsta.webViewClient = object : WebViewClient() {

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    webView1: WebView,
                    url: String?
                ): Boolean {
                    webView1.loadUrl(url!!)
                    return true
                }

                override fun onLoadResource(
                    webView: WebView?,
                    str: String?
                ) {
                    super.onLoadResource(webView, str)
                }


                override fun onPageFinished(
                    webView: WebView,
                    str: String?
                ) {
                    super.onPageFinished(webView, str)
                    Log.e(
                        "workkkk url",
                        "binding.progressBar reciveing data $str"
                    )

                    try {


                        val jsscript = ("javascript:(function() { "
                                + "var ell = document.getElementsByTagName('input');"
                                // + "ell[1].value ='" + "keepsaveit" + "';"
                                // + "ell[2].value ='" + "keepsaveit12345" + "';"

                                + "ell[0].value ='" + urlwithoutlettersqp + "';"

                                + "var bbb = document.getElementsByTagName('button');"
                                + "bbb[5].click();"
                                + "})();")



                        binding.webViewInsta.evaluateJavascript(
                            jsscript
                        ) {

                            Log.e(
                                "workkkk0",
                                "binding.progressBar reciveing data $it"
                            )


                            try {
                                handler2.postDelayed(object : Runnable {
                                    override fun run() {
                                        myselectedActivity?.runOnUiThread {


                                            binding.webViewInsta.evaluateJavascript(
                                                "(function() { " +
                                                        "var text='';" +
                                                        "var aaa = document.getElementsByTagName('a');" +
                                                        "for (var i = 0; i < aaa.length; i++) {" +
                                                        // "if(aaa[i].getAttribute('href').includes('https://scontent') || aaa[i].getAttribute('href').includes('https://instagram')){" +
                                                        "  text += aaa[i].getAttribute('href')+'@_@';" +
                                                        //  "}" +
                                                        "}" +
                                                        "var withoutLast3 = text.slice(0, -3);" +
                                                        "return withoutLast3+''; })();"
                                            ) { html ->

                                                Log.e(
                                                    "workkkk0",
                                                    "binding.progressBar reciveing data $html"
                                                )

                                                //                                        val unescapedString =
                                                //                                            Parser.unescapeEntities(html, true)

                                                //   var dsd :Document= Jsoup.parse(unescapedString)
                                                //                                        val document = Jsoup.parse(html)

//https://snapxcdn.com/dl/v1?token=


                                                binding.webViewInsta.evaluateJavascript(
                                                    "javascript:(function() { "
                                                            + "var bbb = document.getElementById('closeModalBtn');"
                                                            + "bbb.click();"
                                                            + "})();"
                                                ) { value ->
                                                    Log.e(
                                                        "workkkk0",
                                                        "binding.progressBar reciveing data3 $value"
                                                    )
                                                }


                                                val sss = html.split("@_@")
                                                for (i in sss) {


                                                    if (i.contains("/?token=") && !i.contains(
                                                            "/instagram-story-download"
                                                        ) && !i.contains(
                                                            "/instagram-reels-video-download"
                                                        ) && !i.contains("/instagram-photo-download") && !i.contains(
                                                            "/instagram-story-viewer"
                                                        )
                                                    ) {
                                                        Log.d("HTML vid", "" + i)

                                                        listoflink_videos.add(i)


                                                    }

                                                    if (i.contains("instagram") && !i.contains(
                                                            "/?token="
                                                        ) && !i.contains("/instagram-story-download") && !i.contains(
                                                            "/instagram-reels-video-download"
                                                        ) && !i.contains("/instagram-photo-download") && !i.contains(
                                                            "/instagram-story-viewer"
                                                        )
                                                    ) {
                                                        Log.d("HTMLimg", "" + i)


                                                        listoflink_photos.add(i)
                                                    }
                                                }


                                                if (!isdownloadstarted && (listoflink_videos.size > 0 || listoflink_photos.size > 0)) {

                                                    dismissMyDialogFrag()


                                                    handler2.removeCallbacksAndMessages(
                                                        null
                                                    )

                                                    isdownloadstarted = true

                                                    if ((listoflink_videos != null || listoflink_photos != null) || (listoflink_videos.size > 0 || listoflink_photos.size > 0)) {


                                                        for (i in listoflink_videos) {


                                                            DownloadFileMain.startDownloading(
                                                                myselectedActivity!!,
                                                                i,
                                                                myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getVideoFilenameFromURL(
                                                                    i
                                                                ),
                                                                ".mp4"
                                                            )

                                                        }

                                                        for (i in listoflink_photos) {

                                                            DownloadFileMain.startDownloading(
                                                                myselectedActivity!!,
                                                                i,
                                                                myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getImageFilenameFromURL(
                                                                    i
                                                                ),
                                                                ".png"
                                                            )
                                                        }
                                                    }

                                                } else {

//                                                    handler2.removeCallbacksAndMessages(
//                                                        null
//                                                    )
//
//                                                    myselectedActivity?.runOnUiThread {
//
//                                                        progressDralogGenaratinglink.setMessage(
//                                                            "Please Wait"
//                                                        )
//                                                    }
//                                                    Log.d(
//                                                        "HTML nolink fould",
//                                                        ""
//                                                    )
//
//                                                    try {
//                                                        System.err.println("workkkkkkkkk 4")
//
//                                                        val urlwithoutlettersqp2 =
//                                                            "$urlwithoutlettersqp?__a=1&__d=dis"
//
//
//                                                        System.err.println("workkkkkkkkk 4.5")
//
//                                                        downloadInstagramImageOrVideodata(
//                                                            urlwithoutlettersqp2,
//                                                            iUtils.myInstagramTempCookies
//                                                        )
//
//
//                                                    } catch (e: java.lang.Exception) {
//                                                        dismissMyDialogErrortoastFrag()
//                                                        System.err.println("workkkkkkkkk 5.1")
//                                                        e.printStackTrace()
//
//
//                                                    }


                                                }


                                            }

                                        }


                                        handler2.postDelayed(this, 2000)
                                    }
                                }, 2000)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                dismissMyDialogErrorToastFrag()

                            }


                        }


                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        dismissMyDialogErrorToastFrag()

                    }
                }


                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    webView: WebView?,
                    i: Int,
                    str: String?,
                    str2: String?
                ) {
                    super.onReceivedError(webView, i, str, str2)
                    dismissMyDialogErrorToastFrag()

                }

                override fun shouldInterceptRequest(
                    webView: WebView?,
                    webResourceRequest: WebResourceRequest?
                ): WebResourceResponse? {
                    return super.shouldInterceptRequest(
                        webView,
                        webResourceRequest
                    )
                }

                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
                ): WebResourceResponse? {
                    if (url!!.contains("google") || url.contains("facebook")) {
                        val textStream: InputStream = ByteArrayInputStream("".toByteArray())
                        return WebResourceResponse("text/plain", "UTF-8", textStream)
                    }
                    return super.shouldInterceptRequest(view, url)
                }


                override fun shouldOverrideUrlLoading(
                    webView: WebView?,
                    webResourceRequest: WebResourceRequest?
                ): Boolean {
                    return super.shouldOverrideUrlLoading(
                        webView,
                        webResourceRequest
                    )
                }
            }


            CookieSyncManager.createInstance(myselectedActivity)
            binding.webViewInsta.loadUrl("https://snapinsta.app/")


        } catch (e: java.lang.Exception) {
            dismissMyDialogErrorToastFrag()
            System.err.println("workkkkkkkkk 5" + e.localizedMessage)
            e.printStackTrace()

        }
    }


    @Keep
    private fun loginSnapIntaWebigram(urlwithoutlettersqp: String) {
        try {
            binding.webViewInsta.clearCache(true)
            val cookieManager1 = CookieManager.getInstance()
            cookieManager1.setAcceptThirdPartyCookies(binding.webViewInsta, true)
            cookieManager1.setAcceptCookie(true)
            cookieManager1.acceptCookie()
            binding.webViewInsta.clearFormData()
            binding.webViewInsta.settings.saveFormData = true
            val random1 = Random()
            val j = random1.nextInt(iUtils.UserAgentsList.size)
            binding.webViewInsta.settings.setUserAgentString(iUtils.UserAgentsListLogin.get(j))
            binding.webViewInsta.settings.allowFileAccess = true
            binding.webViewInsta.settings.javaScriptEnabled = true
            binding.webViewInsta.settings.defaultTextEncodingName = "UTF-8"
            binding.webViewInsta.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            binding.webViewInsta.settings.saveFormData = true
            binding.webViewInsta.settings.builtInZoomControls = true
            binding.webViewInsta.settings.setSupportZoom(true)
            binding.webViewInsta.settings.useWideViewPort = true
            binding.webViewInsta.settings.domStorageEnabled = true
            binding.webViewInsta.settings.loadWithOverviewMode = true
            binding.webViewInsta.settings.loadsImagesAutomatically = true
            binding.webViewInsta.settings.blockNetworkImage = false
            binding.webViewInsta.settings.blockNetworkLoads = false
            val isdownloadstarted = booleanArrayOf(false)
            val isdownloadclicked = booleanArrayOf(false)
            Log.e(
                "workkkk sel",
                "binding.loggedIn "
            )
            val handler2 = Handler()
            var listoflink_videos = ArrayList<String>()
            var listoflink_photos = ArrayList<String>()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            binding.webViewInsta.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.e(
                        "workkkk url",
                        "binding.progressBar reciveing data0 $url"
                    )
                    try {
                        val jsscript = ("javascript:(function() { "
                                + "var ell = document.getElementById('url');" // + "ell[1].value ='" + "keepsaveit" + "';"
                                // + "ell[2].value ='" + "keepsaveit12345" + "';"
                                + "ell.value ='" + urlwithoutlettersqp + "';"
                                + "var bbb = document.getElementById('submit');"
                                + "bbb.click();"
                                + "})();")
                        if (!isdownloadclicked[0]) {
                            binding.webViewInsta.evaluateJavascript(jsscript) { value ->
                                isdownloadclicked[0] = true
                                Log.e(
                                    "workkkk0",
                                    "binding.progressBar reciveing data1 $value"
                                )
                                try {
                                    handler2.postDelayed(object : Runnable {
                                        override fun run() {
                                            myselectedActivity!!.runOnUiThread(Runnable {
                                                binding.webViewInsta.evaluateJavascript(
                                                    "(function() { " +
                                                            "var text='';" +
                                                            "var aaa = document.getElementsByTagName('a');" +
                                                            "for (var i = 0; i < aaa.length; i++) {" +  // "if(aaa[i].getAttribute('href').includes('https://scontent') || aaa[i].getAttribute('href').includes('https://instagram')){" +
                                                            "  text += aaa[i].getAttribute('href')+'@_@';" +  //  "}" +
                                                            "}" +
                                                            "var withoutLast3 = text.slice(0, -3);" +
                                                            "return withoutLast3+''; })();"
                                                ) { html ->
                                                    Log.e(
                                                        "workkkk0",
                                                        "binding.progressBar reciveing data2 $html"
                                                    )
                                                    val sss: List<String> = html.split("@_@")
                                                    for (i in sss) {
                                                        if (i.contains("scontent") || i.contains(
                                                                "cdninstagram"
                                                            )
                                                        ) {
                                                            if (i.contains(".jpg")) {
                                                                Log.d(
                                                                    "HTMLimg",
                                                                    "" + i
                                                                )
                                                                listoflink_photos.add(i)
                                                            } else if (i.contains(".mp4")) {
                                                                Log.d(
                                                                    "HTML vid",
                                                                    "" + i
                                                                )
                                                                listoflink_videos.add(i)
                                                            }
                                                        }
                                                    }
                                                    if (!isdownloadstarted[0] && (listoflink_videos.size > 0 || listoflink_photos.size > 0)) {
                                                        dismissMyDialogFrag()
                                                        handler2.removeCallbacksAndMessages(
                                                            null
                                                        )
                                                        isdownloadstarted[0] = true
                                                        if (listoflink_videos != null || listoflink_photos != null || listoflink_videos.size > 0 || listoflink_photos.size > 0) {

                                                            listoflink_videos =
                                                                iUtils.removeDuplicates(
                                                                    listoflink_videos
                                                                )
                                                            listoflink_photos =
                                                                iUtils.removeDuplicates(
                                                                    listoflink_photos
                                                                )



                                                            for (i in listoflink_videos) {


                                                                DownloadFileMain.startDownloading(
                                                                    myselectedActivity!!,
                                                                    i,
                                                                    myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getVideoFilenameFromURL(
                                                                        i
                                                                    ),
                                                                    ".mp4"
                                                                )

                                                            }

                                                            for (i in listoflink_photos) {

                                                                DownloadFileMain.startDownloading(
                                                                    myselectedActivity!!,
                                                                    i,
                                                                    myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getImageFilenameFromURL(
                                                                        i
                                                                    ),
                                                                    ".png"
                                                                )
                                                            }
                                                        }
                                                    } else {

                                                        //                                                                handler2.removeCallbacksAndMessages(
                                                        //                                                                        null
                                                        //                                                                );
                                                        //
                                                        //                                                                InstagramActivity.this.runOnUiThread(new Runnable() {
                                                        //                                                                    @Override
                                                        //                                                                    public void run() {
                                                        //
                                                        //                                                                        Log.d(
                                                        //                                                                                "HTML nolink fould",
                                                        //                                                                                ""
                                                        //                                                                        );
                                                        //
                                                        //                                                                        try {
                                                        //                                                                            System.err.println("workkkkkkkkk 4");
                                                        //
                                                        //
                                                        //                                                                            System.err.println("workkkkkkkkk 4.5");
                                                        //
                                                        //                                                                            callDownloadServer4(urlwithoutlettersqp);
                                                        //
                                                        //
                                                        //                                                                        } catch (Exception e) {
                                                        //                                                                            Utils.hideProgressDialog(InstagramActivity.this);
                                                        //                                                                            System.err.println("workkkkkkkkk 5.1");
                                                        //                                                                            e.printStackTrace();
                                                        //
                                                        //
                                                        //                                                                        }
                                                        //
                                                        //                                                                    }
                                                        //
                                                        //                                                                });
                                                    }
                                                }
                                            })
                                            handler2.postDelayed(this, 3500)
                                        }
                                    }, 3000)
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    dismissMyDialogErrorToastFrag()
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        dismissMyDialogErrorToastFrag()
                    }
                }

                override fun onLoadResource(view: WebView, url: String) {
                    super.onLoadResource(view, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return super.shouldInterceptRequest(view, request)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
                ): WebResourceResponse? {
                    if (url!!.contains("google") || url.contains("facebook")) {
                        val textStream: InputStream = ByteArrayInputStream("".toByteArray())
                        return WebResourceResponse("text/plain", "UTF-8", textStream)
                    }
                    return super.shouldInterceptRequest(view, url)
                }

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    dismissMyDialogErrorToastFrag()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    // view.loadUrl(urlwithoutlettersqp);
                    return false
                }
            }
            CookieSyncManager.createInstance(myselectedActivity)
            binding.webViewInsta.loadUrl("https://igram.io/")
        } catch (e: java.lang.Exception) {
            dismissMyDialogErrorToastFrag()
            e.printStackTrace()
        }
    }


    @Keep
    private fun loginDownloadgram(urlwithoutlettersqp: String) {
        try {
            binding.webViewInsta.clearCache(true)
            val cookieManager1 = CookieManager.getInstance()
            cookieManager1.setAcceptThirdPartyCookies(binding.webViewInsta, true)
            cookieManager1.setAcceptCookie(true)
            cookieManager1.acceptCookie()
            binding.webViewInsta.clearFormData()
            binding.webViewInsta.settings.saveFormData = true
            val random1 = Random()
            val j = random1.nextInt(iUtils.UserAgentsList.size)
            binding.webViewInsta.settings.setUserAgentString(iUtils.UserAgentsListLogin.get(j))
            binding.webViewInsta.settings.allowFileAccess = true
            binding.webViewInsta.settings.javaScriptEnabled = true
            binding.webViewInsta.settings.defaultTextEncodingName = "UTF-8"
            binding.webViewInsta.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            binding.webViewInsta.settings.builtInZoomControls = true
            binding.webViewInsta.settings.setSupportZoom(true)
            binding.webViewInsta.settings.useWideViewPort = true
            binding.webViewInsta.settings.domStorageEnabled = true
            binding.webViewInsta.settings.loadWithOverviewMode = true
            binding.webViewInsta.settings.loadsImagesAutomatically = true
            binding.webViewInsta.settings.blockNetworkImage = false
            binding.webViewInsta.settings.blockNetworkLoads = false
            val isdownloadstarted = booleanArrayOf(false)
            val isdownloadclicked = booleanArrayOf(false)
            Log.e(
                "workkkk sel",
                "binding.loggedIn "
            )
            val handler2 = Handler()
            var listoflink_videos = ArrayList<String>()
            var listoflink_photos = ArrayList<String>()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            binding.webViewInsta.webViewClient = object : WebViewClient() {

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.e(
                        "workkkk url",
                        "binding.progressBar reciveing data0 $url"
                    )
                    try {
                        val jsscript = ("javascript:(function() { "
                                + "var ell = document.getElementById('url');" // + "ell[1].value ='" + "keepsaveit" + "';"
                                // + "ell[2].value ='" + "keepsaveit12345" + "';"
                                + "ell.value ='" + urlwithoutlettersqp + "';"
                                + "var bbb = document.getElementById('submit');"
                                + "bbb.click();"
                                + "})();")
                        if (!isdownloadclicked[0]) {
                            binding.webViewInsta.evaluateJavascript(jsscript) { value ->
                                isdownloadclicked[0] = true
                                Log.e(
                                    "workkkk0",
                                    "binding.progressBar reciveing data1 $value"
                                )
                                try {
                                    handler2.postDelayed(object : Runnable {
                                        override fun run() {
                                            myselectedActivity!!.runOnUiThread(Runnable {
                                                binding.webViewInsta.evaluateJavascript(
                                                    "(function() { " +
                                                            "var text='';" +
                                                            "var aaa = document.getElementsByTagName('a');" +
                                                            "for (var i = 0; i < aaa.length; i++) {" +  // "if(aaa[i].getAttribute('href').includes('https://scontent') || aaa[i].getAttribute('href').includes('https://instagram')){" +
                                                            "  text += aaa[i].getAttribute('href')+'@_@';" +  //  "}" +
                                                            "}" +
                                                            "var withoutLast3 = text.slice(0, -3);" +
                                                            "return withoutLast3+''; })();"
                                                ) { html ->
                                                    Log.e(
                                                        "workkkk0",
                                                        "binding.progressBar reciveing data2 $html"
                                                    )
                                                    val sss: List<String> = html.split("@_@")
                                                    for (i in sss) {
                                                        if (i.contains("scontent") || i.contains(
                                                                "cdninstagram"
                                                            )
                                                        ) {
                                                            if (i.contains(".jpg")) {
                                                                Log.d(
                                                                    "HTMLimg",
                                                                    "" + i
                                                                )
                                                                listoflink_photos.add(i)
                                                            } else if (i.contains(".mp4")) {
                                                                Log.d(
                                                                    "HTML vid",
                                                                    "" + i
                                                                )
                                                                listoflink_videos.add(i)
                                                            }
                                                        }
                                                    }
                                                    if (!isdownloadstarted[0] && (listoflink_videos.size > 0 || listoflink_photos.size > 0)) {
                                                        dismissMyDialogFrag()
                                                        handler2.removeCallbacksAndMessages(
                                                            null
                                                        )
                                                        isdownloadstarted[0] = true
                                                        if (listoflink_videos != null || listoflink_photos != null || listoflink_videos.size > 0 || listoflink_photos.size > 0) {

                                                            listoflink_videos =
                                                                iUtils.removeDuplicates(
                                                                    listoflink_videos
                                                                )
                                                            listoflink_photos =
                                                                iUtils.removeDuplicates(
                                                                    listoflink_photos
                                                                )



                                                            for (i in listoflink_videos) {


                                                                DownloadFileMain.startDownloading(
                                                                    myselectedActivity!!,
                                                                    i,
                                                                    myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getVideoFilenameFromURL(
                                                                        i
                                                                    ),
                                                                    ".mp4"
                                                                )

                                                            }

                                                            for (i in listoflink_photos) {

                                                                DownloadFileMain.startDownloading(
                                                                    myselectedActivity!!,
                                                                    i,
                                                                    myInstaUsername + "_instagram_" + System.currentTimeMillis() + "_" + iUtils.getImageFilenameFromURL(
                                                                        i
                                                                    ),
                                                                    ".png"
                                                                )
                                                            }
                                                        }
                                                    } else {


                                                    }
                                                }
                                            })
                                            handler2.postDelayed(this, 3500)
                                        }
                                    }, 3000)
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    dismissMyDialogErrorToastFrag()
                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        dismissMyDialogErrorToastFrag()
                    }
                }

                override fun onLoadResource(view: WebView, url: String) {
                    super.onLoadResource(view, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return super.shouldInterceptRequest(view, request)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
                ): WebResourceResponse? {
                    if (url!!.contains("google") || url.contains("facebook")) {
                        val textStream: InputStream = ByteArrayInputStream("".toByteArray())
                        return WebResourceResponse("text/plain", "UTF-8", textStream)
                    }
                    return super.shouldInterceptRequest(view, url)
                }

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    dismissMyDialogErrorToastFrag()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    // view.loadUrl(urlwithoutlettersqp);
                    return false
                }
            }
            CookieSyncManager.createInstance(myselectedActivity)
            binding.webViewInsta.loadUrl("https://downloadgram.org/")
        } catch (e: java.lang.Exception) {
            dismissMyDialogErrorToastFrag()
            e.printStackTrace()
        }
    }


    @Keep
    fun downloadInstagramImageOrVideoResponseOkhttp(URL: String?) {

//TODO check
//        Unirest.config()
//            .socketTimeout(500)
//            .connectTimeout(1000)
//            .concurrency(10, 5)
//            .proxy(Proxy("https://proxy"))
//            .setDefaultHeader("Accept", "application/json")
//            .followRedirects(false)
//            .enableCookieManagement(false)
//            .addInterceptor(MyCustomInterceptor())

        object : Thread() {
            override fun run() {


                try {

                    val cookieJar: ClearableCookieJar = PersistentCookieJar(
                        SetCookieCache(),
                        SharedPrefsCookiePersistor(myselectedActivity)
                    )

                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    // init OkHttpClient
                    val client: OkHttpClient = OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .addInterceptor(logging)
                        .build()

                    val request: Request = Request.Builder()
                        .url("$URL?__a=1&__d=dis")
                        .method("GET", null)
                        .build()
                    val response = client.newCall(request).execute()

                    val ressd = response.body!!.string()
                    var code = response.code
                    if (!ressd.contains("shortcode_media")) {
                        code = 400
                    }
                    if (code == 200) {


                        try {


                            val listType =
                                object : TypeToken<ModelInstagramResponse?>() {}.type
                            val modelInstagramResponse: ModelInstagramResponse? =
                                GsonBuilder().create()
                                    .fromJson<ModelInstagramResponse>(
                                        ressd,
                                        listType
                                    )


                            if (modelInstagramResponse != null) {


                                if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                    val modelGetEdgetoNode: ModelGetEdgetoNode =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children

                                    val modelEdNodeArrayList: List<ModelEdNode> =
                                        modelGetEdgetoNode.modelEdNodes
                                    for (i in 0 until modelEdNodeArrayList.size) {
                                        if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                            myVideoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.video_url


                                            DownloadFileMain.startDownloading(
                                                myselectedActivity!!,
                                                myVideoUrlIs,
                                                myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                    myVideoUrlIs
                                                ),
                                                ".mp4"
                                            )
                                            dismissMyDialogFrag()


                                            myVideoUrlIs = ""
                                        } else {
                                            myPhotoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src

                                            DownloadFileMain.startDownloading(
                                                myselectedActivity!!,
                                                myPhotoUrlIs,
                                                myInstaUsername + iUtils.getImageFilenameFromURL(
                                                    myPhotoUrlIs
                                                ),
                                                ".png"
                                            )
                                            myPhotoUrlIs = ""
                                            dismissMyDialogFrag()
                                            // etText.setText("")
                                        }
                                    }
                                } else {
                                    val isVideo =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                                    if (isVideo) {
                                        myVideoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url


                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myVideoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myVideoUrlIs
                                            ),
                                            ".mp4"
                                        )
                                        dismissMyDialogFrag()
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src


                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myPhotoUrlIs,
                                            myInstaUsername + iUtils.getImageFilenameFromURL(
                                                myPhotoUrlIs
                                            ),
                                            ".png"
                                        )
                                        dismissMyDialogFrag()
                                        myPhotoUrlIs = ""
                                    }
                                }


                            } else {
                                Toast.makeText(
                                    myselectedActivity!!,
                                    resources.getString(R.string.somthing),
                                    Toast.LENGTH_SHORT
                                ).show()

                                dismissMyDialogFrag()

                            }


                        } catch (e: Exception) {
                            myselectedActivity!!.runOnUiThread {
                                progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2")
                            }
                            downloadInstagramImageOrVideoResOkhttpM2(URL!!)

                        }

                    } else {
                        myselectedActivity!!.runOnUiThread {
                            progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2")
                        }
                        downloadInstagramImageOrVideoResOkhttpM2(URL!!)
                    }


                } catch (e: Throwable) {
                    e.printStackTrace()
                    println("The request has failed " + e.message)
                    myselectedActivity!!.runOnUiThread {
                        progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2")
                    }
                    downloadInstagramImageOrVideoResOkhttpM2(URL!!)
                }
            }
        }.start()
    }


    @Keep
    fun downloadInstagramImageOrVideoResOkhttpM2(URL: String?) {


        try {


            val cookieJar: ClearableCookieJar = PersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(myselectedActivity)
            )

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            // init OkHttpClient
            val client: OkHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .build()

            val request: Request = Request.Builder()
                .url(URL + "embed/captioned/")
                .method("GET", null)
                .build()
            val response = client.newCall(request).execute()

            val ss = response.body!!.string()
            var code = response.code
            if (!ss.contains("shortcode_media")) {
                code = 400
            }
            if (code == 200) {

                try {


                    val start = "window.__additionalDataLoaded("
                    val end = "}}});"
                    var dd: String =
                        ss.substring(ss.indexOf(start) + 38, ss.indexOf(end) + 3).trim()
                    dd = "{\"graphql\":$dd,\"showQRModal\":false\"}"

                    //  println("HttpResponse ddffd " + dd)
                    println("HttpResponse ddffd23 " + dd.substring(dd.length - 30))


                    val listType = object : TypeToken<ModelInstagramResponse?>() {}.type
                    val modelInstagramResponse: ModelInstagramResponse? =
                        GsonBuilder().create()
                            .fromJson<ModelInstagramResponse>(
                                dd,
                                listType
                            )


                    if (modelInstagramResponse != null) {


                        if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                            val modelGetEdgetoNode: ModelGetEdgetoNode =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children

                            val modelEdNodeArrayList: List<ModelEdNode> =
                                modelGetEdgetoNode.modelEdNodes
                            for (i in 0 until modelEdNodeArrayList.size) {
                                if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                    myVideoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.video_url


                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    dismissMyDialogFrag()


                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src

                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getImageFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    myPhotoUrlIs = ""
                                    dismissMyDialogFrag()
                                    // etText.setText("")
                                }
                            }
                        } else {
                            val isVideo =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                            if (isVideo) {
                                myVideoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url


                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myVideoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(
                                        myVideoUrlIs
                                    ),
                                    ".mp4"
                                )
                                dismissMyDialogFrag()
                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src


                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myPhotoUrlIs,
                                    myInstaUsername + iUtils.getImageFilenameFromURL(
                                        myPhotoUrlIs
                                    ),
                                    ".png"
                                )
                                dismissMyDialogFrag()
                                myPhotoUrlIs = ""
                            }
                        }


                    } else {
                        Toast.makeText(
                            myselectedActivity!!,
                            resources.getString(R.string.somthing),
                            Toast.LENGTH_SHORT
                        ).show()

                        dismissMyDialogFrag()


                    }


                } catch (e: Exception) {

                    myselectedActivity!!.runOnUiThread {
                        progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3")
                    }
                    downloadInstagramImageOrVideo_tikinfApi(URL)


                }

            } else {

                myselectedActivity!!.runOnUiThread {
                    progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3")
                }
                downloadInstagramImageOrVideo_tikinfApi(URL)
            }


        } catch (e: Throwable) {
            e.printStackTrace()
            println("The request has failed " + e.message)
            myselectedActivity!!.runOnUiThread {
                progressDralogGenaratinglink.setMessage("Method 2 failed trying method 3")
            }
            downloadInstagramImageOrVideo_tikinfApi(URL)
        }
    }


    @Keep
    fun downloadInstagramImageOrVideodata_old(URL: String?, Cookie: String?) {
        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        object : Thread() {
            override fun run() {
                Looper.prepare()
                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(URL!!)
                    .method("GET", null)
                    .addHeader("Cookie", Cookie!!)
                    .addHeader(
                        "User-Agent",
                        iUtils.UserAgentsList[j]
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()

                    System.err.println("workkkkkkkkk 6 " + response.code)

                    if (response.code == 200) {

                        try {
                            val ressss = response.body!!.string()
                            System.err.println("workkkkkkkkk 6.78 $ressss")

                            val listType: Type =
                                object : TypeToken<ModelInstagramResponse?>() {}.type
                            val modelInstagramResponse: ModelInstagramResponse = Gson().fromJson(
                                ressss,
                                listType
                            )

                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                val modelGetEdgetoNode: ModelGetEdgetoNode =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                                myInstaUsername =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                                val modelEdNodeArrayList: List<ModelEdNode> =
                                    modelGetEdgetoNode.modelEdNodes
                                for (i in modelEdNodeArrayList.indices) {
                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                        myVideoUrlIs = modelEdNodeArrayList[i].modelNode.video_url
                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myVideoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myVideoUrlIs
                                            ),
                                            ".mp4"
                                        )
                                        // etText.setText("")
                                        try {
                                            dismissMyDialogFrag()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myPhotoUrlIs,
                                            myInstaUsername + iUtils.getImageFilenameFromURL(
                                                myPhotoUrlIs
                                            ),
                                            ".png"
                                        )
                                        myPhotoUrlIs = ""
                                        try {
                                            dismissMyDialogFrag()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        // etText.setText("")
                                    }
                                }
                            } else {
                                val isVideo =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video

                                myInstaUsername =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                                if (isVideo) {
                                    myVideoUrlIs =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getImageFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myPhotoUrlIs = ""
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
                            try {
                                try {
                                    System.err.println("workkkkkkkkk 4")
                                    downloadInstagramImageOrVideodata(
                                        URL, ""
                                    )
                                } catch (e: java.lang.Exception) {
                                    dismissMyDialogFrag()
                                    System.err.println("workkkkkkkkk 5.1")
                                    e.printStackTrace()
                                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                myselectedActivity!!.runOnUiThread {
                                    dismissMyDialogFrag()

                                    if (!myselectedActivity!!.isFinishing) {
                                        val alertDialog =
                                            AlertDialog.Builder(myselectedActivity!!)
                                                .create()
                                        alertDialog.setTitle(getString(R.string.logininsta))
                                        alertDialog.setMessage(getString(R.string.urlisprivate))
                                        alertDialog.setButton(
                                            AlertDialog.BUTTON_POSITIVE,
                                            getString(R.string.logininsta)
                                        ) { dialog, _ ->
                                            dialog.dismiss()
                                            val intent = Intent(
                                                myselectedActivity!!,
                                                InstagramLoginActivity::class.java
                                            )
                                            startActivityForResult(intent, 200)
                                        }
                                        alertDialog.setButton(
                                            AlertDialog.BUTTON_NEGATIVE,
                                            getString(R.string.cancel)
                                        ) { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        alertDialog.show()
                                    }
                                }
                            }
                        }

                    } else {
                        object : Thread() {
                            override fun run() {

                                val client = OkHttpClient().newBuilder()
                                    .build()
                                val request: Request = Request.Builder()
                                    .url(URL)
                                    .method("GET", null)
                                    .addHeader("Cookie", iUtils.myInstagramTempCookies)
                                    .addHeader(
                                        "User-Agent",
                                        iUtils.UserAgentsList[j]
                                    ).build()
                                try {
                                    val response1: Response = client.newCall(request).execute()
                                    System.err.println("workkkkkkkkk 6 1 " + response1.code)

                                    if (response1.code == 200) {
                                        try {
                                            val listType: Type =
                                                object :
                                                    TypeToken<ModelInstagramResponse?>() {}.type
                                            val modelInstagramResponse: ModelInstagramResponse =
                                                Gson().fromJson(
                                                    response1.body!!.string(),
                                                    listType
                                                )
                                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                                val modelGetEdgetoNode: ModelGetEdgetoNode =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                                                myInstaUsername =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                                                val modelEdNodeArrayList: List<ModelEdNode> =
                                                    modelGetEdgetoNode.modelEdNodes
                                                for (i in modelEdNodeArrayList.indices) {
                                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                                        myVideoUrlIs =
                                                            modelEdNodeArrayList[i].modelNode.video_url
                                                        DownloadFileMain.startDownloading(
                                                            myselectedActivity!!,
                                                            myVideoUrlIs,
                                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                                myVideoUrlIs
                                                            ),
                                                            ".mp4"
                                                        )
                                                        // etText.setText("")
                                                        try {
                                                            dismissMyDialogFrag()
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                        myVideoUrlIs = ""
                                                    } else {
                                                        myPhotoUrlIs =
                                                            modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                                        DownloadFileMain.startDownloading(
                                                            myselectedActivity!!,
                                                            myPhotoUrlIs,
                                                            myInstaUsername + iUtils.getImageFilenameFromURL(
                                                                myPhotoUrlIs
                                                            ),
                                                            ".png"
                                                        )
                                                        myPhotoUrlIs = ""
                                                        try {
                                                            dismissMyDialogFrag()
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                        // etText.setText("")
                                                    }
                                                }
                                            } else {
                                                val isVideo =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                                                myInstaUsername =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username + "_"

                                                if (isVideo) {
                                                    myVideoUrlIs =
                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                                    DownloadFileMain.startDownloading(
                                                        myselectedActivity!!,
                                                        myVideoUrlIs,
                                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                            myVideoUrlIs
                                                        ),
                                                        ".mp4"
                                                    )
                                                    try {
                                                        dismissMyDialogFrag()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    myVideoUrlIs = ""
                                                } else {
                                                    myPhotoUrlIs =
                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                                    DownloadFileMain.startDownloading(
                                                        myselectedActivity!!,
                                                        myPhotoUrlIs,
                                                        myInstaUsername + iUtils.getImageFilenameFromURL(
                                                            myPhotoUrlIs
                                                        ),
                                                        ".png"
                                                    )
                                                    try {
                                                        dismissMyDialogFrag()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    myPhotoUrlIs = ""
                                                }
                                            }
                                        } catch
                                            (e: java.lang.Exception) {
                                            System.err.println("workkkkkkkkk 4vvv errrr " + e.message)
                                            e.printStackTrace()
                                            try {
                                                dismissMyDialogFrag()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    } else {
                                        System.err.println("workkkkkkkkk 6bbb errrr ")
                                        myselectedActivity!!.runOnUiThread {
                                            dismissMyDialogFrag()

                                            if (!myselectedActivity!!.isFinishing) {
                                                val alertDialog =
                                                    AlertDialog.Builder(myselectedActivity!!)
                                                        .create()
                                                alertDialog.setTitle(getString(R.string.logininsta))
                                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_POSITIVE,
                                                    getString(R.string.logininsta)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                    val intent = Intent(
                                                        myselectedActivity!!,
                                                        InstagramLoginActivity::class.java
                                                    )
                                                    startActivityForResult(intent, 200)
                                                }
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_NEGATIVE,
                                                    getString(R.string.cancel)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                alertDialog.show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }.start()
                    }
                    println("working errpr \t Value: " + response.body!!.string())
                } catch (e: Exception) {
                    try {
                        println("response1122334455:   " + "Failed1 " + e.message)
                        dismissMyDialogFrag()
                    } catch (e: Exception) {

                    }
                }
            }
        }.start()
    }


    @Keep
    fun downloadInstagramImageOrVideodata_old_withlogin(URL: String?, Cookie: String?) {
        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        object : Thread() {
            override fun run() {
                Looper.prepare()
                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(URL!!)
                    .method("GET", null)
                    .addHeader("Cookie", Cookie!!)
                    .addHeader(
                        "User-Agent",
                        iUtils.UserAgentsList[j]
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()

                    if (response.code == 200) {

                        val ress = response.body!!.string()
                        println("working runed \t Value: $ress")

                        try {
                            val listType: Type =
                                object : TypeToken<ModelInstaWithLogin?>() {}.type
                            val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                                ress,
                                listType
                            )
                            println("workkkkk777 " + modelInstagramResponse.items[0].code)


                            if (modelInstagramResponse.items[0].mediaType == 8) {


                                println("workkkkk777 mediacount " + modelInstagramResponse.items[0].carouselMediaCount)


                                myInstaUsername =
                                    modelInstagramResponse.items[0].user.username + "_"

                                val modelGetEdgetoNode = modelInstagramResponse.items[0]
                                val modelEdNodeArrayList: List<CarouselMedia> =
                                    modelGetEdgetoNode.carouselMedia
                                for (i in modelEdNodeArrayList.indices) {
                                    if (modelEdNodeArrayList[i].mediaType == 2) {
                                        myVideoUrlIs =
                                            modelEdNodeArrayList[i].videoVersions[0].geturl()
                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myVideoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myVideoUrlIs
                                            ),
                                            ".mp4"
                                        )
                                        // etText.setText("")
                                        try {
                                            dismissMyDialogFrag()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                                .geturl()
                                        DownloadFileMain.startDownloading(
                                            myselectedActivity!!,
                                            myPhotoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myPhotoUrlIs
                                            ),
                                            ".png"
                                        )
                                        myPhotoUrlIs = ""
                                        try {
                                            dismissMyDialogFrag()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        // etText.setText("")
                                    }
                                }
                            } else {
                                val modelGetEdgetoNode = modelInstagramResponse.items[0]
                                myInstaUsername =
                                    modelInstagramResponse.items[0].user.username + "_"

                                if (modelGetEdgetoNode.mediaType == 2) {
                                    myVideoUrlIs =
                                        modelGetEdgetoNode.videoVersions[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        myselectedActivity!!,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myPhotoUrlIs
                                        ),

                                        ".png"
                                    )
                                    try {
                                        dismissMyDialogFrag()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myPhotoUrlIs = ""
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            System.err.println("workkkkkkkkk 5nny errrr " + e.message)
                            try {
                                try {
                                    System.err.println("workkkkkkkkk 4")

                                    val sharedPrefsFor =
                                        SharedPrefsForInstagram(myselectedActivity!!)
                                    val map = sharedPrefsFor.preference
                                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                                    ) {
                                        System.err.println("workkkkkkkkk 5.5")
                                        downloadInstagramImageOrVideodata(
                                            URL, "ds_user_id=" + map.preferencE_USERID
                                                    + "; sessionid=" + map.preferencE_SESSIONID
                                        )
                                    } else {
                                        dismissMyDialogFrag()
                                        System.err.println("workkkkkkkkk 5.1")
                                        e.printStackTrace()
                                        ShowToast(
                                            myselectedActivity!!,
                                            getString(R.string.error_occ)
                                        )
                                    }
                                } catch (e: java.lang.Exception) {
                                    dismissMyDialogFrag()
                                    System.err.println("workkkkkkkkk 5.1")
                                    e.printStackTrace()
                                    ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                try {
                                    myselectedActivity!!.runOnUiThread {
                                        dismissMyDialogFrag()
                                        if (!myselectedActivity!!.isFinishing) {
                                            val alertDialog =
                                                AlertDialog.Builder(myselectedActivity!!).create()
                                            alertDialog.setTitle(getString(R.string.logininsta))
                                            alertDialog.setMessage(getString(R.string.urlisprivate))
                                            alertDialog.setButton(
                                                AlertDialog.BUTTON_POSITIVE,
                                                getString(R.string.logininsta)
                                            ) { dialog, _ ->
                                                dialog.dismiss()
                                                val intent = Intent(
                                                    myselectedActivity!!,
                                                    InstagramLoginActivity::class.java
                                                )
                                                startActivityForResult(intent, 200)
                                            }
                                            alertDialog.setButton(
                                                AlertDialog.BUTTON_NEGATIVE,
                                                getString(R.string.cancel)
                                            ) { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            alertDialog.show()
                                        }
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        }
                    } else {
                        object : Thread() {
                            override fun run() {

                                val client = OkHttpClient().newBuilder()
                                    .build()
                                val request: Request = Request.Builder()
                                    .url(URL)
                                    .method("GET", null)
                                    .addHeader("Cookie", iUtils.myInstagramTempCookies)
                                    .addHeader(
                                        "User-Agent",
                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36"
                                    ).build()
                                try {
                                    val response1: Response = client.newCall(request).execute()

                                    if (response1.code == 200) {

                                        try {
                                            val listType: Type =
                                                object : TypeToken<ModelInstaWithLogin?>() {}.type
                                            val modelInstagramResponse: ModelInstaWithLogin =
                                                Gson().fromJson(
                                                    response.body!!.string(),
                                                    listType
                                                )
                                            if (modelInstagramResponse.items[0].mediaType == 8) {
                                                myInstaUsername =
                                                    modelInstagramResponse.items[0].user.username + "_"

                                                val modelGetEdgetoNode =
                                                    modelInstagramResponse.items[0]
                                                val modelEdNodeArrayList: List<CarouselMedia> =
                                                    modelGetEdgetoNode.carouselMedia
                                                for (i in modelEdNodeArrayList.indices) {
                                                    if (modelEdNodeArrayList[i].mediaType == 2) {
                                                        myVideoUrlIs =
                                                            modelEdNodeArrayList[i].videoVersions[0].geturl()
                                                        DownloadFileMain.startDownloading(
                                                            myselectedActivity!!,
                                                            myVideoUrlIs,
                                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                                myVideoUrlIs
                                                            ),
                                                            ".mp4"
                                                        )
                                                        // etText.setText("")
                                                        try {
                                                            dismissMyDialogFrag()
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                        myVideoUrlIs = ""
                                                    } else {
                                                        myPhotoUrlIs =
                                                            modelEdNodeArrayList[i].imageVersions2.candidates[0].geturl()
                                                        DownloadFileMain.startDownloading(
                                                            myselectedActivity!!,
                                                            myPhotoUrlIs,
                                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                                myPhotoUrlIs
                                                            ),
                                                            ".png"
                                                        )
                                                        myPhotoUrlIs = ""
                                                        try {
                                                            dismissMyDialogFrag()
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                        // etText.setText("")
                                                    }
                                                }
                                            } else {
                                                myInstaUsername =
                                                    modelInstagramResponse.items[0].user.username + "_"

                                                val modelGetEdgetoNode =
                                                    modelInstagramResponse.items[0]
                                                if (modelGetEdgetoNode.mediaType == 2) {
                                                    myVideoUrlIs =
                                                        modelGetEdgetoNode.videoVersions[0]
                                                            .geturl()
                                                    DownloadFileMain.startDownloading(
                                                        myselectedActivity!!,
                                                        myVideoUrlIs,
                                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                            myVideoUrlIs
                                                        ),
                                                        ".mp4"
                                                    )
                                                    try {
                                                        dismissMyDialogFrag()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    myVideoUrlIs = ""
                                                } else {
                                                    myPhotoUrlIs =
                                                        modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                                    DownloadFileMain.startDownloading(
                                                        myselectedActivity!!,
                                                        myPhotoUrlIs,
                                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                            myPhotoUrlIs
                                                        ),
                                                        ".png"
                                                    )
                                                    try {
                                                        dismissMyDialogFrag()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    myPhotoUrlIs = ""
                                                }
                                            }
                                        } catch (e: java.lang.Exception) {
                                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
                                            e.printStackTrace()
                                            try {
                                                myselectedActivity!!.runOnUiThread {
                                                    dismissMyDialogFrag()

                                                    if (!myselectedActivity!!.isFinishing) {
                                                        val alertDialog =
                                                            AlertDialog.Builder(myselectedActivity!!)
                                                                .create()
                                                        alertDialog.setTitle(getString(R.string.logininsta))
                                                        alertDialog.setMessage(getString(R.string.urlisprivate))
                                                        alertDialog.setButton(
                                                            AlertDialog.BUTTON_POSITIVE,
                                                            getString(R.string.logininsta)
                                                        ) { dialog, _ ->
                                                            dialog.dismiss()
                                                            val intent = Intent(
                                                                myselectedActivity!!,
                                                                InstagramLoginActivity::class.java
                                                            )
                                                            startActivityForResult(intent, 200)
                                                        }
                                                        alertDialog.setButton(
                                                            AlertDialog.BUTTON_NEGATIVE,
                                                            getString(R.string.cancel)
                                                        ) { dialog, _ ->
                                                            dialog.dismiss()
                                                        }
                                                        alertDialog.show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    } else {
                                        System.err.println("workkkkkkkkk 6bbb errrr ")
                                        myselectedActivity!!.runOnUiThread {
                                            dismissMyDialogFrag()

                                            if (!myselectedActivity!!.isFinishing) {
                                                val alertDialog =
                                                    AlertDialog.Builder(myselectedActivity!!)
                                                        .create()
                                                alertDialog.setTitle(getString(R.string.logininsta))
                                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_POSITIVE,
                                                    getString(R.string.logininsta)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                    val intent = Intent(
                                                        myselectedActivity!!,
                                                        InstagramLoginActivity::class.java
                                                    )
                                                    startActivityForResult(intent, 200)
                                                }
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_NEGATIVE,
                                                    getString(R.string.cancel)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                alertDialog.show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }.start()
                    }
                } catch (e: Exception) {
                    try {
                        println("response1122334455:   " + "Failed1 " + e.message)
                        dismissMyDialogFrag()
                    } catch (e: Exception) {

                    }
                }
            }
        }.start()
    }

    @Keep
    fun downloadInstagramImageOrVideodata_withlogin(URL: String?, Cookie: String?) {
        /*instagram product types
        * product_type
        *
        * igtv "media_type": 2
        * carousel_container  "media_type": 8
        * clips  "media_type": 2
        * feed   "media_type": 1
        * */

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)

        var Cookie = Cookie
        if (TextUtils.isEmpty(Cookie)) {
            Cookie = ""
        }
        val apiService: RetrofitApiInterface =
            RetrofitClient.getClient()

        val callResult: Call<JsonObject> = apiService.getInstagramData(
            URL,
            Cookie,
            iUtils.UserAgentsList[j]
        )
        callResult.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: retrofit2.Response<JsonObject?>
            ) {

                try {
                    val listType: Type =
                        object : TypeToken<ModelInstaWithLogin?>() {}.type
                    val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                        response.body(),
                        listType
                    )
                    println("workkkkk777 " + modelInstagramResponse.items[0].code)

                    if (modelInstagramResponse.items[0].mediaType == 8) {
                        myInstaUsername = modelInstagramResponse.items[0].user.username + "_"

                        val modelGetEdgetoNode = modelInstagramResponse.items[0]

                        val modelEdNodeArrayList: List<CarouselMedia> =
                            modelGetEdgetoNode.carouselMedia
                        for (i in modelEdNodeArrayList.indices) {

                            System.err.println("workkkkkkkkklogin issue " + modelEdNodeArrayList[i].mediaType)


                            if (modelEdNodeArrayList[i].mediaType == 2) {
                                System.err.println("workkkkkkkkklogin issue vid " + modelEdNodeArrayList[i].videoVersions[0].geturl())


                                myVideoUrlIs =
                                    modelEdNodeArrayList[i].videoVersions[0].geturl()
                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myVideoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                    ".mp4"
                                )
                                // etText.setText("")
                                try {
                                    dismissMyDialogFrag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                myVideoUrlIs = ""
                            } else {

                                System.err.println("workkkkkkkkklogin issue img " + modelEdNodeArrayList[i].imageVersions2.candidates[0].geturl())


                                myPhotoUrlIs =
                                    modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                        .geturl()
                                DownloadFileMain.startDownloading(
                                    myselectedActivity!!,
                                    myPhotoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                    ".png"
                                )
                                myPhotoUrlIs = ""
                                try {
                                    dismissMyDialogFrag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                // etText.setText("")
                            }
                        }
                    } else {
                        val modelGetEdgetoNode = modelInstagramResponse.items[0]
                        myInstaUsername = modelInstagramResponse.items[0].user.username + "_"

                        if (modelGetEdgetoNode.mediaType == 2) {
                            myVideoUrlIs =
                                modelGetEdgetoNode.videoVersions[0].geturl()
                            DownloadFileMain.startDownloading(
                                myselectedActivity!!,
                                myVideoUrlIs,
                                myInstaUsername + iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                ".mp4"
                            )
                            try {
                                dismissMyDialogFrag()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            myVideoUrlIs = ""
                        } else {
                            myPhotoUrlIs =
                                modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                            DownloadFileMain.startDownloading(
                                myselectedActivity!!,
                                myPhotoUrlIs,
                                myInstaUsername + iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                ".png"
                            )
                            try {
                                dismissMyDialogFrag()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            myPhotoUrlIs = ""
                        }
                    }

                } catch (e: java.lang.Exception) {
                    System.err.println("workkkkkkkkk 5nn errrr " + e.message)



                    try {

                        try {
                            System.err.println("workkkkkkkkk 4")

                            val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
                            val map = sharedPrefsFor.preference
                            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                            ) {
                                System.err.println("workkkkkkkkk 5.2")
                                downloadInstagramImageOrVideodata_old(
                                    URL, "ds_user_id=" + map.preferencE_USERID
                                            + "; sessionid=" + map.preferencE_SESSIONID
                                )
                            } else {
                                dismissMyDialogFrag()
                                System.err.println("workkkkkkkkk 5.1")
                                e.printStackTrace()
                                ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                            }
                        } catch (e: java.lang.Exception) {
                            dismissMyDialogFrag()
                            System.err.println("workkkkkkkkk 5.1")
                            e.printStackTrace()
                            ShowToast(myselectedActivity!!, getString(R.string.error_occ))
                        }

                    } catch (e: Exception) {

                        e.printStackTrace()
                        myselectedActivity!!.runOnUiThread {
                            dismissMyDialogFrag()
                            if (!myselectedActivity!!.isFinishing) {
                                val alertDialog =
                                    AlertDialog.Builder(myselectedActivity!!)
                                        .create()
                                alertDialog.setTitle(getString(R.string.logininsta))
                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE,
                                    getString(R.string.logininsta)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    val intent = Intent(
                                        myselectedActivity!!,
                                        InstagramLoginActivity::class.java
                                    )
                                    startActivityForResult(intent, 200)
                                }
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_NEGATIVE,
                                    getString(R.string.cancel)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                alertDialog.show()
                            }
                        }
                    }


                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("response1122334455:   " + "Failed0")
                try {
                    dismissMyDialogFrag()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Toast.makeText(
                    myselectedActivity!!,
                    resources.getString(R.string.somthing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @Keep
    private fun callStoriesDetailApi(UserId: String) {
        try {
            binding.progressLoadingBar.visibility = View.VISIBLE

            val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
            val map = sharedPrefsFor.preference
            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != "") {

                getFullDetailsOfClickedFeed(
                    UserId,
                    "ds_user_id=" + map.preferencE_USERID
                        .toString() + "; sessionid=" + map.preferencE_SESSIONID
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            dismissMyDialogFrag()
            System.err.println("workkkkkkkkk 5")
            ShowToast(myselectedActivity!!, getString(R.string.error_occ))
        }
    }

    private fun showAdmobAds_int_video() {
        if (Constants.show_Ads) {
            if (nn == "nnn") {
                if (rewardedInterstitialAd != null) {
                    rewardedInterstitialAd!!.show(myselectedActivity!!) {
                        Log.i(TAG, "onUserEarnedReward " + it.amount)

                    }
                } else {
                    Log.i(TAG, "load int video failed;")

                }
            }
        }
    }

    private fun showAdmobAds() {
        if (Constants.show_Ads) {
            if (nn == "nnn") {
                AdsManager.showAdmobInterstitialAd(
                    activity as Activity?,
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            AdsManager.loadInterstitialAd(activity as Activity?)
                        }
                    })
            }
        }
    }

    @Keep
    private fun getallstoriesapicall() {
        try {
            binding.progressLoadingBar.visibility = View.VISIBLE

            val sharedPrefsFor = SharedPrefsForInstagram(myselectedActivity!!)
            val map = sharedPrefsFor.preference
            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != "") {

                getallStories(
                    "ds_user_id=" + map.preferencE_USERID
                        .toString() + "; sessionid=" + map.preferencE_SESSIONID
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Keep
    @SuppressLint("NotifyDataSetChanged")
    fun getallStories(Cookie: String?) {
        var Cookie = Cookie
        if (TextUtils.isEmpty(Cookie)) {
            Cookie = ""
        }
        println("mycookies are = $Cookie")

        AndroidNetworking.get("https://i.instagram.com/api/v1/feed/reels_tray/")
            .setPriority(Priority.LOW)
            .addHeaders("Cookie", Cookie)
            .addHeaders(
                "User-Agent",
                "\"Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+\""
            )
            .build()
            .getAsObject(
                InstaStoryModelClass::class.java,
                object : ParsedRequestListener<InstaStoryModelClass> {

                    override fun onResponse(response: InstaStoryModelClass) {
                        try {
                            println("response1122334455_story:  " + response.tray)
                            binding.recUserList.visibility = View.VISIBLE
                            binding.progressLoadingBar.visibility = View.GONE
                            storyUsersListAdapter = StoryUsersListAdapter(
                                myselectedActivity!!,
                                response.tray, this@DownloadMainFragment
                            )
                            val linearLayoutManager =
                                LinearLayoutManager(
                                    myselectedActivity,
                                    RecyclerView.HORIZONTAL,
                                    false
                                )

                            binding.recUserList.layoutManager = linearLayoutManager
                            binding.recUserList.adapter = storyUsersListAdapter
                            storyUsersListAdapter!!.notifyDataSetChanged()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            println("response1122334455_storyERROR:  " + e.message)
                            binding.progressLoadingBar.visibility = View.GONE
                        }

                    }

                    override fun onError(anError: ANError) {
                        anError.printStackTrace()
                    }
                })


    }

    @Keep
    fun getFullDetailsOfClickedFeed(UserId: String, Cookie: String?) {
        AndroidNetworking.get("https://i.instagram.com/api/v1/users/$UserId/full_detail_info?max_id=")
            .setPriority(Priority.LOW)
            .addHeaders("Cookie", Cookie)
            .addHeaders(
                "User-Agent",
                "\"Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+\""
            )
            .build()
            .getAsObject(
                ModelFullDetailsInstagram::class.java,
                object : ParsedRequestListener<ModelFullDetailsInstagram> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: ModelFullDetailsInstagram) {
                        try {
                            binding.recUserList.visibility = View.VISIBLE
                            binding.progressLoadingBar.visibility = View.GONE
                            println("response1122334455_fulldetails:   ${response.reel_feed}")

                            if (response.reel_feed.items.size == 0) {
                                ShowToast(
                                    myselectedActivity!!,
                                    getString(R.string.nostoryfound)
                                )
                            }
                            listAllStoriesOfUserAdapter = ListAllStoriesOfUserAdapter(
                                myselectedActivity!!,
                                response.reel_feed.items
                            )
                            binding.recStoriesList.visibility = View.VISIBLE

                            val gridLayoutManager = GridLayoutManager(myselectedActivity, 3)

                            binding.recStoriesList.layoutManager = gridLayoutManager
                            binding.recStoriesList.isNestedScrollingEnabled = true
                            binding.recStoriesList.adapter = listAllStoriesOfUserAdapter
                            listAllStoriesOfUserAdapter!!.notifyDataSetChanged()
                        } catch (e: java.lang.Exception) {
                            binding.recStoriesList.visibility = View.GONE
                            e.printStackTrace()
                            binding.progressLoadingBar.visibility = View.GONE
                            ShowToast(myselectedActivity!!, getString(R.string.nostoryfound))
                        }
                    }

                    override fun onError(anError: ANError) {
                        println("response1122334455:   " + "Failed2")
                        binding.progressLoadingBar.visibility = View.GONE
                    }
                })
    }


    @Keep
    override fun onclickUserStoryListeItem(position: Int, modelUsrTray: ModelUsrTray?) {
        println("response1122ff334455:   $modelUsrTray$position")
        callStoriesDetailApi(modelUsrTray?.user?.pk.toString())
    }


    fun downloadInstagramImageOrVideodataExternalApi2(
        context: Context,
        URL: String?
    ) {
        object : Thread() {
            override fun run() {
                try {
                    Looper.prepare()
                    val client = OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()

                    val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("link", URL.toString())
                        .addFormDataPart("downloader", "video")
                        .build()
                    val request: Request = Request.Builder()
                        .url("https://igdownloader.com/ajax")
                        .method("POST", body)
                        .addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
                        )
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .build()
                    val response = client.newCall(request).execute()
                    System.err.println("workkkkkkkkk 4 " + response.code)
                    if (response.code == 200) {
                        try {
                            val cv: JSONObject = JSONObject(response.body!!.string())

                            System.err.println("workkkkkkkkk 4 " + cv.getString("html"))

                            val mylis = iUtils.extractUrls(cv.getString("html"));

                            if (mylis != null && mylis.size > 0) {

                                for (i in 0 until mylis.size) {
                                    if (mylis[i].contains(".mp4")) {

                                        DownloadFileMain.startDownloading(
                                            myselectedActivity,
                                            mylis[i],
                                            "Instagram_" + iUtils.getVideoFilenameFromURL(mylis[i]),
                                            ".mp4"
                                        )

                                    } else if (mylis[i].contains(".jpg")) {

                                        DownloadFileMain.startDownloading(
                                            myselectedActivity,
                                            mylis[i],
                                            "Instagram_" + iUtils.getImageFilenameFromURL(mylis[i]),
                                            ".jpg"
                                        )

                                    }
                                }
                            }

                            dismissMyDialogFrag()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()

                            println("myresponseis111 try exp " + e.message)

                            dismissMyDialogFrag()
                            ShowToast(
                                myselectedActivity,
                                resources.getString(R.string.somthing)
                            )
                        }


                    } else {
                        Toast.makeText(context, "Failed try again ", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        "Failed , Login and try again " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
        System.err.println("workkkkkkkkk 4 $URL")
    }


}