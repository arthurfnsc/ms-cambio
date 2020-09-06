package br.com.fiap.mba.mscambio.corda.flows

import br.com.fiap.mba.mscambio.corda.contracts.NegociacaoContract
import br.com.fiap.mba.mscambio.corda.states.NegociacaoState
import br.com.fiap.mba.mscambio.corda.states.PropostaState
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

object AceitePropostaFlow {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(
        private val propostaId: UniqueIdentifier
    ) : FlowLogic<Unit>() {
        override val progressTracker = ProgressTracker()

        @Suspendable
        override fun call() {
            // Retrieving the input from the vault.
            val inputCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(propostaId))
            val inputStateAndRef = serviceHub.vaultService.queryBy<PropostaState>(inputCriteria).states.single()
            val input = inputStateAndRef.state.data

            // Creating the output.
            val output = NegociacaoState(
                linearId = input.linearId,
                comprador = input.comprador,
                vendedor = input.vendedor,
                taxa = input.taxa
            )

            // Creating the command.
            val requiredSigners = listOf(input.proponente.owningKey, input.oblato.owningKey)
            val command = Command(NegociacaoContract.Commands.Aceitar(), requiredSigners)

            // Building the transaction.
            val notary = inputStateAndRef.state.notary
            val txBuilder = TransactionBuilder(notary)
            txBuilder.addInputState(inputStateAndRef)
            txBuilder.addOutputState(output, NegociacaoContract.ID)
            txBuilder.addCommand(command)

            // Signing the transaction ourselves.
            val partStx = serviceHub.signInitialTransaction(txBuilder)

            // Gathering the counterparty's signature.
            val counterparty = if (ourIdentity == input.proponente) input.oblato else input.proponente
            val counterpartySession = initiateFlow(counterparty)
            val fullyStx = subFlow(CollectSignaturesFlow(partStx, listOf(counterpartySession)))

            // Finalising the transaction.
            subFlow(FinalityFlow(fullyStx, listOf(counterpartySession)))
        }
    }

    @InitiatedBy(Initiator::class)
    class Responder(
        val counterpartySession: FlowSession
    ) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            val signTransactionFlow = object : SignTransactionFlow(counterpartySession) {
                override fun checkTransaction(stx: SignedTransaction) {
                    val ledgerTx = stx.toLedgerTransaction(serviceHub, false)
                    val oblato = ledgerTx.inputsOfType<PropostaState>().single().oblato
                    if (oblato != counterpartySession.counterparty) {
                        throw FlowException("Só um oblato pode aceitar a proposta.")
                    }
                }
            }

            val txId = subFlow(signTransactionFlow).id

            subFlow(ReceiveFinalityFlow(counterpartySession, txId))
        }
    }
}
