package ipca.stock.bopotav1

data class Equipamento(
    val id: Int,
    val condominioId: Int,
    val tipoLigacaoId: Int,
    val descricao1: String,
    val server: String,
    val deviceId: String,
    val authKey: String,
    val channel: String,
    val ativo: Boolean
)