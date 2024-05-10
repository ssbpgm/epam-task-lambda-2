package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(authType = AuthType.NONE,
	invokeMode = InvokeMode.BUFFERED)
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {

		System.out.println("Hello from lambda");

		// D: to know how the payload is structured see https://docs.aws.amazon.com/lambda/latest/dg/urls-invocation.html#urls-request-payload

		// Get the details from the payload
		Map<String, Object> payloadRequestContext = (Map<String, Object>) request.get("requestContext");

		String domainName = (String) payloadRequestContext.get("domainName");
		System.out.println("Domain Name: " + domainName);

		Map<String, Object> payloadRequestContextHttp = (Map<String, Object>) payloadRequestContext.get("http");
		String httpMethod = (String) payloadRequestContextHttp.get("httpMethod");
		System.out.println("HTTP Method: " + httpMethod);

		// path is the part starting from '/' coming after the domain name
		String path = (String) payloadRequestContextHttp.get("path");
		System.out.println("Path: " + path);


		// Construct the URL
		String url = "https://" + domainName + path;
		System.out.println("URL: " + url);

		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (path.equals("/hello")) {
			resultMap.put("statusCode", 200);

			// since it's going to be called like and endpoint, we need to return a JSON object
			resultMap.put("body",
					"{" +
							"\"statusCode\": 200," +
							"\"message\": \"Hello from Lambda\"" +
							"}");
		}
		else {
			resultMap.put("statusCode", 400);

			// since it's going to be called like and endpoint, we need to return a JSON object
			resultMap.put("body",
					"{" +
							"\"statusCode\": 400," +
							"\"message\": \"Bad request syntax or unsupported method. Request path: /cmtr-3ff04848. HTTP method: GET\"" +
							"}");

		}

		return resultMap;
	}
}
