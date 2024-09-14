package com.matheusnavarrobueno.projetofinal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.matheusnavarrobueno.projetofinal.databinding.ActivityLoginBinding
import com.matheusnavarrobueno.projetofinal.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    // Declaração das variáveis de email e senha

    private lateinit var email: String
    private lateinit var senha: String

    // Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    // Verifica se o usuário já está logado

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish() // Finaliza a LoginActivity para que o usuário não possa voltar
        }
    }

    // Inicializa os eventos de clique

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener {
            if (validarCampos()) {
                logarUsuario()
            }
        }
    }

    // Realiza o login do usuário

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                exibirMensagem("Logado com sucesso!")
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                finish() // Finaliza a LoginActivity para que o usuário não possa voltar
            }
            .addOnFailureListener { erro ->
                //Tratativas

                try {
                    throw erro
                } catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                    erroUsuarioInvalido.printStackTrace()
                    exibirMensagem("E-mail não cadastrado")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail ou senha estão incorretos!")
                }
            }
    }

    // Valida os campos de email e senha

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if (email.isNotEmpty()) {
            binding.textInputLayoutLoginEmail.error = null
            if (senha.isNotEmpty()) {
                binding.textInputLayoutLoginSenha.error = null
                return true
            } else {
                binding.textInputLayoutLoginSenha.error = "Preencha a senha"
                return false
            }
        } else {
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}