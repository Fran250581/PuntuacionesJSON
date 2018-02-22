package com.example.fran.puntuacionesjson

import android.util.Log
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import java.io.*
/**
 * Created by Fran on 17/02/2018.
 */
class JSONManager {

    fun getJsonString(url: String, method: String, params: List<NameValuePair>?): String {
        var url = url
        try {
            if (method == "POST") {
                val httpClient = DefaultHttpClient()
                val httpPost = HttpPost(url)
                httpPost.setEntity(UrlEncodedFormEntity(params))
                val httpResponse = httpClient.execute(httpPost)
                val httpEntity = httpResponse.getEntity()
                inputStream = httpEntity.getContent()
            }
            else if (method == "GET") {
                val httpClient = DefaultHttpClient()
                if (params != null) {
                    val paramString = URLEncodedUtils.format(params, "utf-8")
                    url += "?" + paramString
                }
                val httpGet = HttpGet(url)
                val httpResponse = httpClient.execute(httpGet)
                val httpEntity = httpResponse.getEntity()
                inputStream = httpEntity.getContent()
            }
        }
        catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        catch (e: ClientProtocolException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val reader = BufferedReader(InputStreamReader(inputStream, "iso-8859-1"), 8)
            val stringBuilder = StringBuilder()
            var linea: String? = null
            while ({linea = reader.readLine(); linea}() != null) {
                stringBuilder.append(linea!! + "\n")
            }
            inputStream!!.close()
            jsonString = stringBuilder.toString()
        }
        catch (e: Exception) {
            Log.e("Error", "Error obteniendo JSON " + e.toString())
        }

        return jsonString
    }

    companion object {
        internal var inputStream: InputStream? = null
        internal var jsonString = ""
    }
}

