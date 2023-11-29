package com.aula.wash.it.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.aula.wash.it.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class Entrega extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega);

        // OCULTA O LABEL DE TITULO e COLOCA UMA COR NA BARRA DE STATUS
        Objects.requireNonNull(getSupportActionBar()).hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();


        menu.findItem(R.id.idEntrega).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.idPagamento) {
                startActivity(new Intent(Entrega.this, Pagamento.class));
                return true;
            } else if (itemId == R.id.idEntrega) {
                startActivity(new Intent(Entrega.this, Entrega.class));
                return true;
            } else if (itemId == R.id.idHome) {
                startActivity(new Intent(Entrega.this, TelaCentral.class));
                return true;
            } else if (itemId == R.id.idServico) {
                startActivity(new Intent(Entrega.this, Servico.class));
                return true;
            }else if (itemId == R.id.idPedido) {
                startActivity(new Intent(Entrega.this, Pedido.class));
                return true;
            }
            // Se nenhum item corresponder, retorne falso
            return false;
        });

    }
}