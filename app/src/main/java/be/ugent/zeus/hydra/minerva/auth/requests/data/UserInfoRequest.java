package be.ugent.zeus.hydra.minerva.auth.requests.data;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.auth.models.GrantInformation;
import be.ugent.zeus.hydra.minerva.auth.requests.TokenRequestInterceptor;
import be.ugent.zeus.hydra.requests.common.JsonSpringRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * This is the user information request. This is a special request that needs a token, since this request is part of
 * the account creation process and is called before the account is saved on the device.
 *
 * All other requests to Minerva should use the account functionality, which is provided in {@link MinervaRequest}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class UserInfoRequest extends JsonSpringRequest<GrantInformation> {

    private String token;

    /**
     * @param token The access token to use with the request.
     */
    public UserInfoRequest(String token) {
        super(GrantInformation.class);
        this.token = token;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MinervaConfig.GRANT_INFORMATION_ENDPOINT;
    }

    @Override
    protected RestTemplate createRestTemplate() throws RequestFailureException {
        RestTemplate t = super.createRestTemplate();
        //Add the token interceptor.
        t.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new TokenRequestInterceptor(token)));
        return t;
    }
}