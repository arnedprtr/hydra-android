package be.ugent.zeus.hydra.requests.exceptions;

/**
 * This exception should be thrown when the request could not be completed due to IO failure. The most common use will
 * be network failure.
 *
 * @author Niko Strijbol
 */
public class IOFailureException extends RequestFailureException {

    public IOFailureException(Throwable cause) {
        super(cause);
    }
}
