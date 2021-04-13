package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusResponseEvent;

public class SicaReadHistoryResponse extends SicaEvent implements EventBusResponseEvent{
//	public int serverId;
//	public int groupId;
	public String device;
	public boolean valid;
	public String history;
	public String message;
}
