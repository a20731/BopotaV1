package ipca.stock.bopotav1

import PreferencesManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ipca.stock.bopotav1.TokenManager.Companion.obterEstadoEquipamento

class GaragemDetailActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garagem_detail)

        val buttonOpenClose = findViewById<Button>(R.id.buttonOpenClose)

        buttonOpenClose.setOnClickListener {
            // Certifique-se de ter o token e o equipamentoId disponíveis
            val token = preferencesManager.getToken()
            val equipamentoId = intent.getIntExtra("equipamento_id", -1)

            if (token != null && equipamentoId != -1) {

                TokenManager.atualizarEstadoEquipamento(token, equipamentoId, "on") { sucesso, mensagem ->
                    runOnUiThread {
                        if (sucesso) {
                            // A atualização foi bem-sucedida
                            Log.d("ButtonOpenClose", "Estado do equipamento atualizado com sucesso")

                            // Chame a função para atualizar os logs de acesso

                            atualizarLogsAcesso(token, equipamentoId,true)
                            val buttonOpenClose = findViewById<Button>(R.id.buttonOpenClose)

                            buttonOpenClose?.let {
                                if (it.text.toString().trim() == "Abrir") {
                                    it.text = "Fechar"
                                } else if (it.text.toString().trim() == "Fechar") {
                                    it.text = "Abrir"
                                }
                                }
                        } else {
                            // Houve um erro ao atualizar o estado
                            Log.e("ButtonOpenClose", "Erro ao atualizar o estado do equipamento: $mensagem")

                            // Verifique as condições específicas de erro
                            when {
                                mensagem.contains("validtoken=false") -> {
                                    // Token inválido, remover o token das preferências
                                    Toast.makeText(this, "Erro token:", Toast.LENGTH_SHORT).show()
                                    preferencesManager.removeToken()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish() // Fecha a GaragemDetailActivity para impedir o retorno com o botão de voltar
                                }
                                mensagem.contains("code=402") -> {
                                    // Exibir mensagem de erro na tela
                                    // Aqui, você pode exibir a mensagem na interface do usuário, por exemplo, usando um TextView
                                    Toast.makeText(this, "Nao pode alterar estado do equipamento em menos de 1 minuto - AGUARDE", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    // Se necessário, você pode reverter o estado do Button
                                    // Adicione aqui a lógica para reverter o estado do Button, se necessário
                                }
                            }
                        }
                    }
                }
            } else {
                // Token ou equipamentoId não disponíveis, lidar com isso conforme necessário
                Log.e("ButtonOpenClose", "Token ou equipamentoId não disponíveis")
            }
        }

        // Inicialize a ListView
        listView = findViewById(R.id.listView)
        preferencesManager = PreferencesManager(this)
        val textViewGateName = findViewById<TextView>(R.id.textViewGateName)
//
        // Recupere o ID do equipamento do Intent
        val equipamentoId = intent.getIntExtra("equipamento_id", -1)
        val equipamentoNome = intent.getStringExtra("equipamento_nome")
        if (equipamentoNome != null) {
            // O valor foi passado com sucesso, faça algo com ele
            textViewGateName.text = equipamentoNome
        } else {
            // Lidar com o caso em que equipamentoNome é nulo
            textViewGateName.text = "Nome do Equipamento Indefinido"
        }

        if (equipamentoId != -1) {
            // O ID do equipamento foi passado com sucesso
            // Obtenha o token das preferências
            val token = preferencesManager.getToken()

            if (token != null) {
                // Token obtido com sucesso, chame a função para obter os logs de acesso
                atualizarLogsAcesso(token, equipamentoId,false)
            } else {
                // Token não encontrado nas preferências, lidar com isso (por exemplo, redirecionar para tela de login)
            }
        } else {
            // O ID do equipamento não foi passado corretamente
            // Lidar com o caso de ID não válido
        }
    }

    private fun atualizarLogsAcesso(token: String, equipamentoId: Int,vemBotao:Boolean) {
        TokenManager.obterLogsAcesso(token, equipamentoId) { logs, message ->
            runOnUiThread {
                if (logs != null) {
                    // Logs obtidos com sucesso
                    // Preencha as linhas (rows) da ListView com os dados dos logs
                    val adapter = LogAcessoAdapter(this, logs)
                    listView.adapter = adapter

                    // Agora, chame a função para obter o estado do equipamento
                    obterEstadoEquipamento(token, equipamentoId) { estadoEquipamento, mensagem ->
                        runOnUiThread {
                            // Trate o estado do equipamento conforme necessário
                            Log.d("estadoEquipamento", "antes $estadoEquipamento")
                            if (estadoEquipamento != null) {
                                Log.d("estadoEquipamento", "depois $estadoEquipamento")

                                val buttonOpenClose = findViewById<Button>(R.id.buttonOpenClose)


                                if (estadoEquipamento) {
                                    if (vemBotao==false)
                                    {
                                        buttonOpenClose.text = "Abrir"
                                    }

                                } else {
                                    if( vemBotao==false)
                                    {
                                    buttonOpenClose.text = "Fechar"
                                    }
                                }

                                // Adicione aqui a lógica necessária para lidar com o estado do Button, se necessário
                            } else {
                                // Lidar com falha ao obter o estado do equipamento
                            }
                        }
                    }
                } else {
                    // Falha ao obter logs
                    // Exiba uma mensagem de erro, se necessário
                }
            }
        }
    }
}
