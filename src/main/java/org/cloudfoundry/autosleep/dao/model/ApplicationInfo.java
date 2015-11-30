package org.cloudfoundry.autosleep.dao.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudfoundry.autosleep.remote.ApplicationActivity;
import org.cloudfoundry.autosleep.util.serializer.InstantDeserializer;
import org.cloudfoundry.autosleep.util.serializer.InstantSerializer;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;

import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Getter
@Slf4j
@JsonAutoDetect()
public class ApplicationInfo {

    @JsonSerialize
    private UUID uuid;

    @JsonSerialize
    private String name;

    @JsonSerialize
    private CloudApplication.AppState appState;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant lastEvent;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant lastLog;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant nextCheck;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant lastCheck;

    @JsonSerialize @Setter
    private ApplicationStateMachine stateMachine = new ApplicationStateMachine();

    @JsonSerialize
    private final HashMap<String /**serviceId.**/,ServiceInstanceState> serviceInstances = new HashMap<>();


    public enum ServiceInstanceState {
        /** service instance is bound to the application. */
        BOUND ,
        /** service (with NO_OPTOUT_PARAMETER set to false) was manually unbound,
         * it won't be automatically bound again.*/
        BLACKLISTED
    }

    /**
     * Should never be called. Only for JSON auto serialization.
     */
    @SuppressWarnings("unused")
    private ApplicationInfo() {
    }

    public ApplicationInfo(UUID uuid) {
        this.uuid = uuid;
    }

    public ApplicationInfo withRemoteInfo(ApplicationActivity activity) {
        updateRemoteInfo(activity);
        return this;
    }

    public void addBoundService(String serviceId) {
        serviceInstances.put(serviceId, ServiceInstanceState.BOUND);
    }

    public void removeBoundService(String serviceId, boolean addToBlackList) {
        if (addToBlackList) {
            serviceInstances.put(serviceId, ServiceInstanceState.BLACKLISTED);
        } else {
            serviceInstances.remove(serviceId);
        }
    }

    @JsonIgnore
    public boolean isWatched() {
        return serviceInstances.values().stream().filter(
                serviceInstanceState -> serviceInstanceState == ServiceInstanceState.BOUND
        ).findAny().isPresent();
    }

    public void updateRemoteInfo(ApplicationActivity activity) {
        this.appState = activity.getState();
        this.lastEvent = activity.getLastEvent();
        this.lastLog = activity.getLastLog();
        this.name = activity.getApplication().getName();
    }

    public void markAsChecked(Instant next) {
        this.lastCheck = Instant.now();
        this.nextCheck = next;
    }

    public void clearCheckInformation() {
        this.lastCheck = Instant.now();
        this.nextCheck = null;
        this.appState = null;
    }

    public void markAsPutToSleep() {
        this.appState = AppState.STOPPED;
        this.lastEvent = Instant.now();
    }


    @Override
    public String toString() {
        return "[ApplicationInfo:" + getName() + "/" + getUuid() + " lastEvent:"
                + getLastEvent() + " lastLog:" + getLastLog() + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ApplicationInfo)) {
            return false;
        }
        ApplicationInfo other = (ApplicationInfo) object;

        return Objects.equals(this.getUuid(), other.getUuid())
                && Objects.equals(this.getName(), other.getName())
                && Objects.equals(this.getLastLog(), other.getLastLog())
                && Objects.equals(this.getLastEvent(), other.getLastEvent())
                && Objects.equals(this.getLastCheck(), other.getLastCheck())
                && Objects.equals(this.getNextCheck(), other.getNextCheck())
                && Objects.equals(this.getStateMachine(), other.getStateMachine())
                && Objects.equals(this.getAppState(), other.getAppState());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }


}
