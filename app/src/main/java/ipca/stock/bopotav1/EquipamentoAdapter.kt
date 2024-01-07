package ipca.stock.bopotav1

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView

class EquipamentoAdapter(private val context: Context, private val equipamentos: List<Equipamento>) : BaseAdapter() {

    override fun getCount(): Int {
        return equipamentos.size
    }

    override fun getItem(position: Int): Any {
        return equipamentos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            // Inflar o layout personalizado para cada item na lista
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.row_garage, parent, false)

            // Inicializar o ViewHolder
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        // Preencher os dados do equipamento no layout
        val equipamento = equipamentos[position]
        viewHolder.textViewGarageName.text = equipamento.descricao1
        viewHolder.switchStatus.isChecked = equipamento.ativo

        // Adicionar um listener de clique ao item da lista
        view.setOnClickListener {
            // Criar um Intent
            val intent = Intent(context, GaragemDetailActivity::class.java)

            // Adicionar o ID do equipamento como extra
            intent.putExtra("equipamento_id", equipamento.id)
            intent.putExtra("equipamento_nome", equipamento.descricao1)
            // Iniciar a GaragemDetailActivity
            context.startActivity(intent)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val textViewGarageName: TextView = view.findViewById(R.id.textViewGarageName)
        val switchStatus: Switch = view.findViewById(R.id.switchStatusGarage) // Corrigido para switchStatus
    }
}
