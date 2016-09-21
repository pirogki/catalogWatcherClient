package com.catalogWatcher;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class SimpleMqttClient implements MqttCallback {

  MqttClient myClient;
	MqttConnectOptions connOpt;

	static String brokerUrl = "tcp://localhost:1883";
	static final String TOPIC = "catalogWatcer";
	static final String CLIENT_ID = "publish";
	static final String REQUESTS_TOPIC = "requests";

	// the following two flags control whether this example is a publisher, a subscriber or both
	static final Boolean subscriber = true;
	static final Boolean publisher = false;

	/**
	 * 
	 * connectionLost
	 * This callback is invoked upon losing the MQTT connection.
	 * 
	 */
	
	public void connectionLost(Throwable t) {
		System.out.println("Connection lost!");
		// code to reconnect to the broker would go here if desired
	}

	/**
	 * 
	 * deliveryComplete
	 * This callback is invoked when a message published by this client
	 * is successfully received by the broker.
	 * 
	 */

	public void deliveryComplete(MqttDeliveryToken token) {
		//System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
	}




	/**
	 * 
	 * MAIN
	 * 
	 */
	public static void main(String[] args) {
		
		if(args.length == 1)
		{
			brokerUrl = args[0];
		}
		
		SimpleMqttClient smc = new SimpleMqttClient();
		smc.runClient();
	}
	
	/**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void runClient() {
		
		// setup MQTT Client
		String clientID = CLIENT_ID;
		connOpt = new MqttConnectOptions();
		
		//connOpt.setCleanSession(true);
		//connOpt.setKeepAliveInterval(30);
		
		// Connect to Broker
		try {
			myClient = new MqttClient(brokerUrl, clientID);
			myClient.setCallback(this);
			myClient.connect(connOpt);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Connected to " + brokerUrl);

		// setup topic
		// topics on m2m.io are in the form <domain>/<stuff>/<thing>

		MqttTopic topic = myClient.getTopic(REQUESTS_TOPIC);

		// subscribe to topic if subscriber

		try {
			int subQoS = 0;
			myClient.subscribe(TOPIC, subQoS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Запросим дерево файлов у сервера
		// publish messages if publisher

   		String pubMsg = "Tree";
   		int pubQoS = 0;
		MqttMessage message = new MqttMessage(pubMsg.getBytes());
    	message.setQos(pubQoS);
    	message.setRetained(false);

    	// Publish the message
    	System.out.println("Publishing to topic \"" + topic + "\" qos " + pubQoS);
    	MqttDeliveryToken token = null;
    	try {
    		// publish message to broker
			token = topic.publish(message);
	    	// Wait until the message has been delivered to the broker
			token.waitForCompletion();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		// disconnect
		/*try {
			// wait to ensure subscribed messages are delivered
			if (subscriber) {
				Thread.sleep(5000);
			}
			myClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * messageArrived
	 * This callback is invoked when a message is received on a subscribed topic.
	 * 
	 */
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		System.out.println(new String(message.getPayload()));
		
	}
}