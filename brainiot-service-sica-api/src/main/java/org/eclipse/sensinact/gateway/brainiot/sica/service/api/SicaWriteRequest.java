package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusRequestEvent;

public class SicaWriteRequest extends SicaEvent implements EventBusRequestEvent {
//	public int serverId;
//	public int groupId;
	public String device;
	public long timestamp;
	public double[] value;
}
