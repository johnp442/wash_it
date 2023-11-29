package com.aula.wash.it.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aula.wash.it.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Pagamento extends AppCompatActivity {

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_pagamento);
                Intent intent = getIntent();
                double subtotal = intent.getDoubleExtra("totalOrderValue", 0.0);
                Button processPaymentButton = findViewById(R.id.processPaymentButton);

                processPaymentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Obtenha as informações do cartão dos campos EditTex
                        EditText numeroCartaoInput = findViewById(R.id.cardNumberInput);
                        String numeroCartao = numeroCartaoInput.getText().toString();

                        EditText dataExpicaoInput = findViewById(R.id.expiryDateInput);
                        String dataExpedicao = dataExpicaoInput.getText().toString();

                        EditText cvvInput = findViewById(R.id.cvvInput);
                        String cvv = cvvInput.getText().toString();

                        // Verifique campos vazios e validações de campos
                        if (numeroCartao.isEmpty() || dataExpedicao.isEmpty() || cvv.isEmpty()) {
                            Toast.makeText(Pagamento.this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
                            return;
                        } else if (numeroCartao.length() != 16) {
                            numeroCartaoInput.setError("O número do cartão deve ter 16 dígitos");
                            return;
                        } else if (!dataExpedicao.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                            dataExpicaoInput.setError("A data de validade deve estar no formato MM/AA.");
                            return;
                        } else if (cvv.length() != 3) {
                            cvvInput.setError("O CVV deve ter 3 dígitos");
                            return;
                        }

                        // Crie um novo objeto de pedido
                        Map<String, Object> order = new HashMap<>();
                        order.put("cardNumber", numeroCartao);
                        order.put("expiryDate", dataExpedicao);
                        order.put("cvv", cvv);
                        order.put("total", subtotal);

                        // Obter o usuário atual
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Salve o pedido no Firebase Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("pedidos").document(user.getUid()).set(order)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Mostre um recibo para o usuário.
                                            Toast.makeText(Pagamento.this, "Pagamento processado com sucesso" , Toast.LENGTH_LONG).show();

                                            //Mostre um dialog
                                            new AlertDialog.Builder(Pagamento.this)
                                                    .setTitle("Pedido realizado")
                                                    .setMessage("Obrigado pela compra!")
                                                    .setPositiveButton(android.R.string.ok, null)
                                                    .setIcon(R.drawable.placed)
                                                    .show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Pagamento.this, "Erro no processamento do pagamento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });




                // OCULTA A BARRA DO LABEL
                Objects.requireNonNull(getSupportActionBar()).hide();
                // TROCA A COR DA BARRA DE STATUS
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
                }

                // Recupere o TextView
                TextView subtotalValor = findViewById(R.id.subTotalValue);

                // Defina o texto para o subtotal
                subtotalValor.setText(String.format("R$ %.2f", subtotal));

                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();


        menu.findItem(R.id.idPagamento).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.idPagamento) {
                startActivity(new Intent(Pagamento.this, Pagamento.class));
                return true;
            } else if (itemId == R.id.idEntrega) {
                startActivity(new Intent(Pagamento.this, Entrega.class));
                return true;
            } else if (itemId == R.id.idHome) {
                startActivity(new Intent(Pagamento.this, TelaCentral.class));
                return true;
            } else if (itemId == R.id.idServico) {
                startActivity(new Intent(Pagamento.this, Servico.class));
                return true;
            }else if (itemId == R.id.idPedido) {
                startActivity(new Intent(Pagamento.this, Pedido.class));
                return true;
            }
            // Se nenhum item corresponder, retorne falso
            return false;
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