package com.example.movie15.domain.payment.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.movie15.domain.payment.dto.PaymentDto;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

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
	public String failPage(HttpServletRequest request, Model model) {

		model.addAttribute("code", request.getParameter("code"));
		model.addAttribute("message", request.getParameter("message"));
		return "/payment/fail";
	}
}
