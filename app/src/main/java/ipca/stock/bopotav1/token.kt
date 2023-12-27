package ipca.stock.bopotav1
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

data class Token(val id: Int, val userId: Int, val token: String)

class TokenManager {

    companion object {
        const val BASE_URL = "https://roberto-pereira.outsystemscloud.com/BopotaCloud/rest/V1/"


        fun atualizarEstadoEquipamento(token: String, equipamentoId: Int, estado: String, callback: (Boolean, String) -> Unit) {
            val client = OkHttpClient()

            val url = "$BASE_URL/SetStateEquipment?token=$token&EquipamentoId=$equipamentoId&state=$estado"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false, "Erro de conexão com o servidor")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val sucesso = parseAtualizarEstado(responseData)
                        callback(sucesso, "Sucesso")
                    } else {
                        when (response.code) {
                            401 -> {
                                // Token inválido, informar o callback
                                callback(false, "validtoken=false")
                            }
                            else -> {
                                callback(false, response.toString())
                            }
                        }
                    }
                }
            })
        }

        private fun parseAtualizarEstado(responseData: String?): Boolean {
            try {
                responseData?.let {
                    val json = JSONObject(it)
                    // Assuma que o sucesso está em um campo chamado "result" no JSON
                    return json.getBoolean("result")
                }
            } catch (e: JSONException) {
                Log.e("TokenManager", "Erro de análise JSON", e)
            }
            return false
        }















        fun obterEstadoEquipamento(token: String, equipamentoId: Int, callback: (Boolean?, String?) -> Unit) {
            val client = OkHttpClient()

            val url = "$BASE_URL/GetStateEquipment?token=$token&EquipamentoId=$equipamentoId"
            Log.d("api", "url $url")
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, "Erro de conexão com o servidor")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val estado = parseEstado(responseData)
                        Log.d("api", "estado $responseData")
                        callback(estado, "Sucesso")
                    } else {
                        when (response.code) {
                            401 -> {
                                // Token inválido, informar o callback
                                callback(null, "Token inválido")
                            }
                            else -> {
                                callback(null, "Erro no servidor")
                            }
                        }
                    }
                }
            })
        }

        private fun parseEstado(responseData: String?): Boolean? {
            try {
                responseData?.let {
                    // Converta a string diretamente para um booleano
                    return it.toBoolean()
                }
            } catch (e: Exception) {
                Log.e("TokenManager2", "Erro ao converter para booleano", e)
            }
            return null
        }




        fun getEquipamentos(token: String, callback: (List<Equipamento>?, String?) -> Unit) {
            val client = OkHttpClient()

            val url = "$BASE_URL/GetEquipamentos?token=$token"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, "Erro de conexão com o servidor")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val equipamentos = parseEquipamentos(responseData)
                        callback(equipamentos, "Sucesso")
                    } else {
                        when (response.code) {
                            401 -> {
                                // Token inválido, informar o callback
                                callback(null, "Token inválido")
                            }
                            else -> {
                                callback(null, "Erro no servidor")
                            }
                        }
                    }
                }
            })
        }


        private fun parseEquipamentos(responseData: String?): List<Equipamento>? {
            try {
                responseData?.let {
                    val jsonArray = JSONArray(it)
                    val equipamentos = mutableListOf<Equipamento>()

                    for (i in 0 until jsonArray.length()) {
                        val equipamentoJson = jsonArray.getJSONObject(i).getJSONObject("Equipamento")
                        val id = equipamentoJson.getInt("id")
                        val condominioId = equipamentoJson.getInt("Condominioid")
                        val tipoLigacaoId = equipamentoJson.getInt("TipoLigacaoid")
                        val descricao1 = equipamentoJson.getString("descricao1")
                        val server = equipamentoJson.getString("server")
                        val deviceId = equipamentoJson.getString("deviceId")
                        val authKey = equipamentoJson.getString("authKey")
                        val channel = equipamentoJson.getString("channel")

                        // Verifica se o campo "ativo" está presente no JSON
                        val ativo = if (equipamentoJson.has("ativo")) {
                            equipamentoJson.getBoolean("ativo")
                        } else {
                            false // Se não estiver presente, assume como false
                        }

                        val equipamento = Equipamento(id, condominioId, tipoLigacaoId, descricao1, server, deviceId, authKey, channel, ativo)
                        equipamentos.add(equipamento)
                    }

                    return equipamentos
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }




        fun getToken(username: String, password: String, callback: (Token?) -> Unit) {
            val client = OkHttpClient()

            val url = "$BASE_URL/GetToken?Username=$username&Password=$password"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Adicione um log para falha
                    Log.e("TokenManager", "Falha na solicitação do token", e)
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        // Adicione um log para a resposta recebida
                        Log.d("TokenManager", "Resposta recebida: $responseData")
                        val token = parseToken(responseData)
                        callback(token)
                    } else {
                        // Adicione um log para resposta não bem-sucedida
                        Log.e("TokenManager", "Resposta não bem-sucedida: ${response.code}")
                        callback(null)
                    }
                }
            })
        }

        private fun parseToken(responseData: String?): Token? {
            try {
                responseData?.let {
                    val json = JSONObject(it)
                    val id = json.getInt("id")
                    val userId = json.getInt("Utilizadorid")
                    val tokenValue = json.getString("token_value")
                    return Token(id, userId, tokenValue)
                }
            } catch (e: JSONException) {
                // Adicione um log para erro de análise
                Log.e("TokenManager", "Erro de análise JSON", e)
            }
            return null
        }

        fun obterLogsAcesso(token: String, equipamentoId: Int, callback: (List<LogAcesso>?, String?) -> Unit) {
            val client = OkHttpClient()

            val url = "$BASE_URL/GetLogEquipment?token=$token&EquipamentoId=$equipamentoId"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null, "Erro de conexão com o servidor")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val logsAcesso = parseLogsAcesso(responseData)
                        callback(logsAcesso, "Sucesso")
                    } else {
                        when (response.code) {
                            401 -> {
                                // Token inválido, informar o callback
                                callback(null, "Token inválido")
                            }
                            else -> {
                                callback(null, "Erro no servidor")
                            }
                        }
                    }
                }
            })
        }

        private fun parseLogsAcesso(responseData: String?): List<LogAcesso>? {
            try {
                responseData?.let {
                    val jsonArray = JSONArray(it)
                    val logsAcesso = mutableListOf<LogAcesso>()

                    for (i in 0 until jsonArray.length()) {
                        val logJson = jsonArray.getJSONObject(i)
                        val id = logJson.getInt("id")
                        val equipamentoId = logJson.getInt("Equipamentoid")
                        val utilizadorId = logJson.getInt("Utilizadorid")
                        val dataHora = logJson.getString("datahora")
                        val tipoLogId = logJson.getInt("TIpoLogid")

                        val logAcesso = LogAcesso(id, equipamentoId, utilizadorId, dataHora, tipoLogId)
                        logsAcesso.add(logAcesso)
                    }

                    return logsAcesso
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }










    }
}