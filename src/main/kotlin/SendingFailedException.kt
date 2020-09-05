import java.lang.RuntimeException

class SendingFailedException(cause: Exception) : RuntimeException(cause)