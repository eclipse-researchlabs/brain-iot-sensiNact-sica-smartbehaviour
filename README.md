# brain-iot-sensiNact-sica-smartbehaviour

brain-iot-sensiNact-sica-smartbehaviour's modules allow to use sensiNact's southbound SICA bridge as a Brain-IoT Service, to be automatically deployed by the Brain-IoT Service Fabric when the appropiate `BrainIoTEvents` are propagated through the `EventBus`.

### brain-iot-service-sica-api

In this module, the extended `BraintIoTEvents` and the intermediate data structures allowing to translate them into the appropriate sensiNact's `AccessMethods` are defined

### brain-iot-service-sica-impl

In this module, the mechanism allowing to translate `BraintIoTEvents` into sensiNact's `AccessMethod` requests, and sensiNact's `AccessMethod` responses into `BraintIoTEvents`is implemented

### brain-iot-service-sica-app

This module provides a standalone executable jar archive allowing to launch a sensiNact IoT gateway instance integrated to a Brain-IoT `EventBus` through brain-iot-sensiNact-sica southbound bridge, brain-iot-sensiNact-smartbehaviour and brain-iot-sensiNact-sica-smartbehaviour apis and implementations for a local execution.
