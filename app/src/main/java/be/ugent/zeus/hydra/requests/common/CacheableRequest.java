package be.ugent.zeus.hydra.requests.common;

import java.io.Serializable;

import be.ugent.zeus.hydra.loader.cache.CacheRequest;

/**
 * Cacheable request. This is also the main abstract request for Zeus & DSA API requests.
 *
 * @param <T> The type of the result of the request.
 *
 * @author feliciaan
 */
public abstract class CacheableRequest<T extends Serializable> extends AbstractRequest<T> implements CacheRequest<T> {

    protected final String DSA_API_URL = "http://student.ugent.be/hydra/api/2.0/";
    protected final String ZEUS_API_URL = "https://zeus.UGent.be/hydra/api/";

    public CacheableRequest(Class<T> clazz) {
        super(clazz);
    }
}