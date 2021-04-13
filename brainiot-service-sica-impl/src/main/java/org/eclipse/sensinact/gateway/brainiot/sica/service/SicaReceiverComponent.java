package org.eclipse.sensinact.gateway.brainiot.sica.service;

import java.util.Arrays;

import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusRequestEvent;
import org.eclipse.sensinact.gateway.brainiot.service.api.EventBusResponseEvent;
import org.eclipse.sensinact.gateway.brainiot.service.bus.EventBusRequestHandlerComponent;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaEvent;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryLockAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryLockAttributeResponse;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadHistoryResponse;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaReadRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteHistoryAttributeRequest;
import org.eclipse.sensinact.gateway.brainiot.sica.service.api.SicaWriteRequest;
import org.eclipse.sensinact.gateway.common.bundle.Mediator;
import org.eclipse.sensinact.gateway.common.execution.Executable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.brain.iot.eventing.annotation.SmartBehaviourDefinition;
import eu.brain.iot.eventing.api.BrainIoTEvent;
import eu.brain.iot.eventing.api.EventBus;
import eu.brain.iot.eventing.api.SmartBehaviour;

@Component
@SmartBehaviourDefinition(consumed = { SicaReadHistoryRequest.class, SicaReadRequest.class, SicaWriteRequest.class }, filter="(timestamp=*)", name="Sica Requests Listener")
public class SicaReceiverComponent implements SmartBehaviour<SicaEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SicaReceiverComponent.class);

	@Reference
	EventBusRequestHandlerComponent eventBusRequestHandlerComponent;

	private Mediator mediator;


	@Activate
	public void activate(ComponentContext componentContext) {
		LOG.info("SicaReceiverComponent ACTIVATED");
		this.mediator = new Mediator(componentContext.getBundleContext());
	}
	
	@Override
	public void notify(SicaEvent event) {
		LOG.info("[EventBus] {} RECEIVED : {}", event.getClass().getSimpleName(), event);			
		EventBusResponseEvent response = null;
		
		if (event instanceof SicaReadHistoryRequest) {
			response = preProcessSicaReadHistoryRequest((SicaReadHistoryRequest) event);
		}				
		if (response == null) {
			response = eventBusRequestHandlerComponent.handle((EventBusRequestEvent) event);
		}
		if (response == null) {
			LOG.error("Response is null after receiving {}", event);
		} else if (response instanceof BrainIoTEvent) {
			LOG.info("[EventBus] {} SENT : {}", response.getClass().getSimpleName(), response);
			final BrainIoTEvent resp = (BrainIoTEvent)response;
			this.mediator.callService(EventBus.class, new Executable<EventBus,Void>(){
				@Override
				public Void execute(EventBus eventBus) throws Exception {
					eventBus.deliver( resp);
					return null;
				}
			});			
		} else {
			LOG.error("Computed response is {} after receiving {}", response, event);
		}
	}

	private EventBusResponseEvent preProcessSicaReadHistoryRequest(SicaReadHistoryRequest event) {
		EventBusResponseEvent response = null;
		int wait = 5000;

		while (true) {
			SicaReadHistoryLockAttributeRequest req = new SicaReadHistoryLockAttributeRequest();
//			req.serverId = event.serverId;
//			req.groupId = event.groupId;
			req.device = event.device;
			SicaReadHistoryLockAttributeResponse resp = (SicaReadHistoryLockAttributeResponse) 
					eventBusRequestHandlerComponent.handle(req);
			if (resp.valid && !resp.lock)
				break;
			wait -= 150;
			if (wait < 0) {
				break;
			}
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				wait = -1;
				Thread.interrupted();
			}
		}

		if (wait < 0) {
			SicaReadHistoryResponse res = new SicaReadHistoryResponse();
//			res.serverId = event.serverId;
//			res.groupId = event.groupId;
			res.device = event.device;
			res.valid = false;
			res.message = "Timeout : unable to lock the history resource";
			response = res;
		} else {
			SicaWriteHistoryAttributeRequest req = new SicaWriteHistoryAttributeRequest();
			req.attribute = "lock";
//			req.groupId = event.groupId;
//			req.serverId = event.serverId;
			req.device = event.device;
			req.type = "boolean";
			req.value = "true";
			eventBusRequestHandlerComponent.handle(req);

			req = new SicaWriteHistoryAttributeRequest();
			req.attribute = "from";
//			req.groupId = event.groupId;
//			req.serverId = event.serverId;
			req.device = event.device;
			req.type = "long";
			req.value = String.valueOf(event.startDate);
			eventBusRequestHandlerComponent.handle(req);

			req = new SicaWriteHistoryAttributeRequest();
			req.attribute = "to";
//			req.groupId = event.groupId;
//			req.serverId = event.serverId;
			req.device = event.device;
			req.type = "long";
			req.value = String.valueOf(event.endDate);
			eventBusRequestHandlerComponent.handle(req);

			req = new SicaWriteHistoryAttributeRequest();
			req.attribute = "fields";
//			req.groupId = event.groupId;
//			req.serverId = event.serverId;
			req.device = event.device;
			req.type = "Array of java.lang.String";
			req.value = event.fields == null ? "null" : Arrays.toString(event.fields);
			eventBusRequestHandlerComponent.handle(req);

			response = eventBusRequestHandlerComponent.handle(event);

			req = new SicaWriteHistoryAttributeRequest();
			req.attribute = "lock";
//			req.groupId = event.groupId;
//			req.serverId = event.serverId;
			req.device = event.device;
			req.type = "boolean";
			req.value = "false";
			eventBusRequestHandlerComponent.handle(req);
		}

		return response;
	}
}
