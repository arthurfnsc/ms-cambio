package br.com.fiap.mba.corda.flows

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.math.BigDecimal

abstract class FlowTestsBase {
    lateinit var network: MockNetwork
    lateinit var a: StartedMockNode
    lateinit var b: StartedMockNode

    @BeforeAll
    fun setup() {

        this.network = MockNetwork(
            MockNetworkParameters(
                cordappsForAllNodes = listOf(
                    TestCordapp.findCordapp("br.com.fiap.mba.corda.flows"),
                    TestCordapp.findCordapp("br.com.fiap.mba.corda.contracts")
                )
            )
        )

        this.a = network.createPartyNode()
        this.b = network.createPartyNode()

        val responseFlows = listOf(
            AceitePropostaFlow.Responder::class.java,
            ContraPropostaFlow.Responder::class.java,
            PropostaFlow.Responder::class.java,
            RecusaPropostaFlow.Responder::class.java
        )

        listOf(this.a, this.b).forEach {
            for (flow in responseFlows) {
                it.registerInitiatedFlow(flow)
            }
        }

        this.network.runNetwork()
    }

    @AfterAll
    fun tearDown() {

        this.network.stopNodes()
    }

    fun nodeACriarProposta(
        instituicaoFinanceira: Party,
        moeda: String,
        quantidade: Int,
        cotacaoReal: BigDecimal,
        taxa: BigDecimal
    ): UniqueIdentifier {

        val flow = PropostaFlow.Initiator(
            instituicaoFinanceira = instituicaoFinanceira,
            moeda = moeda,
            quantidade = quantidade,
            cotacaoReal = cotacaoReal,
            taxa = taxa
        )

        val future = this.a.startFlow(flow)

        this.network.runNetwork()

        return future.get()
    }

    fun nodeBAceitaProposta(
        proposalId: UniqueIdentifier
    ) {

        val flow = AceitePropostaFlow.Initiator(proposalId)
        val future = this.b.startFlow(flow)

        this.network.runNetwork()

        future.get()
    }

    fun nodeAContraProposta(
        propostaId: UniqueIdentifier,
        novaTaxa: BigDecimal
    ) {

        val flow = ContraPropostaFlow.Initiator(propostaId, novaTaxa)
        val future = this.a.startFlow(flow)

        this.network.runNetwork()

        future.get()
    }

    fun nodeBContraProposta(
        propostaId: UniqueIdentifier,
        novaTaxa: BigDecimal
    ) {

        val flow = ContraPropostaFlow.Initiator(propostaId, novaTaxa)
        val future = this.b.startFlow(flow)

        this.network.runNetwork()

        future.get()
    }

    fun nodeBRecusaProposta(
        propostaId: UniqueIdentifier
    ) {

        val flow = AceitePropostaFlow.Initiator(propostaId)
        val future = this.b.startFlow(flow)

        this.network.runNetwork()

        future.get()
    }
}
