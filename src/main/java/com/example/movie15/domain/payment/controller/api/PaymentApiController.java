package com.example.movie15.domain.payment.controller.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.movie15.domain.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentApiController {

	@Value("${payment.toss.secretKey}")
	private String secretKey;

	private final PaymentService paymentService;

	@PostMapping( "/toss/confirm")
	public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {

		JSONObject response = sendRequest(parseRequestData(jsonBody), secretKey, "https://api.tosspayments.com/v1/payments/confirm");
		int statusCode = response.containsKey("error") ? 400 : 200;

		return ResponseEntity.status(statusCode).body(response);
	}

	@PostMapping("/booking/{bookingId}/payment")
	public ResponseEntity tossPaymentCancel(
		@PathVariable Long bookingId,
		@RequestParam String paymentKey,
		@RequestParam String cancelReason
	) {
		return ResponseEntity.ok().body(paymentService.tossPaymentCancel(bookingId, paymentKey, cancelReason));
	}


	private JSONObject parseRequestData(String jsonBody) {
		try {
			return (JSONObject) new JSONParser().parse(jsonBody);
		} catch (ParseException e) {
			log.error("JSON Parsing Error", e);
			return new JSONObject();
		}
	}

	private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
		HttpURLConnection connection = createConnection(secretKey, urlString);
		try (OutputStream os = connection.getOutputStream()) {
			os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
		}

		try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
			 Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
			return (JSONObject) new JSONParser().parse(reader);
		} catch (Exception e) {
			log.error("Error reading response", e);
			JSONObject errorResponse = new JSONObject();
			errorResponse.put("error", "Error reading response");
			return errorResponse;
		}
	}

	private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		return connection;
	}
}