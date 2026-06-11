package se.fk.rimfrost.framework.erbjudande.topic.exception;

public class ErbjudandeTopicException extends Exception
{
   private final ErrorType errorType;

   public ErbjudandeTopicException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   public ErbjudandeTopicException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   public ErrorType getErrorType()
   {
      return errorType;
   }

   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR
   }
}
