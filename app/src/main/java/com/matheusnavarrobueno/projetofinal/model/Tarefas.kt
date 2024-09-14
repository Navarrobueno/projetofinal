package com.matheusnavarrobueno.projetofinal.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Tarefa(
    @DocumentId
    var id: String = "",
    var titulo: String = "",
    var descricao: String = "",
    var dataCriacao: Date? = null,
    var dataFinalizacao: Date? = null
)