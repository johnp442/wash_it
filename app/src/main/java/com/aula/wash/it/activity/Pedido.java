package com.aula.wash.it.activity;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aula.wash.it.Adapters.AdapterServicos;
import com.aula.wash.it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Pedido extends AppCompatActivity {

    private Spinner paymentMethodSpinner;
    double subtotal = 0.0;
    double deliveryFee = 10.0;
    double total = 0.0;
    private static final String TAG = AdapterServicos.class.getSimpleName();

  private String formatServiceId(String servicoID) {
      // Verifica se servicoID não é nulo
      if (servicoID != null) {
          // Substitua caracteres especiais, remova espaços e converta para minúsculas.
          return servicoID.replace("ç", "c").replaceAll("\\s+", "").toLowerCase();
      } else {
          // Se servicoID for nulo, retorne uma string vazia ou tome outra ação apropriada
          return "";
      }
  }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);


        Button btnLimpar = findViewById(R.id.btnLimpar);
        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Excluir todos os serviços do carrinho do usuário
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db.collection("users").document(userId).collection("cart")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        db.collection("users").document(userId).collection("cart").document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot excluído com sucesso!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Erro ao excluir o documento", e);
                                                    }
                                                });
                                    }
                                    LinearLayout parentLayout = findViewById(R.id.parentLayout);
                                    // Remova todas as visualizações filhas do layout pai
                                    parentLayout.removeAllViews();
                                } else {
                                    Log.d(TAG, "Error getting documents:", task.getException());
                                }
                            }
                        });
            }
        });

        // OCULTA A BARRA DO LABEL
        Objects.requireNonNull(getSupportActionBar()).hide();
        // TROCA A COR DA BARRA DE STATUS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }



        // Obtenha os serviços do carrinho do usuário no Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        LinearLayout parentLayout = findViewById(R.id.parentLayout);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int widthDp = 350;
                            int heightDp = 130;
                            float density = getResources().getDisplayMetrics().density;


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double preco = Double.parseDouble(document.getString("preco").replace("R$", "").trim());
                                subtotal += preco;

                                // Crie um novo CardView e defina seus parâmetros
                                CardView childCardView = new CardView(Pedido.this);

                                //Backfround CardView
                                childCardView.setCardBackgroundColor(ContextCompat.getColor(Pedido.this, R.color.azul_do_wash_azul));

                                LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                cardViewParams.setMargins(15, 15, 15, 15);
                                childCardView.setLayoutParams(cardViewParams);
                                childCardView.setRadius(8);
                                childCardView.setCardElevation(8);
                                childCardView.setUseCompatPadding(true);

                                // Crie um novo LinearLayout (childLayout) e defina seus parâmetros
                                LinearLayout childLayout = new LinearLayout(Pedido.this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                childLayout.setLayoutParams(layoutParams);
                                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                                childLayout.setPadding(16,0,0,0);

                                // Adicione um ImageView ao childLayout
                                ImageView imageView = new ImageView(Pedido.this);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(150, 150));

                                //Obtenha o ID do serviço
                                String ServicoID = document.getString("ServicoID");
                                String formattedServiceId = formatServiceId(ServicoID);
                                int imageResId = Pedido.this.getResources().getIdentifier(formattedServiceId, "drawable", Pedido.this.getPackageName());
                                imageView.setImageResource(imageResId);

                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                childLayout.addView(imageView);
                                // Adicione um LinearLayout (innerLayout) ao childLayout
                                LinearLayout innerLayout = new LinearLayout(Pedido.this);
                                LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                innerLayout.setLayoutParams(innerLayoutParams);
                                innerLayout.setOrientation(LinearLayout.VERTICAL);

                                //Adicione TextViews ao innerLayou
                                TextView txtServico = new TextView(Pedido.this);
                                txtServico.setText(document.getString("servicoID"));
                                txtServico.setTextColor(ContextCompat.getColor(Pedido.this, R.color.white));
                                txtServico.setTextSize(24);
                                txtServico.setTypeface(txtServico.getTypeface(), Typeface.BOLD);
                                innerLayout.addView(txtServico);

                                TextView txtDescricao = new TextView(Pedido.this);
                                txtDescricao.setText("Higienização geral"); // Substitua com a descrição real
                                txtDescricao.setTextColor(ContextCompat.getColor(Pedido.this, R.color.white));
                                innerLayout.addView(txtDescricao);

                                TextView txtPreco = new TextView(Pedido.this);
                                txtPreco.setText(document.getString("preco")); // Substitua com o preço real
                                txtPreco.setTextColor(ContextCompat.getColor(Pedido.this, R.color.white));
                                innerLayout.addView(txtPreco);

                                childLayout.addView(innerLayout);
                                childCardView.addView(childLayout);

                                //Adicione o childCardView ao parentLayout
                                parentLayout.addView(childCardView);
                            }


                            double totalOrderValue = subtotal + deliveryFee;

                            // Atualize os TextViews
                            TextView subtotalValor = findViewById(R.id.subTotalValue);
                            subtotalValor.setText(String.format("R$ %.2f", subtotal));

                            TextView taxaEntregaValue = findViewById(R.id.taxaEntregaValue);
                            taxaEntregaValue.setText(String.format("R$ %.2f", deliveryFee));

                            TextView tp_pedidoValue = findViewById(R.id.tp_pedidoValue);
                            tp_pedidoValue.setText(String.format("R$ %.2f", totalOrderValue));


                            Button button = findViewById(R.id.button);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Pedido.this, Pagamento.class);
                                    intent.putExtra("totalOrderValue", totalOrderValue);
                                    startActivity(intent);
                                }
                            });

                        }

                        else {
                            Log.w(TAG, "Erro ao obter documentos", task.getException());

                        }



                    }
                });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

        menu.findItem(R.id.idPedido).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.idPagamento) {
                startActivity(new Intent(Pedido.this, Pagamento.class));
                return true;
            } else if (itemId == R.id.idEntrega) {
                startActivity(new Intent(Pedido.this, Entrega.class));
                return true;
            } else if (itemId == R.id.idHome) {
                startActivity(new Intent(Pedido.this, TelaCentral.class));
                return true;
            } else if (itemId == R.id.idServico) {
                startActivity(new Intent(Pedido.this, Servico.class));
                return true;
            } else if (itemId == R.id.idPedido) {
                startActivity(new Intent(Pedido.this, Pedido.class));
                return true;
            }
            // Se nenhum item corresponder, retorne falso
            return false;
        });

        // VOLTA PARA A TELA DE SERVIÇOS AO CLICAR NO BOTÃO DE VOLTAR (btnVoltarCarrinho)
        ImageButton btnVoltarCarrinho = findViewById(R.id.btnvoltarCarrinho);

        btnVoltarCarrinho.setOnClickListener(view -> {
            Intent intent = new Intent(Pedido.this, Servico.class);

            // Inicie a nova Activity
            startActivity(intent);
        });


        TextView txtCadastroNV = findViewById(R.id.txtCadastroNV);
        txtCadastroNV.setOnClickListener(view -> {
            Intent intent = new Intent(Pedido.this, Cadastrar_endereco.class);
            startActivity(intent);
        });



        // Inicialize o Spinner
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);

        // Defina as opções de pagamento, incluindo "Selecionar" como a primeira opção
        String[] paymentOptions = {"Selecionar", "Cartão de Crédito", "Cartão de Débito"};

        // Crie um ArrayAdapter e conecte-o ao Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);


        // Inicialize o Spinner
        Spinner addressSpinner = findViewById(R.id.addressSpinner);

        //  lista de endereços salvos em um array
        String[] savedAddresses = {"Selecionar", "Endereço 1", "Endereço 2", "Endereço 3"};

        // ArrayAdapter e conecte-o ao Spinner
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, savedAddresses);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressAdapter);

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Quando nada é selecionado, se necessário
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
