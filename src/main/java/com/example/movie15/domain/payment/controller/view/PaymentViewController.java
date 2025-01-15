package com.example.movie15.domain.payment.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.movie15.domain.payment.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentViewController {

	private final PaymentService paymentService;


	@GetMapping
	public String checkoutPage(Model model) {
		model.addAttribute("orderId", "testId");
		model.addAttribute("orderName", "testName");
		model.addAttribute("amount", 10000);

		return "/payment/checkout";
	}

	@GetMapping("/success")
	public String successPage() {

		return "/payment/success";
	}

	@GetMapping("/fail")
	public String failPage(@RequestParam String orderId, HttpServletRequest request, Model model) {

		paymentService.tossPaymentFail(orderId);

		model.addAttribute("code", request.getParameter("code"));
		model.addAttribute("message", request.getParameter("message"));
		return "/payment/fail";
	}
}
