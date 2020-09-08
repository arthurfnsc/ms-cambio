package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.contracts.NegociacaoContract
import br.com.fiap.mba.corda.states.PropostaState
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
import java.time.LocalDateTime

object RecusaPropostaFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(
        private val proposalId: UniqueIdentifier
    ) : FlowLogic<Unit>() {

        override val progressTracker = ProgressTracker()

        @Suspendable
        override fun call() {

            // Retrieving the input from the vault.
            val inputCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(proposalId))
            val inputStateAndRef = serviceHub.vaultService.queryBy<PropostaState>(inputCriteria).states.single()
            val input = inputStateAndRef.state.data

            // Creating the output.
            val counterparty = if (ourIdentity == input.proponente) input.oblato else input.proponente

            // Creating the output.
            var output = input.copy(
                proponente = ourIdentity,
                oblato = counterparty,
                atualizadoEm = LocalDateTime.now(),
                statusTransacao = "REJEITADO"
            )

            // Creating the command.
            val requiredSigners = listOf(input.proponente.owningKey, input.oblato.owningKey)
            val command = Command(NegociacaoContract.Commands.Recusar(), requiredSigners)

            // Obtain a reference from a notary we wish to use.
            val notary = inputStateAndRef.state.notary

            // Building the transaction.
            val txBuilder = TransactionBuilder(notary)
            txBuilder.addInputState(inputStateAndRef)
            NegociacaoContract.ID?.let { txBuilder.addOutputState(output, it) }
            txBuilder.addCommand(command)

            // Signing the transaction ourselves.
            val partStx = serviceHub.signInitialTransaction(txBuilder)

            // Gathering the counterparty's signature.
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
                    val proposee = ledgerTx.inputsOfType<PropostaState>().single().oblato
                    if (proposee != counterpartySession.counterparty) {
                        throw FlowException("Só um oblato pode recusar a proposta.")
                    }
                }
            }

            val txId = subFlow(signTransactionFlow).id

            subFlow(ReceiveFinalityFlow(counterpartySession, txId))
        }
    }
}
