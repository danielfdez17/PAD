package es.ucm.fdi.saborearte;
import java.util.List;
import android.content.Context;
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

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta recetaActual = listaRecetas.get(position);
        holder.tvTitulo.setText(recetaActual.getTitulo());
        holder.tvTiempo.setText(recetaActual.getTiempoPreparacion() + " minutos");

        // Glide para cargar imágenes desde URLs
         Glide.with(context).load(recetaActual.getImage_uri()).into(holder.ivImagen);
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
            //tvTitulo = itemView.findViewById(R.id.tv_titulo);
            //tvTiempo = itemView.findViewById(R.id.tv_tiempo);
            //ivImagen = itemView.findViewById(R.id.iv_imagen);
        }
    }
}
