package se.fk.rimfrost.framework.erbjudande.topic;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.SkipInject;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.fk.rimfrost.framework.erbjudande.topic.adapter.ErbjudandeTopicAdapter;
import se.fk.rimfrost.framework.erbjudande.topic.exception.ErbjudandeTopicException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusComponentTest
public class ErbjudandeTopicAdapterTest
{
   private static WireMockServer server;

   @Inject
   ErbjudandeTopicAdapter erbjudandeTopicAdapter;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(
            options()
                  .dynamicPort()
                  .usingFilesUnderDirectory("src/test/resources"));
      server.start();

      System.setProperty("erbjudande.topic.api.base-url", server.baseUrl());
   }

   @AfterAll
   public static void teardown()
   {
      if (server != null)
      {
         server.stop();
      }
   }

   @BeforeEach
   void resetStubs()
   {
      server.resetToDefaultMappings();
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_throw_with_error_type_bad_request_on_status_400(@SkipInject String erbjudandeId)
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/topic/" + erbjudandeId))
            .willReturn(WireMock.aResponse().withStatus(400)));
      var exception = assertThrows(ErbjudandeTopicException.class, () -> erbjudandeTopicAdapter.getTopic(erbjudandeId));
      assertEquals(ErbjudandeTopicException.ErrorType.BAD_REQUEST, exception.getErrorType());
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_throw_with_error_type_not_found_on_status_404(@SkipInject String erbjudandeId)
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/topic/" + erbjudandeId))
            .willReturn(WireMock.aResponse().withStatus(404)));
      var exception = assertThrows(ErbjudandeTopicException.class, () -> erbjudandeTopicAdapter.getTopic(erbjudandeId));
      assertEquals(ErbjudandeTopicException.ErrorType.NOT_FOUND, exception.getErrorType());
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_throw_with_error_type_service_unavailable_on_status_503(@SkipInject String erbjudandeId)
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/topic/" + erbjudandeId))
            .willReturn(WireMock.aResponse().withStatus(503)));
      var exception = assertThrows(ErbjudandeTopicException.class, () -> erbjudandeTopicAdapter.getTopic(erbjudandeId));
      assertEquals(ErbjudandeTopicException.ErrorType.SERVICE_UNAVAILABLE, exception.getErrorType());
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_throw_with_error_type_unexpected_error_on_status_500(@SkipInject String erbjudandeId)
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/topic/" + erbjudandeId))
            .willReturn(WireMock.aResponse().withStatus(500)));
      var exception = assertThrows(ErbjudandeTopicException.class, () -> erbjudandeTopicAdapter.getTopic(erbjudandeId));
      assertEquals(ErbjudandeTopicException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_throw_with_error_type_unexpected_error_on_null_respons(@SkipInject String erbjudandeId)
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/topic/" + erbjudandeId))
            .willReturn(WireMock.aResponse().withStatus(200).withBody((String) null)));
      var exception = assertThrows(ErbjudandeTopicException.class, () -> erbjudandeTopicAdapter.getTopic(erbjudandeId));
      assertEquals(ErbjudandeTopicException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @ParameterizedTest
   @CsvSource(
   {
         "1222370d-128e-4ca6-bc65-d8c729278881"
   })
   public void should_return_expected_topic_on_success(@SkipInject String erbjudandeId) throws ErbjudandeTopicException
   {
      var topic = erbjudandeTopicAdapter.getTopic(erbjudandeId);
      assertEquals("test", topic);
   }
}
