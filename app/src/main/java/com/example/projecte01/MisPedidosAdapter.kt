package com.example.projecte01

import Order
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R

class MisPedidosAdapter(private val pedidosList: List<Order>) :
    RecyclerView.Adapter<MisPedidosAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pedidoId: TextView = view.findViewById(R.id.pedidoIdTextView)
        val pedidoStatus: TextView = view.findViewById(R.id.pedidoStatusTextView)
        val pedidoTotal: TextView = view.findViewById(R.id.pedidoTotalTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidosList[position]
        holder.pedidoId.text = "Pedido ID: ${pedido.order_id}"
        holder.pedidoStatus.text = "Estado: ${pedido.status}"
        holder.pedidoTotal.text = "Total: ${pedido.total} €"
    }

    override fun getItemCount(): Int = pedidosList.size
}
