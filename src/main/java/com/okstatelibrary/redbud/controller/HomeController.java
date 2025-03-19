package com.okstatelibrary.redbud.controller;

import java.security.Principal;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.okstatelibrary.redbud.entity.Notification;
import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.service.NotificationService;
import com.okstatelibrary.redbud.service.UserService;
import com.okstatelibrary.redbud.util.AppSystemProperties;

@Controller
public class HomeController {

	@Autowired
	private AppSystemProperties appSystems;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@RequestMapping("/")
	public String index() {

		System.out.println(appSystems.toString());

		return "redirect:/index";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/index")
	public String index(Principal principal, Model model) throws ParseException {

		User user = null;

		if (principal != null) {

			user = userService.findByUsername(principal.getName());

			model.addAttribute("user", user);
		}

		Optional<Notification> notification = notificationService.getNotification();

		if (notification.isPresent()) {
			model.addAttribute("notification_type", "ribbon-" + notification.get().getNotificationType());
			model.addAttribute("notification_message", notification.get().getNotification());
		}

		model.addAttribute("user", user);

		return "index";
	}

}
