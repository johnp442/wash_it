package com.aula.wash.it.activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.wash.it.Adapters.AdapterServicos;
import com.aula.wash.it.Adapters.VideosAdapter;
import com.aula.wash.it.R;
import com.aula.wash.it.model.Videos;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelaCentral extends AppCompatActivity {

    private TextInputEditText searchEditText;
    private static final String TAG = AdapterServicos.class.getSimpleName();
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private ViewFlipper viewFlipper;
    private float lastX;
    private List<Videos> Videos;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_central);

        // Recupere o UID da intent.
        String uid = getIntent().getStringExtra("uid");

        // Use o UID conforme necessário em sua atividade
        if (uid != null) {
            Log.d(TAG, "UID: " + uid);
            // Realize as operações necessárias com o UID.
        }
        Intent intent = new Intent(TelaCentral.this, Servico.class);
        intent.putExtra("uid", uid); // Passe o UID para a próxima atividade

        RecyclerView recyclerVideos = findViewById(R.id.recyclerVideos);
        Videos = new ArrayList<>();

        // DEFINE layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerVideos.setLayoutManager(layoutManager);

        // DEFINE adapter
        this.prepararVideos();
        VideosAdapter adapter = new VideosAdapter(Videos);
        recyclerVideos.setAdapter(adapter);

        //OCULTA A LABEL DO TITULO DO APP
        Objects.requireNonNull(getSupportActionBar()).hide();
        // ALTERA A COR DOS STATUS (BARRA DE ICONES, ex. horas, wifi ...)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.azul_do_wash_azul));

        // MENU BOTTOM
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

        // Marcar o item "Home" como ativo
        menu.findItem(R.id.idHome).setChecked(true);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.idPagamento) {
                startActivity(new Intent(TelaCentral.this, Pagamento.class));
                return true;
            } else if (itemId == R.id.idEntrega) {
                startActivity(new Intent(TelaCentral.this, Entrega.class));
                return true;
            } else if (itemId == R.id.idServico) {
                startActivity(new Intent(TelaCentral.this, Servico.class));
                return true;
            } else if (itemId == R.id.idPedido) {
                startActivity(new Intent(TelaCentral.this, Pedido.class));
                return true;
            } else if (itemId == R.id.idHome) {
                startActivity(new Intent(TelaCentral.this, TelaCentral.class));
                return true;
            }
            // Se nenhum item corresponder, retorne falso
            return false;
        });

        // Inicialize os elementos de interface do usuário

        ConstraintLayout mainLayout = findViewById(R.id.layouprincipal);




        // Adicione um ouvinte de clique ao layout principal para remover o foco do campo de pesquisa
        mainLayout.setOnTouchListener((v, event) -> {
            // Remova o foco do campo de pesquisa ao tocar fora dele

            return false;
        });

        // Inicializa o ViewFlipper
        viewFlipper = findViewById(R.id.sliderflipper);

        // Adicione um ouvinte de toque para detectar gestos de deslize
        viewFlipper.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float currentX = event.getX();
                    if (lastX < currentX) {
                        // Deslize para a esquerda (anterior)
                        viewFlipper.showPrevious();
                    } else if (lastX > currentX) {
                        // Deslize para a direita (próximo)
                        viewFlipper.showNext();
                    }
                    break;
            }
            return true;
        });

        // Inicialize os botões para trocar imagens
        ImageButton btnAnterior = findViewById(R.id.btnAnterior);
        ImageButton btnProximo = findViewById(R.id.btnProximo);

        // Adicione um ouvinte de clique para os botões
        btnAnterior.setOnClickListener(v -> viewFlipper.showPrevious());

        btnProximo.setOnClickListener(v -> viewFlipper.showNext());


    }

    public void prepararVideos() {
        Videos prepVideo = new Videos("Um detalhe faz diferença!", "Nossos serviços de ponta vão além da limpeza padrão - removemos manchas, restauramos cores e devolvemos o brilho original aos seus tênis. ", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video1));
        this.Videos.add(prepVideo);

        Videos prepVideo2 = new Videos("Transformação", "Na Wash It, acreditamos que não existem sneakers feios, apenas sneakers que ainda não conhecem o poder dos nossos produtos", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video2));
        this.Videos.add(prepVideo2);

        Videos prepVideo3 = new Videos("Lavagem completa", "Na Wash It, sabemos que a lavagem faz toda a diferença quando se trata dos seus sneakers favoritos. Nossa equipe especializada está pronta para cuidar de cada par, garantindo uma limpeza minuciosa e restauração impecável.", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video3));
        this.Videos.add(prepVideo3);

        Videos prepVideo4 = new Videos("Começando a semana", "Comece a semana com o pé direito e os tênis impecáveis! Na Wash It, estamos prontos para receber seus sneakers e começar a semana com uma produção de lavagem de alta qualidade.", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video4));
        this.Videos.add(prepVideo4);

        Videos prepVideo5 = new Videos("Antes e Depois", "Conte com a Wash It para deixar seus tênis brilhando como novos, do início ao fim!", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video5));
        this.Videos.add(prepVideo5);

        Videos prepVideo6 = new Videos("Historias", "Cada sneaker possui uma história única e valiosa. Na Wash It, entendemos a importância dessas histórias e nos esforçamos para preservá-las.", Uri.parse("android.resource://com.aula.wash.it/" + R.raw.video6));
        this.Videos.add(prepVideo6);
    }



    // Iniciar o reconhecimento de voz
    private void startVoiceRecognition() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            // Trate qualquer exceção que possa ocorrer ao iniciar o reconhecimento de voz
        }
    }

    // Lida com o resultado do reconhecimento de voz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            java.util.ArrayList<String> result = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String recognizedText = result.get(0);
                searchEditText.setText(recognizedText);
            }
        }
    }

    // Oculta o teclado virtual ao clicar fora de um campo
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
