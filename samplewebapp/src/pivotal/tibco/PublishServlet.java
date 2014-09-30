package pivotal.tibco;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.RabbitServiceInfo;
import org.springframework.cloud.service.common.RelationalServiceInfo;

/**
 * Servlet implementation class PublishServlet
 */
@WebServlet({ "/PublishServlet", "/" })
public class PublishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PublishServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    Connection      connection   = null;
		    Session         session      = null;
		    MessageProducer msgProducer  = null;
		    Destination     destination  = null;

		    String serverUrl, userName, password, name;

		      // Using jackson to parse VCAP_SERVICES
	        Map result = new ObjectMapper().readValue(System.getenv("VCAP_SERVICES"), HashMap.class);

	        userName = (String) ((Map) ((Map) ((List) result.get("Tibco")).get(0)).get("credentials")).get("username");
	        password = (String) ((Map) ((Map) ((List) result.get("Tibco")).get(0)).get("credentials")).get("password");
	        serverUrl = (String) ((Map) ((Map) ((List) result.get("Tibco")).get(0)).get("credentials")).get("server");
	        name = (String) ((Map) ((Map) ((List) result.get("Tibco")).get(0)).get("credentials")).get("queue");
	        
		    response.getWriter().print("got "+userName);
		    
		    try {
		            TextMessage msg;
		            int         i;
		            response.getWriter().print("Publishing to destination '"+name+"'\n");

		            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);

		            connection = factory.createConnection(userName,password);

		            /* create the session */
		            session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);

		                destination = session.createQueue(name);

		            /* create the producer */
		            msgProducer = session.createProducer(null);

		                msg = session.createTextMessage();

		                /* set message text */
		                msg.setText("Message from PCF " + System.currentTimeMillis());

		                    msgProducer.send(destination, msg);

		                response.getWriter().print("Published message: "+msg.getText());

		                /* close the connection */
		            connection.close();
		        } 
		        catch (JMSException e) 
		        {
		            e.printStackTrace();
		            throw new RuntimeException(e);
		            //response.getWriter().print(e);
		            
		        }
		
		response.getWriter().print("DONE SERGIO");
	}

}
