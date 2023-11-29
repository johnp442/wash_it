package com.aula.wash.it.Adapters;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.wash.it.R;
import com.aula.wash.it.model.Videos;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {
    private List<Videos> Videos;

    public VideosAdapter(List<Videos> ListaVideos) {
        this.Videos = ListaVideos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(itemList);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Videos videos = Videos.get(position);
        holder.tituloVideo.setText(videos.getTituloVideo());
        TextView descricaoVideo = holder.descricaoVideo; // Adicione esta linha
        VideoView videoView = holder.videoView;
        ImageView imgCover = holder.imgCover;
        ImageView idPlayer = holder.idPlayer;
        MediaController mediaController = holder.mediaController;
        descricaoVideo.setText(videos.getDescricaoVideo()); // Use descricaoVideo aqui
        videoView.setVideoURI(videos.getVideoUri());
        videoView.setMediaController(mediaController);

        // Adicione um OnClickListener para a imagem do reprodutor (idPlayer)
        idPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar o vídeo
                videoView.start();
                videoView.setVisibility(View.VISIBLE);
                // Tornar o reprodutor e a imagem de capa invisíveis
                idPlayer.setVisibility(View.GONE);
                imgCover.setVisibility(View.GONE);
            }
        });

        // Adicione um OnPreparedListener para o VideoView
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Defina a visibilidade do reprodutor e da imagem de capa para INVISÍVEL
                idPlayer.setVisibility(View.GONE);
                imgCover.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
            }
        });



        // Adicione um OnTouchListener ao VideoView para detectar toques na tela
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()) {
                    // Verificar se o vídeo está reproduzindo e pausá-lo
                    videoView.pause();
                    // Mostrar as imagens (imgCover e idPlayer) quando o vídeo é pausado
                    idPlayer.setVisibility(View.VISIBLE);
                    imgCover.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Videos.size(); // Retorna o número de vídeos na lista
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tituloVideo;
        private VideoView videoView;
        private ImageView imgCover, idPlayer;
        private MediaController mediaController;
        private Button buttonExpand;
        private TextView descricaoVideo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicialize as views adicionais
            tituloVideo = itemView.findViewById(R.id.tituloVideo);
            videoView = itemView.findViewById(R.id.videoView);
            imgCover = itemView.findViewById(R.id.imgCover);
            idPlayer = itemView.findViewById(R.id.idPlayer);
            mediaController = new MediaController(itemView.getContext());

            // Inicialize as views adicionais (descricaoVideo e buttonExpand)
            descricaoVideo = itemView.findViewById(R.id.descricaoVideo); // Adicione esta linha
        }
    }
}
