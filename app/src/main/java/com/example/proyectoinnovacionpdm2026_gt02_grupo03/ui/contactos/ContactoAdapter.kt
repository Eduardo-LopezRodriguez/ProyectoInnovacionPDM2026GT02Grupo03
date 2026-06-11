package com.example.proyectoinnovacionpdm2026_gt02_grupo03.ui.contactos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.R
import com.example.proyectoinnovacionpdm2026_gt02_grupo03.data.local.entity.ContactoConfianzaEntity

class ContactoAdapter(
    private var contactos: List<ContactoConfianzaEntity>,
    private val onEditar: (ContactoConfianzaEntity) -> Unit,
    private val onEliminar: (ContactoConfianzaEntity) -> Unit
) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    class ContactoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreContacto)
        val txtDetalle: TextView = itemView.findViewById(R.id.txtDetalleContacto)
        val txtPrioridad: TextView = itemView.findViewById(R.id.txtPrioridadContacto)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarContacto)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarContacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto, parent, false)
        return ContactoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = contactos[position]

        holder.txtNombre.text = contacto.nombre
        holder.txtDetalle.text = "${contacto.parentesco} • ${contacto.telefono}"
        holder.txtPrioridad.text = "Prioridad: ${contacto.prioridad}"

        holder.btnEditar.setOnClickListener {
            onEditar(contacto)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(contacto)
        }
    }

    override fun getItemCount(): Int = contactos.size

    fun actualizarLista(nuevaLista: List<ContactoConfianzaEntity>) {
        contactos = nuevaLista
        notifyDataSetChanged()
    }
}
