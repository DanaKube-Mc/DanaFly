package fr.danakube.danafly.command;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.command.player.CmdPay;
import fr.danakube.danafly.time.AsyncTimeParameters;
import fr.danakube.danafly.user.FlightUser;
import fr.danakube.danafly.util.V;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CmdPayPartialTest {

    private ServerMock server;
    private DanaFly plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DanaFly.class);

        V.payable = true;
        V.maxTimeBase = 10800.0; // 3h max
        V.maxTimeGroups.clear();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Partial pay should only debit accepted amount and keep refund amount for sender")
    void shouldPerformPartialPayment() {
        PlayerMock sender = server.addPlayer("Sender");
        PlayerMock receiver = server.addPlayer("Receiver");

        FlightUser senderUser = plugin.getFlightManager().getUser(sender);
        senderUser.setTime(7200.0); // 2h

        FlightUser receiverUser = plugin.getFlightManager().getUser(receiver);
        receiverUser.setTime(10200.0); // 10200s (limit is 10800s, can only accept 600s)

        // Sender tries to send 3600s (1h)
        CmdPay cmdPay = new CmdPay(plugin, new String[]{"pay", "Receiver", "1h"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmdPay, sender, receiver, 3600.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(10200.0);

        cmdPay.execute(params);

        // Receiver should have capped at 10800.0 (+600.0)
        double receiverTime = plugin.getTimeManager().getTime(receiver.getUniqueId());
        assertThat(receiverTime).isEqualTo(10800.0);

        // Sender should only be debited 600.0 -> 7200.0 - 600.0 = 6600.0
        double senderTime = plugin.getTimeManager().getTime(sender.getUniqueId());
        assertThat(senderTime).isEqualTo(6600.0);
    }

    @Test
    @DisplayName("Pay should reject completely when receiver is already at maximum capacity")
    void shouldRejectWhenReceiverIsFull() {
        PlayerMock sender = server.addPlayer("Sender2");
        PlayerMock receiver = server.addPlayer("Receiver2");

        FlightUser senderUser = plugin.getFlightManager().getUser(sender);
        senderUser.setTime(5000.0);

        FlightUser receiverUser = plugin.getFlightManager().getUser(receiver);
        receiverUser.setTime(10800.0); // Full

        CmdPay cmdPay = new CmdPay(plugin, new String[]{"pay", "Receiver2", "30m"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmdPay, sender, receiver, 1800.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(10800.0);

        cmdPay.execute(params);

        // Balances should be completely unchanged
        assertThat(plugin.getTimeManager().getTime(sender.getUniqueId())).isEqualTo(5000.0);
        assertThat(plugin.getTimeManager().getTime(receiver.getUniqueId())).isEqualTo(10800.0);
    }

    @Test
    @DisplayName("Pay should transfer full amount when receiver has sufficient capacity")
    void shouldTransferFullAmountWhenCapacityAllows() {
        PlayerMock sender = server.addPlayer("Sender3");
        PlayerMock receiver = server.addPlayer("Receiver3");

        FlightUser senderUser = plugin.getFlightManager().getUser(sender);
        senderUser.setTime(5000.0);

        FlightUser receiverUser = plugin.getFlightManager().getUser(receiver);
        receiverUser.setTime(1000.0);

        CmdPay cmdPay = new CmdPay(plugin, new String[]{"pay", "Receiver3", "1000s"});
        AsyncTimeParameters params = new AsyncTimeParameters(plugin, cmdPay, sender, receiver, 1000.0);
        params.setMaxTime(10800.0);
        params.setCurrentTime(1000.0);

        cmdPay.execute(params);

        assertThat(plugin.getTimeManager().getTime(sender.getUniqueId())).isEqualTo(4000.0);
        assertThat(plugin.getTimeManager().getTime(receiver.getUniqueId())).isEqualTo(2000.0);
    }
}
