# Android-based Mobile offloading frameworks
This GitHub project is mainly used for the paper "Developing Offloading-enabled Application Development Frameworks for Android Mobile Devices", presenting the main experimental code of the paper, including SOM framework, GOM framework, decision-making application and cloud service code.

The architecture and the execution flow of the SOM is illustrated in Figure 1. In SOM, the MainActivity method calls the proxy. In the proxy, XBinder, which extends from IBinder, is invoked. The internal structure of XBinder is illustrated in Figure 2. The offloading decision (by calling the makeDecision method shown in Figure 2) is made inXBinder. If the offloading decision is yes, the service implemented in the Cloud is invoked (by invoking the service specified by "http" in Figure 2). Otherwise, the local service is called (by calling iBinder shown in Figure 2), in which the stub in the Service is called and then the local service operation (KMeansService in this example) is called.


Fig. 1.The architecture of SOM
