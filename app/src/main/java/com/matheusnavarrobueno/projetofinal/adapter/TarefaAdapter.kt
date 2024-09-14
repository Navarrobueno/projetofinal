package com.matheusnavarrobueno.projetofinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matheusnavarrobueno.projetofinal.model.Tarefa
import java.text.SimpleDateFormat
import java.util.*

class TarefaAdapter(
    private val listaTarefas: List<Tarefa>,
    private val editarTarefa: (Tarefa) -> Unit,
    private val deletarTarefa: (Tarefa) -> Unit
) : RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    // Cria novos views

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarefa, parent, false)
        return TarefaViewHolder(view)
    }

    // Substitui o conteúdo de uma view

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = listaTarefas[position]
        holder.bind(tarefa)
    }

    // Retorna o tamanho do dataset

    override fun getItemCount(): Int = listaTarefas.size

    inner class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val textViewDescricao: TextView = itemView.findViewById(R.id.textViewDescricao)
        private val textViewDataCriacao: TextView = itemView.findViewById(R.id.txtData)
        private val textViewDataFinalizacao: TextView = itemView.findViewById(R.id.textViewDataFinalizacao)
        private val buttonEditar: Button = itemView.findViewById(R.id.buttonEditar)
        private val buttonExcluir: Button = itemView.findViewById(R.id.buttonExcluir)

        // Vincula os dados da tarefa aos views

        fun bind(tarefa: Tarefa) {
            textViewTitulo.text = tarefa.titulo
            textViewDescricao.text = tarefa.descricao

            // Formatar a data de criação
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dataCriacao = tarefa.dataCriacao?.let { dateFormat.format(it) } ?: "Data não definida"
            textViewDataCriacao.text = "Criada em: $dataCriacao"

            // Formatar a data de finalização
            val dataFinalizacao = tarefa.dataFinalizacao?.let { dateFormat.format(it) } ?: "Data não definida"
            textViewDataFinalizacao.text = "Finaliza em: $dataFinalizacao"

            buttonEditar.setOnClickListener {
                editarTarefa(tarefa)
            }

            buttonExcluir.setOnClickListener {
                deletarTarefa(tarefa)
            }
        }
    }
}