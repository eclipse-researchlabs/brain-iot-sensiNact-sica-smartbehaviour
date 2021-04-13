package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

public class SicaReadRequestBuilder {

	private String field;
//	public int serverId;
//	public int groupId;
	public String device;


	public SicaReadRequestBuilder field(String field) {
		this.field = field;
		return this;
	}

//	public SicaReadRequestBuilder variable(int serverId, int groupId) {
//		this.serverId = serverId;
//		this.groupId = groupId;
//		return this;
//	}

	public SicaReadRequestBuilder variable(String device) {
		this.device = device;
		return this;
	}
	
	public SicaReadRequest build() {
		SicaReadRequest request = new SicaReadRequest();
//		request.serverId = this.serverId;
//		request.groupId = this.groupId;
		request.device = this.device;
		request.field = this.field;
		return request;
	}
}
