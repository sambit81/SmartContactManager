package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helpers.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private MyOrderRepository myOrderRepository;

	// method for adding common data
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME " + userName);

		// get the user using username(Email)

		User user = userRepository.getUserByUserName(userName);

		System.out.println("USER " + user);

		model.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result, Principal p,
			@RequestParam("profileImage") MultipartFile file, Model m) {

		try {
//			if (3>2) {
//				throw new Exception();
//			}
			if (result.hasErrors()) {
				// m.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}
			String name = p.getName();
			User u = userRepository.getUserByUserName(name);
			if (file.isEmpty()) {
				System.out.println("File is emptty");
				contact.setImage("profile.jpg");
			} else {
				// place the file to folder and update the name to contact
				String uniqueFilename = System.currentTimeMillis() + file.getOriginalFilename();
				contact.setImage(uniqueFilename);
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(
						saveFile.getAbsolutePath() + File.separator + uniqueFilename);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image saved");
			}

			contact.setUser(u);
			u.getContacts().add(contact);

			userRepository.save(u);
			System.out.println("DATA " + contact);
			System.out.println("Added to data base");
			m.addAttribute("msg", new Message("Contact successfully added !!", "success"));
		} catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
			e.printStackTrace();
			m.addAttribute("msg", new Message("Something went wrong !!", "danger"));
		}
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// show contacts handler
	// per page = 5[n]
	// current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show User Contacts");

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// currentPage -page
		// Contact per page -5
		// The pageable contains the above two information
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	@GetMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") int cid, Model m, Principal p) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		if (contactOptional.isEmpty()) {
			System.out.println("Optional is empty");
			m.addAttribute("title", "Not authorized");
			return "normal/unauthorized";
		}
		Contact contact = contactOptional.get();

		String userName = p.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() != contact.getUser().getId()) {
			m.addAttribute("title", "Not authorized");
			return "normal/unauthorized";
		}
		m.addAttribute("contact", contact);
		m.addAttribute("title", contact.getName());
		return "normal/contact_detail";
	}

	// delete contact handler

	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model m, HttpSession session, Principal p, RedirectAttributes redirectAttributes) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		if (contactOptional.isEmpty()) {
			m.addAttribute("title", "Not authorized");
			return "normal/unauthorized";
		}
		Contact contact = contactOptional.get();
		System.out.println(contactOptional);
		String name = p.getName();
		User u = this.userRepository.getUserByUserName(name);
		if (contact.getUser().getId() != u.getId()) {
			m.addAttribute("title", "Not authorized");
			return "normal/unauthorized";
		}
		//contact.setUser(null);
		//this.contactRepository.delete(contact);
		System.out.println("Contact "+contact.getcId());
		u.getContacts().remove(contact);
		
		this.userRepository.save(u);
		

		redirectAttributes.addFlashAttribute("msg", new Message("Contact deleted !!", "success"));
		return "redirect:/user/show-contacts/0";

	}

	// open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, Principal p) {
		try {
			Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
			String originalImageName = oldContactDetail.getImage();
			User u = this.userRepository.getUserByUserName(p.getName());
			if (!file.isEmpty()) {

				// file work

				// delete old photo

				if (!originalImageName.equals("profile.jpg")) {
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile, oldContactDetail.getImage());
					file1.delete();
				}

				
				File saveFile = new ClassPathResource("static/img").getFile();
				String uniqueFilename = System.currentTimeMillis() + file.getOriginalFilename();
				Path path = Paths.get(
						saveFile.getAbsolutePath() + File.separator + uniqueFilename);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image saved");
				contact.setImage(uniqueFilename);
			} else {
				contact.setImage(originalImageName);
			}

			contact.setUser(u);
			this.contactRepository.save(contact);

			m.addAttribute("msg", new Message("Contact updated successfully", "success"));
			System.out.println("Contact updated");
		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("msg", new Message("Contact update failed", "danger"));
		}

		return "redirect:/user/contact/" + contact.getcId();
	}
	
	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings | Smart Contact Manager");
		return "normal/settings";
	}
	
	// change password..handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, RedirectAttributes redirectAttributes) {
		System.out.println("OLD PASSWORD " + oldPassword);
		System.out.println("NEW PASSWORD " + newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			if (newPassword.isEmpty()) {
				redirectAttributes.addFlashAttribute("msg", new Message("New password can't be empty !!", "danger"));
				return "redirect:/user/settings";
			}
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			redirectAttributes.addFlashAttribute("msg", new Message("Password changed successfully !!", "success"));
		} else {
			redirectAttributes.addFlashAttribute("msg", new Message("Old password didn't matched !!", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal p) throws RazorpayException {
		// System.out.println("Hey order function ex.");
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient razorpay = null;
		try {
			razorpay = new RazorpayClient("rzp_live_YilPsUteiF70fU", "pmL3fHNGTqpVZE6nfgqJZELm");

		} catch (RazorpayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// creating new order
	    JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amt*100);
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "txn_234646");
						
		Order order = razorpay.orders.create(orderRequest);
		// if you want you can save this to your data..
		
		
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(p.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepository.save(myOrder);
		
		return order.toString();
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
		MyOrder myorder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myorder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg", "updated"));
	}
	
}
