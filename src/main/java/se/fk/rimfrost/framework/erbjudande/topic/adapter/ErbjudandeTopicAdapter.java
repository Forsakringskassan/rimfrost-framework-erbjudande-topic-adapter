package se.fk.rimfrost.framework.erbjudande.topic.adapter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.erbjudande.kafka.topic.jaxrsspec.controllers.generatedsource.ErbjudandeTopicApi;
import se.fk.rimfrost.framework.erbjudande.topic.exception.ErbjudandeTopicException;

import java.util.Objects;

@SuppressWarnings("unused")
@ApplicationScoped
public class ErbjudandeTopicAdapter
{
   @ConfigProperty(name = "erbjudande.topic.api.base-url")
   String erbjudandeTopicBaseUrl;

   private ErbjudandeTopicApi erbjudandeTopicClient;

   private Client client;

   Logger LOGGER = LoggerFactory.getLogger(ErbjudandeTopicAdapter.class);

   @PostConstruct
   void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new ApacheConnectorProvider());
      client = ClientBuilder.newClient(clientConfig);
      erbjudandeTopicClient = WebResourceFactory.newResource(
            ErbjudandeTopicApi.class,
            client.target(this.erbjudandeTopicBaseUrl));
   }

   @PreDestroy
   void destroy()
   {
      erbjudandeTopicClient = null;

      if (client != null)
      {
         client.close();
         client = null;
      }
   }

   public String getTopic(String erbjudandeId) throws ErbjudandeTopicException
   {
      try
      {
         var topicResponse = erbjudandeTopicClient.getTopic(Objects.requireNonNull(erbjudandeId));

         if (topicResponse == null)
         {
            var message = "Received an unexpected null response while fetching topic for erbjudande id: " + erbjudandeId;
            LOGGER.error(message);
            throw new ErbjudandeTopicException(ErbjudandeTopicException.ErrorType.UNEXPECTED_ERROR, message);
         }

         return topicResponse.getTopic();
      }
      catch (NotFoundException ex)
      {
         var message = "Topic not found for erbjudande id: " + erbjudandeId;
         LOGGER.error(message, ex);
         throw new ErbjudandeTopicException(ErbjudandeTopicException.ErrorType.NOT_FOUND, message);
      }
      catch (BadRequestException ex)
      {
         var message = "Request was rejected as a bad request when attempting to fetch topic for erbjudande id: " + erbjudandeId;
         LOGGER.error(message, ex);
         throw new ErbjudandeTopicException(ErbjudandeTopicException.ErrorType.BAD_REQUEST, message);
      }
      catch (ServiceUnavailableException ex)
      {
         var message = "Request could not be handled by server";
         LOGGER.error(message, ex);
         throw new ErbjudandeTopicException(ErbjudandeTopicException.ErrorType.SERVICE_UNAVAILABLE, message);
      }
      catch (ProcessingException | WebApplicationException ex)
      {
         var message = "An unexpected error occurred while fetching topic for erbjudande id: " + erbjudandeId;
         LOGGER.error(message, ex);
         throw new ErbjudandeTopicException(ErbjudandeTopicException.ErrorType.UNEXPECTED_ERROR, message, ex);
      }
   }
}
