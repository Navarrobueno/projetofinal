package com.matheusnavarrobueno.projetofinal

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdicionarTarefaActivity : AppCompatActivity() {

    // Declaração das variáveis de UI e Firebase
    private lateinit var editTextTitulo: EditText
    private lateinit var editTextDescricao: EditText
    private lateinit var textViewDataCriacao: TextView
    private lateinit var buttonAdicionar: Button
    private lateinit var buttonSelecionarData: Button
    private lateinit var textViewDataSelecionada: TextView
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Variáveis para armazenar informações da tarefa

    private var tarefaId: String? = null
    private var dataCriacao: Date? = null
    private var dataFinalizacao: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_tarefa)

        // Inicializa as views da interface de usuário

        editTextTitulo = findViewById(R.id.edTitulo)
        editTextDescricao = findViewById(R.id.edDescricao)
        textViewDataCriacao = findViewById(R.id.txtData)
        buttonAdicionar = findViewById(R.id.btnAdicionar)
        buttonSelecionarData = findViewById(R.id.btnData)
        textViewDataSelecionada = findViewById(R.id.txtDataselect)

        // Verifica se está editando uma tarefa existente ou criando uma nova

        tarefaId = intent.getStringExtra("tarefaId")
        if (tarefaId != null) {
            // Se estiver editando, preenche os campos com os dados da tarefa existente
            editTextTitulo.setText(intent.getStringExtra("tarefaTitulo"))
            editTextDescricao.setText(intent.getStringExtra("tarefaDescricao"))
            val dataCriacaoMillis = intent.getLongExtra("tarefaDataCriacao", -1)
            if (dataCriacaoMillis != -1L) {
                dataCriacao = Date(dataCriacaoMillis)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                textViewDataCriacao.text = "Data de Criação: ${dateFormat.format(dataCriacao)}"
            }
            val dataFinalizacaoMillis = intent.getLongExtra("tarefaDataFinalizacao", -1)
            if (dataFinalizacaoMillis != -1L) {
                dataFinalizacao = Date(dataFinalizacaoMillis)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                textViewDataSelecionada.text = "Data de Finalização: ${dateFormat.format(dataFinalizacao)}"
            }
            buttonAdicionar.setText("Atualizar Tarefa")
        } else {
            // Se estiver criando uma nova tarefa, define a data de criação como a data atual
            dataCriacao = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textViewDataCriacao.text = "Data de Criação: ${dateFormat.format(dataCriacao)}"
        }

        // Configura o listener para o botão de selecionar data

        buttonSelecionarData.setOnClickListener {
            mostrarDatePickerDialog()
        }

        // Configura o listener para o botão de adicionar/atualizar tarefa

        buttonAdicionar.setOnClickListener {
            val titulo = editTextTitulo.text.toString().trim()
            val descricao = editTextDescricao.text.toString().trim()

        // Valida os campos de título e descrição

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha o título", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (descricao.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha a descrição", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dataFinalizacao == null) {
                Toast.makeText(this, "Por favor, selecione uma data de finalização", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria um mapa com os dados da tarefa

            val tarefa = hashMapOf(
                "titulo" to titulo,
                "descricao" to descricao,
                "dataCriacao" to dataCriacao,
                "dataFinalizacao" to dataFinalizacao,
                "uid" to firebaseAuth.currentUser?.uid // Adiciona o UID do usuário
            )
            // Adiciona ou atualiza a tarefa no Firestore

            if (tarefaId == null) {
                db.collection("tarefas")
                    .add(tarefa)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Tarefa adicionada com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao adicionar tarefa: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {

                // Atualiza uma tarefa existente

                db.collection("tarefas").document(tarefaId!!)
                    .set(tarefa)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Tarefa atualizada com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao atualizar tarefa: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Mostra um DatePickerDialog para selecionar a data de finalização

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            dataFinalizacao = calendar.time
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textViewDataSelecionada.text = "Data de Finalização: ${dateFormat.format(dataFinalizacao)}"
        }, year, month, day)

        datePickerDialog.show()
    }
}