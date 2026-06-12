package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.servicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ServicioEmergenciaEntity

class ServicioEmergenciaAdapter(
    private var servicios: List<ServicioEmergenciaEntity>,
    private val onLlamar: (ServicioEmergenciaEntity) -> Unit,
    private val onVerMapa: (ServicioEmergenciaEntity) -> Unit
) : RecyclerView.Adapter<ServicioEmergenciaAdapter.ServicioViewHolder>() {

    class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreServicio: TextView = itemView.findViewById(R.id.txtNombreServicio)
        val txtDetalleServicio: TextView = itemView.findViewById(R.id.txtDetalleServicio)
        val txtDireccionServicio: TextView = itemView.findViewById(R.id.txtDireccionServicio)
        val btnLlamarServicio: Button = itemView.findViewById(R.id.btnLlamarServicio)
        val btnVerMapaServicio: Button = itemView.findViewById(R.id.btnVerMapaServicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio_emergencia, parent, false)

        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]

        holder.txtNombreServicio.text = servicio.nombre
        holder.txtDetalleServicio.text = "${servicio.tipoServicio} • ${servicio.telefono}"
        holder.txtDireccionServicio.text = "${servicio.direccion}, ${servicio.municipio}, ${servicio.departamento}"

        holder.btnLlamarServicio.setOnClickListener {
            onLlamar(servicio)
        }

        holder.btnVerMapaServicio.setOnClickListener {
            onVerMapa(servicio)
        }
    }

    override fun getItemCount(): Int = servicios.size

    fun actualizarLista(nuevaLista: List<ServicioEmergenciaEntity>) {
        servicios = nuevaLista
        notifyDataSetChanged()
    }
}
