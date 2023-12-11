package es.ucm.fdi.saborearte;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class RecetaResultListAdapter extends RecyclerView.Adapter<RecetaResultListAdapter.RecetaViewHolder> {

    private List<Receta> listaRecetas;
    private Context context;
    private List<Receta> listaDeRecetas;

    // Constructor
    public RecetaResultListAdapter(Context context, List<Receta> listaRecetas) {
        this.context = context;
        this.listaRecetas = listaRecetas;
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    public void setRecetas(List<Receta> recetas) {
        this.listaDeRecetas = recetas;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta recetaActual = listaRecetas.get(position);
        holder.tvTitulo.setText(recetaActual.getTitulo());
        holder.tvTiempo.setText(recetaActual.getTiempoPreparacion() + " min.");

         Glide.with(context).load(recetaActual.getImage_uri()).into(holder.ivImagen);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecetaActivity.class);
                intent.putExtra("receta", recetaActual);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvTiempo;
        ImageView ivImagen;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.item_nombre);
            tvTiempo = itemView.findViewById(R.id.item_time);
            ivImagen = itemView.findViewById(R.id.item_imagen);
        }
    }
}
