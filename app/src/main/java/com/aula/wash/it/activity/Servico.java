package com.aula.wash.it.activity;

import static android.content.Intent.getIntent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.wash.it.Adapters.AdapterServicos;
import com.aula.wash.it.R;
import com.aula.wash.it.model.ServicosNovos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Servico extends AppCompatActivity {

    private static final String TAG = AdapterServicos.class.getSimpleName();
    private List<com.aula.wash.it.model.ServicosNovos> ServicosNovos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OCULTA A BARRA DO LABEL
        Objects.requireNonNull(getSupportActionBar()).hide();
        // TROCA A COR DA BARRA DE STATUS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));
        }
        setContentView(R.layout.activity_servico);
        String uid = getIntent().getStringExtra("uid");

        // Use o UID conforme necessário em sua atividade
        if (uid != null) {
            Log.d(TAG, "UID: " + uid);
            // Realize as operações necessárias com o UID
        }

        // Obtenha os serviços do carrinho do usuário no Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Obtenha o ID do usuário logado

        db.collection("users").document(userId).collection("cart")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Servico", "Listen failed.", e);
                            return;
                        }

                        int totalItems = queryDocumentSnapshots.size();

                        // Obtenha uma referência para o TextView
                        TextView quantidadeItens = findViewById(R.id.quantidadeItens);

                        // Atualize o TextView
                        quantidadeItens.setText(String.valueOf(totalItems));
                    }
                });

        //MENU NAV
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.idServico).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.idPagamento) {
                startActivity(new Intent(Servico.this, Pagamento.class));
                return true;
            } else if (itemId == R.id.idEntrega) {
                startActivity(new Intent(Servico.this, Entrega.class));
                return true;
            } else if (itemId == R.id.idHome) {
                startActivity(new Intent(Servico.this, TelaCentral.class));
                return true;
            } else if (itemId == R.id.idPedido) {
                startActivity(new Intent(Servico.this, Pedido.class));
                return true;
            } else if (itemId == R.id.idServico) {
                startActivity(new Intent(Servico.this, Servico.class));
                return true;
            }
            // Se nenhum item corresponder, retorne falso
            return false;
        });

        RecyclerView recyclerServicos = findViewById(R.id.RecyclerServicos);
        // DEFINE LAYOUT
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerServicos.setLayoutManager(layoutManager);

        // DEFINE ADAPTER
        this.prepararServicos();
        AdapterServicos adapter = new AdapterServicos(ServicosNovos);
        recyclerServicos.setAdapter(adapter);

        // BOTÃO DO CARRINHO QUE INDICA O TOTAL
        ImageButton carrinhoDecompras = findViewById(R.id.carrinhoDecompras);

        carrinhoDecompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crie uma Intent para iniciar a nova Activity
                Intent intent = new Intent(getApplicationContext(), Pedido.class);

                // Inicie a nova Activity
                startActivity(intent);
            }
        });


    }

    // CONFIGURA UMA NOVA LISTA DE SERVIÇOS
    public void prepararServicos() {
        ServicosNovos servico1 = new ServicosNovos();
        servico1.setImgServico(R.drawable.servico1);
        servico1.setTxtServico("Serviço 1");
        servico1.setTxtTitulo("Higienização geral");
        servico1.setTxtPreco("R$ 50.00");
        this.ServicosNovos.add(servico1);

        ServicosNovos servico2 = new ServicosNovos();
        servico2.setImgServico(R.drawable.servico2);
        servico2.setTxtServico("Serviço 2");
        servico2.setTxtTitulo("Pintura de boost");
        servico2.setTxtPreco("R$ 20.00");
        this.ServicosNovos.add(servico2);

        ServicosNovos servico3 = new ServicosNovos();
        servico3.setImgServico(R.drawable.servico3);
        servico3.setTxtServico("Serviço 3");
        servico3.setTxtTitulo("Pintura de mid");
        servico3.setTxtPreco("R$ 30.00");
        this.ServicosNovos.add(servico3);

        ServicosNovos servico4 = new ServicosNovos();
        servico4.setImgServico(R.drawable.servico4);
        servico4.setTxtServico("Serviço 4");
        servico4.setTxtTitulo("Impermeabilização");
        servico4.setTxtPreco("R$ 20.00");
        this.ServicosNovos.add(servico4);

        ServicosNovos servico5 = new ServicosNovos();
        servico5.setImgServico(R.drawable.servico5);
        servico5.setTxtServico("Serviço 5");
        servico5.setTxtTitulo("Hidratação");
        servico5.setTxtPreco("R$ 30.00");
        this.ServicosNovos.add(servico5);

        ServicosNovos servico6 = new ServicosNovos();
        servico6.setImgServico(R.drawable.servico6);
        servico6.setTxtServico("Serviço 6");
        servico6.setTxtTitulo("Renovação camurça");
        servico6.setTxtPreco("R$ 55.00");
        this.ServicosNovos.add(servico6);

        ServicosNovos servico7 = new ServicosNovos();
        servico7.setImgServico(R.drawable.servico7);
        servico7.setTxtServico("Serviço 7");
        servico7.setTxtTitulo("Colagem");
        servico7.setTxtPreco("R$ 39.99");
        this.ServicosNovos.add(servico7);

        ServicosNovos servico8 = new ServicosNovos();
        servico8.setImgServico(R.drawable.servico8);
        servico8.setTxtServico("Serviço 8");
        servico8.setTxtTitulo("Reparos");
        servico8.setTxtPreco("R$ 39.99");
        this.ServicosNovos.add(servico8);

        ServicosNovos servico9 = new ServicosNovos();
        servico9.setImgServico(R.drawable.servico9);
        servico9.setTxtServico("Serviço 9");
        servico9.setTxtTitulo("Remoção de crease");
        servico9.setTxtPreco("R$ 39.99");
        this.ServicosNovos.add(servico9);
    }

    public void adicionarAoCarrinho(View view) {
        // Tornar o botão invisível
        Button addCarrinho = findViewById(R.id.addCarrinho);
        addCarrinho.setVisibility(View.GONE);

        // Tornar o layout de aumento, diminuição e quantidade visível
        LinearLayout layoutBotoes = findViewById(R.id.layoutBotoes);
        layoutBotoes.setVisibility(View.VISIBLE);

        // Configurar um valor inicial para a quantidade (por exemplo, 1)
        final int[] quantidade = {1};
        TextView quantidadeText = findViewById(R.id.quantidadeText);
        quantidadeText.setText(String.valueOf(quantidade[0]));

        // Defini os ouvintes dos botões de aumento e diminuição
        ImageButton btnDiminuir = findViewById(R.id.btnDiminuir);
        ImageButton btnAumentar = findViewById(R.id.btnAumentar);

        btnDiminuir.setOnClickListener(v -> {
            quantidade[0]--;
            if (quantidade[0] < 1) {
                quantidade[0] = 1;
                addCarrinho.setVisibility(View.VISIBLE);
            }
            quantidadeText.setText(String.valueOf(quantidade[0]));

            if (quantidade[0] == 0) {
                // Tornar o botão "Adicionar ao Carrinho" visível novamente
                addCarrinho.setVisibility(View.VISIBLE);
                layoutBotoes.setVisibility(View.GONE);
            }
        });

        btnAumentar.setOnClickListener(v -> {
            quantidade[0]++;
            quantidadeText.setText(String.valueOf(quantidade[0]));
        });
    }


}
