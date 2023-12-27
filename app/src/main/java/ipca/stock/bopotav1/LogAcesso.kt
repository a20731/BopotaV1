package ipca.stock.bopotav1

data class LogAcesso(
    val id: Int,
    val equipamentoId: Int,
    val utilizadorId: Int,
    val dataHora: String,
    val tipoLogId: Int
)
