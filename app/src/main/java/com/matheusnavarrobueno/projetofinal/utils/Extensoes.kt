package com.matheusnavarrobueno.projetofinal.utils

import android.app.Activity
import android.widget.Toast

// Função de extensão para a classe Activity que exibe uma mensagem Toast

fun Activity.exibirMensagem( mensagem: String ){
    Toast.makeText(
        this,
        mensagem,
        Toast.LENGTH_LONG
    ).show()
}