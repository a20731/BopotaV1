package ipca.stock.bopotav1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class LogAcessoAdapter(private val context: Context, private val logs: List<LogAcesso>) : BaseAdapter() {


    private fun formatarData(dataHoraOriginal: String): String {
        val formatoOriginal = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val formatoDesejado = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

        try {
            val data = formatoOriginal.parse(dataHoraOriginal)
            return formatoDesejado.format(data)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dataHoraOriginal // Retorna a data original se ocorrer algum erro
    }


    override fun getCount(): Int {
        return logs.size
    }




    override fun getItem(position: Int): Any {
        return logs[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.row_logacess, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val log = logs[position]
        viewHolder.textViewTitle.text = if (log.tipoLogId == 1) "Abrir" else "Fechar"
        //viewHolder.textViewDescription.text = "Equipamento ID: ${log.equipamentoId}" // Ajuste conforme necessário
        viewHolder.textViewDate.text = formatarData(log.dataHora)

        return view
    }

    private class ViewHolder(view: View) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        //val textViewDescription: TextView = view.findViewById(R.id.textViewDescription)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
    }
}
