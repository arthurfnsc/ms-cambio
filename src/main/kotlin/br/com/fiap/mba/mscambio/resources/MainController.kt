package br.com.fiap.mba.mscambio.resources

import br.com.fiap.mba.mscambio.rpc.NodeRPCConnection
import net.corda.core.contracts.ContractState
import net.corda.core.messaging.vaultQueryBy
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/")
open class MainController(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val myLegalName = rpc.proxy.nodeInfo().legalIdentities.first().name
    private val proxy = rpc.proxy

    @GetMapping(value = ["/servertime"], produces = ["text/plain"])
    private fun serverTime() = LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC")).toString()

    @GetMapping(value = ["/addresses"], produces = ["text/plain"])
    private fun addresses() = proxy.nodeInfo().addresses.toString()

    @GetMapping(value = ["/identities"], produces = ["text/plain"])
    private fun identities() = proxy.nodeInfo().legalIdentities.toString()

    @GetMapping(value = ["/platformversion"], produces = ["text/plain"])
    private fun platformVersion() = proxy.nodeInfo().platformVersion.toString()

    @GetMapping(value = ["/peers"], produces = ["text/plain"])
    private fun peers() = proxy.networkMapSnapshot().flatMap { it.legalIdentities }.toString()

    @GetMapping(value = ["/notaries"], produces = ["text/plain"])
    private fun notaries() = proxy.notaryIdentities().toString()

    @GetMapping(value = ["/flows"], produces = ["text/plain"])
    private fun flows() = proxy.registeredFlows().toString()

    @GetMapping(value = ["/states"], produces = ["text/plain"])
    private fun states() = proxy.vaultQueryBy<ContractState>().states.toString()
}
