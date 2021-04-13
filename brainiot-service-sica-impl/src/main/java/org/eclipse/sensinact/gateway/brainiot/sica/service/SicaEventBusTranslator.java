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


import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusEvent;
import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusNotificationEvent;
import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusRequestEvent;
import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusResponseEvent;
import org.eclipse.sensinact.gateway.brainiot.service.bus.EventBusTranslator;
import org.eclipse.sensinact.gateway.brainiot.service.bus.definition.EventBusNotificationDefinition;
import org.eclipse.sensinact.gateway.brainiot.service.bus.definition.EventBusRequestDefinition;
import org.eclipse.sensinact.gateway.brainiot.service.bus.definition.EventBusResponseDefinition;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryLockAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryLockAttributeResponse;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryResponse;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadResponse;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteHistoryAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteResponse;
import org.eclipse.sensinact.gateway.core.DataResource;
import org.eclipse.sensinact.gateway.util.UriUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(
	immediate=true, 
	service = EventBusTranslator.class, 
	property = {
			"event=fr.cea.brain.iot.sensinact.api.sica.SicaReadRequest",
			"event=fr.cea.brain.iot.sensinact.api.sica.SicaReadHistoryRequest",
			"event=fr.cea.brain.iot.sensinact.api.sica.SicaReadHistoryLockAttributeRequest",
			"event=fr.cea.brain.iot.sensinact.api.sica.SicaWriteRequest",
			"event=fr.cea.brain.iot.sensinact.api.sica.SicaWriteHistoryAttributeRequest"}
)
public class SicaEventBusTranslator implements EventBusTranslator {

	private static final Logger LOG = LoggerFactory.getLogger(SicaEventBusTranslator.class);
	
	public SicaEventBusTranslator() {
	}

	@Override
	public boolean handle(Class<? extends EventBusEvent> clazz) {
		if(SicaWriteRequest.class == clazz)
			return true;
		if(SicaReadRequest.class == clazz)
			return true;
		if(SicaReadHistoryRequest.class == clazz)
			return true;
		if(SicaReadHistoryLockAttributeRequest.class == clazz)
			return true;
		if(SicaWriteHistoryAttributeRequest.class == clazz)
			return true;
		return false;
	}

	@Override
	public EventBusRequestDefinition translate(EventBusRequestEvent event) {
		if(event instanceof SicaReadRequest) 
			return new SicaEventBusRequestDefinition((SicaReadRequest) event);
		if(event instanceof SicaReadHistoryRequest) 
			return new SicaEventBusRequestDefinition((SicaReadHistoryRequest) event);
		if(event instanceof SicaReadHistoryLockAttributeRequest) 
			return new SicaEventBusRequestDefinition((SicaReadHistoryLockAttributeRequest) event);
		if(event instanceof SicaWriteRequest) 
			return new SicaEventBusRequestDefinition((SicaWriteRequest) event);
		if(event instanceof SicaWriteHistoryAttributeRequest) 
			return new SicaEventBusRequestDefinition((SicaWriteHistoryAttributeRequest) event);
		return null;
	}

	@Override
	public EventBusResponseEvent translate(EventBusResponseDefinition definition) {
		String[] pathElements = UriUtils.getUriElements(definition.getPath());
//		String[] sicaProfileElements = pathElements[1].split("_");
//		
//		int serverId = Integer.parseInt(sicaProfileElements[1]);
//		int groupId = Integer.parseInt(sicaProfileElements[2]);
		String resource = pathElements[3];
		JSONObject json = null;
		try {
			json = new JSONObject(definition.getContent());
		}catch(NullPointerException | JSONException e) {
			LOG.error(definition.getContent() + " --> " + e.getMessage(), e);
		}
		
		String attribute = JSONObject.NULL.equals(json)?null:json.optString("name");

		if(resource!=null && (resource.equals(attribute) || DataResource.VALUE.equals(attribute))) {
			switch(definition.getType()) {
			case "GET":
				switch(resource) {
					case "history":
						SicaReadHistoryResponse sicaReadHistoryResponse = new SicaReadHistoryResponse();
						sicaReadHistoryResponse.valid = (definition.getStatus() == 200);
						sicaReadHistoryResponse.history = String.valueOf(json.opt(DataResource.VALUE));
						sicaReadHistoryResponse.message = "History found!";
//						sicaReadHistoryResponse.groupId = groupId;
//						sicaReadHistoryResponse.serverId = serverId;
						sicaReadHistoryResponse.device = pathElements[1];
						return sicaReadHistoryResponse;
					default:
						SicaReadResponse sicaReadResponse = new SicaReadResponse();
//						sicaReadResponse.groupId = groupId;
//						sicaReadResponse.serverId = serverId;
						sicaReadResponse.device = pathElements[1];
						sicaReadResponse.timestamp= definition.getTimestamp();
						sicaReadResponse.valid = (definition.getStatus() == 200);
						Object value = null;
						try {
							value =  !json.has("value")?new Double[0]:json.opt("value");
							if(value.getClass() == String.class) {
								try {
									value = new JSONArray(String.valueOf(value));
								}catch(JSONException e) {
									e.printStackTrace();
									//do nothing
								}
							}							
							if (!value.getClass().isArray()) {
								if(value.getClass() == JSONArray.class) {
									double[] tmp_value = new double[((JSONArray)value).length()];
									for(int i = 0; i< ((JSONArray)value).length();i++)
										tmp_value[i] = Double.valueOf(String.valueOf((Object)((JSONArray)value).opt(i)));
									value = tmp_value;
								} else
									value = new double[] {Double.valueOf(String.valueOf(value))};								
							} else if(value.getClass().getComponentType() != double.class)							
								value=new double[0];
						} catch(Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage(),e);
							value = new double[0];
						}		
						sicaReadResponse.value=(double[])value;
						sicaReadResponse.message=definition.getContent();
						return sicaReadResponse;
				}
			case "SET":
				SicaWriteResponse sicaWriteResponse = new SicaWriteResponse();
//				sicaWriteResponse.serverId = serverId;
//				sicaWriteResponse.groupId  = groupId;
				sicaWriteResponse.device = pathElements[1];
				sicaWriteResponse.valid = (definition.getStatus() == 200);
				sicaWriteResponse.message = definition.getContent();
				return sicaWriteResponse;
			}
		} else if (resource!=null &&  attribute!=null) {
			switch(definition.getType()) {
			case "GET":
				SicaReadHistoryLockAttributeResponse sicaReadHistoryLocakAttributeResponse = new SicaReadHistoryLockAttributeResponse();
				sicaReadHistoryLocakAttributeResponse.valid = (definition.getStatus() == 200);
//				sicaReadHistoryLocakAttributeResponse.groupId = groupId;
//				sicaReadHistoryLocakAttributeResponse.serverId = serverId;
				sicaReadHistoryLocakAttributeResponse.device = pathElements[1];
				sicaReadHistoryLocakAttributeResponse.lock = "lock".equals(attribute) && json.has("value") && !json.optBoolean("value")?false:true;
				return sicaReadHistoryLocakAttributeResponse;					
			case "SET":
				SicaWriteResponse sicaWriteResponse = new SicaWriteResponse();
//				sicaWriteResponse.serverId = serverId;
//				sicaWriteResponse.groupId  = groupId;
				sicaWriteResponse.device = pathElements[1];
				sicaWriteResponse.valid = (definition.getStatus() == 200);
				sicaWriteResponse.message = definition.getContent();
				return sicaWriteResponse;
			}
		}
		
		return null;
	}

	@Override
	public EventBusNotificationEvent translate(EventBusNotificationDefinition definition) {
		return null;
	}

}
