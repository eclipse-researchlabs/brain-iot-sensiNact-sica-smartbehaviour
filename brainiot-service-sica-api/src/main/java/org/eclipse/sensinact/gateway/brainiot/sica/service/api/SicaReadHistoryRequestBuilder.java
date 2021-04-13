package org.eclipse.sensinact.gateway.brainiot.sica.service.api;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.ofInstant;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SicaReadHistoryRequestBuilder {

	private ZonedDateTime dateFrom;
	private ZonedDateTime dateTo;
//	public int serverId;
//	public int groupId;
	public String device;
	private List<String> fields;

	public SicaReadHistoryRequestBuilder() {		
		this.fields = new ArrayList<>();
	}
	
	public SicaReadHistoryRequestBuilder field(String field) {
		if(field !=null)
			this.fields.add(field);
		return this;
	}
	
	public SicaReadHistoryRequestBuilder fields(Collection<String> fields) {
		if(fields !=null && !fields.isEmpty())
			this.fields.addAll(fields);
		return this;
	}

	public SicaReadHistoryRequestBuilder fields(String[] fields) {
		if(fields !=null && fields.length > 0)
			this.fields(Arrays.asList(fields));
		return this;
	}
	
	public SicaReadHistoryRequestBuilder from(ZonedDateTime startTime) {
		dateFrom = startTime;
		return this;
	}

	public SicaReadHistoryRequestBuilder from(Date startTime) {
		dateFrom = ofInstant(startTime.toInstant(), UTC);
		return this;
	}

	public SicaReadHistoryRequestBuilder to(ZonedDateTime endTime) {
		dateTo = endTime;
		return this;
	}

	public SicaReadHistoryRequestBuilder to(Date endTime) {
		dateTo = ofInstant(endTime.toInstant(), UTC);
		return this;
	}

//	public SicaReadHistoryRequestBuilder variable(int serverId, int groupId) {
//		this.serverId = serverId;
//		this.groupId = groupId;
//		return this;
//	}

	public SicaReadHistoryRequestBuilder variable(String device) {
		this.device = device;
		return this;
	}

	public SicaReadHistoryRequest build() {
		SicaReadHistoryRequest request = new SicaReadHistoryRequest();
//		request.serverId = this.serverId;
//		request.groupId = this.groupId;
		request.device = this.device;
		request.fields = this.fields.toArray(new String[0]);
		request.startDate=Instant.from(this.dateFrom).toEpochMilli();
		request.endDate=Instant.from(this.dateTo).toEpochMilli();
		return request;
	}
}
