package com.matheusnavarrobueno.projetofinal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.matheusnavarrobueno.projetofinal.databinding.ActivityMainBinding
import com.matheusnavarrobueno.projetofinal.model.Tarefa

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Instâncias do FirebaseAuth e FirebaseFirestore
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Lista de tarefas e o adaptador para o RecyclerView

    private lateinit var listaTarefas: MutableList<Tarefa>
    private lateinit var adapter: TarefaAdapter
    private var tarefasListenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Método chamado quando a atividade é criada
        inicializarToolbar()
        inicializarRecyclerView()
        carregarTarefasEmTempoReal()
        exibirMensagemDeBoasVindas()


        // Configura o fab

        binding.fabAdicionar.setOnClickListener {
            val intent = Intent(this, AdicionarTarefaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        carregarTarefasEmTempoReal()
    }

    override fun onStop() {
        super.onStop()
        tarefasListenerRegistration?.remove()
    }
    // Configura a toolbar da atividade principal

    private fun inicializarToolbar() {
        val toolbar = binding.includeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Lista de Tarefas"
        }

        // Adiciona um menu à toolbar

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.item_sair) {
                        deslogarUsuario()
                    }
                    return true
                }
            }
        )
    }
    // Inicializa o RecyclerView
    private fun inicializarRecyclerView() {
        listaTarefas = mutableListOf()
        adapter = TarefaAdapter(listaTarefas, this::editarTarefa, this::deletarTarefa)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Carrega as tarefas do Firestore

    private fun carregarTarefasEmTempoReal() {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            tarefasListenerRegistration = firestore.collection("tarefas")
                .whereEqualTo("uid", uid) // Filtra as tarefas pelo UID do usuário logado
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {

                        return@addSnapshotListener
                    }

                    if (snapshots != null) {
                        atualizarListaTarefas(snapshots)
                    }
                }
        }
    }

    // Atualiza a lista de tarefas

    private fun atualizarListaTarefas(snapshots: QuerySnapshot) {
        listaTarefas.clear()
        for (document in snapshots) {
            val tarefa = document.toObject(Tarefa::class.java)
            tarefa.id = document.id
            listaTarefas.add(tarefa)
        }
        adapter.notifyDataSetChanged()
    }

    // Método para editar  tarefa

    private fun editarTarefa(tarefa: Tarefa) {
        val intent = Intent(this, AdicionarTarefaActivity::class.java)
        intent.putExtra("tarefaId", tarefa.id)
        intent.putExtra("tarefaTitulo", tarefa.titulo)
        intent.putExtra("tarefaDescricao", tarefa.descricao)
        intent.putExtra("tarefaDataCriacao", tarefa.dataCriacao?.time)
        intent.putExtra("tarefaDataFinalizacao", tarefa.dataFinalizacao?.time)
        startActivity(intent)
    }

    // Método para deletar uma tarefa

    private fun deletarTarefa(tarefa: Tarefa) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Tarefa")
            .setMessage("Deseja realmente excluir esta tarefa?")
            .setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sim") { _, _ ->
                firestore.collection("tarefas").document(tarefa.id)
                    .delete()
                    .addOnSuccessListener {
                        listaTarefas.remove(tarefa)
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->

                    }
            }
            .create()
            .show()
    }
    // Método para deslogar o usuário do app

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sim") { _, _ ->
                firebaseAuth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish() // Finaliza a MainActivity para que o usuário não possa voltar
            }
            .create()
            .show()
    }


    // Exibe uma mensagem de boas-vindas com o nome do usuário

    private fun exibirMensagemDeBoasVindas() {
        val user = firebaseAuth.currentUser
        user?.let {
            val uid = it.uid
            firestore.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nome = document.getString("nome") ?: "Usuário"
                        binding.textViewBemVindo.text = "Olá, $nome"
                    }
                }
                .addOnFailureListener { exception ->

                }
        }
    }
}