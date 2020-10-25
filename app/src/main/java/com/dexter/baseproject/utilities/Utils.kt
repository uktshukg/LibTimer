package com.dexter.baseproject.utilities

import android.content.Context
import java.util.concurrent.TimeUnit

 fun convertMILLISToStandard(millis: Long): CharSequence? {
    val day = TimeUnit.MILLISECONDS.toDays(millis).toInt()
    val hours = TimeUnit.MILLISECONDS.toHours(millis) - day * 24
    val minute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.MILLISECONDS.toHours(millis) * 60
    val second = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toMinutes(millis) * 60

    return ("Hour $hours Minute $minute Seconds $second")
}

object SharedPref{
    fun getLong(context: Context?, string: String): Long {
        val sharedPref = context?.getSharedPreferences("meesho", Context.MODE_PRIVATE)
        return sharedPref?.getLong(string, 0L)!!
    }

    fun setLong(
        string: String,
        passedtime: Long,
        context: Context?
    ) {
        val sharedPref = context?.getSharedPreferences("meesho",Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putLong(string,passedtime )
            apply()
        }
    }
    fun setString(
        name: String,
        value: String,
        context: Context?
    ) {
        val sharedPref = context?.getSharedPreferences("meesho",Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(name,value )
            apply()
        }
    }

    fun getString(
        name: String,
        context: Context?
    ):String {
        val sharedPref = context?.getSharedPreferences("meesho", Context.MODE_PRIVATE)
        return sharedPref?.getString(name, "")!!
    }
    fun getFloat(
        name: String,
        context: Context?
    ):Float {
        val sharedPref = context?.getSharedPreferences("meesho", Context.MODE_PRIVATE)
        return sharedPref?.getFloat(name, 0.0f)!!
    }

    fun setFloat(
        name: String,
        value: Float,
        context: Context?
    ) {
        val sharedPref = context?.getSharedPreferences("meesho",Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putFloat(name,value )
            apply()
        }
    }

    fun clearAll(context: Context) {
        val sharedPref = context?.getSharedPreferences("meesho",Context.MODE_PRIVATE) ?: return
        sharedPref.edit().clear().apply()
    }

}
fun removeQuotesAndUnescape(uncleanJson: String): String? {
    val noQuotes = uncleanJson.replace("^\"|\"$".toRegex(), "")
    return org.apache.commons.text.StringEscapeUtils.unescapeJava(noQuotes)
}