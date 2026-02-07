package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import data.models.Dette;
import androidx.annotation.NonNull;
import com.example.or.bf.carnetdettes.R;


public class DetteAdapter extends RecyclerView.Adapter<DetteAdapter.ViewHolder> {

    private final List<Dette> dettes;

    public DetteAdapter(List<Dette> dettes) {
        this.dettes = dettes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dette, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dette d = dettes.get(position);
        holder.tvDescription.setText(d.getDescription());
        holder.tvMontant.setText(d.getMontant() + " FCFA");
    }

    @Override
    public int getItemCount() {
        return dettes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvMontant;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvMontant = itemView.findViewById(R.id.tvMontant);
        }
    }
}
