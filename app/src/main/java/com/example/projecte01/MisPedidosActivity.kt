package com.example.projecte01

import Order
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MisPedidosActivity : AppCompatActivity() {

    private lateinit var pedidosAdapter: MisPedidosAdapter
    private lateinit var pedidosList: MutableList<Order>
    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos)

        val botonAtras = findViewById<ImageButton>(R.id.botonAtras)
        botonAtras.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPedidos)
        pedidosList = mutableListOf()
        pedidosAdapter = MisPedidosAdapter(pedidosList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pedidosAdapter

        SocketHandler.establishConnection()
        socket = SocketHandler.getSocket()!!

        socket.on("cambioEstado", onPedidoStatusChange)

        cargarPedidos()
    }

    private fun cargarPedidos() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.instance.getOrders()
            if (response.isSuccessful) {
                pedidosList.clear()
                response.body()?.let {

                }

            }
        }
    }

    private val onPedidoStatusChange = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val orderId = data.getInt("order_id")
        val newStatus = data.getString("status")

        runOnUiThread {
            val pedido = pedidosList.find { it.order_id == orderId }
            if (pedido != null) {
                pedido.status = newStatus
                pedidosAdapter.notifyDataSetChanged()
                Toast.makeText(this, "El pedido ${pedido.order_id} cambió a $newStatus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.off("cambioEstado", onPedidoStatusChange)
        SocketHandler.closeConnection()
    }
}
