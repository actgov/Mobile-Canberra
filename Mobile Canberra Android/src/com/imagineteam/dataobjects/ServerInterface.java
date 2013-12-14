package com.imagineteam.dataobjects;

import com.loopj.android.http.*;

public class ServerInterface {
  private static final String BASE_URL = "http://mobilecanberra.imagineteamsolutions.com:8080/mobilecanberra/";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  client.setTimeout(30);
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}