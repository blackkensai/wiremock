package com.github.tomakehurst.wiremock.extension.callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.tomakehurst.wiremock.extension.helpers.INamedHelper;
import com.github.tomakehurst.wiremock.extension.helpers.SupplementHelpers;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.extension.responsetemplating.helpers.WiremockHelpers;
import com.google.common.collect.ImmutableMap;

public class AsyncCallbackTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private BlockingQueue<Runnable> queue = new BlockingArrayQueue<>();
	private Executor executor = new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, queue);
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private Handlebars handlebars = new Handlebars();

	public AsyncCallbackTask() {
		super();

		for (StringHelpers helper : StringHelpers.values()) {
			this.handlebars.registerHelper(helper.name(), helper);
		}

		// Add all available wiremock helpers
		for (WiremockHelpers helper : WiremockHelpers.values()) {
			this.handlebars.registerHelper(helper.name(), helper);
		}
		
        for(INamedHelper helper: SupplementHelpers.values()) {
        	this.handlebars.registerHelper(helper.getName(), helper);
        }
	}

	private String resolveTemplate(String template, CallbackTask task) throws IOException {
		final ImmutableMap<String, RequestTemplateModel> model = ImmutableMap.of("request",
				RequestTemplateModel.from(task.getRequest()));
		Template templateCompiled = handlebars.compileInline(template);
		return templateCompiled.apply(model);
	}

	public void execute(final CallbackTask task) {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					@SuppressWarnings("unchecked")
					Map<Object, Object> callbackParameters = (Map<Object, Object>) task.getResponseDefinition()
							.getTransformerParameters().getOrDefault("callback", new HashMap<Object, Object>());
					String method = callbackParameters.getOrDefault("method", "GET").toString();
					if (!callbackParameters.containsKey("url")) {
						return;
					}
					String url = resolveTemplate(callbackParameters.get("url").toString(), task);
					String body = null;
					if (callbackParameters.containsKey("body")) {
						body = resolveTemplate(callbackParameters.get("body").toString(), task);
					}
					@SuppressWarnings("unchecked")
					Map<String, String> headers = (Map<String, String>) callbackParameters.get("headers");
					if (headers != null) {
						for (String key : headers.keySet()) {
							headers.put(key, resolveTemplate(headers.get(key), task));
						}
					}
					Integer delay = 0;
					if (callbackParameters.containsKey("delay")) {
						try {
							delay = (Integer) callbackParameters.get("delay");
						} catch (Exception e) {
						}
					}

					HttpUriRequest request;
					switch (method) {
					case "GET":
						request = new HttpGet(url);
						break;
					case "POST":
						request = new HttpPost(url);
						break;
					case "PUT":
						request = new HttpPut(url);
						break;
					case "DELETE":
						request = new HttpDelete(url);
						break;
					default:
						return;
					}
					if (headers != null) {
						for (Entry<String, String> entry : headers.entrySet()) {
							request.addHeader(entry.getKey(), entry.getValue());
						}
					}
					if (body != null && request instanceof HttpEntityEnclosingRequestBase) {
						((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body));
					}
					if (delay > 0) {
						Thread.sleep(delay);
					}
					logger.info("Execute callback %s %s", method, url);
					httpclient.execute(request);
				} catch (Exception e) {
					logger.warn("Failed to execute callback:", e);
				}
			}
		});

	}
}
