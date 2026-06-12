package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.lugares

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.LugarImportanteEntity

class LugarAdapter(
    private var lugares: List<LugarImportanteEntity>,
    private val onEditar: (LugarImportanteEntity) -> Unit,
    private val onEliminar: (LugarImportanteEntity) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreLugar)
        val txtDetalle: TextView = itemView.findViewById(R.id.txtDetalleLugar)
        val txtUbicacion: TextView = itemView.findViewById(R.id.txtUbicacionLugar)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarLugar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarLugar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lugares[position]

        holder.txtNombre.text = lugar.nombre
        holder.txtDetalle.text = "${lugar.tipoLugar} • ${lugar.direccion}"
        holder.txtUbicacion.text = "${lugar.municipio}, ${lugar.departamento}"

        holder.btnEditar.setOnClickListener {
            onEditar(lugar)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(lugar)
        }
    }

    override fun getItemCount(): Int = lugares.size

    fun actualizarLista(nuevaLista: List<LugarImportanteEntity>) {
        lugares = nuevaLista
        notifyDataSetChanged()
    }
}
