package com.locker.kickstarterapp.model.fake

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStreamReader

fun <T: Any> Context.loadJsonFromAssets(fileName: String, clazz: Class<T>) : T? {
    var data: T? = null
    try {
        val inputStream = assets.open(fileName);

        val gson = Gson()
        val reader = InputStreamReader(inputStream)

        data = gson.fromJson(reader, clazz)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return data
}