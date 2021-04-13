package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusRequestEvent;

public class SicaWriteHistoryAttributeRequest implements EventBusRequestEvent {
//	public int serverId;
//	public int groupId;
	public String device;
	public String attribute;
	public String type;
	public String value;
}
