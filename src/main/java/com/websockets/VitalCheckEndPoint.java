package com.websockets;

import javax.websocket.server.ServerEndpoint;

import jdk.jshell.spi.ExecutionControl.ExecutionControlException;

import java.util.Collections;
import java.io.StringWriter;
import java.util.*;
import javax.websocket.*;
import javax.json.*;

@ServerEndpoint(value="/VitalCheckEndPoint",configurator=VitalCheckConfigurator.class)
public class VitalCheckEndPoint {
	static Set<Session> subscribers=Collections.synchronizedSet(new HashSet<Session>());
	@OnOpen
	public void handleOpen(EndpointConfig endpointconfig,Session userSession) {
		userSession.getUserProperties().put("username", endpointconfig.getUserProperties().get("username"));
		subscribers.add(userSession);
	}
	
	@OnMessage
	public void handleMessage(String Message,Session userSession) {
		String username=(String)userSession.getUserProperties().get("username");
		
		if(username!=null && !username.startsWith("doctor")) {
			subscribers.stream().forEach(x->{
				
				try {
					String[] mess=Message.split(",");
					if(((String)(x.getUserProperties().get("username"))).startsWith("doctor")) {
						
						if((Integer.parseInt(mess[0])< 90)&&(Integer.parseInt(mess[0])>=70))
					     {
					    	 x.getBasicRemote().sendText(buildJSON(username,Message));
					     }
					}
					if(Integer.parseInt(mess[0])< 70) {
						
						if(((String)(x.getUserProperties().get("username"))).startsWith("ambulance"))
					     {
					    	 x.getBasicRemote().sendText(buildJSON(username,"Requires an ambulance,"+mess[1]));
					     }
						if(!((String)(x.getUserProperties().get("username"))).startsWith("ambulance") && !((String)(x.getUserProperties().get("username"))).startsWith("doctor") )
					     {
							x.getBasicRemote().sendText(buildJSON("....","paracetomal"+","+"630mg"));
					     }
					}
				}
				catch(Exception e) {
					e.getStackTrace();
				}
				
				
			});
		}
		else if(username!=null && username.startsWith("doctor")) {
			String[] messages=Message.split(",");
			String patient=messages[0];
			String subject=messages[1];
			
			subscribers.stream().forEach(x->{
				try {
					if(subject.startsWith("ambulance")) {
						if(x.getUserProperties().get("username").equals(patient)) {
							x.getBasicRemote().sendText(buildJSON(username,"has summoned an ambulance"));
						
						}
						else if(((String)(x.getUserProperties().get("username"))).startsWith("ambulance")) {
							x.getBasicRemote().sendText(buildJSON(patient,"Requires an ambulance,"+messages[2]));
						
						}
					}
					else if(subject.equals("medication")) {
						if(x.getUserProperties().get("username").equals(patient)) {
							x.getBasicRemote().sendText(buildJSON(username,messages[2]+","+messages[3]));
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			});
		}
		
		}
	@OnClose
	public void handleClose(Session userSession) {
		subscribers.remove(userSession);
		
	}
	@OnError
	public void handleError(Throwable t) {
		
	}
	private String buildJSON(String username,String message) {
		
		JsonObject jsonObject=Json.createObjectBuilder().add("message",username+","+message).build();
		StringWriter stringWriter=new StringWriter();
		try(JsonWriter jsonWriter=Json.createWriter(stringWriter))
		{
			jsonWriter.write(jsonObject);
		}
		
		return stringWriter.toString();
	}
	
	
	
}




