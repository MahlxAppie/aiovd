@file:Suppress("DEPRECATION")

package com.infusiblecoder.allinonevideodownloader.utils

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.infusiblecoder.allinonevideodownloader.R
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.CarouselMedia
import com.infusiblecoder.allinonevideodownloader.models.instawithlogin.ModelInstaWithLogin
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelEdNode
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelGetEdgetoNode
import com.infusiblecoder.allinonevideodownloader.models.storymodels.ModelInstagramResponse
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*


object Constants {


    const val DlApisUrl3: String = "https://iphoting-yt-dl-api.herokuapp.com/api/info?url="
    const val DlApisUrl2: String = "https://dlphpapis.herokuapp.com/api/info?url="
    const val DlApisUrl: String = "https://dlphpapis21.herokuapp.com/api/info?url="

    //https://api.m3u8-downloader.com/api/parse?url=https%3A%2F%2Fwww.tiktok.com%2F%40leemayy_%2Fvideo%2F7126189062129470747
    //https://dlphpapis.herokuapp.com/api/info?url=
    //https://downloaderapi1127.herokuapp.com/api/info?url=

    const val STARTFOREGROUND_ACTION =
        "com.infusiblecoder.allinonevideodownloader.action.startforeground"
    const val STOPFOREGROUND_ACTION =
        "com.infusiblecoder.allinonevideodownloader.action.stopforeground"

    const val PREF_APPNAME: String = "Mahlx VideoDownloader"
    const val PREF_CLIP: String = "MahlxVideoDownloader"
    const val FOLDER_NAME = "/WhatsApp/"
    const val FOLDER_NAME_Whatsappbusiness = "/WhatsApp Business/"
    const val FOLDER_NAME_Whatsapp_and11 = "/Android/media/com.whatsapp/WhatsApp/"
    const val FOLDER_NAME_Whatsapp_and11_B = "/Android/media/com.whatsapp.w4b/WhatsApp Business/"
    const val SAVE_FOLDER_NAME = "/Download/Mahlx Video Downloader/"
    const val MY_ANDROID_10_IDENTIFIER_OF_FILE = "Mahlx_Video_Downloader_"

    const val tiktokWebviewUrl = "https://www.tiktok.com/?lang=en"

    const val directoryInstaShoryDirectorydownload_videos = "/Mahlx Video Downloader/videos/"
    const val directoryInstaShoryDirectorydownload_images = "/Mahlx Video Downloader/images/"
    const val directoryInstaShoryDirectorydownload_audio = "/Mahlx Video Downloader/audios/"


    //TODO NOTE: Should make both false if you want to upload to the playstore
    const val showyoutube = false
    const val showsoundcloud = false

    //TODO NOTE: if you want to disable ads you can make this false
    const val show_Ads = true
    const val show_earning_card_in_extrafragment = false


    var myVideoUrlIs: String? = ""
    var myPhotoUrlIs: String? = ""
    lateinit var myprogressDD: ProgressDialog


    fun startInstaDownload(context: Context, Url: String) {

        System.err.println("workkkkkkkkk cons 4$Url")

        try {
            System.err.println("workkkkkkkkk cons 4")


            val sharedPrefsFor = SharedPrefsForInstagram(context)
            val map = sharedPrefsFor.preference
            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != "") {

                downloadInstagramImageOrVideodata(
                    context,
                    Url,
                    "ds_user_id=" + map.preferencE_USERID + "; sessionid=" + map.preferencE_SESSIONID,
                    true
                )
            } else {
                downloadInstagramImageOrVideodata(
                    context,
                    Url,
                    iUtils.myInstagramTempCookies,
                    false
                )
            }


        } catch (e: java.lang.Exception) {
            System.err.println("workkkkkkkkk 5")
            e.printStackTrace()
        }
    }


    fun downloadInstagramImageOrVideodata(
        context: Context,
        URL: String?,
        Cookie: String?,
        isLog: Boolean
    ) {

        myprogressDD = ProgressDialog(context)
        myprogressDD.setMessage("Loading....")
        myprogressDD.setCancelable(false)
        myprogressDD.show()

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)


        AndroidNetworking.get(URL!!)
            .setPriority(Priority.MEDIUM)
            .addHeaders("Cookie", Cookie!!)
            .addHeaders(
                "User-Agent",
                iUtils.UserAgentsList[j]
            )
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    println("wojfdjhfdjhtik yyyy $response")

                    try {

                        if (!isLog) {
                            val listType = object : TypeToken<ModelInstagramResponse?>() {}.type
                            val modelInstagramResponse: ModelInstagramResponse? =
                                GsonBuilder().create()
                                    .fromJson<ModelInstagramResponse>(
                                        response.toString(),
                                        listType
                                    )
                            if (modelInstagramResponse != null) {
                                if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                    val modelGetEdgetoNode: ModelGetEdgetoNode =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children


                                    val modelEdNodeArrayList: List<ModelEdNode> =
                                        modelGetEdgetoNode.modelEdNodes
                                    for (i in modelEdNodeArrayList.indices) {
                                        if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                            myVideoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.video_url
                                            DownloadFileMain.startDownloading(
                                                context,
                                                myVideoUrlIs,
                                                iUtils.getVideoFilenameFromURL(
                                                    myVideoUrlIs
                                                ),
                                                ".mp4"
                                            )
                                            // etText.setText("");
                                            try {
                                                myprogressDD.dismiss()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            myVideoUrlIs = ""
                                        } else {
                                            myPhotoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                            DownloadFileMain.startDownloading(
                                                context,
                                                myPhotoUrlIs,
                                                iUtils.getImageFilenameFromURL(
                                                    myPhotoUrlIs
                                                ),
                                                ".png"
                                            )
                                            myPhotoUrlIs = ""
                                            try {
                                                myprogressDD.dismiss()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            // etText.setText("");
                                        }
                                    }
                                } else {
                                    val isVideo =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video


                                    if (isVideo) {
                                        myVideoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                        DownloadFileMain.startDownloading(
                                            context,
                                            myVideoUrlIs,
                                            iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                            ".mp4"
                                        )
                                        try {
                                            myprogressDD.dismiss()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                        DownloadFileMain.startDownloading(
                                            context,
                                            myPhotoUrlIs,
                                            iUtils.getImageFilenameFromURL(myPhotoUrlIs),
                                            ".png"
                                        )
                                        try {
                                            myprogressDD.dismiss()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myPhotoUrlIs = ""
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.somthing),
                                    Toast.LENGTH_SHORT
                                ).show()
                                try {
                                    myprogressDD.dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {

                            val listType: Type =
                                object : TypeToken<ModelInstaWithLogin?>() {}.type
                            val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                                response.toString(),
                                listType
                            )
                            println("workkkkk777 " + modelInstagramResponse.items[0].code)
                            var myInstaUsername: String? = ""

                            if (modelInstagramResponse.items[0].mediaType == 8) {
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
                                            context,
                                            myVideoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myVideoUrlIs
                                            ),
                                            ".mp4"
                                        )
                                        // etText.setText("");
                                        try {
                                            myprogressDD.dismiss()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                                .geturl()
                                        DownloadFileMain.startDownloading(
                                            context,
                                            myPhotoUrlIs,
                                            myInstaUsername + iUtils.getVideoFilenameFromURL(
                                                myPhotoUrlIs
                                            ),
                                            ".png"
                                        )
                                        myPhotoUrlIs = ""
                                        try {
                                            myprogressDD.dismiss()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        // etText.setText("");
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
                                        context,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    try {
                                        myprogressDD.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        context,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myPhotoUrlIs
                                        ),

                                        ".png"
                                    )
                                    try {
                                        myprogressDD.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myPhotoUrlIs = ""
                                }
                            }


                        }
                    } catch (e: java.lang.Exception) {
                        myprogressDD.dismiss()
                        val alertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle(context.getString(R.string.logininsta))
                        alertDialog.setMessage(context.getString(R.string.urlisprivate))
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()

                    }

                }

                override fun onError(error: ANError) {
                    println("myresponseis111 exp " + error.message)
                    try {
                        println("response1122334455:   " + "Failed1 " + error.message)
                        myprogressDD.dismiss()
                        val alertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle(context.getString(R.string.logininsta))
                        alertDialog.setMessage(context.getString(R.string.urlisprivate))
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    } catch (e: Exception) {

                    }
                }
            })


    }


}