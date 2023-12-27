// GarageActivity.kt
package ipca.stock.bopotav1

import PreferencesManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import ipca.stock.bopotav1.Equipamento
import ipca.stock.bopotav1.EquipamentoAdapter
import ipca.stock.bopotav1.TokenManager

class GarageActivity : AppCompatActivity() {

    private lateinit var PreferencesManager: PreferencesManager
    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        PreferencesManager = PreferencesManager(this)
        listView = findViewById(R.id.listView)
        progressBar = findViewById(R.id.progressBar)

        // Obter token salvo nas preferências
        val savedToken = PreferencesManager.getToken()

        if (savedToken != null) {
            // Exibir ProgressBar enquanto os equipamentos estão sendo carregados
            showProgressBar()

            // Obter equipamentos usando o token
            Log.d("GarageActivity", "Token: $savedToken")
            TokenManager.getEquipamentos(savedToken) { equipamentos, message ->
                runOnUiThread {
                    hideProgressBar() // Sempre esconder ProgressBar após a chamada

                    if (equipamentos != null) {
                        // Equipamentos obtidos com sucesso
                        Log.d("GarageActivity", "Equipamentos obtidos com sucesso: $equipamentos")
                        // Atualizar a ListView com os equipamentos
                        val adapter = EquipamentoAdapter(this, equipamentos)
                        listView.adapter = adapter
                        Log.d("teste", "estou aqui antes setonlick")

                        // Adicionar um listener de clique aos itens da lista

                    } else {
                        // Falha ao obter equipamentos
                        Log.e("GarageActivity", "Falha ao obter equipamentos: $message")

                        if (message == "Token inválido") {
                            // Token inválido, remover o token das preferências
                            PreferencesManager.removeToken()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish() // Fecha a GarageActivity para impedir o retorno com o botão de voltar

                            // Aqui você pode redirecionar para a tela de login, se necessário
                        }

                        // Exibir mensagem de erro, se necessário
                    }
                }
            }
        } else {
            // O token não está salvo, lidar de acordo (por exemplo, redirecionar para a tela de login)
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE


    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
