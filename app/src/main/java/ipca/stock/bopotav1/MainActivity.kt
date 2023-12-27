// MainActivity.kt
package ipca.stock.bopotav1

import PreferencesManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var PreferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferencesManager = PreferencesManager(this)

        // Imprimir o token salvo nas preferências ao iniciar o aplicativo
        val savedToken = PreferencesManager.getToken()
        if (savedToken != null) {
            Log.d("MainActivity", "Token salvo nas preferências: $savedToken")
            startActivity(Intent(this, GarageActivity::class.java))
            finish() // Finalize a MainActivity para que não seja possível voltar a ela a partir da GarageActivity

        } else {
            Log.d("MainActivity", "Nenhum token salvo nas preferências.")
        }

        val loginButton: Button = findViewById(R.id.buttonLogin)
        val usernameEditText: EditText = findViewById(R.id.editTextUsername)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)

        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isNotEmpty() && password.isNotEmpty()) {
                    TokenManager.getToken(username, password) { token ->
                        token?.let {
                            // Salve o token nas preferências
                            PreferencesManager.saveToken(it.token)
                            startActivity(Intent(this@MainActivity, GarageActivity::class.java))
                            finish()

                            Log.d("MainActivity", "Token obtido: ${it.token}")
                        } ?: run {
                            Log.e("MainActivity", "Falha ao obter o token")
                        }
                    }
                } else {
                    Log.w("MainActivity", "Por favor, preencha os campos de usuário e senha")
                }
            }
        })
    }
}
