package br.com.fiap.mba.corda.contracts

import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.contracts.CommandWithParties
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class NegociacaoContract : Contract {

    companion object {

        val ID = NegociacaoContract::class.qualifiedName

        const val ASSINATURA_OBLITO_OBRIGATORIA = "A assinatura do oblito é obrigatória!"
        const val ASSINATURA_PROPONENTE_OBRIGATORIA = "A assinatura do proponente é obrigatória!"
        
        const val COMPRADOR_NAO_MODIFICADO = "O comprador não foi modificado no output!"
        const val COMPRADOR_VENDEDOR = "O comprador e vendedor devem ser respectivamente o proponente e oblito!"
        const val TAXA_NAO_MODIFICADA = "A taxa não deve ser modificada no output!"
        const val VENDEDOR_NAO_MODIFICADO = "O vendedor não foi modificado no output!"

        const val NENHUM_INPUT = "Não existe nenhum input!"
        const val NENHUM_TIMESTAMP = "Não existe nenhum timestamp!"
        const val UNICO_COMMAND = "Existe apenas 1 command!";
        const val UNICO_INPUT = "Existe apenas 1 input!";
        const val UNICO_INPUT_PROPOSTASTATE = "Existe apenas 1 input do tipo PropostaState!";
        const val UNICO_OUTPUT = "Existe apenas 1 output!";
        const val UNICO_OUTPUT_PROPOSTASTATE = "Existe apenas 1 output do tipo PropostaState!";
    }

    private fun validarAceite(
        tx: LedgerTransaction,
        cmd: CommandWithParties<Commands>
    ) {

        requireThat {

            NENHUM_TIMESTAMP using (tx.timeWindow == null)
            UNICO_COMMAND using (tx.commands.size == 1)
            UNICO_INPUT using (tx.inputStates.size == 1)
            UNICO_INPUT_PROPOSTASTATE using (tx.inputsOfType<PropostaState>().size == 1)
            UNICO_OUTPUT using (tx.outputStates.size == 1)

            val input = tx.inputsOfType<PropostaState>().single()
            val output = tx.outputsOfType<PropostaState>().single()

            COMPRADOR_NAO_MODIFICADO using (input.comprador == output.comprador)
            TAXA_NAO_MODIFICADA using (output.taxa == input.taxa)
            VENDEDOR_NAO_MODIFICADO using (input.vendedor == output.vendedor)

            ASSINATURA_OBLITO_OBRIGATORIA using (cmd.signers.contains(input.oblato.owningKey))
            ASSINATURA_PROPONENTE_OBRIGATORIA using (cmd.signers.contains(input.proponente.owningKey))
        }
    }

    private fun validarContraProposta(
        tx: LedgerTransaction,
        cmd: CommandWithParties<Commands>
    ) {

        requireThat {

            NENHUM_TIMESTAMP using (tx.timeWindow == null)
            UNICO_COMMAND using (tx.commands.size == 1)
            UNICO_INPUT using (tx.inputStates.size == 1)
            UNICO_INPUT_PROPOSTASTATE using (tx.inputsOfType<PropostaState>().size == 1)
            UNICO_OUTPUT using (tx.outputStates.size == 1)
            UNICO_OUTPUT_PROPOSTASTATE using (tx.outputsOfType<PropostaState>().size == 1)

            val output = tx.outputsOfType<PropostaState>().single()
            val input = tx.inputsOfType<PropostaState>().single()

            COMPRADOR_NAO_MODIFICADO using (input.comprador == output.comprador)
            VENDEDOR_NAO_MODIFICADO using (input.vendedor == output.vendedor)

            ASSINATURA_OBLITO_OBRIGATORIA using (cmd.signers.contains(output.oblato.owningKey))
            ASSINATURA_PROPONENTE_OBRIGATORIA using (cmd.signers.contains(output.proponente.owningKey))
        }
    }

    private fun validarProposta(
        tx: LedgerTransaction,
        cmd: CommandWithParties<Commands>
    ) {

        requireThat {

            NENHUM_INPUT using (tx.inputStates.isEmpty())
            NENHUM_TIMESTAMP using (tx.timeWindow == null)
            UNICO_COMMAND using (tx.commands.size == 1)
            UNICO_OUTPUT using (tx.outputStates.size == 1)
            UNICO_OUTPUT_PROPOSTASTATE using (tx.outputsOfType<PropostaState>().size == 1)

            val output = tx.outputsOfType<PropostaState>().single()

            COMPRADOR_VENDEDOR using(
                setOf(output.comprador, output.vendedor) == setOf(output.proponente, output.oblato)
                )

            ASSINATURA_OBLITO_OBRIGATORIA using (cmd.signers.contains(output.oblato.owningKey))
            ASSINATURA_PROPONENTE_OBRIGATORIA using (cmd.signers.contains(output.proponente.owningKey))
        }
    }

    private fun validarRecusa(
        tx: LedgerTransaction,
        cmd: CommandWithParties<Commands>
    ) {

        requireThat {

            NENHUM_TIMESTAMP using (tx.timeWindow == null)
            UNICO_COMMAND using (tx.commands.size == 1)
            UNICO_INPUT using (tx.inputStates.size == 1)
            UNICO_INPUT_PROPOSTASTATE using (tx.inputsOfType<PropostaState>().size == 1)
            UNICO_OUTPUT using (tx.outputStates.size == 1)
            UNICO_OUTPUT_PROPOSTASTATE using (tx.outputsOfType<PropostaState>().size == 1)

            val input = tx.inputsOfType<PropostaState>().single()
            val output = tx.outputsOfType<PropostaState>().single()

            COMPRADOR_NAO_MODIFICADO using (input.comprador == output.comprador)
            TAXA_NAO_MODIFICADA using (output.taxa == input.taxa)
            VENDEDOR_NAO_MODIFICADO using (input.vendedor == output.vendedor)

            ASSINATURA_OBLITO_OBRIGATORIA using (cmd.signers.contains(input.oblato.owningKey))
            ASSINATURA_PROPONENTE_OBRIGATORIA using (cmd.signers.contains(input.proponente.owningKey))
        }
    }

    override fun verify(tx: LedgerTransaction) {

        val cmd = tx.commands.requireSingleCommand<Commands>()

        when (cmd.value) {

            is Commands.Aceitar -> validarAceite(tx, cmd)

            is Commands.ContraProposta -> validarContraProposta(tx, cmd)

            is Commands.Proposta -> validarProposta(tx, cmd)

            is Commands.Recusar -> validarRecusa(tx, cmd)
        }
    }

    // Used to indicate the transaction's intent.
    sealed class Commands : TypeOnlyCommandData() {
        
        class Aceitar : Commands()
        class ContraProposta : Commands()
        class Proposta : Commands()
        class Recusar : Commands()
    }
}
