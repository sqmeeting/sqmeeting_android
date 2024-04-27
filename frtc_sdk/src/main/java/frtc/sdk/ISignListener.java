package frtc.sdk;

import frtc.sdk.model.SignInResult;
import frtc.sdk.internal.model.ResultType;

public interface ISignListener {
    void onSignInResult(ResultType resultType, SignInResult signInResult);

}
