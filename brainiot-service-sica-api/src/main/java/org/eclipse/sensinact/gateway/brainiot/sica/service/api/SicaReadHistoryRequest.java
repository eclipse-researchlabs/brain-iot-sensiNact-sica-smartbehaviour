package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusRequestEvent;

public class SicaReadHistoryRequest extends SicaEvent implements EventBusRequestEvent{
//	public int serverId;
//	public int groupId;
	public String device;
	public String[] fields;
	public long startDate;
	public long endDate;	
}
