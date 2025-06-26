package com.okstatelibrary.redbud.controller;

import com.okstatelibrary.redbud.config.HttpSessionConfig;
import com.okstatelibrary.redbud.entity.User;
import com.okstatelibrary.redbud.enums.ErrorType;
import com.okstatelibrary.redbud.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("")
public class AdminController extends MainController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String users(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		List<User> users = userService.findUserList();

		model.addAttribute("user", user);
		model.addAttribute("userList", users);


		return "admin/users";
	}

	@GetMapping("/sessions")
	public String sessions(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		HttpSessionConfig config = new HttpSessionConfig();
		model.addAttribute("number_of_sessions", config.getActiveSessions().size());

		return "admin/sessions";
	}

	@GetMapping("/profile")
	public String profile(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		// messageSetup(model);

		return "profile";
	}

	@PostMapping("/profile")
	public String profilePost(@ModelAttribute("user") User newUser, Model model) {

		User user = userService.findByUsername(newUser.getUsername());
		user.setUsername(newUser.getUsername());
		user.setFirstName(newUser.getFirstName());
		user.setLastName(newUser.getLastName());
		user.setEmail(newUser.getEmail());

		model.addAttribute("user", user);

		User savedUser = userService.saveUser(user);

		if (savedUser != null && savedUser.getUserId() > 0) {
			// message = "User details changed successfully!";

			// this.errorType = ErrorType.SUCESS;
		} else {
			// message = "User details changed unsuccessful. Contact administrator for
			// further info!";

			// this.errorType = ErrorType.ERROR;
		}

		return "redirect:/profile";
	}

	@GetMapping("/adduser")
	public String adduser(Principal principal, Model model) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		// messageSetup(model);

		return "adduser";
	}

	@PostMapping("/adduser")
	public String adduser(@ModelAttribute("user") User user, Model model, Principal principal) {

		if (userService.checkUserExists(user.getUsername(), user.getEmail())) {

			if (userService.checkEmailExists(user.getEmail())) {

//				this.errorType = ErrorType.ERROR;
//				this.message = "Email exists.";

				// model.addAttribute("emailExists", true);
			}

			if (userService.checkUsernameExists(user.getUsername())) {

//				this.errorType = ErrorType.ERROR;
//				this.message = "User name exists.";
			}

			return "adduser";
		} else {

			user.setDeleted(false);
			user.setEnabled(true);

			userService.createUser(user);

			// this.message = "User details saved successfully!";

			User adminUser = userService.findByUsername(principal.getName());
			model.addAttribute("user", adminUser);

			return "redirect:/users";
		}
	}

	@PostMapping("/updateuser")
	public String update(@ModelAttribute("user") User newUser, Model model) {

		User user = userService.findByUsername(newUser.getUsername());

		user.setUsername(newUser.getUsername());
		user.setFirstName(newUser.getFirstName());
		user.setLastName(newUser.getLastName());
		user.setEmail(newUser.getEmail());

		model.addAttribute("user", user);

		userService.saveUser(user);

		// message = "User details changed successfully!";

		return "redirect:/users";
	}

	@RequestMapping("user/{username}")
	public String details(@PathVariable String username, Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());
		User detailUser = userService.findByUsername(username);

		model.addAttribute("user", user);
		model.addAttribute("userdetails", detailUser);

		// messageSetup(model);

		return "admin/userdetails";
	}

	@PostMapping("/updateuserstatus")
	public String updatestatus(@ModelAttribute("user") User newUser, Model model) {

		User user = userService.findByUsername(newUser.getUsername());

		user.setEnabled(!user.isEnabled());

		userService.saveUser(user);

		// message = "Update user status successfully!";

		return "redirect:/user/" + user.getUsername();
	}

	@PostMapping("/resetpasswordbyuser")
	public String resetpasswordbyuser(@ModelAttribute("user") User newUser, Model model) {

		String username = newUser.getUsername();

		updatePassword(username, newUser.getPassword());

		// message = "Password changed successfully!";

		return "redirect:/profile";
	}

	@PostMapping("/resetpassword")
	public String resetpassword(@ModelAttribute("user") User newUser, Model model) {

		String username = newUser.getUsername();

		updatePassword(username, "abc123");

		this.errorType = ErrorType.SUCESS;
		
		message = "Reset password successfully!";

		return "redirect:/users";

	}

	@PostMapping("/deleteuser")
	public String deleteuser(@ModelAttribute("user") User newUser, Model model) {

		User user = userService.findByUsername(newUser.getUsername());

		user.setDeleted(true);

		userService.saveUser(user);

		// message = "Delete user successfully!";

		return "redirect:/users";
	}

	private void updatePassword(String username, String newPassword) {
		User user = userService.findByUsername(username);

		user.setPassword(newPassword);

		userService.updatePassword(user);
	}
}