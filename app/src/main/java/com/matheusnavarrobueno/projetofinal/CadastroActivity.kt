package com.matheusnavarrobueno.projetofinal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.matheusnavarrobueno.projetofinal.databinding.ActivityCadastroBinding
import com.matheusnavarrobueno.projetofinal.model.Usuario
import com.matheusnavarrobueno.projetofinal.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    // Variáveis

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    // Instâncias do FirebaseAuth e Firestore

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToolbar()
        inicializarEventosClique()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Valida se os campos de nome, email e senha

    private fun validarCampos(): Boolean {
        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        // Verifica se o campo nome está preenchido

        if (nome.isNotEmpty()) {
            binding.textInputNome.error = null

            // Verifica se o campo email está preenchido

            if (email.isNotEmpty()) {
                binding.textInputEmail.error = null

                // Verifica se o campo senha está preenchido

                if (senha.isNotEmpty()) {
                    binding.textInputSenha.error = null
                    return true

                } else {
                    binding.textInputSenha.error = "Preencha a senha"
                    return false
                }
            } else {
                binding.textInputEmail.error = "Preencha o seu e-mail!"
                return false
            }
        } else {
            binding.textInputNome.error = "Preencha o seu nome!"
            return false
        }
    }

    // Inicializa os eventos de clique dos botões

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    // Função para cadastrar um novo usuário no Firebase Authentication

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { resultado ->
                if (resultado.isSuccessful) {

                    // Se o cadastro for bem-sucedido, cria um objeto "Usuario" e salva no Firestore

                    val idUsuario = resultado.result.user?.uid
                    if (idUsuario != null) {
                        val usuario = Usuario(idUsuario, nome, email)
                        salvarUsuarioFirestore(usuario)
                    }

                    startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                    )
                    finish() // Finaliza a CadastroActivity para que o usuário não possa voltar
                }
            }
            .addOnFailureListener { erro ->

                // Trata os erros específicos ao criar o usuário no FirebaseAuth

                try {
                    throw erro
                } catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                    erroSenhaFraca.printStackTrace()
                    exibirMensagem("Senha fraca, digite outra com letras, número e caracteres especiais")
                } catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                    erroUsuarioExistente.printStackTrace()
                    exibirMensagem("E-mail já pertence a outro usuário")
                } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("E-mail inválido, digite um outro e-mail")
                }
            }
    }

    // Função que salva o objeto Usuario no Firestore

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao fazer cadastro")
            }
            .addOnFailureListener {
                exibirMensagem("Erro ao fazer cadastro")
            }
    }

    // Inicializa a toolbar e define um título e o botão voltar

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}