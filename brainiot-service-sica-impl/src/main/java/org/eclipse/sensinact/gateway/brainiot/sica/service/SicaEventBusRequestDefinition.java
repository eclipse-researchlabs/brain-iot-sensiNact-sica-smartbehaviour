/*
 * Copyright (c) 2017-2020 CEA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    CEA - initial API and implementation
 */
package org.eclipse.sensinact.gateway.brainiot.sica.service;

import java.util.Arrays;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusNotificationEvent;
import org.eclipse.sensinact.gateway.brainiot.service.bus.definition.EventBusRequestDefinition;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryLockAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteHistoryAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteRequest;
import org.eclipse.sensinact.gateway.core.DataResource;
import org.eclipse.sensinact.gateway.core.security.Authentication;
import org.eclipse.sensinact.gateway.util.UriUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class SicaEventBusRequestDefinition implements EventBusRequestDefinition {

	private String method;
	private String path;
	private String content;

	/**
	 * Constructor
	 * 
	 * @param event the {@link SnaGet} event to build the description of
	 */
	public SicaEventBusRequestDefinition(SicaReadRequest event) {
		this.method = "GET";
//		this.path = UriUtils.getUri(new String[] {"sensinact",new StringBuilder().append("SICA_").append(event.serverId
//			).append("_").append(event.groupId <10?"0":"").append(event.groupId).toString()	,"values", 
//				event.field==null?"all":event.field, "GET"});
		this.path = UriUtils.getUri(new String[] {"sensinact", event.device ,"values", event.field==null?"all":event.field, "GET"});
		JSONArray arr = new JSONArray();
		arr.put(new JSONObject().put("name","attributeName").put("type","string").put("value",DataResource.VALUE));
		this.content = arr.toString();
	}
	
	/**
	 * Constructor
	 * 
	 * @param event the {@link SnaSet} event to build the description of
	 */
	public SicaEventBusRequestDefinition(SicaReadHistoryRequest event) {
		this.method = "GET";		
//		this.path = UriUtils.getUri(new String[] {"sensinact",new StringBuilder().append("SICA_").append(event.serverId
//				).append("_").append(event.groupId<10?"0":"").append(event.groupId).toString(),"values", "history", "GET"});
		this.path = UriUtils.getUri(new String[] {"sensinact", event.device,"values", "history", "GET"});
		JSONArray arr = new JSONArray();
		arr.put(new JSONObject().put("name","attributeName").put("type","string").put("value", DataResource.VALUE));
		this.content = arr.toString();
	}	
	
	/**
	 * Constructor
	 * 
	 * @param event the {@link SnaSet} event to build the description of
	 */
	public SicaEventBusRequestDefinition(SicaReadHistoryLockAttributeRequest event) {
		this.method = "GET";		
//		this.path = UriUtils.getUri(new String[] {"sensinact",new StringBuilder().append("SICA_").append(event.serverId
//				).append("_").append(event.groupId<10?"0":"").append(event.groupId).toString(),"values", "history", "GET"});
		this.path = UriUtils.getUri(new String[] {"sensinact", event.device, "values", "history", "GET"});
		JSONArray arr = new JSONArray();
		arr.put(new JSONObject().put("name","attributeName").put("type","string").put("value", "lock"));
		this.content = arr.toString();
	}	
	
	/**
	 * Constructor
	 * 
	 * @param event the {@link SnaSet} event to build the description of
	 */
	public SicaEventBusRequestDefinition(SicaWriteRequest event) {
		this.method = "POST";		
//		this.path = UriUtils.getUri(new String[] {"sensinact",new StringBuilder().append("SICA_").append(event.serverId
//				).append("_").append(event.groupId<10?"0":"").append(event.groupId).toString(),"values", "all","SET"});
		this.path = UriUtils.getUri(new String[] {"sensinact", event.device, "values", "all","SET"});
		JSONArray arr = new JSONArray();
		arr.put(new JSONObject().put("name","attributeName").put("type","string").put("value", DataResource.VALUE));
		arr.put(new JSONObject().put("name","value").put("type", "Array of double").put("value", Arrays.toString(event.value)));
		this.content = arr.toString();
	}	
	
	/**
	 * Constructor
	 * 
	 * @param event the {@link SnaSet} event to build the description of
	 */
	public SicaEventBusRequestDefinition(SicaWriteHistoryAttributeRequest event) {
		this.method = "POST";		
//		this.path = UriUtils.getUri(new String[] {"sensinact",new StringBuilder().append("SICA_").append(event.serverId
//				).append("_").append(event.groupId<10?"0":"").append(event.groupId).toString(),"values", "history","SET"});
		this.path = UriUtils.getUri(new String[] {"sensinact", event.device, "values", "history","SET"});
		JSONArray arr = new JSONArray();
		arr.put(new JSONObject().put("name","attributeName").put("type","string").put("value", event.attribute));
		arr.put(new JSONObject().put("name","value").put("type",event.type).put("value", event.value));
		this.content = arr.toString();
	}	
	
	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public Authentication<?> getAuthentication() {
		return null;
	}

	@Override
	public Class<? extends EventBusNotificationEvent> getEventBusNotificationEventType() {
		return null;
	}

}
