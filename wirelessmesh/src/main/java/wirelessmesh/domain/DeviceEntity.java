package wirelessmesh.domain;

import com.google.protobuf.Empty;
import io.cloudstate.javasupport.EntityId;
import io.cloudstate.javasupport.eventsourced.*;
import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;
import wirelessmesh.*;
import wirelessmesh.persistence.*;

/**
 * This represents the domain entity that will be the digital twin of one or our wireless mesh devices. We model this as closely
 * as possible to the real world behavior of a router owned by one of our customers.
 */
@EventSourcedEntity
public class DeviceEntity {
    /**
     * Unique identifier for this entity, also just happens to be the same as deviceId.
     */
    private final String entityId;

    /**
     * Has the customer activated this newly acquired device.
     */
    private Boolean activated;


    private String deviceId;

    /**
     * Unique customer id associated with the device.
     */
    private String customerId;

    /**
     * The room in which this device is located.
     */
    private String room = "";

    /**
     * Constructor.
     * @param entityId The entity id will be the deviceId.
     */
    public DeviceEntity(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @CommandHandler
    public Empty activateDeviceHandler(Deviceservice.ActivateDeviceCommand cmd, CommandContext ctx) {
        if (activated) {
            ctx.fail("Device already activated");
        }

        ctx.emit(Domain.DeviceActivated.newBuilder()
                .setDeviceId(cmd.getDeviceId())
                .setCustomerId(cmd.getCustomerId()).build());

        return Empty.getDefaultInstance();
    }

    @CommandHandler
    public Empty assignRoom(Deviceservice.AssignRoomCommand cmd, CommandContext ctx) {
        ctx.emit(Domain.RoomAssigned.newBuilder()
                .setDeviceId(cmd.getDeviceId())
                .setRoom(cmd.getRoom()).build());

        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void routerActivatedHandler(Domain.DeviceActivated deviceActivated) {
        activated = true;
        deviceId = deviceActivated.getDeviceId();
        customerId = deviceActivated.getCustomerId();
    }

    @EventHandler
    public void roomAssignedHandler(Domain.RoomAssigned roomAssigned) {
        room = roomAssigned.getRoom();
    }
}