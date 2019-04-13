package com.actolap.lyve.fe.payments.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;   
import com.actolap.lyve.fe.api.ApiManager;
import com.actolap.lyve.fe.backend.response.ObjectResponse;
import com.actolap.lyve.fe.backend.response.PaymentRequestResponse;
import com.actolap.lyve.fe.backend.response.PaymentVerifyResponse;
import com.actolap.lyve.fe.backend.response.ResponsibleGamingBuyin;
import com.actolap.lyve.fe.common.Constants;
import com.actolap.lyve.fe.common.FeUtils;
import com.actolap.lyve.fe.config.Config;
import com.actolap.lyve.fe.controller.CommonController;
import com.actolap.lyve.fe.interceptor.SessionWrapper;
import com.actolap.lyve.fe.payments.model.PaymentRequest;
import com.actolap.lyve.fe.payments.model.PaymentStatus;
import com.actolap.lyve.fe.payments.model.PaymentType;
import com.actolap.lyve.fe.payments.model.PaymentVerify;
import com.google.gson.Gson;

@Controller
public class PaymentController extends CommonController {

	Gson gson = new Gson();
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@RequestMapping(value = "payment/redirect", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	// public void paymenRedirect(@ModelAttribute("session") SessionWrapper session,
	// HttpServletRequest request,
	// HttpServletResponse response, @RequestBody String data) {

	public void paymenRedirect(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {

		try {
			PaymentVerify paymentVerify = new PaymentVerify();
			paymentVerify.setVerifyRequest(data);
			PaymentVerifyResponse beResponse = ApiManager.paymentVerify(paymentVerify,
					FeUtils.createAPIMeta(null, request));
			// if (beResponse.getStatus().equals(PaymentStatus.completed.toString())) {
			// ApiManager.buyIn(beResponse.getTxnid(), beResponse.getAmount(),
			// FeUtils.createAPIMeta(null, request));
			// }
			request.getSession().setAttribute("amount", beResponse.getAmount());
			request.getSession().setAttribute("txnId", beResponse.getTxnid());
			request.getSession().setAttribute("status", beResponse.getStatus());
			request.getSession().setAttribute("paymentStatus", true);
			if(beResponse.getUniqueId() != null)
				response.sendRedirect(request.getContextPath() + "/pay/popup/redirect/mobile");
			else
				response.sendRedirect(request.getContextPath() + "/payment/popup/redirect");
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
		} 
	} 
	 
	@RequestMapping(value = "paynow", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView paynow(@RequestParam("amount") long amount,  @RequestParam("code") String code,@ModelAttribute("session") SessionWrapper session,
			HttpServletRequest request, HttpServletResponse response) { 
		ModelAndView mvn = new ModelAndView("empty"); 
		mvn.addObject("title", "Payment"); 
		String gatewayId = "594125f655b4f46cf9bdc07e"; 
		try { 
			String firstName = ""; 
			if (session.getPlayer().getFirstName() != null && session.getPlayer().getFirstName() != "")
				firstName = session.getPlayer().getFirstName(); 
			else
				firstName = session.getPlayer().getEmail();
			PaymentRequest paymentRequest = new PaymentRequest(gatewayId, firstName, session.getPlayer().getLastName(),
					amount,code, session.getPlayer().getEmail(), session.getPlayer().getMobile(), "Chips",
					PaymentType.valueOf(Config.PAYMENT_TYPE), session.getPlayer().getPlayerId(),
					request.getContextPath(), "WSE_WEB", PaymentStatus.not_started.toString(),null);
			PaymentRequestResponse beResponse = ApiManager.paymentRequest(paymentRequest,
					FeUtils.createAPIMeta(null, request)); 
			if (beResponse.getFormData() != null) { 
				mvn.addObject("showLoader", true); 
				mvn.addObject("html", beResponse.getFormData().get("html")); 
				mvn.addObject("javaScript", beResponse.getFormData().get("javaScript")); 
			} 
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
		  }  
		return mvn; 
	} 
      
	@RequestMapping(value = "paynowmobile", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody 
	public ModelAndView paynowmobile(@RequestParam("amount") long amount, @RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("email") String email,
			@RequestParam("mobile") String mobile, @RequestParam("playerId") String playerId,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		String gatewayId = "594125f655b4f46cf9bdc07e";
		try {
			PaymentRequest paymentRequest = new PaymentRequest(gatewayId, firstName, lastName, amount,null, email, mobile,
					"Chips", PaymentType.valueOf(Config.PAYMENT_TYPE), playerId, request.getContextPath(), "WSE_WEB",
					PaymentStatus.not_started.toString(),null);
			PaymentRequestResponse beResponse = ApiManager.paymentRequest(paymentRequest,
					FeUtils.createAPIMeta(null, request));
			if (beResponse.getFormData() != null) {
				mvn.addObject("showLoader", true);
				mvn.addObject("html", beResponse.getFormData().get("html"));
				mvn.addObject("javaScript", beResponse.getFormData().get("javaScript"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return mvn;
	}

	@RequestMapping(value = "payment/popupMobile", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView paymentMobile(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		mvn.addObject("showLoader", true);
		mvn.addObject("javaScript", "var form = document.createElement(\"form\");\n"
				+ "    var element1 = document.createElement(\"input\"); \n" + "    form.method = \"POST\";\n"
				+ "    form.action = contextPath + \"/paynowmobile\";   \n" + "    element1.value=window.amount;\n"
				+ "    element1.name=\"amount\";\n" + "    form.appendChild(element1);  \n"

				+ "    var element2 = document.createElement(\"input\"); \n" + "    element2.value=window.firstName;\n"
				+ "    element2.name=\"firstName\";\n" + "    form.appendChild(element2);  \n"

				+ "    var element3 = document.createElement(\"input\"); \n" + "    element3.value=window.lastName;\n"
				+ "    element3.name=\"lastName\";\n" + "    form.appendChild(element3);  \n"

				+ "    var element4 = document.createElement(\"input\"); \n" + "    element4.value=window.email;\n"
				+ "    element4.name=\"email\";\n" + "    form.appendChild(element4);  \n"

				+ "    var element5 = document.createElement(\"input\"); \n" + "    element5.value=window.mobile;\n"
				+ "    element5.name=\"mobile\";\n" + "    form.appendChild(element5);  \n"

				+ "    var element6 = document.createElement(\"input\"); \n" + "    element6.value=window.playerId;\n"
				+ "    element6.name=\"playerId\";\n" + "    form.appendChild(element6);  \n"

				+ "    document.body.appendChild(form);\n" + " form.style.display = \"none\"; \n   form.submit();");
		return mvn;
	}

	@RequestMapping(value = "payment/popup", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView payment(@ModelAttribute("session") SessionWrapper session, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		mvn.addObject("showLoader", true);
		mvn.addObject("javaScript", "var form = document.createElement(\"form\");\n"
				+ "    var element1 = document.createElement(\"input\"); \n" + "    form.method = \"POST\";\n"
				+ "    form.action = contextPath + \"/paynow\";   \n" + "    element1.value=window.amount;\n"
				+ "    element1.name=\"amount\";\n" + "    form.appendChild(element1);  \n"
				+ "    var element2 = document.createElement(\"input\"); \n" + "    element2.value=window.code;\n"
				+ "    element2.name=\"code\";\n" + "    form.appendChild(element2);  \n"
				+ "    document.body.appendChild(form);\n" + " form.style.display = \"none\"; \n   form.submit();");
		return mvn;
	}
	
	@RequestMapping(value = "payment/pdfDownload/popup", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView pdfDownload(@ModelAttribute("session") SessionWrapper session, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		mvn.addObject("showLoader", true);
		mvn.addObject("javaScript", "var form = document.createElement(\"form\");\n"
				+ "    var element1 = document.createElement(\"input\"); \n" + "    form.method = \"POST\";\n"
				+ "    form.action = contextPath + \"/paynow/pdf\";   \n" + "    element1.value=window.amount;\n"
				+ "    element1.name=\"amount\";\n" + "    form.appendChild(element1);  \n"
				+ "    document.body.appendChild(form);\n" + " form.style.display = \"none\"; \n   form.submit();");
		return mvn;
	}
	@RequestMapping(value = "paynow/pdf", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView paynowForPdfDownload(@RequestParam("amount") long amount, @ModelAttribute("session") SessionWrapper session,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		String gatewayId = "594125f655b4f46cf9bdc07e";
		try {
			String firstName = "";
			if (session.getPlayer().getFirstName() != null && session.getPlayer().getFirstName() != "")
				firstName = session.getPlayer().getFirstName(); 
			else
				firstName = session.getPlayer().getEmail();
			PaymentRequest paymentRequest = new PaymentRequest(gatewayId, firstName, session.getPlayer().getLastName(),
					amount,null, session.getPlayer().getEmail(), session.getPlayer().getMobile(), "PdfDownload",
					PaymentType.valueOf(Config.PAYMENT_TYPE), session.getPlayer().getPlayerId(),
					request.getContextPath(), "WSE_WEB", PaymentStatus.not_started.toString(),null);
			PaymentRequestResponse beResponse = ApiManager.paymentRequest(paymentRequest,
					FeUtils.createAPIMeta(null, request));
			if (beResponse.getFormData() != null) {
				mvn.addObject("showLoader", true);
				mvn.addObject("html", beResponse.getFormData().get("html"));
				mvn.addObject("javaScript", beResponse.getFormData().get("javaScript"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		  }
		return mvn;
	}

	@RequestMapping(value = "payment/popup/redirect", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView paymentPopupRedirect(@ModelAttribute("session") SessionWrapper session,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		mvn.addObject("showLoader", true);
		mvn.addObject("javaScript", "window.opener.location.reload(); \n window.close();");
		return mvn;
	}
	
	//for upgrade now
	@RequestMapping(value = "upgrade/payment/popup", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView upgradePayment(@ModelAttribute("session") SessionWrapper session, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		mvn.addObject("showLoader", true);
		mvn.addObject("javaScript", "var form = document.createElement(\"form\");\n"
				+ "    var element1 = document.createElement(\"input\"); \n" + "    form.method = \"POST\";\n"
				+ "    form.action = contextPath + \"/paynow/upgrade\";   \n" + "    element1.value=window.amount;\n"
				+ "    element1.name=\"amount\";\n" + "    form.appendChild(element1);  \n"
				+ "    var element2 = document.createElement(\"input\"); \n" + "    element2.value=window.code;\n"
				+ "    element2.name=\"code\";\n" + "    form.appendChild(element2);  \n"
				+ "    document.body.appendChild(form);\n" + " form.style.display = \"none\"; \n   form.submit();");
		return mvn;
	}
	
	//for coupon payment
		@RequestMapping(value = "coupon/payment/popup", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
		@ResponseBody
		public ModelAndView couponPayment(@ModelAttribute("session") SessionWrapper session, HttpServletRequest request,
				HttpServletResponse response) {
			ModelAndView mvn = new ModelAndView("empty");
			mvn.addObject("title", "Payment");
			mvn.addObject("showLoader", true);
			mvn.addObject("javaScript", "var form = document.createElement(\"form\");\n"
					+ "    var element1 = document.createElement(\"input\"); \n" + "    form.method = \"POST\";\n"
					+ "    form.action = contextPath + \"/coupon/paynow\";   \n" + "    element1.value=window.amount;\n"
					+ "    element1.name=\"amount\";\n" + "    form.appendChild(element1);  \n"
					+ "    var element2 = document.createElement(\"input\"); \n" + "    element2.value=window.code;\n"
					+ "    element2.name=\"code\";\n" + "    form.appendChild(element2);  \n"
					+ "    document.body.appendChild(form);\n" + " form.style.display = \"none\"; \n   form.submit();");
			return mvn;
		}
		
		@RequestMapping(value = "coupon/paynow", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
		@ResponseBody
		public ModelAndView couponPaynow(@RequestParam("amount") long amount,  @RequestParam("code") String code,@ModelAttribute("session") SessionWrapper session,
				HttpServletRequest request, HttpServletResponse response) {
			ModelAndView mvn = new ModelAndView("empty");
			mvn.addObject("title", "Payment");
			String gatewayId = "594125f655b4f46cf9bdc07e";
			try {
				String firstName = "";
				if (session.getPlayer().getFirstName() != null && session.getPlayer().getFirstName() != "")
					firstName = session.getPlayer().getFirstName(); 
				else
					firstName = session.getPlayer().getEmail();
				PaymentRequest paymentRequest = new PaymentRequest(gatewayId, firstName, session.getPlayer().getLastName(),
						amount,code, session.getPlayer().getEmail(), session.getPlayer().getMobile(), "CouponPayment",
						PaymentType.valueOf(Config.PAYMENT_TYPE), session.getPlayer().getPlayerId(),
						request.getContextPath(), "WSE_WEB", PaymentStatus.not_started.toString(),null);
				PaymentRequestResponse beResponse = ApiManager.paymentRequest(paymentRequest,
						FeUtils.createAPIMeta(null, request));
				if (beResponse.getFormData() != null) {
					mvn.addObject("showLoader", true);
					mvn.addObject("html", beResponse.getFormData().get("html"));
					mvn.addObject("javaScript", beResponse.getFormData().get("javaScript"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			  }
			return mvn;
		}
	
	@RequestMapping(value = "paynow/upgrade", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ModelAndView upgradePaynow(@RequestParam("amount") long amount,  @RequestParam("code") String code, @ModelAttribute("session") SessionWrapper session,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mvn = new ModelAndView("empty");
		mvn.addObject("title", "Payment");
		String gatewayId = "594125f655b4f46cf9bdc07e";
		try {
			String firstName = "";
			if (session.getPlayer().getFirstName() != null && session.getPlayer().getFirstName() != "")
				firstName = session.getPlayer().getFirstName(); 
			else
				firstName = session.getPlayer().getEmail();
			PaymentRequest paymentRequest = new PaymentRequest(gatewayId, firstName, session.getPlayer().getLastName(),
					amount, code, session.getPlayer().getEmail(), session.getPlayer().getMobile(), "Upgrade",
					PaymentType.valueOf(Config.PAYMENT_TYPE), session.getPlayer().getPlayerId(),
					request.getContextPath(), "WSE_WEB", PaymentStatus.not_started.toString(),null);
			PaymentRequestResponse beResponse = ApiManager.paymentRequest(paymentRequest,
					FeUtils.createAPIMeta(null, request)); 
			if (beResponse.getFormData() != null) { 
				mvn.addObject("showLoader", true); 
				mvn.addObject("html", beResponse.getFormData().get("html"));
				mvn.addObject("javaScript", beResponse.getFormData().get("javaScript"));
			} 
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
		  } 
		return mvn; 
	} 
	  
	@ResponseBody 
	@RequestMapping(value = "validate/buyin/responsible/gaming", method = RequestMethod.GET)
	public ObjectResponse validateBuyinResponsibleGaming(@RequestParam("amount") String amount, HttpServletRequest request,	@ModelAttribute("session") SessionWrapper session) {  
		ObjectResponse objResponse = new ObjectResponse(); 
		try {  
			ResponsibleGamingBuyin beResponse = ApiManager.validateBuyinResponsibleGaming(amount, session.getPlayer().getPlayerId(), FeUtils.createAPIMeta(session, request));
			if (beResponse != null) { 
				if (beResponse.isS()) { 
					objResponse.setResponse(beResponse); 
					objResponse.setStatus(Constants.SUCCESS); 
				} else { 
					objResponse.setErrorDetails(beResponse.getEd());
					objResponse.setStatus(Constants.FAILED);
				} 
			} else { 
				objResponse.setStatus(Constants.SOME_THING_WENT_WRONG);
			} 
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
		} 
		return objResponse; 
	} 

} 
 

