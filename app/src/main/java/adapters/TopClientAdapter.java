package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.or.bf.carnetdettes.R;

import java.util.List;

import data.models.ClientDebt;

public class TopClientAdapter extends RecyclerView.Adapter<TopClientAdapter.ViewHolder> {

    private final List<ClientDebt> clients;

    public TopClientAdapter(List<ClientDebt> clients) {
        this.clients = clients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_debt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClientDebt client = clients.get(position);
        holder.tvName.setText(client.getNom());
        holder.tvDebt.setText(String.format("%,d FCFA", client.getMontant()));
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDebt;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvClientName);
            tvDebt = itemView.findViewById(R.id.tvClientDebt);
        }
    }
}
