package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.alertas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.repository.HistorialAlertaItem

class HistorialAlertasAdapter(
    private var alertas: List<HistorialAlertaItem>
) : RecyclerView.Adapter<HistorialAlertasAdapter.AlertaViewHolder>() {

    class AlertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTipoAlerta: TextView = itemView.findViewById(R.id.txtTipoAlerta)
        val txtFechaAlerta: TextView = itemView.findViewById(R.id.txtFechaAlerta)
        val txtMensajeAlerta: TextView = itemView.findViewById(R.id.txtMensajeAlerta)
        val txtUbicacionAlerta: TextView = itemView.findViewById(R.id.txtUbicacionAlerta)
        val txtEstadoAlerta: TextView = itemView.findViewById(R.id.txtEstadoAlerta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta_historial, parent, false)

        return AlertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertaViewHolder, position: Int) {
        val alerta = alertas[position]

        holder.txtTipoAlerta.text = "${alerta.tipoAlerta} • ${alerta.origenAlerta}"
        holder.txtFechaAlerta.text = alerta.fechaHora
        holder.txtMensajeAlerta.text = alerta.mensaje
        holder.txtUbicacionAlerta.text = "Lat: ${alerta.latitud} | Lng: ${alerta.longitud}"
        holder.txtEstadoAlerta.text = "Estado: ${alerta.estado}"
    }

    override fun getItemCount(): Int = alertas.size

    fun actualizarLista(nuevaLista: List<HistorialAlertaItem>) {
        alertas = nuevaLista
        notifyDataSetChanged()
    }
}
