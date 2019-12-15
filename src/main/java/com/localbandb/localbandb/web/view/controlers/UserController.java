package com.localbandb.localbandb.web.view.controlers;

import com.localbandb.localbandb.config.authentication.AuthenticationFacade;
import com.localbandb.localbandb.data.models.User;
import com.localbandb.localbandb.web.view.models.UserRegisterModel;
import com.localbandb.localbandb.services.models.UserServiceModel;
import com.localbandb.localbandb.services.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
  private final UserService userService;
  private final AuthenticationFacade authenticationFacade;
  private final ModelMapper mapper;

  @Autowired
  public UserController(UserService userService, AuthenticationFacade authenticationFacade, ModelMapper mapper) {
    this.userService = userService;
    this.authenticationFacade = authenticationFacade;
    this.mapper = mapper;
  }

  @GetMapping("/login")
  public ModelAndView login(@RequestParam(required = false)String error, ModelAndView modelAndView) {
    if(error != null) {
      modelAndView.addObject("error", error);
      this.view("/user/user-login", modelAndView);
    }
    return this.view("user/user-login", modelAndView);
  }

  /*@PostMapping("/login")
  public ModelAndView postLogin(@Valid @ModelAttribute UserLoginModel userLoginModel,BindingResult bindingResult,  HttpSession session, ModelAndView modelAndView) {
    if(bindingResult.hasErrors()) {
      return this.view("user/user-login");
    }

   if(userService.login(userLoginModel.getUsername(), userLoginModel.getPassword())) {
     session.setAttribute("userUsername", userLoginModel.getUsername());
    return this.redirect("property/show/all");
   }
   modelAndView.addObject("message", "Username and/or Password are not correct!");
   return this.view("user/user-login",  modelAndView);
  }*/


  @GetMapping("/register/{role}")
  @PreAuthorize("isAnonymous()")
  public ModelAndView register(@PathVariable String role,@ModelAttribute("userRegisterModel")  UserRegisterModel userRegisterModel, ModelAndView modelAndView) {
   modelAndView.addObject("role", role);
    return new ModelAndView("user/user-register");
  }

  @PostMapping("/register/{role}")
  @PreAuthorize("isAnonymous()")
  public ModelAndView registerConfirm(@PathVariable String role, @Valid @ModelAttribute UserRegisterModel model,
                                      BindingResult bindingResult, RedirectAttributes attributes) {
    if(bindingResult.hasErrors()) {
      return view("user/user-register");
    }

    UserServiceModel userServiceModel = mapper.map(model, UserServiceModel.class);
    userServiceModel.setRole(role.toUpperCase());
    if (!userService.saveUser(userServiceModel)) {
      attributes.addFlashAttribute("message", "message");
      return redirect("user/register");
    }
    return this.redirect("user/login");
  }

  @GetMapping("/home")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView home(ModelAndView modelAndView) {
    Collection<? extends GrantedAuthority> authorities = authenticationFacade.getAuthentication().getAuthorities();
   List<String> roles =  authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

    if(roles.contains("ROLE_ADMIN")) {
      return this.view("home/index");
    } else if (roles.contains("ROLE_GUEST")){
     return this.redirect("reservation/guest/home");
    } else if (roles.contains("ROLE_HOST")) {
        return this.redirect("property/show/all");
    }
    return this.view("reservation/reservation-reservations", modelAndView);
  }
}
