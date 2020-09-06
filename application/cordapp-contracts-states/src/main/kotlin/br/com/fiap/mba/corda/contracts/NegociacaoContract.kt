package br.com.fiap.mba.corda.contracts

import br.com.fiap.mba.corda.states.NegociacaoState
import br.com.fiap.mba.corda.states.PropostaState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class NegociacaoContract : Contract {

    companion object {
        const val ID = "br.com.fiap.mba.corda.contracts.NegociacaoContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val cmd = tx.commands.requireSingleCommand<Commands>()

        when (cmd.value) {

            is Commands.Aceitar -> requireThat {
                "There is exactly one input" using (tx.inputStates.size == 1)
                "The single input is of type PropostaState" using (tx.inputsOfType<PropostaState>().size == 1)
                "There is exactly one output" using (tx.outputStates.size == 1)
                "The single output is of type NegociacaoState" using (tx.outputsOfType<NegociacaoState>().size == 1)
                "There is exactly one command" using (tx.commands.size == 1)
                "There is no timestamp" using (tx.timeWindow == null)

                val input = tx.inputsOfType<PropostaState>().single()
                val output = tx.outputsOfType<NegociacaoState>().single()

                "The taxa is unmodified in the output" using (output.taxa == input.taxa)
                "The comprador is unmodified in the output" using (input.comprador == output.comprador)
                "The vendedor is unmodified in the output" using (input.vendedor == output.vendedor)

                "The proponente is a required signer" using (cmd.signers.contains(input.proponente.owningKey))
                "The oblito is a required signer" using (cmd.signers.contains(input.oblato.owningKey))
            }

            is Commands.ContraProposta -> requireThat {
                "There is exactly one input" using (tx.inputStates.size == 1)
                "The single input is of type PropostaState" using (tx.inputsOfType<PropostaState>().size == 1)
                "There is exactly one output" using (tx.outputStates.size == 1)
                "The single output is of type PropostaState" using (tx.outputsOfType<PropostaState>().size == 1)
                "There is exactly one command" using (tx.commands.size == 1)
                "There is no timestamp" using (tx.timeWindow == null)

                val output = tx.outputsOfType<PropostaState>().single()
                val input = tx.inputsOfType<PropostaState>().single()

                "The taxa is modified in the output" using (output.taxa != input.taxa)
                "The comprador is unmodified in the output" using (input.comprador == output.comprador)
                "The vendedor is unmodified in the output" using (input.vendedor == output.vendedor)

                "The proponente is a required signer" using (cmd.signers.contains(output.proponente.owningKey))
                "The oblito is a required signer" using (cmd.signers.contains(output.oblato.owningKey))
            }

            is Commands.Proposta -> requireThat {
                "There are no inputs" using (tx.inputStates.isEmpty())
                "There is exactly one output" using (tx.outputStates.size == 1)
                "The single output is of type PropostaState" using (tx.outputsOfType<PropostaState>().size == 1)
                "There is exactly one command" using (tx.commands.size == 1)
                "There is no timestamp" using (tx.timeWindow == null)

                val output = tx.outputsOfType<PropostaState>().single()
                "The comprador and vendedor are the proponente and the oblito" using (setOf(output.comprador, output.vendedor) == setOf(output.proponente, output.oblato))

                "The proponente is a required signer" using (cmd.signers.contains(output.proponente.owningKey))
                "The oblito is a required signer" using (cmd.signers.contains(output.oblato.owningKey))
            }

            is Commands.Recusar -> requireThat {
                "There is exactly one input" using (tx.inputStates.size == 1)
                "The single input is of type PropostaState" using (tx.inputsOfType<PropostaState>().size == 1)
                "There is exactly one output" using (tx.outputStates.size == 1)
                "The single output is of type NegociacaoState" using (tx.outputsOfType<NegociacaoState>().size == 1)
                "There is exactly one command" using (tx.commands.size == 1)
                "There is no timestamp" using (tx.timeWindow == null)

                val input = tx.inputsOfType<PropostaState>().single()
                val output = tx.outputsOfType<NegociacaoState>().single()

                "The taxa is unmodified in the output" using (output.taxa == input.taxa)
                "The comprador is unmodified in the output" using (input.comprador == output.comprador)
                "The vendedor is unmodified in the output" using (input.vendedor == output.vendedor)

                "The proponente is a required signer" using (cmd.signers.contains(input.proponente.owningKey))
                "The oblito is a required signer" using (cmd.signers.contains(input.oblato.owningKey))
            }
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
