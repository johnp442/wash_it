package com.aula.wash.it.Adapters;



import static android.content.Intent.getIntent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.wash.it.R;
import com.aula.wash.it.model.ServicosNovos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterServicos extends RecyclerView.Adapter<AdapterServicos.MyViewHolder> {
    private List<com.aula.wash.it.model.ServicosNovos> ServicosNovos;

    private static final String TAG = AdapterServicos.class.getSimpleName();


    public AdapterServicos(List<ServicosNovos> ServicosNovos) {
        this.ServicosNovos = ServicosNovos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ServicosNovos servico = ServicosNovos.get(position);
        holder.imgServico.setImageResource(servico.getImgServico());
        holder.txtServico.setText(servico.getTxtServico());
        holder.txtTitulo.setText(servico.getTxtTitulo());
        holder.txtPreco.setText(servico.getTxtPreco());


        // Inicialize a quantidade como 0
        holder.quantidade = 0;

        holder.addCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verifique se o usuário está logado
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // Usuário não está logado. Mostre uma mensagem toast ou redirecione para a tela de login
                    Toast.makeText(view.getContext(), "Você precisa estar logado para adicionar itens ao seu carrinho", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tornar o botão invisível
                holder.addCarrinho.setVisibility(View.GONE);

                // Tornar o layout de aumento, diminuição e quantidade visível
                holder.layoutBotoes.setVisibility(View.VISIBLE);

                // Configura um valor inicial para a quantidade (por exemplo, 1) para o serviço atual
                holder.quantidadeText.setText("1");

                // Definir o ouvinte dos botões de aumento
                holder.btnAumentar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.quantidade < 5) {
                            holder.quantidade++;
                            holder.quantidadeText.setText(String.valueOf(holder.quantidade));
                        }
                    }
                });

                // Definir o ouvinte dos botões de diminuição
                holder.btnDiminuir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.quantidade > 0) {
                            holder.quantidade--;
                            if (holder.quantidade == 0) {
                                // Torna o botão "Adicionar ao Carrinho" visível novamente
                                holder.addCarrinho.setVisibility(View.VISIBLE);
                                holder.layoutBotoes.setVisibility(View.GONE);
                            }
                            holder.quantidadeText.setText(String.valueOf(holder.quantidade));
                        }
                    }
                });


                // Após definir a quantidade, adicione o serviço ao carrinho do usuário no Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = currentUser.getUid(); //

                // Crie um novo item no carrinho
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("servicoID", servico.getTxtServico());
                cartItem.put("preco", servico.getTxtPreco());

                //Adicione o novo item ao carrinho do usuário no Firestore
                db.collection("users").document(userId).collection("cart").add(cartItem)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Item do carrinho adicionado com ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Erro ao adicionar item ao carrinho", e);
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return ServicosNovos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgServico;
        private TextView txtServico;
        private TextView txtTitulo;
        private TextView txtPreco;
        private Button addCarrinho;
        private LinearLayout layoutBotoes;
        private ImageButton btnDiminuir;
        private ImageButton btnAumentar;
        private TextView quantidadeText;
        private int quantidade; // Associe a quantidade a cada serviço individualmente

        public MyViewHolder(View itemView) {
            super(itemView);
            // Recupere o UID do intent

            imgServico = itemView.findViewById(R.id.imgServico);
            txtServico = itemView.findViewById(R.id.txtServico);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtPreco = itemView.findViewById(R.id.txtPreco);
            addCarrinho = itemView.findViewById(R.id.addCarrinho);
            layoutBotoes = itemView.findViewById(R.id.layoutBotoes);
            btnDiminuir = itemView.findViewById(R.id.btnDiminuir);
            btnAumentar = itemView.findViewById(R.id.btnAumentar);
            quantidadeText = itemView.findViewById(R.id.quantidadeText);
        }
    }
}
