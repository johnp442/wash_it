package com.aula.wash.it.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.aula.wash.it.R;

import java.util.Objects;

public class Cadastrar_endereco extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_endereco);

        // OCULTA A BARRA DO LABEL
        Objects.requireNonNull(getSupportActionBar()).hide();
        // TROCA A COR DA BARRA DE STATUS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }


        ImageButton btnvoltarPedido = findViewById(R.id.btnvoltarPedido);
        btnvoltarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cadastrar_endereco.this, Pedido.class);

                startActivity(intent);
            }
        });
    }
    // Oculta o teclado virtual ao clicar fora de um campo
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}