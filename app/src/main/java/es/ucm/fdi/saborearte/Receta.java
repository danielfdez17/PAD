package es.ucm.fdi.saborearte;
import java.util.List;

public class Receta {
    private String titulo;
    private List<String> ingredientes;
    private int tiempoPreparacion;  // en minutos
    private String imagenUrl;

    // Constructor
    public Receta(String titulo, List<String> ingredientes, int tiempoPreparacion, String imagenUrl) {
        this.titulo = titulo;
        this.ingredientes = ingredientes;
        this.tiempoPreparacion = tiempoPreparacion;
        this.imagenUrl = imagenUrl;
    }
    // Getters
    public String getTitulo() {
        return titulo;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    // Setters
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
