package wirelessmesh.domain;

import com.google.protobuf.Empty;
import io.cloudstate.javasupport.EntityId;
import io.cloudstate.javasupport.eventsourced.*;
import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;
import wirelessmesh.*;

/**
 * This represents the domain entity that will be the digital twin of one or our wireless mesh devices. We model this as closely
 * as possible to the real world behavior of a router owned by one of our customers.
 */
@EventSourcedEntity
public class DeviceEntity {
    private final String entityId;

    /**
     * Has the customer activated this newly acquired device.
     */
    private Boolean activated;

    /**
     * Unique id associated with physical device.
     */
    private String deviceId;

    /**
     * Unique customer id associated with the device.
     */
    private String customerId;

    /**
     * The room in which this device is located.
     */
    private String room;

    public DeviceEntity(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @CommandHandler
    public Empty activateDeviceHandler(Deviceservice.ActivateDeviceCommand cmd, CommandContext ctx) {
        if (activated) {
            ctx.fail("Device already activated");
        }

        // Update internal state
        activated = true;
        deviceId = cmd.getDeviceId();
        customerId = cmd.getCustomerId();

        ctx.emit(DeviceActivated.builder()
                .routerId(cmd.getDeviceId())
                .customerId(cmd.getCustomerId()).build());

        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void routerActivatedHandler(DeviceActivated deviceActivated) {
        activated = true;
        deviceId = deviceActivated.getRouterId();
        customerId = deviceActivated.getCustomerId();
    }
}