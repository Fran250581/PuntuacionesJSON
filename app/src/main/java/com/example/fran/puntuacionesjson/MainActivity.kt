package com.example.fran.puntuacionesjson

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONObject
import android.app.ProgressDialog
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONException
import android.os.AsyncTask
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import java.util.*

class MainActivity : AppCompatActivity() {

    var btnVerPuntuaciones: Button? = null
    var btnCrearPuntuacion: Button? = null
    var puntos: TextView? = null

    private val urlObtener = "http://proves.iesperemaria.com/asteroides/puntuaciones/"
    private val urlGrabar = "http://proves.iesperemaria.com/asteroides/puntuaciones/nueva/"

    private var pDialog: ProgressDialog? = null
    var jsonManager = JSONManager()
    var jsonObject: JSONObject? = null
    var jsonArray: JSONArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnVerPuntuaciones = (findViewById<View>(R.id.btnVerPuntuaciones) as Button)
        btnCrearPuntuacion = (findViewById<View>(R.id.btnCrearPuntuacion) as Button)
        puntos = (findViewById<View>(R.id.puntos) as TextView)

        btnVerPuntuaciones!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                mostrarPuntuaciones()
            }
        })

        btnCrearPuntuacion!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                crearPuntuacion()
            }
        })
    }

    private fun mostrarPuntuaciones() {
        PuntuacionesJSON().execute()
    }

    private fun crearPuntuacion() {
        val puntos = Math.abs(Random().nextInt(99999))
        val fecha = System.currentTimeMillis()
        NuevaPuntuacion().execute(puntos!!.toString(), "Fco Jose Lopez", fecha.toString())
    }

    internal inner class PuntuacionesJSON : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog!!.setMessage("Obteniendo puntuaciones...")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg params: String): String? {
            try {
                return jsonManager.getJsonString(urlObtener, "GET", null)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(jsonString: String) {
            val salida = StringBuilder()
            pDialog!!.dismiss()
            try {
                jsonObject = JSONObject(jsonString)
                jsonArray = jsonObject!!.getJSONArray("puntuaciones")
                for (i in 0 until jsonArray!!.length()) {
                    val nodo = jsonArray!!.getJSONObject(i)
                    salida.append(nodo.getString("puntos") + " " + nodo.getString("nombre") + "\n")
                }
                puntos!!.setText(salida.toString())
            }
            catch (e: JSONException) {
                Toast.makeText(applicationContext, "Error accediendo al servicio", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    internal inner class NuevaPuntuacion : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog!!.setMessage("Almacenando puntuación...")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg params: String): String? {
            try {
                val parametros = ArrayList<NameValuePair>()
                parametros.add(BasicNameValuePair("puntos", params[0]))
                parametros.add(BasicNameValuePair("nombre", params[1]))
                parametros.add(BasicNameValuePair("fecha", params[2]))
                return jsonManager.getJsonString(urlGrabar, "POST", parametros)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(jsonString: String) {
            val salida: String
            pDialog!!.dismiss()
            try {
                jsonObject = JSONObject(jsonString)
                salida = jsonObject!!.getString("id") + " " +
                        jsonObject!!.getString("puntos") + " " +
                        jsonObject!!.getString("nombre")
                puntos!!.setText(salida)
            }
            catch (e: JSONException) {
                Toast.makeText(applicationContext, "Error accediendo al servicio", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

}
