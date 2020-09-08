package br.com.fiap.mba.corda.flows

import br.com.fiap.mba.corda.contracts.NegociacaoContract
import br.com.fiap.mba.corda.states.PropostaState
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.math.BigDecimal
import java.util.*

object PropostaFlow {

    @InitiatingFlow
    @StartableByRPC
    class Initiator(
        private val instituicaoFinanceira: Party,
        private val moeda: String,
        private val quantidade: Int,
        private val cotacaoReal: BigDecimal,
        private val taxa: BigDecimal
    ) : FlowLogic<UniqueIdentifier>() {

        override val progressTracker = ProgressTracker()

        @Suspendable
        override fun call(): UniqueIdentifier {

            // Creating the output.
            val output = PropostaState(
                comprador = ourIdentity,
                proponente = ourIdentity,
                vendedor = instituicaoFinanceira,
                oblato = instituicaoFinanceira,
                moeda = moeda,
                quantidade = quantidade,
                cotacaoReal = cotacaoReal,
                taxa = taxa
            )

            // Creating the command.
            val commandType = NegociacaoContract.Commands.Proposta()
            val requiredSigners = listOf(ourIdentity.owningKey, instituicaoFinanceira.owningKey)
            val command = Command(commandType, requiredSigners)

            // Building the transaction.

            // Obtain a reference from a notary we wish to use.
            /**
             *  METHOD 1: Take first notary on network, WARNING: use for test, non-prod environments, and single-notary networks only!*
             *  METHOD 2: Explicit selection of notary by CordaX500Name - argument can by coded in flow or parsed from config (Preferred)
             *
             *  * - For production you always want to use Method 2 as it guarantees the expected notary is returned.
             */
            val notary = serviceHub.networkMapCache.notaryIdentities.single() // METHOD 1
            // val notary = serviceHub.networkMapCache.getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")) // METHOD 2

            val txBuilder = TransactionBuilder(notary)
            NegociacaoContract.ID?.let { txBuilder.addOutputState(output, it) }
            txBuilder.addCommand(command)

            // Signing the transaction ourselves.
            val partStx = serviceHub.signInitialTransaction(txBuilder)

            // Gathering the counterparty's signature.
            val counterpartySession = initiateFlow(instituicaoFinanceira)
            val fullyStx = subFlow(CollectSignaturesFlow(partStx, listOf(counterpartySession)))

            // Finalising the transaction.
            val finalisedTx = subFlow(FinalityFlow(fullyStx, listOf(counterpartySession)))
            return finalisedTx.tx.outputsOfType<PropostaState>().single().linearId
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
                    // No checking to be done.
                }
            }

            val txId = subFlow(signTransactionFlow).id

            subFlow(ReceiveFinalityFlow(counterpartySession, txId))
        }
    }
}
