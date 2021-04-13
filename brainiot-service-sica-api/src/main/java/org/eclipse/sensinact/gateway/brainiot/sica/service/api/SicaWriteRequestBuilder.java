package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.ofInstant;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

public class SicaWriteRequestBuilder {
	
	private ZonedDateTime timestamp;
	private double[] values;
//	public int serverId;
//	public int groupId;
	public String device;
	
	public SicaWriteRequestBuilder timestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public SicaWriteRequestBuilder timestamp(Date timestamp) {
		this.timestamp = ofInstant(timestamp.toInstant(), UTC);
		return this;
	}

	public SicaWriteRequestBuilder values(double[] values) {
		this.values = values;
		return this;
	}
	
//	public SicaWriteRequestBuilder sicaId(int serverId, int groupId) {
//		this.serverId = serverId;
//		this.groupId = groupId;
//		return this;
//	}
	
	public SicaWriteRequestBuilder sicaId(String device) {
		this.device = device;
		return this;
	}
	
	public SicaWriteRequest build() {
		SicaWriteRequest request = new SicaWriteRequest();
		request.timestamp = Instant.from(timestamp).toEpochMilli();
		request.value = values;
		request.device = device;
//		request.serverId = serverId;
//		request.groupId = groupId;
		return request;
	}
}
