package co.mesquitalabs.api

import android.os.Looper
import android.util.Log
import co.mesquitalabs.model.Address
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

fun getAddress(
    cep: String,
    onResult: (Address?) -> Unit
) {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://brasilapi.com.br/api/cep/v2/$cep")
        .build()

    client.newCall(request).enqueue(object : okhttp3.Callback {

        override fun onFailure(call: okhttp3.Call, e: IOException) {
            Log.e("HTTP", "Erro na requisição", e)
            // erro => devolve null na main thread
            android.os.Handler(Looper.getMainLooper()).post {
                onResult(null)
            }
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            val res = response.body.string()
            Log.d("HTTP", res)

            val json = JSONObject(res)

            val address = Address(
                cep = cep,
                state = json.optString("state"),
                city = json.optString("city"),
                neighborhood = json.optString("neighborhood"),
                street = json.optString("street"),
                latitude = json.optJSONObject("location")
                    ?.optJSONObject("coordinates")
                    ?.optString("latitude"),
                longitude = json.optJSONObject("location")
                    ?.optJSONObject("coordinates")
                    ?.optString("longitude"),
            )

            // volta para a main thread para atualizar estado do Compose
            android.os.Handler(Looper.getMainLooper()).post {
                onResult(address)
            }
        }
    })
}
